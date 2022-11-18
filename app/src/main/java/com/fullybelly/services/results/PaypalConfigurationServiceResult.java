package com.fullybelly.services.results;

import com.fullybelly.models.api.PayPalPaymentsConfiguration;

public final class PaypalConfigurationServiceResult extends ServiceResult {

    //region fields

    private final PayPalPaymentsConfiguration mPaypalConfig;

    //endregion

    //region getters

    public PayPalPaymentsConfiguration getConfiguration() { return mPaypalConfig; }

    //endregion

    //region constructors

    public PaypalConfigurationServiceResult(PayPalPaymentsConfiguration paypalConfig, ServiceResultCodes resultCode) {
        super(resultCode);

        mPaypalConfig = paypalConfig;
    }

    public PaypalConfigurationServiceResult(String error, ServiceResultCodes resultCode) {
        super(resultCode, error);

        mPaypalConfig = null;
    }

    //endregion

}
