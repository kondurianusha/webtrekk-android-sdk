package com.webtrekk.android.test;

import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.mock.MockResources;

import com.webtrekk.android.tracking.WTrack;

import wtrack.wtracksdk.R;

/**
 * Created by user on 06/03/15.
 */
public class TestWTrack extends AndroidTestCase {
    WTrack wtrack;

    public Resources getResources() {
        Resources res = new MockResources() {
            @Override
            public String getString(int id) {
                if (id == R.string.webtrekk_track_domain) {
                    return "testtrackdomain";
                }
                return "";
            }
        };
        return res;
    }


    public void testInit() {
        wtrack = WTrack.getInstance(null, null);
        assertEquals(1,1);
    }

}
