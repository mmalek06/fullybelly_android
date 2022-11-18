package com.fullybelly.viewModels.mocks;

import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.services.local.interfaces.NavigationService;

public final class NavigationServiceMock implements NavigationService {
    public enum NavigationChoice { LOGIN, OFFERS_LIST, OFFER_DETAIL, MAP, ORDERS, SETTINGS }

    public NavigationChoice currentView = NavigationChoice.LOGIN;

    @Override
    public void navigateToOffersList() {
        currentView = NavigationChoice.OFFERS_LIST;
    }

    @Override
    public void navigateToIntro() {

    }

    @Override
    public void navigateToRestaurantPanel() { }

    @Override
    public void navigateToOfferDetails(int restaurantId, int mealId, Double latitude, Double longitude) {
        currentView = NavigationChoice.OFFER_DETAIL;
    }

    @Override
    public void navigateToMap(DetailedOffer offer) {
        currentView = NavigationChoice.MAP;
    }

    @Override
    public void navigateToOrders() {
        currentView = NavigationChoice.ORDERS;
    }

    @Override
    public void navigateToSettings() {
        currentView = NavigationChoice.SETTINGS;
    }
}
