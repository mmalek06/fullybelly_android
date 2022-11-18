package com.fullybelly.services.internet.orders.interfaces;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.OrdersListServiceResult;

public interface OrdersListService extends InternetService {

    OrdersListService configure(
            String buyerEmail,
            String sessionIds,
            ServiceCallback callback);

    interface ServiceCallback {
        void gotOrders(OrdersListServiceResult result);

        void gotError(OrdersListServiceResult result);
    }

}
