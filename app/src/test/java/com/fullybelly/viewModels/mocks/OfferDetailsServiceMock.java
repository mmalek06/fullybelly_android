package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.internet.offers.OfferDetailsService;

public final class OfferDetailsServiceMock implements OfferDetailsService {
    @Override
    public OfferDetailsService configure(
            ServiceCallback callback,
            Double latitude,
            Double longitude,
            int restaurantId,
            int mealId) {
        return this;
    }

    @Override
    public void run() {

    }

    @Override
    public void tearDown() {

    }
}
