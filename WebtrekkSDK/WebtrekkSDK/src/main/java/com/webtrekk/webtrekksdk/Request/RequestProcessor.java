package com.webtrekk.webtrekksdk.Request;

import com.webtrekk.webtrekksdk.Request.RequestUrlStore;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * this class sends the requests to the server
 * it handles just the networking tasks
 * @hide
 */
public class RequestProcessor implements Runnable {

    public static final int NETWORK_CONNECTION_TIMEOUT = 60 * 1000;  // 1 minute
    static final int NETWORK_READ_TIMEOUT = 60 * 1000;  // 1 minute

    private final RequestUrlStore mRequestUrlStore;

    public interface ProcessOutputCallback
    {
        public void process(int statusCode, HttpURLConnection connection);
    }

    public RequestProcessor(RequestUrlStore requestUrlStore) {
        mRequestUrlStore = requestUrlStore;
    }

    /**
     * gets the URL for a string, returns null for invalid urls
     *
     * @param urlString
     * @return
     */
    public URL getUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * returns the http url connection for the given url
     *
     * @param url
     * @return
     * @throws IOException
     */

    public HttpURLConnection getUrlConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * sends the request to the server and returns the status code
     *
     * @param url
     * @return statusCode, 0 for retry, -1 for remove, 200 for success
     */
    public int sendRequest(URL url, ProcessOutputCallback processOutput) {
        HttpURLConnection connection = null;
        try {
            connection = getUrlConnection(url);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(NETWORK_CONNECTION_TIMEOUT);
            connection.setReadTimeout(NETWORK_READ_TIMEOUT);
            connection.setUseCaches(false);
            connection.connect();
            int statusCode = connection.getResponseCode();

            if (processOutput != null)
                processOutput.process(statusCode, connection);

            return statusCode;

        } catch (EOFException e) {
            WebtrekkLogging.log("RequestProcessor: EOF > Will retry later.", e);
            return 0;
        } catch (SocketTimeoutException e) {
            WebtrekkLogging.log("RequestProcessor: SocketTimeout > Will retry later.", e);
            return 0;
        } catch (SocketException e) {
            WebtrekkLogging.log("RequestProcessor: Socket Exception.", e);
            return 0;
        }  catch (UnknownHostException e) {
            WebtrekkLogging.log("RequestProcessor: UnknownHost > Will retry later.", e);
            return 0;
        } catch (IOException e) {
            WebtrekkLogging.log("io exception: can not connect to host", e);
            WebtrekkLogging.log("RequestProcessor: IO > Removing URL from queue because exception cannot be handled.", e);
        } catch (Exception e) {
            // we don't know how to resolve these - cannot retry
            WebtrekkLogging.log("RequestProcessor: Removing URL from queue because exception cannot be handled.", e);
            // IllegalStateException by setrequestproperty in case the connectin is already established
            // NPE
        } finally {
            if (connection != null && connection instanceof HttpURLConnection) {
                connection.disconnect();
            }
        }
        return -1;
    }


    @Override
    public void run() {
        synchronized (mRequestUrlStore) {
            while (mRequestUrlStore.size() > 0) {

                Thread.yield();
                if (Thread.interrupted())
                    break;

                String urlString = mRequestUrlStore.peekLast();
                URL url = getUrl(urlString);
                if (url == null) {
                    WebtrekkLogging.log("Removing invalid URL '" + urlString + "' from queue. remaining: " + mRequestUrlStore.size());
                    mRequestUrlStore.removeLastURL();
                    continue;
                }

                int statusCode = sendRequest(url, null);
                if (statusCode >= 200 && statusCode <= 299) {
                    WebtrekkLogging.log("completed request. Status code:" + statusCode);
                    //successful send, remove url from store
                    mRequestUrlStore.removeLastURL();
                } else if (statusCode == 0) {
                    // client side networking errors, just break and try again with next onSendintervalOver
                    break;
                } else {
                    WebtrekkLogging.log("received status " + statusCode);
                    // all error codes above 400 will be removed, the 300 redirects should not occur
                    // if there are redirects on serverside this has to be changed
                    WebtrekkLogging.log("removing URL from queue because status code cannot be handled: ");
                    mRequestUrlStore.removeLastURL();
                }
            }
        }
        WebtrekkLogging.log("Processing URL task is finished");
    }
}
