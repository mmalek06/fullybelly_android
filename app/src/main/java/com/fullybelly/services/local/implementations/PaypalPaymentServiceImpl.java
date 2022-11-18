package com.fullybelly.services.local.implementations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.models.api.BookedOrder;
import com.fullybelly.models.api.PayPalPaymentsConfiguration;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.BookedOrderCacheService;
import com.fullybelly.services.local.interfaces.PaymentService;
import com.fullybelly.services.results.PaypalConfigurationServiceResult;
import com.fullybelly.services.util.MetadataHelper;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

public class PaypalPaymentServiceImpl extends BaseAppService implements PaymentService {

    //region constants

    private final static String REQ_CODE = "com.fullybelly.PAYPAL_REQ_CODE";

    private final static String TEST_MODE_ENABLED_KEY = "com.fullybelly.PAYMENT_TEST_MODE_ENABLED";

    //endregion

    //region fields

    private final BookedOrderCacheService mBookedOrderCacheService;

    //endregion

    //region constructor

    public PaypalPaymentServiceImpl(FullyBellyApplication application,
                                    BookedOrderCacheService bookedOrderCacheService) {
        super(application);

        mBookedOrderCacheService = bookedOrderCacheService;
    }

    //endregion

    //region public methods

    @Override
    public <T> void makePayment(int transactionValue, String currency, String transactionTitle, String userEmail, PaymentType paymentType, BookedOrder bookedOrder, T configuration) {
        Bundle metadata = MetadataHelper.getMetadata(getContext());

        if (metadata == null) {
            return;
        }

        PayPalPaymentsConfiguration payPalPaymentsConfiguration = ((PaypalConfigurationServiceResult)configuration).getConfiguration();
        PayPalConfiguration config = configureOnPayment(payPalPaymentsConfiguration, metadata);
        BigDecimal realTransactionValue = new BigDecimal(String.valueOf(transactionValue)).divide(new BigDecimal("100"));
        PayPalPayment payment = new PayPalPayment(realTransactionValue,
                                                  currency,
                                                  transactionTitle,
                                                  PayPalPayment.PAYMENT_INTENT_SALE);
        Intent activityIntent = new Intent(getContext(), PaymentActivity.class);
        int reqCode = metadata.getInt(REQ_CODE);

        activityIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        activityIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        mBookedOrderCacheService.put(bookedOrder);
        ((AppCompatActivity)getContext()).startActivityForResult(activityIntent, reqCode);
    }

    @Override
    public boolean canMakePayment() { return MetadataHelper.getMetadata(getContext()) != null; }

    @Override
    public boolean shouldInitializeWithPaymentMethod() { return false; }

    //endregion

    //region methods

    private PayPalConfiguration configureOnPayment(PayPalPaymentsConfiguration payPalConfiguration, Bundle metadata) {
        boolean testModeEnabled = metadata.getBoolean(TEST_MODE_ENABLED_KEY);

        PayPalConfiguration config =
                new PayPalConfiguration()
                        .clientId(payPalConfiguration.getClientId())
                        .rememberUser(true);

        if (testModeEnabled) {
            config.environment(PayPalConfiguration.ENVIRONMENT_SANDBOX);
        } else {
            config.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION);
        }

        return config;
    }

    //endregion

}
