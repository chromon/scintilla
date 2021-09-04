package com.scintilla.http;

import com.scintilla.getter.Getter;
import com.scintilla.group.Group;
import com.scintilla.group.Groups;
import com.scintilla.view.ByteView;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core data structure for HTTP communication between nodes,
 * include server and client.
 */
public class HTTPPool implements HttpHandler {

    /**
     * this peer's base URL, e.g. "https://example.net:8080"
     */
    private String baseURL;

    /**
     * This peer's base path, e.g. "/_cache/"
     */
    private String basePath = "/_cache/";

    private Groups groups;

    /**
     * Constructs HTTPPool with base URL.
     *
     * @param baseURL This peer's base URL.
     */
    public HTTPPool(String baseURL, Groups groups) {
        this.baseURL = baseURL;
        this.groups = groups;
    }

    /**
     * Constructs HTTPPool with base URL and base path.
     *
     * @param baseURL This peer's base URL.
     * @param basePath This peer's base path.
     */
    public HTTPPool(String baseURL, String basePath, Groups groups) {
        this.baseURL = baseURL;
        this.basePath = basePath;
        this.groups = groups;
    }

    /**
     * Log info with server name.
     *
     * @param format Format string.
     * @param obj Format object.
     */
    public void log(String format, Object... obj) {
        Logger logger = Logger.getLogger(Group.class.getName());
        String msg = String.format("[Cache Server %s] %s",
                this.baseURL, String.format(format, obj));
        logger.log(Level.INFO, msg);
    }

    /**
     * Handle the given request and generate an appropriate response.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException io exception.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (!path.startsWith(this.basePath)) {
            throw new RuntimeException("HTTPPool serving unexpected path: "
                    + exchange.getRequestURI().getPath());
        }

        this.log("%s %s", exchange.getRequestMethod(), exchange.getRequestURI().getPath());
        // <baseURL>/<basePath>/<groupName>/<key> required
        // parts[0] = groupName, parts[1] = key
        String[] parts = path.substring("/_cache/".length()).split("/");
        if (parts.length != 2) {
            exchange.sendResponseHeaders(400, 0);
            this.write(exchange, "400 - Status Bad Request: " + exchange.getRequestURI().getPath());
        }

        String groupName = parts[0];
        String key = parts[1];

        Group group = this.groups.getGroup(groupName);
        if (group == null) {
            exchange.sendResponseHeaders(404, 0);
            this.write(exchange, "404 - StatusNotFound: No such group '" + groupName + "'");
        } else {
            ByteView byteView = group.get(key);
            if (byteView == null) {
                exchange.sendResponseHeaders(500, 0);
                this.write(exchange, "500 - Status Internal Server Error.");
            } else {
                exchange.sendResponseHeaders(200, 0);
                this.write(exchange, new String(byteView.byteSlice()));
            }
        }
    }

    /**
     * Output back client.
     *
     * @param exchange HttpExchange.
     * @param response Response message.
     * @throws IOException IO exception.
     */
    public void write(HttpExchange exchange, String response) throws IOException {
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}