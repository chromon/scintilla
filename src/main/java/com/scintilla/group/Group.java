package com.scintilla.group;

import com.scintilla.cache.Cache;
import com.scintilla.cachepb.Cachepb;
import com.scintilla.getter.SourceDataGetter;
import com.scintilla.http.PeerGetter;
import com.scintilla.http.PeerPicker;
import com.scintilla.singleflight.Request;
import com.scintilla.singleflight.WaitGroup;
import com.scintilla.view.ByteView;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Group is a cache namespace and associated data loaded spread over.
 */
public class Group {

    /**
     * Group name as a cache namespace, each Group has a unique name.
     */
    private String name;

    /**
     * Callback interface for getting source data when the cache is not hit.
     */
    private SourceDataGetter sourceDataGetter;

    /**
     * Cache with concurrency features.
     */
    private Cache cache;

    /**
     * Inject the HTTPPool that implements the PeerPicker interface into the Group.
     */
    private PeerPicker peerPicker;

    /**
     * use WaitGroup to make sure that each key is only fetched once.
     */
    private WaitGroup waitGroup;

    /**
     * Constructs a Group and put it in Groups map.
     *
     * @param name Group name.
     * @param cacheBytes Cache size.
     * @param sourceDataGetter Getter callback func.
     * @param groups Group collection.
     */
    public Group(String name, long cacheBytes, Groups groups, SourceDataGetter sourceDataGetter) {
        if (sourceDataGetter == null) {
            throw new RuntimeException("Getter is null.");
        }

        this.name = name;
        this.cache = new Cache(cacheBytes);
        this.sourceDataGetter = sourceDataGetter;
        this.waitGroup = new WaitGroup();

        groups.getGroups().put(name, this);
    }

    /**
     * Registers a PeerPicker for choosing remote peer.
     * Injects the HTTPPool that implements the PeerPicker interface into the Group.
     *
     * @param peerPicker The HTTPPool that implements the PeerPicker.
     */
    public void registerPeers(PeerPicker peerPicker) {
        if (this.peerPicker != null) {
            throw new RuntimeException("Register PeerPicker called more than once.");
        }
        this.peerPicker = peerPicker;
    }

    /**
     * Get value for a key from cache.
     *
     * @param key Cache key.
     * @return Byte view.
     */
    public ByteView get(String key) {
        Logger logger = Logger.getLogger(Group.class.getName());

        if (key.equals("")) {
            logger.log(Level.WARNING, "key is required.");
            return null;
        }

        // Get cache data from Cache, if exists return the data.
        ByteView byteView = this.cache.get(key);
        if (byteView != null) {
            logger.log(Level.INFO,
                    "[Cache] hit, {key: " + key + ", value: " + byteView.toString() + "}");
            return byteView;
        }

        // If not exists invoke load method.
        return this.load(key);
    }

    /**
     * Loads key either by invoking the getter locally or by sending it to another machine.
     * Use the PickPeer() method to select the node and call getFromPeer()
     * to get it from the remote if it is not a native node.
     * If it is a native node or fails, fall back to getLocally().
     */
    public ByteView load(String key) {

        // each key is only fetched once (either locally or remotely)
        // regardless of the number of concurrent callers.
        Object obj = this.waitGroup.requestWait(key, new Request() {
            @Override
            public Object sendRequest() {
                if (peerPicker != null) {
                    PeerGetter peerGetter = peerPicker.pickPeer(key);
                    if (peerGetter != null) {
                        ByteView value = getFromPeer(peerGetter, key);
                        if (value != null) {
                            return value;
                        }
                        System.err.println("[Cache] Failed to get from peer.");
                    }
                }
                return getLocally(key);
            }
        });
        return (ByteView) obj;
    }

    /**
     * Use an httpGetter that implements the PeerGetter interface
     * to get cached values from accessing remote nodes.
     *
     * @param peerGetter an httpGetter that implements the PeerGetter interface .
     * @param key Cache key.
     * @return Cache value.
     */
    public ByteView getFromPeer(PeerGetter peerGetter, String key) {

        // Request.
        Cachepb.Request.Builder reqBuilder = Cachepb.Request.newBuilder();
        reqBuilder.setGroup(this.name);
        reqBuilder.setKey(key);
        Cachepb.Request request = reqBuilder.build();

        // Response.
        Cachepb.Response response = peerGetter.get(request);

        byte[] bytes = response.getValue().toByteArray();
        if (bytes.length == 0) {
            return null;
        }

        ByteView b = new ByteView();
        b.setB(bytes);
        return b;
    }

    /**
     * Loads key by invoking the getter locally
     *
     * @param key Cache key.
     * @return Byte view.
     */
    private ByteView getLocally(String key) {
        // Get the source data, customized by the user.
        byte[] bytes = this.sourceDataGetter.getSourceData(key);
        if (bytes == null) {
            return null;
        }

        // Encapsulated Data for ByteView.
        ByteView value = new ByteView();
        value.setB(value.cloneBytes(bytes));
        // Add source data to cache.
        this.populateCache(key, value);

        return value;
    }

    /**
     * Add source data to cache.
     *
     * @param key Cache key.
     * @param value Cache value.
     */
    private void populateCache(String key, ByteView value) {
        this.cache.add(key, value);
    }
}
