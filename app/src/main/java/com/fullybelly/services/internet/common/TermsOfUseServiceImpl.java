package com.fullybelly.services.internet.common;

import android.net.Uri;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.TermsOfUseConverter;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.services.results.TermsOfUseServiceResult;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TermsOfUseServiceImpl extends BaseInternetService implements TermsOfUseService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String ENDPOINT = "terms-and-conditions";

    //endregion

    //region fields

    private ServiceCallback mCallback;

    private AsyncRequest mRunningTask;

    private String mCountryCode;

    //endregion

    //region constructor

    public TermsOfUseServiceImpl(FullyBellyApplication application) {
        super(application);
    }

    //endregion

    //region public methods

    @Override
    public TermsOfUseServiceImpl configure(ServiceCallback callback, String countryCode) {
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
                    mCallback.gotTermsAndConditionsError(new TermsOfUseServiceResult(null, result.getResultCode()));
                } else {
                    TermsOfUseConverter converter = new TermsOfUseConverter();

                    try {
                        TermsOfUseServiceResult convertedResult = converter.convert(
                                result.getRequestResult(),
                                mSchemeStatic,
                                mRootStatic);

                        if (convertedResult.getTerms() == null) {
                            mCallback.gotTermsAndConditionsError(new TermsOfUseServiceResult(null, ServiceResultCodes.NO_DATA));
                        } else {
                            mCallback.gotTermsAndConditions(convertedResult);
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
        mCallback.gotTermsAndConditionsError(new TermsOfUseServiceResult(null, code));
    }

    //endregion

}
