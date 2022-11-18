package com.fullybelly.services.internet.common;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.TermsOfUseServiceResult;

public interface TermsOfUseService extends InternetService {

    TermsOfUseService configure(ServiceCallback callback, String countryCode);

    interface ServiceCallback {
        void gotTermsAndConditions(TermsOfUseServiceResult result);

        void gotTermsAndConditionsError(TermsOfUseServiceResult result);
    }

}
