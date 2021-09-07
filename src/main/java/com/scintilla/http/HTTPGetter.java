package com.scintilla.http;

import com.google.protobuf.InvalidProtocolBufferException;
import com.scintilla.cachepb.Cachepb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTPGetter implementation PeerGetter
 * as HTTP client for accessing remote nodes.
 */
public class HTTPGetter implements PeerGetter {

    /**
     * Indicates the address of the remote node to be accessed.
     *
     * e.g. http://example.com/_cache/
     */
    private String baseURL;

    /**
     * Constructs HTTPGetter with base url.
     *
     * @param baseURL base url.
     */
    public HTTPGetter(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Get cache value with group name and specified cache key.
     *
     * @param groupName Group name.
     * @param key Cache key.
     * @return Cache value.
     */
    @Override
    public byte[] get(String groupName, String key) {
        // url e.g. http://example.com/_cache/groupname/key
        String url = this.baseURL +
                groupName + "/" +
                key;

        String resp = this.sendGetRequest(url);
        if (resp != null) {
            return resp.getBytes();
        }
        return new byte[0];
    }

    /**
     * Find cached values from the group with protobuf.
     *
     * @param in request.
     */
    @Override
    public Cachepb.Response get(Cachepb.Request in) {
        // url e.g. http://example.com/_cache/groupname/key
        String url = this.baseURL +
                in.getGroup() + "/" +
                in.getKey();

        // send request and get message from server.
        String resp = this.sendGetRequest(url);

        Cachepb.Response response = null;

        if (resp != null) {
            byte[] bytes = resp.getBytes();

            // Protobuf transferred via http need encode base64 code.
            // Decode base64 code.
            Base64.Decoder decoder = Base64.getDecoder();

            try {
                response = Cachepb.Response.parseFrom(decoder.decode(bytes));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * Send get request to remote server.
     *
     * @param url Request url.
     * @return Response message.
     */
    public String sendGetRequest(String url) {

        Logger logger = Logger.getLogger(HTTPGetter.class.getName());
        logger.log(Level.INFO, "[HTTP Client] send request to: " + url);

        StringBuilder result = new StringBuilder();
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();

            // Set request properties.
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            // Establishing the actual connection.
            conn.connect();

            if (conn.getResponseCode() != 200) {
                System.err.println("server returned: " + conn.getResponseMessage());
                return null;
            }

            // Read the response.
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }
}
