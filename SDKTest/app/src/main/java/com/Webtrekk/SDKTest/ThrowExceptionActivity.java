package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.os.Bundle;

import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.File;

/**
 * Created by vartbaronov on 28.04.16.
 */
public class ThrowExceptionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Webtrekk wt = Webtrekk.getInstance();

        Bundle extra = getIntent().getExtras();

        if (extra != null && extra.getBoolean("NULL")) {
            wt.initWebtrekk(getApplication());
            String s = null;

            s.length();
        }else
        {
            //rename file with exception if exists
            final String fileName = "exception.txt";
            final String altFileName = "exception1.txt";
            final String path = getFilesDir().getPath() + File.separator;

            File file = new File(path + fileName);
            final boolean isExists = file.exists();
            File altFile = new File(path + altFileName);

            if (isExists)
               file.renameTo(altFile);
            //init webtrekk
            wt.initWebtrekk(getApplication());

            //rename file with exception back
            if (isExists)
               altFile.renameTo(file);

            //crash applicaiton
            Integer.valueOf("sdfsdf");
        }

        super.onCreate(savedInstanceState);
    }
}
