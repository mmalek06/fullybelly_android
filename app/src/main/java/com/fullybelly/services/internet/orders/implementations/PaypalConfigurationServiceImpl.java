package com.fullybelly.services.internet.orders.implementations;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.models.api.PayPalPaymentsConfiguration;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.PaypalConfigurationConverter;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.PaypalConfigurationServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class PaypalConfigurationServiceImpl extends BaseInternetService implements ConfigurationService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String ENDPOINT = "payments";

    private static final String ENDPOINT_EXT = "configuration-paypal";

    //endregion

    //region fields

    private P24ConfigurationServiceImpl.ServiceCallback mCallback;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    //endregion

    //region constructor

    public PaypalConfigurationServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public ConfigurationService configure(ServiceCallback callback) {
        mCallback = callback;

        setAddresses();

        return this;
    }

    @Override
    public void tearDown() {
        mRunningTask.cancel(true);

        mCallback = null;
    }

    //endregion

    //region methods

    @Override
    protected void internalRun() throws IOException, JSONException, MetadataException {
        mRunningTask = new BaseInternetService.AsyncRequest() {
            @Override
            protected void onPostExecute(InternalResult result) {
                PaypalConfigurationConverter converter = new PaypalConfigurationConverter();

                try {
                    PaypalConfigurationServiceResult convertedResult = converter.convert(
                            result.getRequestResult(),
                            mSchemeStatic,
                            mRootStatic);

                    if (convertedResult.getError() != null) {
                        mCallback.gotConfigurationError(new PaypalConfigurationServiceResult(convertedResult.getError(), ServiceResultCodes.VALIDATION_ERROR));
                    } else if (convertedResult.getConfiguration() == null) {
                        mCallback.gotConfigurationError(new PaypalConfigurationServiceResult((PayPalPaymentsConfiguration) null, ServiceResultCodes.NO_DATA));
                    } else {
                        mCallback.gotConfigurationResult(convertedResult);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                    onError(ServiceResultCodes.BAD_DATA_RECEIVED);
                }
            }
        };
        mRunningTask.execute();
    }

    @Override
    protected URL buildUrl() throws MalformedURLException {
        Uri.Builder builder =
                new Uri.Builder()
                        .scheme(mScheme)
                        .encodedAuthority(mRoot)
                        .appendPath(API_VERSION)
                        .appendPath(ENDPOINT)
                        .appendPath(ENDPOINT_EXT)
                        .appendEncodedPath(mApplication.getCountryCode() + "/");

        return new URL(builder.toString());
    }

    @Override
    protected void onError(ServiceResultCodes code) { mCallback.gotConfigurationError(new PaypalConfigurationServiceResult((String)null, code)); }

    //endregion

}
