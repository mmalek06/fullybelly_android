package com.fullybelly.services.results;

import com.fullybelly.models.api.PaidOrder;

import java.util.List;

public final class OrdersListServiceResult extends ServiceResult {

    //region fields

    private final List<PaidOrder> mOrders;

    //endregion

    //region getters

    public List<PaidOrder> getOrders() { return mOrders; }

    //endregion

    //region constructor

    public OrdersListServiceResult(List<PaidOrder> orders, ServiceResultCodes resultCode) {
        super(resultCode);

        mOrders = orders;
    }

    //endregion

}
