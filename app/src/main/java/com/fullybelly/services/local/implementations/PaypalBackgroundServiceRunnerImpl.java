package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.models.api.PayPalPaymentsConfiguration;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.PaypalBackgroundServiceRunner;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.PaypalConfigurationServiceResult;
import com.fullybelly.services.results.ServiceResult;
import com.fullybelly.services.util.MetadataHelper;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;

public final class PaypalBackgroundServiceRunnerImpl implements PaypalBackgroundServiceRunner, ConfigurationService.ServiceCallback {

    //region constants

    private final static String TEST_MODE_ENABLED_KEY = "com.fullybelly.PAYMENT_TEST_MODE_ENABLED";

    //endregion

    //region fields

    private final FullyBellyApplication mApplication;

    private final ConfigurationServiceFactory mConfigurationServiceFactory;

    private final DialogService mDialogService;

    private final TextService mTextService;

    //endregion

    //region constructor

    public PaypalBackgroundServiceRunnerImpl(
            FullyBellyApplication application,
            ConfigurationServiceFactory configurationServiceFactory,
            DialogService dialogService,
            TextService textService) {
        mApplication = application;
        mConfigurationServiceFactory = configurationServiceFactory;
        mDialogService = dialogService;
        mTextService = textService;
    }

    //endregion

    //region public methods

    @Override
    public void run() {
        if (!shouldRun()) {
            return;
        }

        mConfigurationServiceFactory.get().configure(this).run();
    }

    @Override
    public void stop() {
        if (!shouldRun()) {
            return;
        }

        Context ctx = mApplication.getCurrentActivity();

        ctx.stopService(new Intent(ctx, PayPalService.class));
    }

    @Override
    public void gotConfigurationResult(ServiceResult result) {
        Context ctx = mApplication.getCurrentActivity();
        Bundle metadata = MetadataHelper.getMetadata(ctx);

        if (metadata == null) {
            handleError();

            return;
        }

        PayPalConfiguration configuration = getConfig(result, metadata);
        Intent serviceIntent = new Intent(ctx, PayPalService.class);

        serviceIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        ctx.startService(serviceIntent);
    }

    @Override
    public void gotConfigurationError(ServiceResult result) {
        handleError();
    }

    //endregion

    //region methods

    private boolean shouldRun() {
        String country = mApplication.getCountryCode();

        switch (country) {
            case "de":
                return true;
            default:
                return false;
        }
    }

    private PayPalConfiguration getConfig(ServiceResult result, Bundle metadata) {
        PayPalPaymentsConfiguration payPalPaymentsConfiguration = ((PaypalConfigurationServiceResult)result).getConfiguration();
        boolean testModeEnabled = metadata.getBoolean(TEST_MODE_ENABLED_KEY);

        PayPalConfiguration config =
                new PayPalConfiguration()
                        .clientId(payPalPaymentsConfiguration.getClientId())
                        .rememberUser(true);

        if (testModeEnabled) {
            config.environment(PayPalConfiguration.ENVIRONMENT_SANDBOX);
        } else {
            config.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION);
        }

        return config;
    }

    private void handleError() {
        mDialogService.showDialog(
                mTextService.get(R.string.orderBookingFailed),
                mTextService.get(R.string.tryAgainLater),
                DialogService.DialogLevel.WARN);
    }

    //endregion

}
