package com.scintilla;

import com.scintilla.getter.SourceDataGetter;
import com.scintilla.group.Group;
import com.scintilla.group.Groups;
import com.scintilla.http.HTTPPool;
import com.scintilla.view.ByteView;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class CacheTest {

    private static Map<String, String> db = new HashMap<>();

    private static Groups groups = new Groups();

    public static void main(String[] args) throws IOException, InterruptedException {

        db.put("a", "a1");
        db.put("b", "b2");
        db.put("c", "c3");
        db.put("234848549", "i4");

        int port = 8001;

        Map<Integer, String> addrMap = new HashMap<>();
        addrMap.put(8001, "http://localhost:8001");
        addrMap.put(8002, "http://localhost:8002");
        addrMap.put(8003, "http://localhost:8003");

        String[] addrs = new String[3];
        addrs[0] = "http://localhost:8001";
        addrs[1] = "http://localhost:8002";
        addrs[2] = "http://localhost:8003";

        Group g = createGroup();

        String apiAddr = "http://localhost:9911";
        new Thread() {
            @Override
            public void run() {
                try {
                    startAPIServer(apiAddr, g);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        startCacheServer(addrMap.get(port), addrs, g);
    }

    public static Group createGroup() {
        return new Group("scores", 2 << 10, groups, new SourceDataGetter() {
            @Override
            public byte[] getSourceData(String key) {
                System.out.println("[SlowDB] search key from db: " + key);
                String s = db.get(key);
                if (s != null) {
                    return s.getBytes();
                }
                System.out.println(key + " not exists.");
                return null;
            }
        });
    }

    public static void startCacheServer(String addr, String[] addrs, Group group) throws IOException {
        HTTPPool peers = new HTTPPool(addr, groups);
        peers.setPeersConsistentMap(addrs);
        group.registerPeers(peers);

        listening(addr.substring(7), peers);
    }

    public static void startAPIServer(String apiAddr, Group group) throws IOException {
        APIHandler api = new APIHandler(group);
        apiListening(apiAddr.substring(7), api);
    }

    public static void listening(String address, HTTPPool peers) throws IOException {
        String[] s = address.split(":");
        InetSocketAddress addr = new InetSocketAddress(s[0], Integer.parseInt(s[1]));

        HttpServer server = HttpServer.create(addr,0);
        server.createContext("/_cache/", peers);
        System.out.println("cache is running at :" + addr.getHostName() + ":" +addr.getPort());
        server.start();
    }

    public static void apiListening(String address, HttpHandler peers) throws IOException {
        String[] s = address.split(":");
        InetSocketAddress addr = new InetSocketAddress(s[0], Integer.parseInt(s[1]));

        HttpServer server = HttpServer.create(addr,0);
        server.createContext("/api", peers);
        System.out.println("api is running at :" + addr.getHostName() + ":" +addr.getPort());
        server.start();
    }
}

class APIHandler implements HttpHandler {

    private Group group;

    public APIHandler(Group group) {
        this.group = group;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String queryStr = exchange.getRequestURI().getQuery();

        String[] kv = queryStr.split("=");
        ByteView byteView = group.get(kv[1]);

        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(byteView.byteSlice());
        os.close();
    }
}
