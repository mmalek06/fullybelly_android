package com.fullybelly.services.internet.offers;

import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.OfferDetailsServiceResult;

public interface OfferDetailsService extends InternetService {

    OfferDetailsService configure(
            ServiceCallback callback,
            Double latitude,
            Double longitude,
            int restaurantId,
            int mealId);

    interface ServiceCallback {
        void gotOffer(OfferDetailsServiceResult result);

        void gotError(OfferDetailsServiceResult result);
    }

}
