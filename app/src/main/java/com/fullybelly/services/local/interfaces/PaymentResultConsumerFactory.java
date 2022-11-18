package com.fullybelly.services.local.interfaces;

import com.fullybelly.events.offerDetail.PaymentResult;

public interface PaymentResultConsumerFactory {

    PaymentResultConsumer get(PaymentResult result);

}
