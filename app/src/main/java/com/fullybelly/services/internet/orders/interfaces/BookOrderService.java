package com.fullybelly.services.internet.orders.interfaces;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.OrdersServiceResult;

public interface BookOrderService extends InternetService {

    BookOrderService configure(
            int restaurant,
            int meal,
            int amount,
            String buyerEmail,
            ServiceCallback callback);

    interface ServiceCallback {
        void gotBookingResult(OrdersServiceResult result);

        void gotBookingError(OrdersServiceResult result);
    }

}
