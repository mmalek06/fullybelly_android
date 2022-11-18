package com.fullybelly.events.activities;

import com.fullybelly.models.api.DetailedOffer;

public final class OfferMapActivityInitialized {

    //region fields

    private DetailedOffer mOffer;

    //endregion

    //region getters

    public DetailedOffer getOffer() { return mOffer; }

    //endregion

    //region constructor

    public OfferMapActivityInitialized(DetailedOffer offer) {
        mOffer = offer;
    }

    //endregion

}
