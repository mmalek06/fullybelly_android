package com.fullybelly.services.internet.orders.interfaces;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.ServiceResult;

public interface ConfigurationService extends InternetService {

    ConfigurationService configure(ServiceCallback callback);

    interface ServiceCallback {
        void gotConfigurationResult(ServiceResult result);

        void gotConfigurationError(ServiceResult result);
    }

}
