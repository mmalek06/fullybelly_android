package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.models.searcher.OrderingKind;
import com.fullybelly.models.searcher.SearcherSettings;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.SearcherSettingsService;

public final class SearcherSettingsServiceImpl extends BaseAppService implements SearcherSettingsService {


    //region constructor

    public SearcherSettingsServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void saveSettings(SearcherSettings settings) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        OrderingKind ordering = settings.getOrderingKind();

        editor.putString(getContext().getString(R.string.searcherSettingsPhrase), settings.getSearchTerm());
        editor.putBoolean(getContext().getString(R.string.searcherSettingsFavorites), settings.getFavorite());
        editor.putBoolean(getContext().getString(R.string.searcherSettingsAvailable), settings.getOnlyAvailable());
        editor.putBoolean(getContext().getString(R.string.searcherSettingsForceSearch), settings.shouldForceSearch());

        switch (ordering) {
            case CHEAPEST: editor.putBoolean(getContext().getString(R.string.searcherSettingsCheapest), true); break;
            case CLOSEST: editor.putBoolean(getContext().getString(R.string.searcherSettingsClosest), true); break;
            case PICKUP: editor.putBoolean(getContext().getString(R.string.searcherSettingsPickup), true); break;
        }

        editor.apply();
    }

    @Override
    public SearcherSettings getSettings() {
        SearcherSettings settings = new SearcherSettings();
        Context context = getContext();

        if (context == null) {
            return settings;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        boolean cheapestOrdering = preferences.getBoolean(getContext().getString(R.string.searcherSettingsCheapest), false);
        boolean closestOrdering = preferences.getBoolean(getContext().getString(R.string.searcherSettingsClosest), false);
        boolean pickupOrdering = preferences.getBoolean(getContext().getString(R.string.searcherSettingsPickup), false);

        if (cheapestOrdering) {
            settings.setOrderingKind(OrderingKind.CHEAPEST);
        } else if (closestOrdering) {
            settings.setOrderingKind(OrderingKind.CLOSEST);
        } else if (pickupOrdering) {
            settings.setOrderingKind(OrderingKind.PICKUP);
        }

        settings.setSearchTerm(preferences.getString(getContext().getString(R.string.searcherSettingsPhrase), null));
        settings.setFavorite(preferences.getBoolean(getContext().getString(R.string.searcherSettingsFavorites), false));
        settings.setOnlyAvailable(preferences.getBoolean(getContext().getString(R.string.searcherSettingsAvailable), false));
        settings.setForceSearch(preferences.getBoolean(getContext().getString(R.string.searcherSettingsForceSearch), false));

        return settings;
    }

    //endregion

}
