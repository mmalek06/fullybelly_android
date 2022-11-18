package com.fullybelly.viewModels.mocks;

import android.location.Location;

import com.fullybelly.models.searcher.SearcherSettings;
import com.fullybelly.services.internet.offers.OffersSearcherService;

public final class OffersSearcherServiceMock implements OffersSearcherService {
    public SearcherSettings settings;
    public Location location;
    public int page;
    public boolean isSearching;

    @Override
    public OffersSearcherService configure(
            SearcherSettings settings,
            Location location,
            int page,
            ServiceCallback callback) {
        this.settings = settings;
        this.location = location;
        this.page = page;

        return this;
    }

    @Override
    public void run() {
        isSearching = true;
    }

    @Override
    public void tearDown() {

    }
}
