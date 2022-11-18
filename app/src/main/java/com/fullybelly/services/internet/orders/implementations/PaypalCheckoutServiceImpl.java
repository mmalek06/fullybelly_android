package com.fullybelly.services.internet.orders.implementations;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.base.BasePostInternetService;
import com.fullybelly.services.internet.converters.CheckoutConverter;
import com.fullybelly.services.internet.orders.interfaces.CheckoutService;
import com.fullybelly.services.results.CheckoutServiceResult;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class PaypalCheckoutServiceImpl extends BasePostInternetService implements CheckoutService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String ENDPOINT = "payments";

    private static final String ENDPOINT_EXT = "paypal-checkout";

    //endregion

    //region fields

    private CheckoutService.ServiceCallback mCallback;

    private String mPaymentId;

    private String mSessionId;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    //endregion

    //region constructor

    public PaypalCheckoutServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public CheckoutService configure(ServiceCallback callback, String paymentId, String sessionId) {
        mCallback = callback;
        mPaymentId = paymentId;
        mSessionId = sessionId;

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
                CheckoutConverter converter = new CheckoutConverter();

                try {
                    CheckoutServiceResult convertedResult = converter.convert(
                            result.getRequestResult(),
                            mSchemeStatic,
                            mRootStatic);

                    if (convertedResult.getError() != null) {
                        mCallback.gotCheckoutError(new CheckoutServiceResult(convertedResult.getCheckout(), ServiceResultCodes.VALIDATION_ERROR));
                    } else if (convertedResult.getCheckout() == null) {
                        mCallback.gotCheckoutError(new CheckoutServiceResult(null, ServiceResultCodes.NO_DATA));
                    } else if (convertedResult.getResultCode() != ServiceResultCodes.SERVICE_OK) {
                        mCallback.gotCheckoutError(new CheckoutServiceResult(convertedResult.getCheckout(), ServiceResultCodes.VALIDATION_ERROR));
                    } else {
                        mCallback.gotCheckoutResult(convertedResult);
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
    protected JSONObject getRequest() throws JSONException {
        final JSONObject requestData = new JSONObject();

        requestData.put("payment_id", mPaymentId);
        requestData.put("session_id", mSessionId);

        return requestData;
    }

    @Override
    protected void onError(ServiceResultCodes code) { mCallback.gotCheckoutError(new CheckoutServiceResult(null, code)); }

    //endregion

}
