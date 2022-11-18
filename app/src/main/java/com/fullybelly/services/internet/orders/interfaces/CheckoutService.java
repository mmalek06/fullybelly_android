package com.fullybelly.services.internet.orders.interfaces;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.CheckoutServiceResult;

public interface CheckoutService extends InternetService {

    CheckoutService configure(CheckoutService.ServiceCallback callback, String paymentId, String sessionId);

    interface ServiceCallback {
        void gotCheckoutResult(CheckoutServiceResult result);

        void gotCheckoutError(CheckoutServiceResult result);
    }

}
