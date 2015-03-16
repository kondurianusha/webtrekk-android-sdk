package wtrack.tracking;

/**
 * this class contains the tracking evend and all the tracking params, and allows to create a json of it
 */
public class TrackingRequestJSON extends TrackingRequest{
    private Tracker.Events event;
    private TrackingParams params;

    public TrackingRequestJSON(Tracker.Events e, TrackingParams tp, WTrack wtrack) {
        super(wtrack);
        this.event = e;
        this.params = tp;
    }
}
