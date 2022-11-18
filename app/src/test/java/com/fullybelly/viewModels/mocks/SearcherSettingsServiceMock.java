package com.fullybelly.viewModels.mocks;

import com.fullybelly.models.searcher.SearcherSettings;
import com.fullybelly.services.local.interfaces.SearcherSettingsService;

public final class SearcherSettingsServiceMock implements SearcherSettingsService {
    @Override
    public void saveSettings(SearcherSettings settings) {

    }

    @Override
    public SearcherSettings getSettings() {
        return new SearcherSettings();
    }
}
