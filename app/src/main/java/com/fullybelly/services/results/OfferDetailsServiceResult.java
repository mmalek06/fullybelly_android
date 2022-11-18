package com.fullybelly.services.results;

import com.fullybelly.models.api.DetailedOffer;

public final class OfferDetailsServiceResult extends ServiceResult {

    //region private fields

    private final DetailedOffer mOffer;

    //endregion

    //region getters

    public DetailedOffer getOffer() { return mOffer; }

    //endregion

    //region constructor

    public OfferDetailsServiceResult(DetailedOffer offer, ServiceResultCodes resultCode) {
        super(resultCode);

        mOffer = offer;
    }

    //endregion

}
