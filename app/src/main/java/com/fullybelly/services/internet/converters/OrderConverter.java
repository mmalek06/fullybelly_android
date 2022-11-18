package com.fullybelly.services.internet.converters;

import android.text.TextUtils;

import com.fullybelly.models.api.BookedOrder;
import com.fullybelly.services.results.OrdersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class OrderConverter extends BaseConverter<BookedOrder, OrdersServiceResult> {

    //region methods

    @Override
    protected OrdersServiceResult getResult(BookedOrder input, ServiceResultCodes code) {
        return new OrdersServiceResult(input, code);
    }

    @Override
    protected OrdersServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        BookedOrder bookedOrder = null;

        try {
            String sessionId = json.getString("session_id");
            bookedOrder = new BookedOrder(sessionId);
        } catch (JSONException exc) { }

        if (bookedOrder != null) {
            return getResult(bookedOrder, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    @Override
    protected OrdersServiceResult getConvertedResult(JSONArray json, String scheme, String root) {
        List<String> errors = new ArrayList<>();

        try {
            for (int i = 0; i < json.length(); i++) {
                errors.add(json.getString(i));
            }
        } catch (JSONException exc) { }

        return new OrdersServiceResult(TextUtils.join("; ", errors), ServiceResultCodes.VALIDATION_ERROR);
    }

    //endregion
}
