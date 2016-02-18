package eecs581_582.cortez;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A wrapper class containing all data relevant to a Geofence in Cortez.
 * Construction and parameters are set by the nested Builder class.
 *
 * Created by Joseph on 1/23/16.
 */
public class CortezGeofence {
    private final Geofence geofence;
    private final LatLng geofenceCoordinates;
    private final MarkerOptions geofenceMarkerOptions;
    private final CircleOptions geofenceCircleOptions;
    private final String geofenceEnterNotificationText;
    private final String geofenceDwellNotificationText;
    private final String geofenceExitNotificationText;
    private final String infoActivityMessage1;
    private final String infoActivityMessage2;

    // TODO: be sure to initialize each member variable for CortezGeofence in this constructor.
    // Since this constructor will remain private, there's no way to set the variables later.
    private CortezGeofence(
            Geofence geofence,
            LatLng geofenceCoordinates,
            MarkerOptions geofenceMarkerOptions,
            CircleOptions geofenceCircleOptions,
            String geofenceEnterNotificationText,
            String geofenceDwellNotificationText,
            String geofenceExitNotificationText,
            String infoActivityMessage1,
            String infoActivityMessage2
    ) {
        this.geofence = geofence;
        this.geofenceCoordinates = geofenceCoordinates;
        this.geofenceMarkerOptions = geofenceMarkerOptions;
        this.geofenceCircleOptions = geofenceCircleOptions;
        this.geofenceEnterNotificationText = geofenceEnterNotificationText;
        this.geofenceDwellNotificationText = geofenceDwellNotificationText;
        this.geofenceExitNotificationText = geofenceExitNotificationText;
        this.infoActivityMessage1 = infoActivityMessage1;
        this.infoActivityMessage2 = infoActivityMessage2;
    }

    // Getters for CortezGeofence

    public Geofence getGeofence() {
        return geofence;
    }

    public LatLng getGeofenceCoordinates() {
        return geofenceCoordinates;
    }

    public MarkerOptions getGeofenceMarkerOptions() {
        return geofenceMarkerOptions;
    }

    public CircleOptions getGeofenceCircleOptions() {
        return geofenceCircleOptions;
    }

    public String getGeofenceEnterNotificationText() {
        return geofenceEnterNotificationText;
    }

    public String getGeofenceDwellNotificationText() {
        return geofenceDwellNotificationText;
    }

    public String getGeofenceExitNotificationText() {
        return geofenceExitNotificationText;
    }

    public String getInfoActivityMessage1() {
        return infoActivityMessage1;
    }

    public String getInfoActivityMessage2() {
        return infoActivityMessage2;
    }

    // end getters

    /**
     * As we continue to add support for different types of media that can be handled by Cortez,
     * it will become unwieldy to use constructors for every parameter. This Builder class will
     * allow us to construct a CortezGeofence object with as many parameters as we'd like to set,
     * without requiring "null" for every supported parameter of CortezGeofence.
     *
     */
    public static class Builder {
        // TODO: any member variables introduced to the CortezGeofence class and constructor
        // must also be reflected in this Builder class.
        private Geofence geofence;
        private LatLng geofenceCoordinates;
        private MarkerOptions geofenceMarkerOptions;
        private CircleOptions geofenceCircleOptions;
        private String geofenceEnterNotificationText;
        private String geofenceDwellNotificationText;
        private String geofenceExitNotificationText;
        private String infoActivityMessage1;
        private String infoActivityMessage2;

        // We're going to require Geofence and LatLng objects to build CortezGeofence
        public Builder(final Geofence geofence, final LatLng geofenceCoordinates) {
            this.geofence = geofence;
            this.geofenceCoordinates = geofenceCoordinates;
        }

        public Builder markerOptions(final MarkerOptions geofenceMarkerOptions) {
            this.geofenceMarkerOptions = geofenceMarkerOptions;
            return this;
        }

        public Builder circleOptions(final CircleOptions geofenceCircleOptions) {
            this.geofenceCircleOptions = geofenceCircleOptions;
            return this;
        }

        public Builder enterNotificationText(final String notificationText) {
            this.geofenceEnterNotificationText = notificationText;
            return this;
        }

        public Builder dwellNotificationText(final String notificationText) {
            this.geofenceDwellNotificationText = notificationText;
            return this;
        }

        public Builder exitNotificationText(final String notificationText) {
            this.geofenceExitNotificationText = notificationText;
            return this;
        }

        public Builder infoActivityMessage1(final String infoActivityMessage1) {
            this.infoActivityMessage1 = infoActivityMessage1;
            return this;
        }

        public Builder infoActivityMessage2(final String infoActivityMessage2) {
            this.infoActivityMessage2 = infoActivityMessage2;
            return this;
        }

        public CortezGeofence build() {
            return new CortezGeofence(
                    geofence,
                    geofenceCoordinates,
                    geofenceMarkerOptions,
                    geofenceCircleOptions,
                    geofenceEnterNotificationText,
                    geofenceDwellNotificationText,
                    geofenceExitNotificationText,
                    infoActivityMessage1,
                    infoActivityMessage2);
        }
    } // end Builder class
}

