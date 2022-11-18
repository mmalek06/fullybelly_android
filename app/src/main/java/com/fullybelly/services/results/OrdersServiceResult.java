package com.fullybelly.services.results;

import com.fullybelly.models.api.BookedOrder;

public final class OrdersServiceResult extends ServiceResult {

    //region private fields

    private final BookedOrder mBookedOrder;

    //endregion

    //region getters

    public BookedOrder getOrder() { return mBookedOrder; }

    //endregion

    //region constructor

    public OrdersServiceResult(BookedOrder bookedOrder, ServiceResultCodes resultCode) {
        super(resultCode);

        mBookedOrder = bookedOrder;
    }

    public OrdersServiceResult(String error, ServiceResultCodes resultCode) {
        super(resultCode, error);

        mBookedOrder = null;
    }

    //endregion

}
