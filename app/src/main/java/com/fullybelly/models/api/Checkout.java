package com.fullybelly.models.api;

import java.util.List;

public final class Checkout {

    //region fields

    private final boolean mTransactionSuccessful;

    private final String mSessionId;

    private final List<String> mValidations;

    //endregion

    //region getters

    public boolean isTransactionSuccessful() { return mTransactionSuccessful; }

    public String getSessionId() { return mSessionId; }

    public List<String> getValidations() { return mValidations; }

    //endregion

    //region constructor

    public Checkout(boolean transactionSuccessful, String sessionId, List<String> validations) {
        mTransactionSuccessful = transactionSuccessful;
        mSessionId = sessionId;
        mValidations = validations;
    }

    //endregion

}
