package wtrack.tracking;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.Map;

/**
 * this class contains the tracking event and the trackingparams, and handles the creation of an url string
 * which is send as get request to the configured track domain of the customer
 */
public class TrackingRequestUrl extends TrackingRequest{
    private Tracker.Events event;
    private TrackingParams params;

    public TrackingRequestUrl(Tracker.Events e, TrackingParams tp, WTrack wtrack) {
        super(wtrack);
        this.event = e;
        this.params = tp;

    }

    public String createUrlString() {
        Uri.Builder builder = new Uri.Builder();
        // maybe add https here if that ssl option is set in the configs
        builder.scheme("http")
                .authority(webtrekk_track_domain)
                .appendPath(webtrekk_track_id)
                .appendPath("wt");
        // the tracked event type
        builder.appendQueryParameter("event", event.toString());
        // iterate through all the collected tracking params and append them as url parameter
        for (Map.Entry<TrackingParams.Params, String> entry : params.getTparams().entrySet()) {
            builder.appendQueryParameter(entry.getKey().toString(), entry.getValue());
        }
        urlstring = builder.toString();
        return urlstring;
    }

    @Override
    public Request getRequest() {
        return new StringRequest(Request.Method.GET, urlstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // it worked, but nothing to do here for now
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // simply log error for now, maybe add retry handling later or writing to disk
                Log.e(WTrack.LOGTAG, error.getMessage());
            }
        });
    }

    public String getUrlstring() {
        return createUrlString();
    }

    public void setUrlstring(String urlstring) {
        this.urlstring = urlstring;
    }
}
