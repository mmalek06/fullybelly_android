package com.fullybelly.services.local.interfaces;

import com.fullybelly.models.searcher.SearcherSettings;

public interface SearcherSettingsService {

    void saveSettings(SearcherSettings settings);

    SearcherSettings getSettings();

}
