package com.webtrekk.webtrekksdk;

import android.test.AndroidTestCase;


public class RequestUrlStoreTests extends AndroidTestCase {

    private RequestUrlStore requestUrlStore;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRequestUrlStoreConstructor() {
        try {
            requestUrlStore = new RequestUrlStore(null, 5000);
            fail("null context, IllegalArgumentException");
        } catch (IllegalArgumentException e){  }

        try {
            requestUrlStore = new RequestUrlStore(getContext(), 0);
            fail("maximum requests must be greater than 0");
        } catch (IllegalArgumentException e){  }

        requestUrlStore = new RequestUrlStore(getContext(), 100);

        assertNotNull(requestUrlStore.getRequestList());
        assertNotNull(requestUrlStore.getRequestStoreFile());

    }

    public void testAdd() {
        requestUrlStore = new RequestUrlStore(getContext(), 100);
        for(int i = 1; i<101; i++) {
            requestUrlStore.add("url-" + i);
        }
        assertEquals(100, requestUrlStore.size());
        // make sure the oldest requests gets dropped when more than maxrequest urls are added
        requestUrlStore.add("url-101");
        // the first is now url-2, url-1 got dropped
        assertEquals("url-2", requestUrlStore.get(0));

    }


    public void testSaveAndLoadRequestsToFile() {
        requestUrlStore = new RequestUrlStore(getContext(), 100);
        for(int i = 1; i<101; i++) {
            //test all valid url characters here to make sure there are no encoding problems
            requestUrlStore.add("url-ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()*+,;=-" + i);
        }

        requestUrlStore.saveRequestsToFile();
        requestUrlStore.clear();
        requestUrlStore.loadRequestsFromFile();
        assertEquals(100, requestUrlStore.size());
        assertEquals("url-ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()*+,;=-1", requestUrlStore.get(0));


    }

    public void testDeleteRequestsFile() {
        requestUrlStore = new RequestUrlStore(getContext(), 100);
        for(int i = 1; i<101; i++) {
            //test all valid url characters here to make sure there are no encoding problems
            requestUrlStore.add("url-ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()*+,;=-" + i);
        }

        requestUrlStore.saveRequestsToFile();
        requestUrlStore.deleteRequestsFile();
        assertFalse(requestUrlStore.getRequestStoreFile().exists());
    }
}
