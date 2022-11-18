package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.FavoritesService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FavoritesServiceImpl extends BaseAppService implements FavoritesService {

    //region constructor

    public FavoritesServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void addToFavorites(int restaurantId) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> favRestaurants = preferences.getStringSet(getContext().getString(R.string.settingsFavRestaurants), new HashSet<String>());

        favRestaurants.add(String.valueOf(restaurantId));
        editor.putStringSet(getContext().getString(R.string.settingsFavRestaurants), favRestaurants);
        editor.apply();
    }

    @Override
    public void removeFromFavorites(int restaurantId) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> favRestaurants = preferences.getStringSet(getContext().getString(R.string.settingsFavRestaurants), new HashSet<String>());

        if (favRestaurants.contains(String.valueOf(restaurantId))) {
            favRestaurants.remove(String.valueOf(restaurantId));
        }

        editor.putStringSet(getContext().getString(R.string.settingsFavRestaurants), favRestaurants);
        editor.apply();
    }

    @Override
    public List<Integer> getFavorites() {
        Context context = getContext();

        if (context == null) {
            return new ArrayList<>();
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        Set<String> favRestaurants = preferences.getStringSet(getContext().getString(R.string.settingsFavRestaurants), new HashSet<String>());
        ArrayList<Integer> favsList = new ArrayList<>();

        for (String id : favRestaurants) {
            favsList.add(Integer.valueOf(id));
        }

        return favsList;
    }

    @Override
    public boolean isRestaurantFavorite(int restaurantId) {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        Set<String> favRestaurants = preferences.getStringSet(getContext().getString(R.string.settingsFavRestaurants), new HashSet<String>());

        return favRestaurants.contains(String.valueOf(restaurantId));
    }

    //endregion

}
