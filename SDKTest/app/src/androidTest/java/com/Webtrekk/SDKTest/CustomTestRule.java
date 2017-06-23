package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

/**
 * Created by vartbaronov on 23.06.17.
 */

public class CustomTestRule<T extends Activity> extends ActivityTestRule<T> {
    public interface TestAdapter{
        public void before() throws Exception;
    }

    private final TestAdapter mTestAdapter;

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        try {
            mTestAdapter.before();
        } catch (Exception e) {
            WebtrekkLogging.log("Exception during test:" + e.toString());
        }
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        BaseWebtrekkTest.finishActivitySync(getActivity(), false);
    }


    public CustomTestRule(Class<T> activityClass, TestAdapter adapter) {
        super(activityClass);
        mTestAdapter = adapter;
    }
}
