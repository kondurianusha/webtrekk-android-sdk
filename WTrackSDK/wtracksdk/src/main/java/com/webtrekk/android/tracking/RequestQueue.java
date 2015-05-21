package com.webtrekk.android.tracking;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Created by user on 22/04/15.
 */
public class RequestQueue {

    private static final int    MAXIMUM_URL_COUNT          = 1000;
    private static final int    NETWORK_CONNECTION_TIMEOUT = 60 * 1000;  // 1 minute
    private static final String QUEUE_FILE_NAME            = "webtrekk-queue";

    private File                    backupFile;
    private Context                 context;
    private final long              initialSendDelay;
    private long                    sendDelay;
    private Thread                  sendRequestsThread;
    private int                     successfullSends;
    private final List<String>      urls;
    private boolean isLoggingEnabled;
    private HttpURLConnection urlConnection;




    public RequestQueue(long initialSendDelay, long sendDelay) {

        this.initialSendDelay = initialSendDelay;
        this.sendDelay        = sendDelay;
        this.urls             = new ArrayList<String>();
    }



    public synchronized void addUrl(String url) {
        if (this.urls.size() >= MAXIMUM_URL_COUNT) {
            this.urls.remove(0);
        }
        L.log("adding url: " + url);

        this.urls.add(url);

        this.sendRequests(false);
    }


    public synchronized void clear() {
        this.urls.clear();
        this.saveBackup();
    }


    @Override
    public void finalize() {
        if(this.urlConnection != null) {
            this.urlConnection.disconnect();
        }
    }


    public long getSendDelay() {
        return this.sendDelay;
    }


