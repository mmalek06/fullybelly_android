package com.fullybelly.services.results;

public class InternalResult {

    //region fields

    private final ServiceResultCodes mResultCodes;

    private final JsonResult mRequestResult;

    //endregion

    //region getters

    public ServiceResultCodes getResultCode() { return mResultCodes; }

    public JsonResult getRequestResult() { return mRequestResult; }

    //endregion

    //region constructor

    public InternalResult(JsonResult requestResult, ServiceResultCodes resultCodes) {
        mRequestResult = requestResult;
        mResultCodes = resultCodes;
    }

    //endregion

}
