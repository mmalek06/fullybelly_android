package com.fullybelly.services.local.interfaces;

import java.util.List;

public interface FavoritesService {

    void addToFavorites(int restaurantId);

    void removeFromFavorites(int restaurantId);

    List<Integer> getFavorites();

    boolean isRestaurantFavorite(int restaurantId);

}
