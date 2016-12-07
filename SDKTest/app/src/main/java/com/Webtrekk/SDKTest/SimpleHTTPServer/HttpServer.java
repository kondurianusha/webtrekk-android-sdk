package com.Webtrekk.SDKTest.SimpleHTTPServer;

import android.content.Context;
import android.content.SharedPreferences;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

import java.io.IOException;

/**
 * Created by vartbaronov on 09.05.16.
 */
public class HttpServer extends NanoHTTPD {
    private final static int PORT = 8080;
    private Context mContext;
    static private String REQUEST_COUNT_VALUE = "com.webtrekk.webtrekksdk.Test.RequestCount";
    volatile private long mDelayAfterReceive;
    volatile private long mDelayBeforeReceive;
    private UrlNotifier mNotifier;
    final private Object mDelayMonitor = new Object();
    private boolean mIsDelay;


    public interface UrlNotifier{
        void received (String url);
    }

    public HttpServer() throws IOException {
        super(PORT);
    }

    public void setContext(Context context)
    {
        mContext = context;
    }

    public void setDelay(int delay)
    {
        mDelayAfterReceive = delay;
    }

    public void setBeforeDelay(int delay)
    {
        mDelayBeforeReceive = delay;
    }

    public void setNotifier(UrlNotifier notifier)
    {
        mNotifier = notifier;
    }

    @Override
    public Response serve(IHTTPSession session) {

        try {
            if (mDelayBeforeReceive > 0)
            {
                mIsDelay = true;
                synchronized (mDelayMonitor) {
                    long currentStartTime = System.currentTimeMillis();
                    while (mIsDelay && (currentStartTime + mDelayBeforeReceive) > System.currentTimeMillis()) {
                        mDelayMonitor.wait(mDelayBeforeReceive);
                    }
                }
            }
        } catch (InterruptedException e) {
        }

        String requestURL = "http://"+session.getRemoteHostName()+session.getUri()+"?"+session.getQueryParameterString();
        WebtrekkLogging.log("receive request("+getCurrentRequestNumber()+"):" + requestURL);
        mNotifier.received(requestURL);
        Response response = new Response(Response.Status.OK, "image/gif;charset=UTF-8", null, 0);
        response.closeConnection(true);

        incrementRequestNumber();

        try {
            if (mDelayAfterReceive > 0)
            {
                Thread.sleep(mDelayAfterReceive);
            }
        } catch (InterruptedException e) {
        }

        return response;
    }

    synchronized public long getCurrentRequestNumber()
    {
        if (mContext == null)
        {
            WebtrekkLogging.log("Error. Context is null. No operation with context for HTTP serer");
            return -1;
        }

        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(mContext);

        return pref.getLong(REQUEST_COUNT_VALUE, 0);
    }

    synchronized public void incrementRequestNumber()
    {
        if (mContext == null)
        {
            WebtrekkLogging.log("Error. Context is null. No operation with context for HTTP serer");
            return;
        }

        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(mContext);

        pref.edit().putLong(REQUEST_COUNT_VALUE, pref.getLong(REQUEST_COUNT_VALUE, 0)+1).apply();
    }

    public void resetRequestNumber()
    {
        if (mContext == null)
        {
            WebtrekkLogging.log("Error. Context is null. No operation with context for HTTP serer");
            return;
        }

        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(mContext);

        pref.edit().putLong(REQUEST_COUNT_VALUE, 0).apply();
    }

    public void stopBeforeDelay(){

        synchronized (mDelayMonitor) {
            mIsDelay = false;
            mDelayMonitor.notifyAll();
        }
    }
}