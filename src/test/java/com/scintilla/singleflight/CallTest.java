package com.scintilla.singleflight;

import org.junit.Test;

import java.util.concurrent.Phaser;

public class CallTest {

    @Test
    public void test() {
        Phaser p = new Phaser(2);

        new Thread() {
            @Override
            public void run() {
                System.out.println(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.arrive();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                System.out.println(2);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.arrive();
            }
        }.start();

        p.register();

        new Thread() {
            @Override
            public void run() {
                System.out.println(3);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.arrive();
            }
        }.start();

        p.arriveAndAwaitAdvance();
        System.out.println("done.");
    }
}
