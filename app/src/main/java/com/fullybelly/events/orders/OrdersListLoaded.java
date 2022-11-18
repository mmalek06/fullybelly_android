package com.fullybelly.events.orders;

public final class OrdersListLoaded {

    //region fields

    private final boolean mHasOrders;

    //endregion

    //region getters

    public boolean hasOrders() { return mHasOrders; }

    //endregion

    //region constructor

    public OrdersListLoaded(boolean hasOrders) {
        mHasOrders = hasOrders;
    }

    //endregion

}
