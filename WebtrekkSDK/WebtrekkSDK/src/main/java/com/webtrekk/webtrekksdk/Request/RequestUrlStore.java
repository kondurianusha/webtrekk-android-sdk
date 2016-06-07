package com.webtrekk.webtrekksdk.Request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * this class acts as a local storage for the url strings before the are send
 * it gets instantiated only once by the main webtrekk class
 */

public class RequestUrlStore {
    final private File requestStoreFile;
    final private LruCache<Integer, String> mURLCash;
    //keys for current queu. Key can be point to not loaded URL
    final private SortedMap<Integer, Long> mIDs = Collections.synchronizedSortedMap(new TreeMap<Integer, Long>());
    final int mReadGroupSize = 200;
    final private Map<Integer, String> mLoaddedIDs = new HashMap<Integer, String>(mReadGroupSize);

    //Next string index
    private int mIndex;
    // current readed index in file
    private volatile long mLatestSavedURLID = -1;
    private static String URL_STORE_CURRENT_SIZE = "URL_STORE_CURRENT_SIZE";
    private static String URL_STORE_SENDED_URL_OFSSET = "URL_STORE_SENDED_URL_OFSSET";
    final private Context mContext;

    /**
     * constructs a new RequestlStore object
     *
     * @param context the application/activity context to find the cache dir
     */
    public RequestUrlStore(Context context) {
        if(context == null) {
            throw new IllegalArgumentException("no valid context");
        }

        mContext = context;

        // if the system is running low on storage, this file might be removed and the requests are lost
        requestStoreFile = new File(context.getCacheDir(), "wt-tracking-requests");
        initFileAttributes();

        final int maxSize = 20;

        mURLCash = new LruCache<Integer, String>(maxSize){
            @Override
            protected void entryRemoved(boolean evicted, Integer key, final String oldValue, String newValue) {
                if (evicted && oldValue != null)
                {
                    saveURLsToFile(new SaveURLAction(){

                        @Override
                        public void onSave(PrintWriter writer) {
                            writer.println(oldValue);
                        }
                    });

                    mLatestSavedURLID = key;
                }
            }
        };
    }

    private void initFileAttributes() {
        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(mContext);
        mIndex = pref.getInt(URL_STORE_CURRENT_SIZE, 0);
        long sentURLFileOffset = pref.getLong(URL_STORE_SENDED_URL_OFSSET, -1);

        for (int i = 0; i < mIndex; i++) {
            mIDs.put(i, -1l);
        }
        if (mIndex > 0) {
            mIDs.put(0, sentURLFileOffset);
        }
    }

    private void writeFileAttributes()
    {
        SharedPreferences.Editor prefEdit = HelperFunctions.getWebTrekkSharedPreference(mContext).edit();
        prefEdit.putLong(URL_STORE_SENDED_URL_OFSSET, mIDs.size() == 0 ? -1:mIDs.get(mIDs.firstKey()));
        prefEdit.putInt(URL_STORE_CURRENT_SIZE, mIDs.size()).apply();
    }

    private interface SaveURLAction
    {
        void onSave(PrintWriter writer);
    }

    public void reset()
    {
        // reset only if class was removed
        if (mIDs.size() == 0)
          initFileAttributes();
    }

