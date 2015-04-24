package com.webtrekk.android.trackingplugin;

import android.util.Log;
import android.widget.ListView;

import java.util.HashMap;

import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.TrackingRequest;
import com.webtrekk.android.tracking.WTrack;

/**
 * Created by user on 14/04/15.
 *
 * this example plugin shows how to append an URL parameter in case a special situation is given
 * to initialize it, you need to add the ListView plugin param to your trackingparams
 * if successfull scrolled to the end of the listview, it appends the pageparam number 5 with end as value
 */

public class ExampleScrollBottomPlugin extends Plugin {
    public ExampleScrollBottomPlugin(WTrack wtrack) {
        super(wtrack);
    }


    @Override
    public void before_request(TrackingRequest request) {
        // make shure the neccesary plugin params are passed
        ListView lv = (ListView)request.getParams().getPlugin_params().get("ListView");
        if(lv == null) {
            Log.d(WTrack.LOGTAG, "missing Pluginparameter: ListView");
            return;
        }
        if(lv.getLastVisiblePosition() == lv.getAdapter().getCount() -1 && lv.getChildAt(lv.getChildCount() - 1).getBottom() <= lv.getHeight()) {
            // listview is scrolled to the end, so append an example url param in the tracking request
            // set up the page parameter number 5 to map the position in the listview, basicly this can be anything
            request.getParams().add(TrackingParams.Params.PAGE, "5", "end");
        }


    }

    @Override
    public void after_request(TrackingRequest request) {

    }

}
