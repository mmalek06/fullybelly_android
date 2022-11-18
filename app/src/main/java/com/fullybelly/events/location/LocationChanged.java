package com.fullybelly.events.location;

import android.location.Location;

public final class LocationChanged {

    //region fields

    private Location mLocation;

    private LocationStatus mStatus;

    //endregion

    //region getters

    public Location getLocation() { return mLocation; }

    public LocationStatus getLocationStatus() { return mStatus; }

    //endregion

    //region constructor

    public LocationChanged(Location location, LocationStatus status) {
        mLocation = location;
        mStatus = status;
    }

    //endregion

}
