package com.fullybelly.services.local.interfaces;

public interface OrdersNotificationService {

    boolean userSawOrder();

    boolean isOrderPending();

    void markSeen();

    void markUnseen();

    void markOrderPending();

    void removeOrderPending();

}
