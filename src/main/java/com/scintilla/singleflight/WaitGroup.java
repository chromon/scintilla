package com.scintilla.singleflight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Managing requests with different keys as a wait group.
 */
public class WaitGroup {

    /**
     * Requests in process
     */
    private Map<String, Call> map;

    /**
     * Send requests based on different keys.
     * For the same key, the method is called only once,
     * no matter how many times it is called.
     *
     * @param key Request key.
     * @param req Callback request method.
     * @return Response data.
     */
    public Object requestWait(String key, Request req) {

        if (this.map == null) {
            this.map = new ConcurrentHashMap<>();
        }

        Call call = this.map.get(key);
        if (call != null) {
            // If the request is in progress, waiting.
            call.getPhaser().arriveAndAwaitAdvance();
            // End of request, return results
            return call.getVal();
        }

        call = new Call();
        call.getPhaser().register();
        // Add request to map.
        // Indicates that the key already has a request in process.
        this.map.put(key, call);

        // Invoke callback func, send request.
        call.setVal(req.sendRequest());
        // Request End.
        call.getPhaser().arrive();

        // Update map, remove request.
        this.map.remove(key);

        return call.getVal();
    }
}
