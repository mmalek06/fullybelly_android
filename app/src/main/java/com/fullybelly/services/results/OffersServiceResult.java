package com.fullybelly.services.results;

import com.fullybelly.models.api.Offer;

import java.util.List;

public final class OffersServiceResult extends ServiceResult {

    //region private fields

    private final List<Offer> mOffers;

    private final Double mLatitude;

    private final Double mLongitude;

    //endregion

    //region getters

    public List<Offer> getOffers() { return mOffers; }

    public Double getLatitude() { return mLatitude; }

    public Double getLongitude() { return mLongitude; }

    //endregion

    //region constructor

    public OffersServiceResult(List<Offer> offers, Double latitude, Double longitude, ServiceResultCodes resultCode) {
        super(resultCode);

        mOffers = offers;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    //endregion

}
