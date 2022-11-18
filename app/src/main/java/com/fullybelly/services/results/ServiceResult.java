package com.fullybelly.services.results;

public class ServiceResult {

    //region fields

    protected final ServiceResultCodes mResultCode;

    protected final String mError;

    //endregion

    //region getters

    public ServiceResultCodes getResultCode() { return mResultCode; }

    public String getError() { return mError; }

    //endregion

    //region constructor

    public ServiceResult(ServiceResultCodes resultCode) {
        mResultCode = resultCode;
        mError = null;
    }

    public ServiceResult(ServiceResultCodes resultCode, String error) {
        mResultCode = resultCode;
        mError = error;
    }

    //endregion

}
