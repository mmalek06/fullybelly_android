package com.fullybelly.services.results;

import com.fullybelly.models.api.P24Configuration;

public final class P24ConfigurationServiceResult extends ServiceResult {

    //region fields

    private final P24Configuration mP24Configuration;

    //endregion

    //region getters

    public P24Configuration getConfiguration() { return mP24Configuration; }

    //endregion

    //region constructors

    public P24ConfigurationServiceResult(P24Configuration p24Configuration, ServiceResultCodes resultCode) {
        super(resultCode);

        mP24Configuration = p24Configuration;
    }

    public P24ConfigurationServiceResult(String error, ServiceResultCodes resultCode) {
        super(resultCode, error);

        mP24Configuration = null;
    }

    //endregion

}
