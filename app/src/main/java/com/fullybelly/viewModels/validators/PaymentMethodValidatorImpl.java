package com.fullybelly.viewModels.validators;

import com.fullybelly.services.local.interfaces.PaymentService;

public class PaymentMethodValidatorImpl implements PaymentMethodValidator {

    //region public methods

    @Override
    public boolean validate(PaymentService.PaymentType paymentType) {
        return paymentType == PaymentService.PaymentType.NONE ? false : true;
    }

    //endregion

}
