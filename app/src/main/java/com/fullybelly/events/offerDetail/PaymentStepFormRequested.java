package com.fullybelly.events.offerDetail;

public final class PaymentStepFormRequested {

    //region enums

    public enum Step { USER_DETAILS, PAYMENT_METHOD }

    //endregion

    //region fields

    private final int mRestaurantId;

    private final int mMealId;

    private final int mTransactionValue;

    private final int mAmount;

    private final String mCurrency;

    private final Step mStep;

    //endregion

    //region getters

    public int getRestaurantId() { return mRestaurantId; }

    public int getMealId() { return mMealId; }

    public int getTransactionValue() { return mTransactionValue; }

    public int getAmount() { return mAmount; }

    public String getCurrency() { return mCurrency; }

    public Step getStep() { return mStep; }

    //endregion

    //region constructor

    public PaymentStepFormRequested(int restaurantId, int mealId, int transactionValue, int amount, String currency, Step step) {
        mRestaurantId = restaurantId;
        mMealId = mealId;
        mTransactionValue = transactionValue;
        mAmount = amount;
        mCurrency = currency;
        mStep = step;
    }

    //endregion

}
