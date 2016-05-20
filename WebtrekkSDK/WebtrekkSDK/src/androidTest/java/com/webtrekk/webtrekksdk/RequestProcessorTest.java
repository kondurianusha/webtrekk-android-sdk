package com.webtrekk.webtrekksdk;

import android.test.AndroidTestCase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.mockito.Mockito.*;


public class RequestProcessorTest extends AndroidTestCase {
    private RequestUrlStore requestUrlStore;
    private RequestProcessor requestProcessor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());
        requestUrlStore = mock(RequestUrlStore.class);
        requestProcessor = new RequestProcessor(requestUrlStore);

    }

    public void testEmptyStore() {
        RequestUrlStore requestUrlStore = mock(RequestUrlStore.class);
        when(requestUrlStore.size()).thenReturn(0);
        requestProcessor = spy(requestProcessor);
        when(requestUrlStore.size()).thenReturn(0);
        requestProcessor.run();
        // make sure that the request processor returns without further actions
        verify(requestProcessor, times(0)).getUrl(anyString());
        verify(requestProcessor, times(0)).sendRequest((URL)any(), (RequestProcessor.ProcessOutputCallback)isNull());
        //verifyNoMoreInteractions(requestUrlStore);
    }

    public void testSingleValidUrlInStore() throws IOException {
        requestProcessor = spy(requestProcessor);
        when(requestUrlStore.size()).thenReturn(1).thenReturn(0);
        // test valid url first
        when(requestUrlStore.peekLast()).thenReturn("http://nglab.org");
        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(200);
        doReturn(mockHttpURLConnection).when(requestProcessor).getUrlConnection((URL) any());
        requestProcessor.run();
        // two times, first one returns the urlstring, second one is empty
        verify(requestUrlStore, times(1)).peekLast();
        verify(mockHttpURLConnection, times(1)).connect();
        verify(mockHttpURLConnection, times(1)).getResponseCode();
        verify(requestUrlStore, times(1)).removeLastURL();
        verify(mockHttpURLConnection, times(1)).disconnect();

    }

    public void testMoreValidUrlInStore() throws IOException {
        requestProcessor = spy(requestProcessor);
        when(requestUrlStore.size()).thenReturn(2).thenReturn(1).thenReturn(0);
        // test valid url first
        when(requestUrlStore.peekLast()).thenReturn("http://nglab.org").thenReturn("http://nglab.org");
        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(200).thenReturn(200);
        doReturn(mockHttpURLConnection).when(requestProcessor).getUrlConnection((URL) any());
        requestProcessor.run();
        // two times, first one returns the urlstring, second one is empty
        verify(requestUrlStore, times(2)).peekLast();
        verify(mockHttpURLConnection, times(2)).connect();
        verify(mockHttpURLConnection, times(2)).getResponseCode();
        verify(requestUrlStore, times(2)).removeLastURL();
        verify(mockHttpURLConnection, times(2)).disconnect();
    }

    public void testInvalidUrl() throws IOException {
        requestProcessor = spy(requestProcessor);
        when(requestUrlStore.size()).thenReturn(1).thenReturn(0);
        // test valid url first
        when(requestUrlStore.peekLast()).thenReturn("invalidurl");
        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(200);
        doReturn(mockHttpURLConnection).when(requestProcessor).getUrlConnection((URL) any());
        requestProcessor.run();
        // two times, first one returns the urlstring, second one is empty
        verify(requestUrlStore, times(1)).peekLast();
        verify(mockHttpURLConnection, times(0)).connect();
        verify(mockHttpURLConnection, times(0)).getResponseCode();
        verify(requestUrlStore, times(1)).removeLastURL();
        verify(mockHttpURLConnection, times(0)).disconnect();
    }

    public void testClientSideError() throws IOException {
        requestProcessor = spy(requestProcessor);
        when(requestUrlStore.size()).thenReturn(1).thenReturn(0);
        // test valid url first
        when(requestUrlStore.peekLast()).thenReturn("http://nglab.org");
        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(0);
        doReturn(mockHttpURLConnection).when(requestProcessor).getUrlConnection((URL)any());
        requestProcessor.run();
        // two times, first one returns the urlstring, second one is empty
        verify(requestUrlStore, times(1)).peekLast();
        verify(mockHttpURLConnection, times(1)).connect();
        verify(mockHttpURLConnection, times(1)).getResponseCode();
        verify(requestUrlStore, times(0)).removeLastURL();
        verify(mockHttpURLConnection, times(1)).disconnect();
    }

    public void testServerSideError() throws IOException {
        requestProcessor = spy(requestProcessor);
        when(requestUrlStore.size()).thenReturn(1).thenReturn(0);
        // test valid url first
        when(requestUrlStore.peekLast()).thenReturn("http://nglab.org");
        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(-1);
        doReturn(mockHttpURLConnection).when(requestProcessor).getUrlConnection((URL) any());
        requestProcessor.run();
        // two times, first one returns the urlstring, second one is empty
        verify(requestUrlStore, times(1)).peekLast();
        verify(mockHttpURLConnection, times(1)).connect();
        verify(mockHttpURLConnection, times(1)).getResponseCode();
        verify(requestUrlStore, times(1)).removeLastURL();
        verify(mockHttpURLConnection, times(1)).disconnect();
    }

}
