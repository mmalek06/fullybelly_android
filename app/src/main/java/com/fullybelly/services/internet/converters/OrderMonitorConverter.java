package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.FreshOrderInfo;
import com.fullybelly.services.results.OrdersMonitorServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;
import org.json.JSONObject;

public final class OrderMonitorConverter extends BaseConverter<FreshOrderInfo, OrdersMonitorServiceResult> {

    //region internal methods

    @Override
    protected OrdersMonitorServiceResult getResult(FreshOrderInfo input, ServiceResultCodes code) {
        return new OrdersMonitorServiceResult(input, code);
    }

    @Override
    protected OrdersMonitorServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        FreshOrderInfo freshOrderInfo = null;

        try {
            String sessionId = json.getString("session_id");

            freshOrderInfo = new FreshOrderInfo(sessionId);
        } catch (JSONException exc) { }

        if (freshOrderInfo != null) {
            return getResult(freshOrderInfo, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    //endregion

}
