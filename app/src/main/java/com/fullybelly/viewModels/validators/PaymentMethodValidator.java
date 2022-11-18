package com.fullybelly.viewModels.validators;

import com.fullybelly.services.local.interfaces.PaymentService;

public interface PaymentMethodValidator {

    boolean validate(PaymentService.PaymentType paymentType);

}
