package com.fullybelly.services.internet.common;

import android.net.Uri;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.PartnersConverter;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.PartnersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class PartnersServiceImpl extends BaseInternetService implements PartnersService {

    //region constants

    private static final String API_VERSION = "v2";

    private static final String ENDPOINT = "partners";

    //endregion

    //region fields

    private ServiceCallback mCallback;

    private String mCountryCode;

    private AsyncRequest mRunningTask;

    //endregion

    //region constructor

    public PartnersServiceImpl(FullyBellyApplication application) {
        super(application);
    }

    //endregion

    //region public methods

    @Override
    public PartnersService configure(ServiceCallback callback, String countryCode) {
        if (!canRun()) {
            return this;
        }

        mCallback = callback;
        mCountryCode = countryCode;

        setAddresses();

        return this;
    }

    @Override
    public void tearDown() {
        mRunningTask.cancel(true);
        mCallback = null;
    }

    //endregion

    //region internal methods

    @Override
    protected void internalRun() throws IOException, JSONException, MetadataException {
        mRunningTask = new BaseInternetService.AsyncRequest() {
            @Override
            protected void onPostExecute(InternalResult result) {
                if (result.getResultCode() != ServiceResultCodes.SERVICE_OK) {
                    mCallback.gotPartnersError(new PartnersServiceResult(null, result.getResultCode()));
                } else {
                    PartnersConverter converter = new PartnersConverter(mSchemeStatic, mRootStatic);

                    try {
                        PartnersServiceResult convertedResult = converter.convert(
                                result.getRequestResult(),
                                mSchemeStatic,
                                mRootStatic);

                        if (convertedResult.getPartners() == null || convertedResult.getPartners().size() == 0) {
                            mCallback.gotPartnersError(new PartnersServiceResult(null, ServiceResultCodes.NO_DATA));
                        } else {
                            mCallback.gotPartners(convertedResult);
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                        onError(ServiceResultCodes.BAD_DATA_RECEIVED);
                    }
                }
            }
        };
        mRunningTask.execute();
    }

    @Override
    protected URL buildUrl() throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder().scheme(mScheme)
                .encodedAuthority(mRoot)
                .appendPath(API_VERSION)
                .appendPath(ENDPOINT)
                .appendEncodedPath(mCountryCode + "/");

        return new URL(builder.toString());
    }

    @Override
    protected void onError(ServiceResultCodes code) {
        mCallback.gotPartnersError(new PartnersServiceResult(null, code));
    }

    //endregion

}
