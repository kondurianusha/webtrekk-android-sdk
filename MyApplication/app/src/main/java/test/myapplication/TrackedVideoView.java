package test.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.webtrekk.android.tracking.Tracker;
import com.webtrekk.android.tracking.TrackingParams;

public class TrackedVideoView extends VideoView {
    private Tracker t;
    TrackingParams tp;

    public TrackedVideoView(Context context) {
        super(context);
        initMediaFile();
    }

    public TrackedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMediaFile();
    }

    public TrackedVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMediaFile();
    }
    public void initMediaFile() {
        tp = new TrackingParams();
        tp.add(TrackingParams.Params.MEDIA_FILE, getResources().getResourceEntryName(R.raw.marv3));
        tp.add(TrackingParams.Params.MEDIA_LENGTH, String.valueOf(getDuration()));

        tp.add(TrackingParams.Params.MEDIA_CAT, "1", "mp3");
        tp.add(TrackingParams.Params.MEDIA_CAT, "1", "example");
    }

    @Override
    public void pause() {
        super.pause();
        tp.add(TrackingParams.Params.MEDIA_ACTION, "pause");
        tp.add(TrackingParams.Params.MEDIA_POS, String.valueOf(getCurrentPosition()));
        t.track(tp);
    }

    @Override
    public void start() {
        super.start();
        tp.add(TrackingParams.Params.MEDIA_ACTION, "pause");
        tp.add(TrackingParams.Params.MEDIA_POS, String.valueOf(getCurrentPosition()));
        t.track(tp);
    }

    @Override
    public void seekTo(int msec) {
        super.seekTo(msec);
        tp.add(TrackingParams.Params.MEDIA_ACTION, "seek");
        tp.add(TrackingParams.Params.MEDIA_POS, String.valueOf(msec));
        t.track(tp);
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        tp.add(TrackingParams.Params.MEDIA_ACTION, "stop");
        t.track(tp);
    }

    public Tracker getT() {
        return t;
    }

    public void setT(Tracker t) {
        this.t = t;
    }
}
