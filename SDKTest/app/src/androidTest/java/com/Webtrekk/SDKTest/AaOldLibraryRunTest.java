package com.Webtrekk.SDKTest;

import android.app.Application;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;

/**
 * Created by vartbaronov on 25.05.16.
 */
public class AaOldLibraryRunTest extends ActivityInstrumentationTestCase2<OldWebtrekkActivity> {
    public AaOldLibraryRunTest(){
        super(OldWebtrekkActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //clear all settings.
        SharedPreferences pref = HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext());
        pref.edit().clear().apply();

        Application app = getInstrumentation().newApplication(OldWebtrekkApplication.class, getInstrumentation().getTargetContext());
        getInstrumentation().callApplicationOnCreate(app);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    //test OldSDK for testing new SDK installation
    public void testOldSDK()
    {
        getActivity();
        ActivityInstrumentationTestCase2BaseMain.finishActivitySync(getActivity(), getInstrumentation(), true);
    }

}
