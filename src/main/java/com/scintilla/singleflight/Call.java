package com.scintilla.singleflight;

import java.util.concurrent.Phaser;

/**
 * Represents a request that is in progress, or has been closed.
 */
public class Call {

    /**
     * Lock.
     */
    private Phaser phaser;

    /**
     * Data returned by request
     */
    private Object val;

    public Call() {
        this.phaser = new Phaser();
    }

    public Phaser getPhaser() {
        return phaser;
    }

    public void setPhaser(Phaser phaser) {
        this.phaser = phaser;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }
}