    private void loadBackup() {
        if (this.backupFile == null) {
            return;
        }

        try {
            if (this.backupFile.exists()) {
                List<String> urls = new ArrayList<String>();

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.backupFile), "UTF-8"), 2048);
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        urls.add(line);
                    }
                } finally {
                    reader.close();
                }

                this.urls.addAll(0, urls);
            }
        } catch (Exception e) {
            L.log("loadBackup: Cannot load backup file '" + this.backupFile.getAbsolutePath() + "'", e);
        }
    }


    public synchronized void saveBackup() {
        if (this.backupFile == null) {
            return;
        }

        if (!this.urls.isEmpty()) {
            L.log("saveBackup: Saving backup of " + this.urls.size() + " URLs.");

            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.backupFile), "UTF-8"), 2048));
                try {
                    for (String url : this.urls) {
                        writer.println(url);
                    }
                }
                finally {
                    writer.close();
                }
            }
            catch (FileNotFoundException e) {
                L.log("saveBackup: Cannot save backup of URLs.", e);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            L.log("saveBackup: Deleting backup - queue is clear.");

            if (this.backupFile.exists()) {
                this.backupFile.delete();
            }
        }
    }


    private synchronized void sendRequests(final boolean immediately) {
        if (this.context == null) {
            return;
        }
        if (this.sendRequestsThread != null) {
            return;
        }

        if (this.urls.isEmpty()) {
            return;
        }
        this.sendRequestsThread = new Thread() {
            @Override
            public void run() {
                RequestQueue.this.sendRequestsThreadStart(immediately);
            }
        };
        this.sendRequestsThread.start();
    }


    private void sendRequestsThreadLoop() {
        // this method must run in a separate worker thread!

        for (;;) {
            URL httpGet;
            String url;

            synchronized (this) {
                if (this.urls.isEmpty()) {
                    L.log("sendRequestsThreadLoop: Nothing to do.");

                    this.sendRequestsThread = null;
                    return;
                }

                url = this.urls.get(0);

                try {
                    httpGet = new URL(url);
                }
                catch (MalformedURLException e) {
                    L.log("sendRequestsThreadLoop: Removing invalid URL '" + url + "' from queue.");

                    this.urls.remove(0);
                    continue;
                }
                catch (Exception e) {
                    L.log("sendRequestsThreadLoop: Removing URL from queue because exception cannot be handled.", e);

                    this.urls.remove(0);
                    continue;
                }
            }

            boolean success = false;
            boolean retry = false;

            L.log("sendRequestsThreadLoop: Opening connection to '" + url + "'.");

            try {
                this.urlConnection = (HttpURLConnection) httpGet.openConnection();
                this.urlConnection.setRequestMethod("GET");
                this.urlConnection.setConnectTimeout(NETWORK_CONNECTION_TIMEOUT);
                this.urlConnection.setReadTimeout(NETWORK_CONNECTION_TIMEOUT);
                this.urlConnection.setRequestProperty("User-Agent", WTrack.getUserAgent());
                this.urlConnection.connect();
                try {
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode >= 200 && statusCode <= 299) {
                        L.log("sendRequestsThreadLoop: Completed request to '" + url + "'.");

                        success = true;
                    }
                    else {
                        L.log("sendRequestsThreadLoop: Received status " + statusCode + " for '" + url + "'.");

                        if (statusCode <= 499 || statusCode >= 600) {
                            // client-side error. we cannot handle that.
                            L.log("sendRequestsThreadLoop: Removing URL from queue because status code cannot be handled.");
                        }
                        else {
                            // server-side error. we can wait.
                            retry = true;
                        }
                    }
                }
                catch (Exception e) {
                    L.log("unknown exception: " , e);
                }
            }
            catch (EOFException e) {
                L.log("sendRequestsThreadLoop: EOF > Will retry later.", e);
                retry = true;
            }
            catch (SocketTimeoutException e) {
                L.log("sendRequestsThreadLoop: SocketTimeout > Will retry later.", e);
                retry = true;
            }
            catch (ConnectTimeoutException e) {
                L.log("sendRequestsThreadLoop: ConnectTimeout > Will retry later.", e);
                retry = true;
            }
            catch (NoHttpResponseException e) {
                L.log("sendRequestsThreadLoop: NoHttpResponse > Will retry later.", e);
                retry = true;
            }
            catch (HttpHostConnectException e) {
                L.log("sendRequestsThreadLoop: HttpHostConnect > Will retry later.", e);
                retry = true;
            }
            catch (UnknownHostException e) {
                L.log("sendRequestsThreadLoop: UnknownHost > Will retry later.", e);
                retry = true;
            }
            catch (InterruptedIOException e) {
                // looks like that we are shutting down...

                this.sendRequestsThread = null;
                return;
            }
            catch(IOException e) {
                L.log("io exception: can not connect to host", e);
                L.log("sendRequestsThreadLoop: IO > Removing URL from queue because exception cannot be handled.", e);
            }
            catch (Exception e) {
                // we don't know how to resolve these - cannot retry
                L.log("sendRequestsThreadLoop: Removing URL from queue because exception cannot be handled.", e);
                // IllegalStateException by setrequestproperty in case the connectin is already established
                // NPE
            }

            synchronized (this) {
                if (success) {
                    this.urls.remove(url);
                    ++this.successfullSends;
                }
                else {
                    if (retry) {
                        // move URL to end to prevent failing with the same URL
                        // over and over again
                        this.urls.remove(url);
                        this.urls.add(url);

                        this.sendRequestsThread = null;

                        this.sendRequests(false);
                        return;
                    }
                    else {
                        this.urls.remove(url);
                    }
                }

                if (this.urls.isEmpty()) {
                    L.log("sendRequestsThreadLoop: All done!");

                    this.saveBackup();

                    this.sendRequestsThread = null;
                    return;
                }
            }
        }
    }


    protected void sendRequestsThreadStart(boolean immediately) {
        if (!immediately) {
            try {
                long sendDelay = this.successfullSends > 0 ? this.sendDelay : this.initialSendDelay;

                L.log("sendRequests: Will process next URL in " + sendDelay + " ms.");
                Thread.sleep(sendDelay);
            }
            catch (InterruptedException e) {
                return;
            }

            this.saveBackup();
        }

        this.sendRequestsThreadLoop();
    }


    public synchronized void setContext(Context context) {
        boolean hadNoContext = this.context == null;

        this.context = context;

        if (context != null) {
            if (this.backupFile == null) {
                this.backupFile = new File(context.getCacheDir(), QUEUE_FILE_NAME);
                this.loadBackup();

                if (!this.urls.isEmpty()) {
                    L.log("setContext: Sending " + this.urls.size() + " backupped requests now.");

                    this.sendRequests(true);
                }
            }

            if (hadNoContext) {
                this.sendRequests(false);
            }
        }
    }


    public void setSendDelay(long sendDelay) {
        this.sendDelay = sendDelay;
    }

}
