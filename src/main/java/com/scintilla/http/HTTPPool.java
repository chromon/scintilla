package com.scintilla.http;

import com.google.protobuf.ByteString;
import com.scintilla.cachepb.Cachepb;
import com.scintilla.consistenthash.ConsistentHashMap;
import com.scintilla.group.Group;
import com.scintilla.group.Groups;
import com.scintilla.view.ByteView;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core data structure for HTTP communication between nodes,
 * include server and client.
 */
public class HTTPPool implements HttpHandler, PeerPicker {

    /**
     * this peer's base URL, e.g. "https://example.net:8080"
     */
    private String baseURL;

    /**
     * This peer's base path, e.g. "/_cache/"
     */
    private String basePath = "/_cache/";

    /**
     * Cache groups.
     */
    private Groups groups;

    /**
     * Consistent hash map to select a node based on a specific key.
     */
    private ConsistentHashMap peersConsistentMap;

    /**
     * Mapping remote nodes to httpGetters.
     * Each remote node corresponds to an httpGetter.
     * HttpGetter is related to the address baseURL of the remote node.
     */
    private Map<String, HTTPGetter> httpGetterMap;

    /**
     * Default virtual node multiples.
     */
    private static final int defaultReplicas = 50;

    /**
     * Constructs HTTPPool with base URL.
     *
     * @param baseURL This peer's base URL.
     * @param groups Cache groups.
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
     * @param groups Cache groups.
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
     * Instantiates the consistency hashing map
     * and adds the nodes.
     * Create an HTTP client (HTTPGetter) for each node.
     *
     * @param peers nodes.
     */
    public synchronized void setPeersConsistentMap(String... peers) {
        this.peersConsistentMap = new ConsistentHashMap(defaultReplicas, null);
        this.peersConsistentMap.add(peers);
        this.httpGetterMap = new HashMap<>();

        // Create http client for each node.
        for (String peer : peers) {
            // http://example.com/_cache/
            this.httpGetterMap.put(peer, new HTTPGetter(peer + this.basePath));
        }
    }

    /**
     * Picks a peer according to key.
     *
     * @param key Cache key.
     * @return The HTTP client corresponding to the node.
     */
    @Override
    public synchronized PeerGetter pickPeer(String key) {
        // node key
        String peer = this.peersConsistentMap.get(key);
        if (!peer.equals("") && !peer.equals(this.baseURL)) {
            this.log("Pick peer %s", peer);
            return this.httpGetterMap.get(peer);
        }
        return null;
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

                Cachepb.Response.Builder respBuilder = Cachepb.Response.newBuilder();
                byte[] bytes = byteView.byteSlice();

                respBuilder.setValue(ByteString.copyFrom(bytes));
                Cachepb.Response response = respBuilder.build();

                // Protobuf transferred via http need encode base64 code.
                Base64.Encoder encoder = Base64.getEncoder();
                this.write(exchange, encoder.encodeToString(response.toByteArray()));
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

    /**
     * output back client with protobuf.
     *
     * @param exchange  HttpExchange.
     * @param response  Response message.
     * @throws IOException  IO exception.
     */
    public void write(HttpExchange exchange, byte[] response) throws IOException {
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}