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

/**
 * Created by vartbaronov on 09.05.16.
 */
public class HttpServer extends NanoHTTPD {
    private final static int PORT = 8080;
    private static final String TEST_ULR = "com.webtrekk.webtrekksdk.TEST_URL";
    private Context mContext;
    static private String REQUEST_COUNT_VALUE = "com.webtrekk.webtrekksdk.Test.RequestCount";
    volatile private long mDelayInReceive;
    volatile private boolean mStoped;

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

    @Override
    synchronized public Response serve(IHTTPSession session) {
        if (mStoped)
            return null;
        String requestURL = "http://"+session.getRemoteHostName()+session.getUri()+"?"+session.getQueryParameterString();
        WebtrekkLogging.log("receive request("+getCurrentRequestNumber()+"):" + requestURL);
        sendURLStringForTest(requestURL);
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

    @Override
    public void start() throws IOException {
        super.start();
        mStoped = false;
    }

    @Override
    synchronized public void stop() {
        super.stop();
        mStoped = true;
    }

    private void sendURLStringForTest(String url)
    {
        Intent intent = new Intent(TEST_ULR);

        intent.putExtra("URL", url);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void incrementRequest()
    {
        if (mContext == null) {
            WebtrekkLogging.log("Error increment request number");
            return;
        }
    }

    public long getCurrentRequestNumber()
    {
        if (mContext == null)
        {
            WebtrekkLogging.log("Error. Context is null. No operation with context for HTTP serer");
            return -1;
        }

        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(mContext);

        return pref.getLong(REQUEST_COUNT_VALUE, 0);
    }

    public void incrementRequestNumber()
    {
        if (mContext == null)
        {
            WebtrekkLogging.log("Error. Context is null. No operation with context for HTTP serer");
            return;
        }

        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(mContext);

        pref.edit().putLong(REQUEST_COUNT_VALUE, pref.getLong(REQUEST_COUNT_VALUE, 0)+1).apply();
    }
}