package com.fullybelly.services.results;

import com.fullybelly.models.api.FreshOrderInfo;

public final class OrdersMonitorServiceResult extends ServiceResult {

    //region fields

    private final FreshOrderInfo mOrderInfo;

    //endregion

    //region getters

    public FreshOrderInfo getOrder() { return mOrderInfo; }

    //endregion

    //region constructor

    public OrdersMonitorServiceResult(FreshOrderInfo orderInfo, ServiceResultCodes resultCode) {
        super(resultCode);

        mOrderInfo = orderInfo;
    }

    //endregion

}
