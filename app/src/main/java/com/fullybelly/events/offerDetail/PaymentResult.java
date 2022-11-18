package com.fullybelly.events.offerDetail;

import android.content.Intent;

public final class PaymentResult {

    //region private fields

    private final Intent mData;

    private final boolean mSuccess;

    private final int mRequestCode;

    private final int mResultCode;

    //endregion

    //region getters

    public Intent getData() { return mData; }

    public boolean isSuccess() { return mSuccess; }

    public int getRequestCode() { return mRequestCode; }

    public int getResultCode() { return mResultCode; }

    //endregion

    //region constructor

    public PaymentResult(Intent data, boolean success, int requestCode, int resultCode) {
        mData = data;
        mSuccess = success;
        mRequestCode = requestCode;
        mResultCode = resultCode;
    }

    //endregion

}
