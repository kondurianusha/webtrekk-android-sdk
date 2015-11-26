package test.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Webtrekk;

public class TrackedVideoView extends VideoView {
    private Webtrekk webtrekk;
    TrackingParameter tp;

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
        tp = new TrackingParameter();
        tp.add(TrackingParameter.Parameter.MEDIA_FILE, getResources().getResourceEntryName(R.raw.wt));
        tp.add(TrackingParameter.Parameter.MEDIA_LENGTH, String.valueOf(getDuration()));
        tp.add(TrackingParameter.Parameter.MEDIA_POS, String.valueOf(getCurrentPosition()/1000));
        tp.add(TrackingParameter.Parameter.MEDIA_CAT, "1", "mp4");
        tp.add(TrackingParameter.Parameter.MEDIA_CAT, "1", "example");
    }

    @Override
    public void pause() {
        super.pause();
        tp.add(TrackingParameter.Parameter.MEDIA_ACTION, "pause");
        tp.add(TrackingParameter.Parameter.MEDIA_POS, String.valueOf(getCurrentPosition()/1000));
        webtrekk.track(tp);
    }

    @Override
    public void start() {
        super.start();
        tp.add(TrackingParameter.Parameter.MEDIA_ACTION, "start");
        tp.add(TrackingParameter.Parameter.MEDIA_POS, String.valueOf(getCurrentPosition()/1000));
        webtrekk.track(tp);
    }

    @Override
    public void seekTo(int msec) {
        super.seekTo(msec);
        tp.add(TrackingParameter.Parameter.MEDIA_ACTION, "seek");
        int sec = msec/1000;
        tp.add(TrackingParameter.Parameter.MEDIA_POS, String.valueOf(sec));
        webtrekk.track(tp);
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        tp.add(TrackingParameter.Parameter.MEDIA_POS, String.valueOf(getCurrentPosition()/1000));
        tp.add(TrackingParameter.Parameter.MEDIA_ACTION, "stop");
        webtrekk.track(tp);
    }

    public Webtrekk getWebtrekk() {
        return webtrekk;
    }

    public void setWebtrekk(Webtrekk webtrekk) {
        this.webtrekk = webtrekk;
    }
}
