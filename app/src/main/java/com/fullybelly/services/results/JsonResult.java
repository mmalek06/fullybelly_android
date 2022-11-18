package com.fullybelly.services.results;

public final class JsonResult {

    //region fields

    private final int mResultCode;

    private final Object mResult;

    //endregion

    //region getters

    public int getResultCode() { return mResultCode; }

    public Object getResult() { return mResult; }

    //endregion

    //region constructor

    public JsonResult(int code, Object result) {
        mResultCode = code;
        mResult = result;
    }

    //endregion

}
