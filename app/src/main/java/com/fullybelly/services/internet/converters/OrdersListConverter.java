package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.PaidOrder;
import com.fullybelly.services.results.OrdersListServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class OrdersListConverter extends BaseConverter<List<PaidOrder>, OrdersListServiceResult> {

    //region methods

    @Override
    protected OrdersListServiceResult getConvertedResult(JSONArray json, String scheme, String root) {
        List<PaidOrder> paidOrders = new ArrayList<>();

        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject order = json.getJSONObject(i);
                String name = order.getString("restaurant_name");
                int dishesAmount = order.getInt("dishes_amount");
                String pickupTime = order.getString("pickup_time");
                String number = order.getString("number");
                String sessionId = order.getString("session_id");

                paidOrders.add(new PaidOrder(
                        name,
                        dishesAmount,
                        pickupTime,
                        number,
                        sessionId));
            }
        } catch (JSONException exc) { paidOrders.clear(); }

        if (paidOrders.size() > 0) {
            return getResult(paidOrders, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    @Override
    protected OrdersListServiceResult getResult(List<PaidOrder> input, ServiceResultCodes code) {
        return new OrdersListServiceResult(input, code);
    }

    //endregion

}
