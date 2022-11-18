package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.util.NavigationPayloadNames;
import com.fullybelly.views.activities.IntroActivity;
import com.fullybelly.views.activities.OfferDetailActivity;
import com.fullybelly.views.activities.OfferMapActivity;
import com.fullybelly.views.activities.OffersListActivity;
import com.fullybelly.views.activities.OrdersActivity;
import com.fullybelly.views.activities.SettingsActivity;

public final class NavigationServiceImpl extends BaseAppService implements NavigationService {

    //region constructor

    public NavigationServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void navigateToOffersList() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Intent listIntent = new Intent(getContext(), OffersListActivity.class);

        getContext().startActivity(listIntent);
    }

    @Override
    public void navigateToIntro() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Intent introIntent = new Intent(context, IntroActivity.class);

        context.startActivity(introIntent);
    }

    @Override
    public void navigateToRestaurantPanel() {
        String language = mApplication.getCountryCode();
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://panel.fullybelly." + language));

        getContext().startActivity(viewIntent);
    }

    @Override
    public void navigateToOfferDetails(int restaurantId, int mealId, Double latitude, Double longitude) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Intent detailsIntent = new Intent(getContext(), OfferDetailActivity.class);

        detailsIntent.putExtra(NavigationPayloadNames.RESTAURANT_ID, restaurantId);
        detailsIntent.putExtra(NavigationPayloadNames.MEAL_ID, mealId);
        detailsIntent.putExtra(NavigationPayloadNames.LATITUDE, latitude);
        detailsIntent.putExtra(NavigationPayloadNames.LONGITUDE, longitude);
        getContext().startActivity(detailsIntent);
    }

    @Override
    public void navigateToMap(DetailedOffer offer) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Intent mapIntent = new Intent(getContext(), OfferMapActivity.class);

        mapIntent.putExtra(NavigationPayloadNames.OFFER, offer);
        getContext().startActivity(mapIntent);
    }

    @Override
    public void navigateToOrders() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Intent ordersIntent = new Intent(getContext(), OrdersActivity.class);

        getContext().startActivity(ordersIntent);
    }

    @Override
    public void navigateToSettings() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);

        getContext().startActivity(settingsIntent);
    }

    //endregion

}
