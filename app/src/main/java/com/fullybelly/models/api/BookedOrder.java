package com.fullybelly.models.api;

public final class BookedOrder {

    //region fields

    private final String mSessionId;

    //endregion

    //region getters

    public String getSessionId() { return mSessionId; }

    //endregion

    //region constructor

    public BookedOrder(String sessionId) { mSessionId = sessionId; }

    //endregion

}
