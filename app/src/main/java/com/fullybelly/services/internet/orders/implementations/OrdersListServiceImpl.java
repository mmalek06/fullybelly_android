package com.fullybelly.services.internet.orders.implementations;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.OrdersListConverter;
import com.fullybelly.services.internet.orders.interfaces.OrdersListService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.OrdersListServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

public final class OrdersListServiceImpl extends BaseInternetService implements OrdersListService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String BASE_ENDPOINT = "orders";

    private static final String EXT_ENDPOINT = "list";

    //endregion

    //region fields

    private OrdersListServiceImpl.ServiceCallback mCallback;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    private String mBuyerEmail;

    private String mSessionIds;

    //endregion

    //region constructor

    public OrdersListServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public OrdersListServiceImpl configure(
            String buyerEmail,
            String sessionIds,
            ServiceCallback callback) {
        if (!canRun()) {
            return this;
        }

        mBuyerEmail = buyerEmail;
        mSessionIds = sessionIds;
        mCallback = callback;

        super.setAddresses();

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
    protected void internalRun() {
        mRunningTask = new BaseInternetService.AsyncRequest() {
            @Override
            protected void onPostExecute(InternalResult result) {
                if (result.getResultCode() != ServiceResultCodes.SERVICE_OK) {
                    mCallback.gotError(new OrdersListServiceResult(null, result.getResultCode()));
                } else {
                    OrdersListConverter converter = new OrdersListConverter();

                    try {
                        OrdersListServiceResult convertedResult = converter.convert(
                                result.getRequestResult(),
                                mSchemeStatic,
                                mRootStatic);

                        if (convertedResult.getOrders() == null) {
                            mCallback.gotError(new OrdersListServiceResult(null, ServiceResultCodes.NO_DATA));
                        } else {
                            mCallback.gotOrders(convertedResult);
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
        Uri.Builder builder =
                new Uri.Builder().scheme(mScheme)
                        .encodedAuthority(mRoot)
                        .appendPath(API_VERSION)
                        .appendPath(BASE_ENDPOINT)
                        .appendPath(EXT_ENDPOINT)
                        .appendEncodedPath(mBuyerEmail + "/")
                        .appendQueryParameter("sessions", mSessionIds);
        String url = builder.toString();

        return new URL(url);
    }

    @Override
    protected void onError(ServiceResultCodes errorCode) {
        mCallback.gotError(new OrdersListServiceResult(null, errorCode));
    }

    //endregion

}
