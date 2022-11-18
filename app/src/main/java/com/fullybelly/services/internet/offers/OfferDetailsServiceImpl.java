package com.fullybelly.services.internet.offers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.internet.converters.OfferDetailConverter;
import com.fullybelly.services.results.OfferDetailsServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

public class OfferDetailsServiceImpl extends BaseInternetService implements OfferDetailsService {

    //region constants

    private static final String API_VERSION = "v2";

    private static final String ENDPOINT = "offers";

    private static final String SEGMENT = "detail";

    private static final String NOLOC = "no-location";

    //endregion

    //region fields

    private OfferDetailsServiceImpl.ServiceCallback mCallback;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    private Double mLatitude;

    private Double mLongitude;

    private int mRestaurantId;

    private int mMealId;

    //endregion

    //region constructor

    public OfferDetailsServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public OfferDetailsServiceImpl configure(
            OfferDetailsService.ServiceCallback callback,
            Double latitude,
            Double longitude,
            int restaurantId,
            int mealId) {
        if (!canRun()) {
            return this;
        }

        mCallback = callback;
        mLatitude = latitude;
        mLongitude = longitude;
        mRestaurantId = restaurantId;
        mMealId = mealId;

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
                    mCallback.gotError(new OfferDetailsServiceResult(null, result.getResultCode()));
                } else {
                    OfferDetailConverter converter = new OfferDetailConverter();

                    try {
                        OfferDetailsServiceResult convertedResult = converter.convert(
                                result.getRequestResult(),
                                mSchemeStatic,
                                mRootStatic);

                        if (convertedResult.getOffer() == null) {
                            mCallback.gotError(new OfferDetailsServiceResult(null, ServiceResultCodes.NO_DATA));
                        } else {
                            mCallback.gotOffer(convertedResult);
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
    protected void onError(ServiceResultCodes errorCode) {
        mCallback.gotError(new OfferDetailsServiceResult(null, errorCode));
    }

    @Override
    protected URL buildUrl() throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder().scheme(mScheme)
                .encodedAuthority(mRoot)
                .appendPath(API_VERSION)
                .appendPath(ENDPOINT)
                .appendPath(SEGMENT);

        if (mLatitude != null && mLongitude != null) {
            builder = builder.appendPath(String.valueOf(mLatitude))
                             .appendPath(String.valueOf(mLongitude));
        } else {
            builder = builder.appendPath(NOLOC);
        }

        builder = builder.appendPath(String.valueOf(mRestaurantId))
                         .appendEncodedPath(String.valueOf(mMealId) + "/");

        return new URL(builder.toString());
    }

    //endregion

}
