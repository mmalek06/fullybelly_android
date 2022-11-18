package com.fullybelly.services.local.implementations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.models.api.BookedOrder;
import com.fullybelly.models.api.P24Configuration;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.PaymentService;
import com.fullybelly.services.results.P24ConfigurationServiceResult;
import com.fullybelly.services.util.MetadataHelper;

import pl.dialcom24.p24lib.P24;
import pl.dialcom24.p24lib.P24Config;
import pl.dialcom24.p24lib.P24Payment;

public final class P24PaymentServiceImpl extends BaseAppService implements PaymentService {

    //region constants

    private final static String REQ_CODE = "com.fullybelly.P24_REQ_CODE";

    private final static String URL_STATUS_KEY = "com.fullybelly.P24_URL_STATUS";

    private final static String TEST_MODE_ENABLED_KEY = "com.fullybelly.PAYMENT_TEST_MODE_ENABLED";

    //endregion

    //region private fields

    private final P24Config p24config;

    private final P24 p24;

    private final P24Payment p24payment;

    //endregion

    //region constructor

    public P24PaymentServiceImpl(FullyBellyApplication application) {
        super(application);

        p24config = new P24Config();
        p24 = new P24(p24config);
        p24payment = new P24Payment();

        configureOnBuild();
    }

    //endregion

    //region public methods

    @Override
    public <T> void makePayment(
            int transactionValue,
            String currency,
            String transactionTitle,
            String userEmail,
            PaymentType paymentType,
            BookedOrder bookedOrder,
            T configuration) {
        P24Configuration p24Configuration = ((P24ConfigurationServiceResult)configuration).getConfiguration();
        configureOnPayment(
                transactionValue,
                currency,
                bookedOrder.getSessionId(),
                transactionTitle,
                userEmail,
                paymentType,
                p24Configuration);

        Intent pIntent = p24.getPaymentIntent(getContext(), p24payment);
        Bundle metadata = MetadataHelper.getMetadata(getContext());

        if (metadata == null) {
            return;
        }

        int reqCode = metadata.getInt(REQ_CODE);

        ((AppCompatActivity)getContext()).startActivityForResult(pIntent, reqCode);
    }

    @Override
    public boolean canMakePayment() { return MetadataHelper.getMetadata(getContext()) != null; }

    @Override
    public boolean shouldInitializeWithPaymentMethod() { return true; }

    //endregion

    //region private methods

    private void configureOnBuild() {
        Bundle metadata = MetadataHelper.getMetadata(getContext());

        if (metadata == null) {
            return;
        }

        String urlStatus = metadata.getString(URL_STATUS_KEY);

        p24config.enableTestMode(false);

        boolean testModeEnabled = metadata.getBoolean(TEST_MODE_ENABLED_KEY);

        if (testModeEnabled) {
            p24config.enableTestMode(true);
        }

        p24config.setStoreLoginData(true);
        p24config.setUseMobileStyles(true);
        p24config.setReadSmsPasswords(true);
        p24config.setDontAskForSaveLoginData(false);
        p24config.setAutoFinishTimeout(3000);
        p24config.setTimeLimit(2);
        p24payment.setP24UrlStatus(urlStatus);
    }

    private void configureOnPayment(
            int transactionValue,
            String currency,
            String sessionId,
            String transactionTitle,
            String userEmail,
            PaymentType paymentType,
            P24Configuration p24Configuration) {
        p24payment.setSessionId(sessionId);
        p24payment.setAmount(transactionValue);
        p24payment.setCurrency(currency);
        p24payment.setClientCountry("pl");
        p24payment.setClientEmail(userEmail);
        p24payment.setTransferLabel(transactionTitle);
        p24config.setCrc(p24Configuration.getP24crc());
        p24config.setMerchantId(Integer.valueOf(p24Configuration.getP24merchant()));

        switch (paymentType) {
            case CREDIT_CARD: p24payment.setMethod("152"); break;
            case BANK_TRANSFER: p24payment.setMethod(""); break;
        }
    }

    //endregion

}
