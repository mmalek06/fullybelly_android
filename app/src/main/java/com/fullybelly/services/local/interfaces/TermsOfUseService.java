package com.fullybelly.services.local.interfaces;

import com.fullybelly.models.api.TermsOfUse;

public interface TermsOfUseService {

    int getLastIdentifier();

    void setTermsAndConditions(TermsOfUse termsOfUse);

    TermsOfUse getTermsOfUse();

}
