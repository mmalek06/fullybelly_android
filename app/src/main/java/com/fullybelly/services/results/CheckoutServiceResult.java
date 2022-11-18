package com.fullybelly.services.results;

import com.fullybelly.models.api.Checkout;

public final class CheckoutServiceResult extends ServiceResult {

    //region fields

    private final Checkout mCheckout;

    //endregion

    //region getters

    public Checkout getCheckout() { return mCheckout; }

    //endregion

    //region constructors

    public CheckoutServiceResult(Checkout checkout, ServiceResultCodes resultCode) {
        super(resultCode, checkout != null && checkout.getValidations().size() > 0 ? checkout.getValidations().get(0) : null);

        mCheckout = checkout;
    }

    //endregion

}
