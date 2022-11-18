package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.PayPalPaymentsConfiguration;
import com.fullybelly.services.results.PaypalConfigurationServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;
import org.json.JSONObject;

public final class PaypalConfigurationConverter extends BaseConverter<PayPalPaymentsConfiguration, PaypalConfigurationServiceResult> {

    //region methods

    @Override
    protected PaypalConfigurationServiceResult getResult(PayPalPaymentsConfiguration input, ServiceResultCodes code) {
        return new PaypalConfigurationServiceResult(input, code);
    }

    @Override
    protected PaypalConfigurationServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        PayPalPaymentsConfiguration p24Configuration = fromJson(json, scheme, root);

        if (p24Configuration != null) {
            return getResult(p24Configuration, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    private PayPalPaymentsConfiguration fromJson(JSONObject json, String scheme, String root) {
        try {
            String clientId = json.getString("CLIENT_ID");

            return new PayPalPaymentsConfiguration(clientId);
        } catch (JSONException e) {
            return null;
        }
    }

    //endregion

}
