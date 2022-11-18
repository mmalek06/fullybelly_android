package com.fullybelly.services.internet.common;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.PartnersServiceResult;

public interface PartnersService extends InternetService {

    PartnersService configure(ServiceCallback callback, String countryCode);

    interface ServiceCallback {
        void gotPartners(PartnersServiceResult result);

        void gotPartnersError(PartnersServiceResult result);
    }

}
