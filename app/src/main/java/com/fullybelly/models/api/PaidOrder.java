package com.fullybelly.models.api;

public final class PaidOrder {

    //region fields

    private final String mName;

    private final int mDishesAmount;

    private final String mPickupTime;

    private final String mNumber;

    private final String mSessionId;

    //endregion

    //region getters

    public String getName() { return mName; }

    public int getDishesAmount() { return mDishesAmount; }

    public String getPickupTime() { return mPickupTime; }

    public String getNumber() { return mNumber; }

    public String getSessionId() { return mSessionId; }

    //endregion

    //region constructor

    public PaidOrder(String name, int dishesAmount, String pickupTime, String number, String sessionId) {
        mName = name;
        mDishesAmount = dishesAmount;
        mPickupTime = pickupTime;
        mNumber = number;
        mSessionId = sessionId;
    }

    //endregion

}
