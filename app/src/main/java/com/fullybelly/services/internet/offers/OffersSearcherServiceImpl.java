package com.fullybelly.services.internet.offers;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.models.searcher.SearcherSettings;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.OffersListConverter;
import com.fullybelly.services.local.interfaces.FavoritesService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.OffersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public final class OffersSearcherServiceImpl extends BaseInternetService implements OffersSearcherService {

    //region constants

    private static final String API_VERSION = "v2";

    private static final String ENDPOINT = "offers";

    private static final String NO_LOCATION = "no-location";

    private static final int LIMIT = 10;

    //endregion

    //region fields

    private final FavoritesService mFavoritesService;

    private ServiceCallback mCallback;

    private AsyncTask<Void, Void, InternalResult> mRunningTask;

    private SearcherSettings mSettings;

    private Location mLocation;

    private int mPage;

    //endregion

    //region constructor

    public OffersSearcherServiceImpl(FullyBellyApplication application, FavoritesService favoritesService) {
        super(application);

        mFavoritesService = favoritesService;
    }

    //endregion

    //region public methods

    @Override
    public OffersSearcherServiceImpl configure(
            SearcherSettings settings,
            Location location,
            int page,
            ServiceCallback callback) {
        if (!canRun()) {
            return this;
        }

        mSettings = settings;
        mLocation = location;
        mPage = page;
        mCallback = callback;

        super.setAddresses();

        return this;
    }

    @Override
    public void tearDown() {
        if (mRunningTask != null) {
            mRunningTask.cancel(true);
        }

        mCallback = null;
    }

    //endregion

    //region internal methods

    @Override
    protected void internalRun() {
        mRunningTask = new BaseInternetService.AsyncRequest() {
            @Override
            protected void onPostExecute(InternalResult result) {
                if (mCallback == null) {
                    return;
                }
                if (result.getResultCode() != ServiceResultCodes.SERVICE_OK) {
                    mCallback.gotError(new OffersServiceResult(null, null, null, result.getResultCode()));
                } else {
                    OffersListConverter converter = new OffersListConverter();

                    try {
                        OffersServiceResult convertedResult = converter.convert(
                                result.getRequestResult(),
                                mSchemeStatic,
                                mRootStatic,
                                mLocation == null ? null : mLocation.getLatitude(),
                                mLocation == null ? null : mLocation.getLongitude());

                        if ((convertedResult.getOffers() == null || convertedResult.getOffers().isEmpty()) && mPage == 0) {
                            mCallback.gotError(new OffersServiceResult(null, null, null, ServiceResultCodes.NO_DATA));
                        } else {
                            mCallback.gotOffers(convertedResult);
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
        Double longitude = mLocation == null ? null : mLocation.getLongitude();
        Double latitude = mLocation == null ? null : mLocation.getLatitude();
        String searchTerm = mSettings.getSearchTerm();
        Uri.Builder builder =
                new Uri.Builder().scheme(mScheme)
                        .encodedAuthority(mRoot)
                        .appendPath(API_VERSION)
                        .appendPath(ENDPOINT);

        if (latitude == null || longitude == null) {
            builder = builder.appendEncodedPath(NO_LOCATION + "/");
        } else {
            builder = builder.appendPath(String.valueOf(latitude))
                             .appendEncodedPath(String.valueOf(longitude) + "/");
        }
        if (searchTerm != null && !searchTerm.equals("")) {
            try {
                searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
            } catch (UnsupportedEncodingException ex) { }

            builder = builder.appendEncodedPath(searchTerm + "/");
        }

        int distance = mSettings.getSearchDist();
        String ordering = mSettings.getOrderingKind().toString();
        boolean favFilter = mSettings.getFavorite();
        boolean onlyAvailable = mSettings.getOnlyAvailable();

        if (distance > -1) {
            builder = builder.appendQueryParameter("distance", String.valueOf(distance));
        }
        if (ordering != null) {
            builder = builder.appendQueryParameter("ordering", ordering);
        }
        if (favFilter) {
            builder = builder.appendQueryParameter("favorites", "true")
                    .appendQueryParameter("fav-restaurants", getFavs());
        }
        if (onlyAvailable) {
            builder = builder.appendQueryParameter("only-available", "true");
        }

        builder = builder.appendQueryParameter("offset", String.valueOf(mPage * LIMIT))
                         .appendQueryParameter("limit", String.valueOf(LIMIT))
                         .appendQueryParameter("country-code", mApplication.getCountryCode());

        return new URL(builder.toString());
    }

    @Override
    protected void onError(ServiceResultCodes errorCode) {
        mCallback.gotError(new OffersServiceResult(null, null, null, errorCode));
    }

    private String getFavs() {
        List<Integer> favsList = mFavoritesService.getFavorites();
        StringBuilder builder = new StringBuilder();

        for (int id : favsList) {
            builder.append(String.valueOf(id) + ",");
        }

        String favs = builder.toString();
        String result = "";

        if (favs.length() > 0) {
            result = favs.substring(0, favs.length() - 1);
        }

        return result;
    }

    //endregion

}
