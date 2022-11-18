package com.fullybelly.services.local.interfaces;

import com.fullybelly.models.api.DetailedOffer;

public interface NavigationService {

    void navigateToOffersList();

    void navigateToIntro();

    void navigateToRestaurantPanel();

    void navigateToOfferDetails(int restaurantId, int mealId, Double latitude, Double longitude);

    void navigateToMap(DetailedOffer offer);

    void navigateToOrders();

    void navigateToSettings();

}