    //Save URL to file
    private void saveURLsToFile(SaveURLAction action)
    {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(requestStoreFile, true), "UTF-8")));
            try {
                action.onSave(writer);
            }
            finally {
                writer.close();
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            WebtrekkLogging.log("can not save url", e);
        }
    }

    // flash to file all data, clear cash.
    public void flash()
    {
        WebtrekkLogging.log("Flash items to memory. Size:"+size());
        if (size() > 0) {
            saveURLsToFile(new SaveURLAction() {
                @Override
                public void onSave(PrintWriter writer) {
                    for (Integer id : mIDs.keySet()) {
                        if (id <= mLatestSavedURLID)
                            continue;
                        String url = mURLCash.get(id);
                        if (url != null) {
                            writer.println(url);
                        }
                    }
                }
            });
        }
        writeFileAttributes();
    }

    public void clearAllTrackingData()
    {
        clearLruCash();
        mIDs.clear();
        mLoaddedIDs.clear();
        mIndex = 0;
        mLatestSavedURLID = -1;
        deleteRequestsFile();
        writeFileAttributes();
    }

    // Should be called befor clear ids
    private void clearLruCash()
    {
        for (Integer id: mIDs.keySet())
        {
            mURLCash.remove(id);
        }
    }


    public String peek()
    {
        int id = mIDs.firstKey();
        String url = mURLCash.get(id);
        if (url == null) {
            url = mLoaddedIDs.get(id);
            if (url == null) {
                //not url in cash, get it from file
                if (mLoaddedIDs.size() > 0)
                    WebtrekkLogging.log("Something wrong with logic. mLoaddedIDs should be zero if url isn't found");
                if (isURLFileExists()) {
                    loadRequestsFromFile(mReadGroupSize, mIDs.get(id), id);
                    url = mLoaddedIDs.get(id);

                } else
                    WebtrekkLogging.log("NO url in cash, but file doesn't exists as well. Some issue here");
            }
        }

        if (url == null)
            WebtrekkLogging.log("Can't get URL something wrong. ID:"+id);

        return url;
    }

    private boolean isURLFileExists()
    {
        return requestStoreFile.exists();
    }



    /**
     * adds a new url string to the store, drops old ones if the maximumrequest limit is hit
     *
     * @param requestUrl string representation of a tracking request
     */
    public void addURL(String requestUrl) {
        mURLCash.put(mIndex, requestUrl);
        mIDs.put(mIndex++, -1l);
    }

    public int size()
    {
       return mIDs.size();
    }

    public void removeLastURL() {
         int key = mIDs.firstKey();
         if (mLoaddedIDs.remove(key) == null)
           mURLCash.remove(key);
         mIDs.remove(key);
    }

    /**
     * loads the requests from the cache file if present
     */
    private void loadRequestsFromFile(int numbersToLoad, long startOffset, int firstID) {
        if (!isURLFileExists()) {
            return;
        }

        int id = firstID;
        long offset = startOffset < 0 ? 0 : startOffset;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(requestStoreFile), "UTF-8"));
            reader.skip(offset);
            try {
                String line;
                int ind = 0;
                //set offset for first id
                mIDs.put(id, offset);
                while ((line = reader.readLine()) != null && ind++ < numbersToLoad && mURLCash.get(id) == null) {
                    if (mIDs.get(id) == null)
                        WebtrekkLogging.log("File is more then existed keys. Error. Key"+id+"offset:"+offset);
                    //put URL and increment id
                    mLoaddedIDs.put(id++, line);
                    offset += (line.length() + System.getProperty("line.separator").length());
                   //set offset of next id if exists
                    if (mIDs.get(id) != null && (mLatestSavedURLID >= id || mLatestSavedURLID == -1) )
                        mIDs.put(id, offset);
                }
            } finally {
                reader.close();
            }

        } catch (Exception e) {
            WebtrekkLogging.log("cannot load backup file '" + requestStoreFile.getAbsolutePath() + "'", e);
        }
    }

    /**
     * this method removes the old cache file, it should be called after the requests are loaded into the store
     */
    public void deleteRequestsFile() {
        WebtrekkLogging.log("deleting old backupfile");
        if (!isURLFileExists())
            return;

        if (size() != 0) {
            WebtrekkLogging.log("still items to send. Error delete URL request File");
            return;
        }

        boolean success = requestStoreFile.delete();
        if(success) {
            WebtrekkLogging.log("old backup file deleted");
        } else {
            WebtrekkLogging.log("error deleting old backup file");
        }

        writeFileAttributes();
    }

    /**
     * for unit testing only
     * @return
     */
    public File getRequestStoreFile() {
        return requestStoreFile;
    }
}
