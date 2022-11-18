package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.local.interfaces.OrdersNotificationService;

public final class OrdersNotificationServiceMock implements OrdersNotificationService {
    @Override
    public boolean userSawOrder() {
        return false;
    }

    @Override
    public boolean isOrderPending() {
        return false;
    }

    @Override
    public void markSeen() {

    }

    @Override
    public void markUnseen() {

    }

    @Override
    public void markOrderPending() {

    }

    @Override
    public void removeOrderPending() {

    }
}
