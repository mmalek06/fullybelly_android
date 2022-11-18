package com.fullybelly.services.internet.orders.interfaces;

import com.fullybelly.services.results.OrdersMonitorServiceResult;

public interface OrderMonitorService  {

    OrderMonitorService configure(String sessionId);

    OrdersMonitorServiceResult getResults();

    void run();

}
