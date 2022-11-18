package com.fullybelly.services.internet.orders.implementations;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.models.api.P24Configuration;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.P24ConfigurationConverter;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.P24ConfigurationServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class P24ConfigurationServiceImpl extends BaseInternetService implements ConfigurationService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String ENDPOINT = "payments";

    private static final String ENDPOINT_EXT = "configuration-p24";

    //endregion

    //region fields

    private P24ConfigurationServiceImpl.ServiceCallback mCallback;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    //endregion

    //region constructor

    public P24ConfigurationServiceImpl(FullyBellyApplication application) { super(application); }

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
                P24ConfigurationConverter converter = new P24ConfigurationConverter();

                try {
                    P24ConfigurationServiceResult convertedResult = converter.convert(
                            result.getRequestResult(),
                            mSchemeStatic,
                            mRootStatic);

                    if (convertedResult.getError() != null) {
                        mCallback.gotConfigurationError(new P24ConfigurationServiceResult(convertedResult.getError(), ServiceResultCodes.VALIDATION_ERROR));
                    } else if (convertedResult.getConfiguration() == null) {
                        mCallback.gotConfigurationError(new P24ConfigurationServiceResult((P24Configuration)null, ServiceResultCodes.NO_DATA));
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
                        .appendEncodedPath(ENDPOINT_EXT + "/");

        return new URL(builder.toString());
    }

    @Override
    protected void onError(ServiceResultCodes code) { mCallback.gotConfigurationError(new P24ConfigurationServiceResult((String)null, code)); }

    //endregion

}
