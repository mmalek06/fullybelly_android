package com.fullybelly.services.local.interfaces;

public interface PaymentMethodService {

    boolean isDefault(PaymentService.PaymentType paymentType);

    boolean hasDefault();

    void setDefault(PaymentService.PaymentType paymentType);

}
