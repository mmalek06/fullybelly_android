package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.internet.common.TermsOfUseService;

public final class InternetTermsOfUseMock implements TermsOfUseService {
    @Override
    public TermsOfUseService configure(ServiceCallback callback, String countryCode) { return this; }

    @Override
    public void run() {

    }

    @Override
    public void tearDown() {

    }
}
