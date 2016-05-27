package com.webtrekk.webtrekksdk;

import android.content.Context;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * this class acts as a local storage for the url strings before the are send
 * it gets instantiated only once by the main webtrekk class
 */

public class RequestUrlStore extends LinkedList<String> {
    private int maximumRequests;
    private File requestStoreFile;

    /**
     * constructs a new RequesturlStore object
     *
     * @param context the application/activity context to find the cache dir
     * @param maximumRequests the maximum number of stored requests
     */
    public RequestUrlStore(Context context, int maximumRequests) {
        if(context == null) {
            throw new IllegalArgumentException("no valid context");
        }
        if(maximumRequests < 1) {
            throw new IllegalArgumentException("maximum requests must be greater than 0");
        }
        this.maximumRequests = maximumRequests;
        //requestList = new ArrayList<String>();
        // if the system is running low on storage, this file might be removed and the requests are lost
        // TODO: there are cleaner apps which delete cache files, this could be avoided when using the internal storage instead of cache
        requestStoreFile = new File(context.getCacheDir(), "wt-tracking-requests");
    }

    /**
     * adds a new url string to the store, drops old ones if the maximumrequest limit is hit
     *
     * @param requestUrl string representation of a tracking request
     */
    synchronized public void addURL(String requestUrl) {
        // if the maximumRequest number  is reached drop the oldest request
      if (size() >= maximumRequests) {
                removeLast();
            }
        addFirst(requestUrl);
    }

    synchronized public void removeLastURL() {
            removeLast();
    }

    /**
     * loads the requests from the cache file if present
     */
    public void loadRequestsFromFile() {
        if (requestStoreFile == null || !requestStoreFile.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(requestStoreFile), "UTF-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    addURL(line);
                }
            } finally {
                reader.close();
            }

        } catch (Exception e) {
            WebtrekkLogging.log("cannot load backup file '" + requestStoreFile.getAbsolutePath() + "'", e);
        }
    }

    /**
     * saves the requests from the store to the request file if they could not be send
     * in case all requests are send, the old cache file gets deleted
     */
    public void saveRequestsToFile() {
        if (requestStoreFile == null) {
            // no valid filehandle
            return;
        }

        // save remaining requests to the backup file
        WebtrekkLogging.log("saveBackup: Saving backup of " + size() + " URLs.");

        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(requestStoreFile, true), "UTF-8")));
            try {
                for (String url : this) {
                    writer.println(url);
                }
            }
            finally {
                writer.close();
            }
            clear();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            WebtrekkLogging.log("can not save backupfile", e);
        }

    }

    /**
     * this method removes the old cache file, it should be called after the requests are loaded into the store
     */
    public void deleteRequestsFile() {
        WebtrekkLogging.log("deleting old backupfile");
        if (requestStoreFile == null || !requestStoreFile.exists()) {
            return;
        }
        boolean success = requestStoreFile.delete();
        if(success) {
            WebtrekkLogging.log("old backup file deleted");
        } else {
            WebtrekkLogging.log("error deleting old backup file");
        }

    }

    /**
     * for unit testing only
     * @return
     */
    File getRequestStoreFile() {
        return requestStoreFile;
    }
}
