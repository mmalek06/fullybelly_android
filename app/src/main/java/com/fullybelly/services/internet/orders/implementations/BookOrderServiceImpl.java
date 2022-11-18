package com.fullybelly.services.internet.orders.implementations;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.models.api.BookedOrder;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.base.BasePostInternetService;
import com.fullybelly.services.internet.converters.OrderConverter;
import com.fullybelly.services.internet.orders.interfaces.BookOrderService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.OrdersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public final class BookOrderServiceImpl extends BasePostInternetService implements BookOrderService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String BASE_ENDPOINT = "orders";

    private static final String EXT_ENDPOINT = "book";

    //endregion

    //region fields

    private BookOrderServiceImpl.ServiceCallback mCallback;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    private int mRestaurant;

    private int mMeal;

    private int mAmount;

    private String mBuyerEmail;

    //endregion

    //region constructor

    public BookOrderServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public BookOrderServiceImpl configure(
            int restaurant,
            int meal,
            int amount,
            String buyerEmail,
            ServiceCallback callback) {
        if (!canRun()) {
            return this;
        }

        mRestaurant = restaurant;
        mMeal = meal;
        mAmount = amount;
        mBuyerEmail = buyerEmail;
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

    //region internal methods

    @Override
    protected void internalRun() {
        mRunningTask = new BaseInternetService.AsyncRequest() {
            @Override
            protected void onPostExecute(InternalResult result) {
                OrderConverter converter = new OrderConverter();

                try {
                    OrdersServiceResult convertedResult = converter.convert(
                            result.getRequestResult(),
                            mSchemeStatic,
                            mRootStatic);

                    if (convertedResult.getError() != null) {
                        mCallback.gotBookingError(new OrdersServiceResult(convertedResult.getError(), ServiceResultCodes.VALIDATION_ERROR));
                    } else if (convertedResult.getOrder() == null) {
                        mCallback.gotBookingError(new OrdersServiceResult((BookedOrder)null, ServiceResultCodes.NO_DATA));
                    } else {
                        mCallback.gotBookingResult(convertedResult);
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
            new Uri.Builder().scheme(mScheme)
                             .encodedAuthority(mRoot)
                             .appendPath(API_VERSION)
                             .appendPath(BASE_ENDPOINT)
                             .appendEncodedPath(EXT_ENDPOINT + "/");

        return new URL(builder.toString());
    }

    @Override
    protected JSONObject getRequest() throws JSONException {
        final JSONObject requestData = new JSONObject();

        requestData.put("restaurant", mRestaurant);
        requestData.put("buyer_email", mBuyerEmail);
        requestData.put("meal", mMeal);
        requestData.put("amount", mAmount);

        return requestData;
    }

    @Override
    protected void onError(ServiceResultCodes errorCode) {
        mCallback.gotBookingError(new OrdersServiceResult((String)null, errorCode));
    }

    //endregion

}
