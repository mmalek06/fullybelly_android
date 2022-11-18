package com.fullybelly.services.local.implementations;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.local.interfaces.BookedOrderCacheService;
import com.fullybelly.services.local.interfaces.PaymentService;
import com.fullybelly.services.local.interfaces.PaymentServiceFactory;

import javax.inject.Inject;

public final class PaymentServiceFactoryImpl implements PaymentServiceFactory {

    //region fields

    private final FullyBellyApplication mApplication;

    private final BookedOrderCacheService mBookedOrderCacheService;

    //endregion

    //region constructor

    @Inject
    public PaymentServiceFactoryImpl(FullyBellyApplication application,
                                     BookedOrderCacheService bookedOrderCacheService) {
        mApplication = application;
        mBookedOrderCacheService = bookedOrderCacheService;
    }

    //endregion

    //region public methods

    @Override
    public PaymentService get() {
        String country = mApplication.getCountryCode();

        switch (country) {
            case "pl":
                return new P24PaymentServiceImpl(mApplication);
            case "de":
                return new PaypalPaymentServiceImpl(mApplication,
                                                    mBookedOrderCacheService);
        }

        return null;
    }

    //endregion

}
