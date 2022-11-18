package com.fullybelly.services.results;

import java.util.List;

public final class PartnersServiceResult extends ServiceResult {

    //region private fields

    private final List<String> mPartners;

    //endregion

    //region getters

    public List<String> getPartners() { return mPartners; }

    //endregion

    //region constructor

    public PartnersServiceResult(List<String> partners, ServiceResultCodes resultCode) {
        super(resultCode);

        mPartners = partners;
    }

    //endregion

}
