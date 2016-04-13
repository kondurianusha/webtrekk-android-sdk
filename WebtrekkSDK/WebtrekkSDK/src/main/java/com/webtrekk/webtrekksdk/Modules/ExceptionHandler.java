package com.webtrekk.webtrekksdk.Modules;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

/**
 * Created by vartbaronov on 08.04.16.
 * Class is required to handle exception
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler
{
    Thread.UncaughtExceptionHandler mOldHandler;
    public static int MAX_PARAMETER_NUMBER = 255;

    public void init()
    {
        mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        track(ex);
        //save(ex);

       if (mOldHandler != null)
           mOldHandler.uncaughtException(thread, ex);
    }

    void track(Throwable ex)
    {
        WebtrekkLogging.log("WTErrorlog ToString:" + ex.toString());
        WebtrekkLogging.log("WTErrorlog getLocalizedMessage:"+ex.getLocalizedMessage());
        WebtrekkLogging.log("WTErrorlog getMessage:" + ex.getMessage());
        StackTraceElement stackElement = ex.getStackTrace()[0];
        WebtrekkLogging.log("1 Stack line. File name:" + stackElement.getFileName() + " get Class:" + stackElement.getClassName() + " get Line:" + stackElement.getLineNumber() +
                " Method:" + stackElement.getMethodName());
        WebtrekkLogging.log("WTErrorlog print all stack................................");
        ex.printStackTrace();

        WebtrekkLogging.log("WTErrorlog getCause toString:"+ex.getCause().toString());
        WebtrekkLogging.log("WTErrorlog getCause getMessage:"+ex.getCause().getMessage());
        WebtrekkLogging.log("WTErrorlog getCause getLocalizedMessage:" + ex.getCause().getLocalizedMessage());
        WebtrekkLogging.log("WTErrorlog print all cause stack................................");
        ex.getCause().printStackTrace();
    }

    void track(String type, String name, String message, String stack, String causeStack)
    {

    }

    void loadAndTrack()
    {

    }

    void save(String exception)
    {

    }

    String getEcxeptionStackString(StackTraceElement[] stack)
    {
        return "";
    }

}
