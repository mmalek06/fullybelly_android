package com.fullybelly.events.orders;

public final class OrderCheckRequested {

    //region fields

    private final String mSessionId;

    //endregion

    //region getters

    public String getSessionId() { return mSessionId; }

    //endregion

    //region constructor

    public OrderCheckRequested(String sessionId) {
        mSessionId = sessionId;
    }

    //endregion

}
