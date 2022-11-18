package com.fullybelly.models.api;

public class DetailedOffer extends Offer {

    //region fields

    private String mBackgroundImgUrl;

    private String mDistrict;

    private String mAddress;

    private String mDescription;

    private String mCountry;

    private double mLatitude;

    private double mLongitude;

    private boolean mCanEatOnSite;

    //endregion

    //region getters

    public String getBackgroundImgUrl() { return mBackgroundImgUrl; }

    public String getDistrict() { return mDistrict; }

    public String getAddress() { return mAddress; }

    public String getDescription() { return mDescription; }

    public String getCountry() { return mCountry; }

    public double getLatitude() { return mLatitude; }

    public double getLongitude() { return mLongitude; }

    public String getMealDescription() { return mMealDescription; }

    public boolean canEatOnSite() { return mCanEatOnSite; }

    //endregion

    //region constructors

    public DetailedOffer() {
        super(0, 0, null, null, null, 0, 0, 0, null, null, null, null, false);
    }

    public DetailedOffer(
            int restaurantId,
            int mealId,
            String name,
            String logoUrl,
            String backgroundUrl,
            Double distance,
            String district,
            String address,
            int availableDishes,
            double oldPrice,
            double discountedPrice,
            String currency,
            String pickupFrom,
            String pickupTo,
            String description,
            double latitude,
            double longitude,
            String mealDescription,
            boolean canEatOnSite,
            String country,
            boolean isTodaysOffer) {
        super(restaurantId, mealId, name, logoUrl, distance, availableDishes, oldPrice, discountedPrice, currency, pickupFrom, pickupTo, mealDescription, isTodaysOffer);

        mBackgroundImgUrl = backgroundUrl;
        mDistrict = district;
        mAddress = address;
        mDescription = description;
        mLatitude = latitude;
        mLongitude = longitude;
        mCanEatOnSite = canEatOnSite;
        mCountry = country;
    }

    //endregion

}
