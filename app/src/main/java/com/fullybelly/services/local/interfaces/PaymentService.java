package com.fullybelly.services.local.interfaces;

import com.fullybelly.models.api.BookedOrder;

public interface PaymentService {

    enum PaymentType { NONE, CREDIT_CARD, BANK_TRANSFER, PAYPAL }

    <T> void makePayment(
            int transactionValue,
            String currency,
            String transactionTitle,
            String userEmail,
            PaymentType paymentType,
            BookedOrder bookedOrder,
            T configuration);

    boolean canMakePayment();

    boolean shouldInitializeWithPaymentMethod();

}
