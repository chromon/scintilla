package com.scintilla.http;

import com.scintilla.getter.SourceDataGetter;
import com.scintilla.group.Group;
import com.scintilla.group.Groups;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HTTPPoolTest {

    @Test
    public void testHTTPPool() throws IOException {
        Map<String, String> db = new HashMap<>();
        db.put("a", "1");
        db.put("b", "2");
        db.put("c", "3");

        Groups groups = new Groups();

        new Group("scores", 2 << 10, groups, new SourceDataGetter() {
            @Override
            public byte[] getSourceData(String key) {
                System.out.println("[SlowDB] search key:" + key);
                if (db.get(key) != null) {
                    return db.get(key).getBytes();
                }
                System.out.println(key + " not exist.");
                return null;
            }
        });

        InetSocketAddress addr = new InetSocketAddress("localhost", 8001);

        HttpServer server = HttpServer.create(addr,0);
        server.createContext("/_cache/",
                new HTTPPool(addr.getAddress().getHostAddress(), groups));
        System.out.println("cache is running at :" + addr.getAddress().getHostAddress());
        server.start();
    }
}
