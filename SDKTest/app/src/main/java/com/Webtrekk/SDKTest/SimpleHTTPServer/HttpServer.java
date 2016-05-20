package com.Webtrekk.SDKTest.SimpleHTTPServer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
    static private String ACTIVITIES_COUNT_VALUE = "com.webtrekk.webtrekksdk.Test.RequestCount";
    volatile private long mDelayInReceive;

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
    public Response serve(IHTTPSession session) {
        String requestURL = "http://"+session.getRemoteHostName()+session.getUri()+"?"+session.getQueryParameterString();
        WebtrekkLogging.log("receive request:" + requestURL);
        sendURLStringForTest(requestURL);
        Response response = new Response(Response.Status.OK, "image/gif;charset=UTF-8", null, 0);
        response.closeConnection(true);

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

}