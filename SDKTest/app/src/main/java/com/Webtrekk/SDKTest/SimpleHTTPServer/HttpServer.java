package com.Webtrekk.SDKTest.SimpleHTTPServer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created by vartbaronov on 09.05.16.
 */
public class HttpServer extends NanoHTTPD {
    private final static int PORT = 8080;
    private Context mContext;
    static private String REQUEST_COUNT_VALUE = "com.webtrekk.webtrekksdk.Test.RequestCount";
    volatile private long mDelayInReceive;
    private UrlNotifier mNotifier;


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
        mDelayInReceive = delay;
    }

    public void setNotifier(UrlNotifier notifier)
    {
        mNotifier = notifier;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String requestURL = "http://"+session.getRemoteHostName()+session.getUri()+"?"+session.getQueryParameterString();
        WebtrekkLogging.log("receive request("+getCurrentRequestNumber()+"):" + requestURL);
        mNotifier.received(requestURL);
        Response response = new Response(Response.Status.OK, "image/gif;charset=UTF-8", null, 0);
        response.closeConnection(true);

        incrementRequestNumber();
        if (mDelayInReceive > 0)
        {
            try {
                Thread.sleep(mDelayInReceive);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
}