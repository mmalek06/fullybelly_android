package com.fullybelly.services.internet.offers;

import android.location.Location;

import com.fullybelly.models.searcher.SearcherSettings;
import com.fullybelly.services.internet.base.InternetService;
import com.fullybelly.services.results.OffersServiceResult;

public interface OffersSearcherService extends InternetService {

    OffersSearcherService configure(
            SearcherSettings settings,
            Location location,
            int page,
            ServiceCallback callback);

    interface ServiceCallback {
        void gotOffers(OffersServiceResult result);

        void gotError(OffersServiceResult result);
    }

}
