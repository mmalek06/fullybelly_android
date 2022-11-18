package com.fullybelly.services.internet.orders.implementations;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;

import javax.inject.Inject;

public final class ConfigurationServiceFactoryImpl implements ConfigurationServiceFactory {

    //region fields

    private final FullyBellyApplication mApplication;

    //endregion

    //region constructor

    @Inject
    public ConfigurationServiceFactoryImpl(FullyBellyApplication application) {
        mApplication = application;
    }

    //endregion

    //region public methods

    public ConfigurationService get() {
        String country = mApplication.getCountryCode();

        switch (country) {
            case "pl":
                return new P24ConfigurationServiceImpl(mApplication);
            case "de":
                return new PaypalConfigurationServiceImpl(mApplication);
        }

        return null;
    }

    //endregion

}
