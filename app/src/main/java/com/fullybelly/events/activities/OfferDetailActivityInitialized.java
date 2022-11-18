package com.fullybelly.events.activities;

public final class OfferDetailActivityInitialized {

    //region fields

    private final int mRestaurantId;

    private final int mMealId;

    private final Double mLatitude;

    private final Double mLongitude;

    //endregion

    //region getters

    public int getRestaurantId() { return mRestaurantId; }

    public int getMealId() { return mMealId; }

    public Double getLatitude() { return mLatitude; }

    public Double getLongitude() { return mLongitude; }

    //endregion

    //region constructor

    public OfferDetailActivityInitialized(int restaurantId, int mealId, Double latitude, Double longitude) {
        mRestaurantId = restaurantId;
        mMealId = mealId;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    //endregion

}
