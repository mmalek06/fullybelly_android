package com.fullybelly.services.local.interfaces;

import com.fullybelly.events.offerDetail.PaymentResult;

public interface PaymentResultConsumer {

    void consume(PaymentResult result);

}
