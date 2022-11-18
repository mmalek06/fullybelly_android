package com.fullybelly.models.api;

import java.io.Serializable;

public class Offer implements Serializable {

    //region fields

    protected int mRestaurantId;

    protected int mMealId;

    protected String mName;

    protected String mMealDescription;

    protected String mLogoUrl;

    protected Double mDistance;

    protected int mAvailableDishes;

    protected double mStandardPrice;

    protected double mDiscountedPrice;

    protected final String mCurrency;

    protected String mPickupFrom;

    protected String mPickupTo;

    protected boolean mIsTodaysOffer;

    //endregion

    public int getRestaurantId() { return mRestaurantId; }

    public int getMealId() { return mMealId; }

    public String getName() { return mName; }

    public String getLogoUrl() { return mLogoUrl; }

    public Double getDistance() { return mDistance; }

    public int getAvailableDishes() { return mAvailableDishes; }

    public double getStandardPrice() { return mStandardPrice; }

    public double getDiscountedPrice() { return mDiscountedPrice; }

    public String getCurrency() { return mCurrency; }

    public String getPickupFrom() { return mPickupFrom; }

    public String getPickupTo() { return mPickupTo; }

    public String getMealDescription() { return mMealDescription; }

    public boolean getIsTodaysOffer() { return mIsTodaysOffer; }

    //region constructor

    public Offer(
            int restaurantId,
            int mealId,
            String name,
            String logoUrl,
            Double distance,
            int availableDishes,
            double standardPrice,
            double discountedPrice,
            String currency,
            String pickupFrom,
            String pickupTo,
            String mealDescription,
            boolean isTodaysOffer) {
        mRestaurantId = restaurantId;
        mMealId = mealId;
        mName = name;
        mLogoUrl = logoUrl;
        mDistance = distance;
        mAvailableDishes = availableDishes;
        mStandardPrice = standardPrice;
        mDiscountedPrice = discountedPrice;
        mCurrency = currency;
        mPickupFrom = pickupFrom;
        mPickupTo = pickupTo;
        mMealDescription = mealDescription;
        mIsTodaysOffer = isTodaysOffer;
    }

    //endregion

}
