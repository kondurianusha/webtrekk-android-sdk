package test.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.webtrekk.android.tracking.MediaCategories;
import com.webtrekk.android.tracking.MediaSession;
import com.webtrekk.android.tracking.Webtrekk;


public class MediaActivity extends ActionBarActivity {
    private static final int MEDIA_LENGTH = 360;

    SeekBar playProgressBar;
    TextView currentMediaPositionField;
    MediaSession mediaSession;

    int getCurrentPlayProgress() {
        return (int) (MEDIA_LENGTH * (this.playProgressBar.getProgress() / 100.0) * 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_media);

        this.playProgressBar = (SeekBar) this.findViewById(R.id.media_current_psoition_progressbar);
        this.currentMediaPositionField = (TextView) this.findViewById(R.id.media_current_position_field);

        final Button playButton = (Button) this.findViewById(R.id.button_media_play);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = MediaActivity.this.getCurrentPlayProgress();

                if (MediaActivity.this.mediaSession == null) {
                    MediaCategories mediaCategories = new MediaCategories();
                    mediaCategories.category1 = "demo-category-1";
                    mediaCategories.category2 = "demo-category-2";
                    mediaCategories.category3 = "demo-category-3";

                    MediaActivity.this.mediaSession = Webtrekk.trackMedia("android-demo-media", MEDIA_LENGTH, progress, mediaCategories);
                }

                MediaActivity.this.mediaSession.trackEvent(com.webtrekk.android.tracking.MediaSession.MediaEvent.Play, progress);
            }
        });

        final Button pauseButton = (Button) this.findViewById(R.id.button_media_pause);
        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = MediaActivity.this.getCurrentPlayProgress();

                if (MediaActivity.this.mediaSession != null) {
                    MediaActivity.this.mediaSession.trackEvent(com.webtrekk.android.tracking.MediaSession.MediaEvent.Pause, progress);
                }
            }
        });

        final Button stopButton = (Button) this.findViewById(R.id.button_media_stop);
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = MediaActivity.this.getCurrentPlayProgress();

                if (MediaActivity.this.mediaSession != null) {
                    MediaActivity.this.mediaSession.trackEvent(com.webtrekk.android.tracking.MediaSession.MediaEvent.Pause, progress);
                    MediaActivity.this.mediaSession = null;
                }
            }
        });

        this.playProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = MediaActivity.this.getCurrentPlayProgress();

                if (MediaActivity.this.mediaSession != null) {
                    MediaActivity.this.mediaSession.trackEvent(MediaSession.MediaEvent.SeekEnd, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress = MediaActivity.this.getCurrentPlayProgress();

                if (MediaActivity.this.mediaSession != null) {
                    MediaActivity.this.mediaSession.trackEvent(com.webtrekk.android.tracking.MediaSession.MediaEvent.Seek, progress);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //not interesting for us
            }
        });
    }

    @Override
    protected void onStart () {
        super.onStart();

        Webtrekk.activityStart(this);



        MediaCategories mediaCategories = new MediaCategories();
        mediaCategories.category1 = "demo-category-1";
        mediaCategories.category2 = "demo-category-2";
        mediaCategories.category3 = "demo-category-3";

        MediaSession mediaTracking = Webtrekk.trackMedia(
                "mediaId",		// Name des Videos
                120000,			// Dauer des Videos in Millisekunden
                0,				// Startposition des Videos in Millisekunden
                mediaCategories	// optional - Mediakategorien
        );

        mediaTracking.trackEvent(com.webtrekk.android.tracking.MediaSession.MediaEvent.Play, 10000);



    }


    @Override
    protected void onResume() {
        super.onResume();

        Webtrekk.trackPage("media");
    }


    @Override
    protected void onStop() {
        Webtrekk.activityStop(this);

        super.onStop();
    }
}
