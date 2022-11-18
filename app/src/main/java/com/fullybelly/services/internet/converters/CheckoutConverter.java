package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.Checkout;
import com.fullybelly.services.results.CheckoutServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class CheckoutConverter extends BaseConverter<Checkout, CheckoutServiceResult> {

    //region methods

    @Override
    protected CheckoutServiceResult getResult(Checkout input, ServiceResultCodes code) {
        return new CheckoutServiceResult(input, code);
    }

    @Override
    protected CheckoutServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        Checkout checkout = fromJson(json, scheme, root);

        if (checkout != null && checkout.isTransactionSuccessful()) {
            return getResult(checkout, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(checkout, ServiceResultCodes.VALIDATION_ERROR);
        }
    }

    private Checkout fromJson(JSONObject json, String scheme, String root) {
        try {
            boolean transactionSuccessful = json.getBoolean("transaction_successful");
            String sessionId = json.getString("session_id");
            List<String> validations = new ArrayList<>();
            JSONArray jArray = json.has("validations") ? json.getJSONArray("validations") : null;

            if (jArray != null) {
                for (int i=0; i < jArray.length(); i++) {
                    validations.add(jArray.getString(i));
                }
            }

            return new Checkout(transactionSuccessful, sessionId, validations);
        } catch (JSONException e) {
            return null;
        }
    }

    //endregion

}
