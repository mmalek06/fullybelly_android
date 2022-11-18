package com.fullybelly.viewModels.mocks;

import com.fullybelly.models.api.TermsOfUse;
import com.fullybelly.services.local.interfaces.TermsOfUseService;


public final class LocalTermsOfUseMock implements TermsOfUseService {
    public TermsOfUse terms;

    @Override
    public int getLastIdentifier() { return terms.getId(); }

    @Override
    public void setTermsAndConditions(TermsOfUse termsOfUse) {
        terms = termsOfUse;
    }

    @Override
    public TermsOfUse getTermsOfUse() { return terms; }
}
