package com.fullybelly.viewModels.fragments;

import android.databinding.ObservableBoolean;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.events.activities.OfferMapActivityInitialized;
import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.models.api.FreshOrderInfo;
import com.fullybelly.services.local.interfaces.FavoritesService;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.local.interfaces.NotificationService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.OrdersMonitorServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.viewModels.base.BaseViewModel;
import com.fullybelly.views.activities.OfferDetailActivity;
import com.fullybelly.views.activities.OfferMapActivity;
import com.fullybelly.views.activities.OffersListActivity;
import com.fullybelly.views.activities.OrdersActivity;
import com.fullybelly.views.activities.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public final class TopToolbarViewModel extends BaseViewModel {

    //region enums

    private enum ToolbarAccessibleViews { UNKNOWN, OFFERS_LIST, OFFER_DETAILS, OFFER_MAP, SETTINGS, ORDERS_LIST }

    //endregion

    //region properties

    public ObservableBoolean hasNewOrder = new ObservableBoolean();

    public ObservableBoolean offerDetailActive = new ObservableBoolean();

    public ObservableBoolean listActive = new ObservableBoolean();

    public ObservableBoolean ordersActive = new ObservableBoolean();

    public ObservableBoolean settingsActive = new ObservableBoolean();

    public ObservableBoolean isFavorite = new ObservableBoolean();

    //endregion

    //region fields

    private final NavigationService mNavigationService;

    private final OrdersSessionsService mSessionsService;

    private final OrdersNotificationService mOrdersNotificationService;

    private final TextService mTextService;

    private final FavoritesService mFavoritesService;

    private final NotificationService mNotificationService;

    private ToolbarAccessibleViews activeView;

    private DetailedOffer mOffer;

    //endregion

    //region constructor

    @Inject
    public TopToolbarViewModel(NavigationService navigationService,
                               OrdersSessionsService sessionsService,
                               OrdersNotificationService ordersNotificationService,
                               TextService textService,
                               FavoritesService favoritesService,
                               NotificationService notificationService) {
        mNavigationService = navigationService;
        mSessionsService = sessionsService;
        mOrdersNotificationService = ordersNotificationService;
        mTextService = textService;
        mFavoritesService = favoritesService;
        mNotificationService = notificationService;

        setActiveView();
    }

    //endregion

    //region public methods

    public void init() {
        if (mOrdersNotificationService.userSawOrder()) {
            hasNewOrder.set(false);
        }
        if (mOrdersNotificationService.isOrderPending() && !mOrdersNotificationService.userSawOrder()) {
            hasNewOrder.set(true);
        }

        setUpMessaging();
    }

    public void navigateToOffers() {
        if (activeView == ToolbarAccessibleViews.OFFERS_LIST) {
            return;
        }

        mNavigationService.navigateToOffersList();
    }

    public void navigateToOrders() {
        if (activeView == ToolbarAccessibleViews.ORDERS_LIST) {
            return;
        }

        mOrdersNotificationService.markSeen();
        mNavigationService.navigateToOrders();
    }

    public void navigateToSettings() {
        if (activeView == ToolbarAccessibleViews.SETTINGS) {
            return;
        }

        mNavigationService.navigateToSettings();
    }

    public void navigateToMap() { mNavigationService.navigateToMap(mOffer); }

    public void addToFavorites() {
        mFavoritesService.addToFavorites(mOffer.getRestaurantId());
        mNotificationService.showNotification(mTextService.get(R.string.addedToFavorites));
        isFavorite.set(true);
    }

    public void removeFromFavorites() {
        mFavoritesService.removeFromFavorites(mOffer.getRestaurantId());
        mNotificationService.showNotification(mTextService.get(R.string.removedFromFavorites));
        isFavorite.set(false);
    }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrdersMonitorServiceResult(OrdersMonitorServiceResult event) {
        FreshOrderInfo orderInfo = event.getOrder();

        EventBus.getDefault().removeStickyEvent(event);

        if (orderInfo == null) {
            return;
        }

        mSessionsService.removeSessionToCheck(orderInfo.getSessionId());

        if (event.getResultCode() == ServiceResultCodes.SERVICE_OK) {
            hasNewOrder.set(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfferLoaded(DetailedOffer offer) {
        mOffer = offer;

        isFavorite.set(mFavoritesService.isRestaurantFavorite(mOffer.getRestaurantId()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOfferLoaded(OfferMapActivityInitialized result) {
        mOffer = result.getOffer();

        isFavorite.set(mFavoritesService.isRestaurantFavorite(mOffer.getRestaurantId()));
    }

    //endregion

    //region private methods

    private void setActiveView() {
        Class<?> activityClass = FullyBellyApplication.getInstance().getCurrentActivity().getClass();

        if (activityClass.equals(OffersListActivity.class)) {
            activeView = ToolbarAccessibleViews.OFFERS_LIST;

            offerDetailActive.set(false);
            listActive.set(true);
            ordersActive.set(false);
            settingsActive.set(false);
        } else if (activityClass.equals(OrdersActivity.class)) {
            activeView = ToolbarAccessibleViews.ORDERS_LIST;

            offerDetailActive.set(false);
            listActive.set(false);
            ordersActive.set(true);
            settingsActive.set(false);
        } else if (activityClass.equals(SettingsActivity.class)) {
            activeView = ToolbarAccessibleViews.SETTINGS;

            offerDetailActive.set(false);
            listActive.set(false);
            ordersActive.set(false);
            settingsActive.set(true);
        } else if (activityClass.equals(OfferDetailActivity.class) || activityClass.equals(OfferMapActivity.class)) {
            activeView = ToolbarAccessibleViews.OFFER_DETAILS;

            offerDetailActive.set(true);
            listActive.set(false);
            ordersActive.set(false);
            settingsActive.set(false);
        } else {
            activeView = ToolbarAccessibleViews.UNKNOWN;

            offerDetailActive.set(false);
            listActive.set(false);
            ordersActive.set(false);
            settingsActive.set(false);
        }
    }

    //endregion

}
