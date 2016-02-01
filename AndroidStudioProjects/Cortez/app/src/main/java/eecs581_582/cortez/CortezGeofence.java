package eecs581_582.cortez;

import android.os.Parcel;
import android.os.Parcelable;

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
public class CortezGeofence implements Parcelable {
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

    /**
     * We want to be able to pass data from CortezGeofence objects to other Contexts / Activities
     * (for example, InfoActivity), when their geofence is triggered. This is typicall by putting
     * type "extras" into the Intent you're sending from Context A to Context B.
     *
     * HOWEVER, the CortezGeofence object is too complex to send on its own (i.e., it's not a
     * primitive type). Therefore, we have to explicitly describe how to pack / unpack a stream of
     * data representing the CortezGeofence object. The object we create for doing that is called
     * a Parcel; hence the reason CortezGeofence implements Parcelable.
     */

    // Begin Parcelable interface definitions

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO: any member variables introduced to the CortezGeofence class and constructor
        // must also be written to the Parcel.

        // Write the Geofence object to the Parcel
        dest.writeParcelable((Parcelable) geofence, flags);

        // Write the LatLng object to the Parcel
        dest.writeParcelable(geofenceCoordinates, flags);

        // Write the MarkerOptions object to the Parcel
        dest.writeParcelable(geofenceMarkerOptions, flags);

        // Write the CircleOptions object to the Parcel
        dest.writeParcelable(geofenceCircleOptions, flags);

        // Write all String objects to the Parcel
        dest.writeString(geofenceEnterNotificationText);
        dest.writeString(geofenceDwellNotificationText);
        dest.writeString(geofenceExitNotificationText);
        dest.writeString(infoActivityMessage1);
        dest.writeString(infoActivityMessage2);
    }

    /**
     * The Parcel creator.
     */
    public static final Parcelable.Creator<CortezGeofence> CREATOR
            = new Parcelable.Creator<CortezGeofence>() {
        public CortezGeofence createFromParcel(Parcel in) {
            return new CortezGeofence(in);
        }

        public CortezGeofence[] newArray(int size) {
            return new CortezGeofence[size];
        }
    };

    /**
     * Reconstructs a CortezGeofence from the specified Parcel.
     * @param in the Parcel assumed to contain data for a CortezGeofence object.
     */
    private CortezGeofence(Parcel in) {
        // TODO: any member variables introduced to the CortezGeofence class and constructor
        // must also be reflected in this constructor.
        geofence = in.readParcelable(Geofence.class.getClassLoader());
        geofenceCoordinates = in.readParcelable(LatLng.class.getClassLoader());
        geofenceMarkerOptions = in.readParcelable(MarkerOptions.class.getClassLoader());
        geofenceCircleOptions = in.readParcelable(CircleOptions.class.getClassLoader());
        geofenceEnterNotificationText = in.readString();
        geofenceDwellNotificationText = in.readString();
        geofenceExitNotificationText = in.readString();
        infoActivityMessage1 = in.readString();
        infoActivityMessage2 = in.readString();
    }

    // end Parcelable interface definitions
}

