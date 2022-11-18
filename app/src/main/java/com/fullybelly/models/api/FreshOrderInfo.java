package com.fullybelly.models.api;

public final class FreshOrderInfo {

    //region fields

    private final String mSessionId;

    //endregion

    //region getters

    public String getSessionId() { return mSessionId; }

    //endregion

    //region constructor

    public FreshOrderInfo(String sessionId) {
        mSessionId = sessionId;
    }

    //endregion

}
