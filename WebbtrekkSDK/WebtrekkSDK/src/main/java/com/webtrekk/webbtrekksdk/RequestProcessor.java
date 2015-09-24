package com.webtrekk.webbtrekksdk;

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

/**
 * this class sends the requests to the server
 * it handles just the networking tasks
 */
public class RequestProcessor implements Runnable{

    static final int    NETWORK_CONNECTION_TIMEOUT = 60 * 1000;  // 1 minute
    static final int    NETWORK_READ_TIMEOUT = 60 * 1000;  // 1 minute

    private final RequestUrlStore requestUrlStore;

    public RequestProcessor(RequestUrlStore requestUrlStore) {
        this.requestUrlStore = requestUrlStore;
    }


    @Override
    public void run() {
        while(true) {
            if(requestUrlStore.size() == 0) {
                // no request urls in the store, return
                break;
            }
            URL url = null;
            String urlString = null;

            try {
                urlString = requestUrlStore.get(0);
                url = new URL(urlString);
            } catch (Exception e) {
                WebtrekkLogging.log("Removing invalid URL '" + urlString + "' from queue.");

                this.requestUrlStore.remove(0);
                continue;
            }

            try {
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(NETWORK_CONNECTION_TIMEOUT);
                connection.setReadTimeout(NETWORK_READ_TIMEOUT);
                connection.setUseCaches(false);
                connection.connect();
                try {
                    int statusCode = connection.getResponseCode();
                    if (statusCode >= 200 && statusCode <= 299) {
                        WebtrekkLogging.log("completed request");
                        //successful send, remove url from store
                        this.requestUrlStore.remove(0);
                    } else {
                        WebtrekkLogging.log("received status " + statusCode);
                        // all error codes above 400 will be removed, the 300 redirects should not occur
                        // if there are redirects on serverside this has to be changed
                        WebtrekkLogging.log("removing URL from queue because status code cannot be handled.");
                        this.requestUrlStore.remove(0);

                        break;

                    }
                } catch (Exception e) {
                    WebtrekkLogging.log("unknown exception: ", e);
                }


            } catch (EOFException e) {
                WebtrekkLogging.log("sendRequestsThreadLoop: EOF > Will retry later.", e);
            } catch (SocketTimeoutException e) {
                WebtrekkLogging.log("sendRequestsThreadLoop: SocketTimeout > Will retry later.", e);
            } catch (ConnectTimeoutException e) {
                WebtrekkLogging.log("sendRequestsThreadLoop: ConnectTimeout > Will retry later.", e);
            } catch (NoHttpResponseException e) {
                WebtrekkLogging.log("sendRequestsThreadLoop: NoHttpResponse > Will retry later.", e);
            } catch (HttpHostConnectException e) {
                WebtrekkLogging.log("sendRequestsThreadLoop: HttpHostConnect > Will retry later.", e);
            } catch (UnknownHostException e) {
                WebtrekkLogging.log("sendRequestsThreadLoop: UnknownHost > Will retry later.", e);
            } catch (IOException e) {
                WebtrekkLogging.log("io exception: can not connect to host", e);
                WebtrekkLogging.log("sendRequestsThreadLoop: IO > Removing URL from queue because exception cannot be handled.", e);
            } catch (Exception e) {
                // we don't know how to resolve these - cannot retry
                WebtrekkLogging.log("sendRequestsThreadLoop: Removing URL from queue because exception cannot be handled.", e);
                // IllegalStateException by setrequestproperty in case the connectin is already established
                // NPE
            }

        }

    }
}
