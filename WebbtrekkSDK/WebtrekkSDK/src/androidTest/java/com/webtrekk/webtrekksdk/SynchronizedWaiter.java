package com.webtrekk.webtrekksdk;

/**
 * Created by user on 02/11/15.
 */
public class SynchronizedWaiter {
    public void doWait(long l){
        synchronized(this){
            try {
                this.wait(l);
            } catch(InterruptedException e) {
            }
        }
    }

    public void doNotify() {
        synchronized(this) {
            this.notify();
        }
    }

    public void doWait() {
        synchronized(this){
            try {
                this.wait();
            } catch(InterruptedException e) {
            }
        }
    }
}
