package wtrack.tracking;

import com.android.volley.Request;

/**
 * this is the abstract base class for tracking requests,
 *
 */
public abstract class TrackingRequest {
    protected String type;
    protected String webtrekk_track_domain;
    protected String webtrekk_track_id;
    protected int sampling;
    protected Request request;
    protected String urlstring;

    protected TrackingRequest(WTrack wtrack) {
        if(wtrack != null) {
            this.webtrekk_track_domain = wtrack.getWebtrekk_track_domain();
            this.webtrekk_track_id = wtrack.getWebtrekk_track_id();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebtrekk_track_domain() {
        return webtrekk_track_domain;
    }

    public void setWebtrekk_track_domain(String webtrekk_track_domain) {
        this.webtrekk_track_domain = webtrekk_track_domain;
    }

    public String getWebtrekk_track_id() {
        return webtrekk_track_id;
    }

    public void setWebtrekk_track_id(String webtrekk_track_id) {
        this.webtrekk_track_id = webtrekk_track_id;
    }

    public int getSampling() {
        return sampling;
    }

    public void setSampling(int sampling) {
        this.sampling = sampling;
    }

    public Request getRequest() {
        return request;
    }

    public String getURLString() {
        return urlstring;
    }
}
