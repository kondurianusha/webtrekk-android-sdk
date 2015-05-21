package com.webtrekk.android.tracking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MediaSession {

    public enum MediaEvent {
        Pause("pause"),
        Play("play"),
        Position("pos"),
        Seek("seek"),
        SeekEnd(null),
        Stop("stop");


        private final String name;

        private MediaEvent(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }


    private static final String SPECIAL_EVENT_NAME_EOF  = "eof";
    private static final String SPECIAL_EVENT_NAME_INIT = "init";

    private static final HashSet<String> RESERVED_EVENT_NAMES = new HashSet<String>();
    static {
        RESERVED_EVENT_NAMES.add(SPECIAL_EVENT_NAME_EOF);
        RESERVED_EVENT_NAMES.add(SPECIAL_EVENT_NAME_INIT);
        RESERVED_EVENT_NAMES.add(MediaEvent.Pause.getName());
        RESERVED_EVENT_NAMES.add(MediaEvent.Play.getName());
        RESERVED_EVENT_NAMES.add(MediaEvent.Position.getName());
        RESERVED_EVENT_NAMES.add(MediaEvent.Seek.getName());
        RESERVED_EVENT_NAMES.add(MediaEvent.Stop.getName());
    }

    private static final String PARAMETER_PIXEL                  = "p";
    private static final String PARAMETER_VIDEO_MEDIA_ID         = "mi";
    private static final String PARAMETER_VIDEO_BANDWIDTH        = "bw";
    private static final String PARAMETER_VIDEO_ACTION           = "mk";
    private static final String PARAMETER_VIDEO_CURRENT_POSITION = "mt1";
    private static final String PARAMETER_VIDEO_LENGTH           = "mt2";
    private static final String PARAMETER_VIDEO_VOLUME           = "vol";
    private static final String PARAMETER_VIDEO_MUTED            = "mut";
    private static final String PARAMETER_VIDEO_MEDIA_CATEGORY   = "mg";
    private static final String PIXEL_VALUE                      = "314,st";


    private Integer      bandwidth;
    private final Core   core;
    private MediaEvent   currentState;
    private final int    duration;
    private boolean      ended;
    private final String mediaId;
    private Boolean      muted;
    private MediaEvent   stateBeforeSeeking;
    private Integer      volume;



    MediaSession(Core core, String mediaId, int duration, int initialPosition, MediaCategories categories){
        this.core         = core;
        this.currentState = MediaEvent.Stop;
        this.duration     = duration;
        this.mediaId      = mediaId;

        this.trackInit(initialPosition, categories);
    }


    public Integer getBandwidth() {
        return this.bandwidth;
    }


    public MediaEvent getCurrentState() {
        return this.currentState;
    }


    public String getMediaId() {
        return this.mediaId;
    }


    public Boolean getMuted() {
        return this.muted;
    }


    public Integer getVolume() {
        return this.volume;
    }


    private void log(String message) {
        if (this.core.isLoggingEnabled()) {
            this.core.log("MediaSession: " + message);
        }
    }


    public void setBandwidth(Integer bandwidth) {
        if (bandwidth != null && (bandwidth.intValue() < 0)) {
            this.log("setBandwidth: Value must not be negative.");
            return;
        }

        this.bandwidth = bandwidth;
    }


    public void setMuted(Boolean muted) {
        this.muted = muted;
    }


    public void setVolume(Integer volume) {
        if (volume != null && (volume.intValue() < 0 || volume.intValue() > 255)) {
            this.log("setVolume: Value must be between 0 and 255.");
            return;
        }

        this.volume = volume;
    }


    public void trackCustomEvent(String name, int position) {
        if (name == null || name.isEmpty()) {
            this.log("trackCustomEvent: 'name' must not be empty.");
            return;
        }
        if (position < 0) {
            this.log("trackCustomEvent: 'position' must not be negative.");
            return;
        }
        if (RESERVED_EVENT_NAMES.contains(name)) {
            this.log("trackCustomEvent: '" + name + "' is a reserved event name.");
            return;
        }
        if (this.ended) {
            this.log("trackCustomEvent: Media session already ended.");
            return;
        }

        this.trackEvent(name, position, null);
    }


    public void trackEvent(MediaEvent event, int position) {
        if (position < 0) {
            this.log("trackEvent: 'position' must not be negative.");
            return;
        }
        if (this.ended) {
            this.log("trackEvent: Media session already ended.");
            return;
        }
        if (event == MediaEvent.SeekEnd) {
            if (this.currentState != MediaEvent.Seek) {
                this.log("trackEvent: Cannot track seek end event. Media session is not seeking.");
                return;
            }

            // stop will become pause, otherwise we'd kill our session.
            if (this.stateBeforeSeeking == MediaEvent.Play) {
                event = MediaEvent.Play;
            }
            else {
                event = MediaEvent.Pause;
            }
        }

        String eventName = event.getName();
        switch (event) {
            case Pause:
                if (this.currentState == MediaEvent.Pause) {
                    this.log("trackEvent: Cannot track pause event. Media session is already paused.");
                    return;
                }
                break;

            case Play:
                if (this.currentState == MediaEvent.Play) {
                    this.log("trackEvent: Cannot track play event. Media session is already playing.");
                    return;
                }
                break;

            case Position:
                if (this.currentState != MediaEvent.Play) {
                    this.log("trackEvent: Cannot track position event. Media session is not playing.");
                    return;
                }
                break;

            case Seek:
            case SeekEnd: // never happens - handled above
                if (this.currentState == MediaEvent.Seek) {
                    this.log("trackEvent: Cannot track seek event. Media session is already seeking.");
                    return;
                }

                this.stateBeforeSeeking = this.currentState;
                break;

            case Stop:
                if (this.currentState == MediaEvent.Stop) {
                    this.log("trackEvent: Cannot track stop event. Media session is already stopped.");
                    return;
                }

                this.ended = true;

                if (position == this.duration) {
                    // natural ending
                    eventName = SPECIAL_EVENT_NAME_EOF;
                }
                break;
        }

        if (event != MediaEvent.Position) {
            this.currentState = event;
        }

        this.trackEvent(eventName, position, null);
    }


    private void trackEvent(String name, int position, Map<String,String> data) {
        Map<String,String> parameters = data != null ? new HashMap<String,String>(data) : new HashMap<String,String>();

        parameters.put(PARAMETER_PIXEL,                  PIXEL_VALUE);
        parameters.put(PARAMETER_VIDEO_MEDIA_ID,         this.mediaId);
        parameters.put(PARAMETER_VIDEO_ACTION,           name);
        parameters.put(PARAMETER_VIDEO_CURRENT_POSITION, Integer.toString(position / 1000));

        if (this.bandwidth != null) {
            parameters.put(PARAMETER_VIDEO_BANDWIDTH, this.bandwidth.toString());
        }
        if (this.muted != null) {
            parameters.put(PARAMETER_VIDEO_MUTED, this.muted.booleanValue() ? "1" : "0");
        }
        if (this.volume != null) {
            parameters.put(PARAMETER_VIDEO_VOLUME, this.volume.toString());
        }

        this.core.trackEvent(parameters);
    }


    private void trackInit(int initialPosition, MediaCategories categories) {
        Map<String,String> data = new HashMap<String,String>();
        data.put(PARAMETER_VIDEO_LENGTH, Integer.toString(this.duration));

        if (categories != null) {
            if (categories.category1 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"1", categories.category1);
            }
            if (categories.category2 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"2", categories.category2);
            }
            if (categories.category3 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"3", categories.category3);
            }
            if (categories.category4 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"4", categories.category4);
            }
            if (categories.category5 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"5", categories.category5);
            }
            if (categories.category6 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"6", categories.category6);
            }
            if (categories.category7 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"7", categories.category7);
            }
            if (categories.category8 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"8", categories.category8);
            }
            if (categories.category9 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"9", categories.category9);
            }
            if (categories.category10 != null) {
                data.put(PARAMETER_VIDEO_MEDIA_CATEGORY+"10", categories.category10);
            }
        }

        this.trackEvent(SPECIAL_EVENT_NAME_INIT, initialPosition, data);
    }
}