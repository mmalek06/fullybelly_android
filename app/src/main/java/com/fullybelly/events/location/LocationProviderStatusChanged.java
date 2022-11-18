package com.fullybelly.events.location;

public final class LocationProviderStatusChanged {

    //region fields

    private ProviderStatus mStatus;

    //endregion

    //region getters

    public ProviderStatus getStatus() { return mStatus; }

    //endregion

    //region constructor

    public LocationProviderStatusChanged(ProviderStatus status) {
        mStatus = status;
    }

    //endregion

}
