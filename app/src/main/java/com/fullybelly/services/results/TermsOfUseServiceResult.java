package com.fullybelly.services.results;

import com.fullybelly.models.api.TermsOfUse;

public final class TermsOfUseServiceResult extends ServiceResult {

    //region private fields

    private final TermsOfUse mTermsOfUse;

    //endregion

    //region getters

    public TermsOfUse getTerms() { return mTermsOfUse; }

    //endregion

    //region constructor

    public TermsOfUseServiceResult(TermsOfUse termsOfUsage, ServiceResultCodes resultCode) {
        super(resultCode);

        mTermsOfUse = termsOfUsage;
    }

    //endregion

}
