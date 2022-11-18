package com.fullybelly.viewModels.activities;

import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.location.Location;

import com.android.databinding.library.baseAdapters.BR;
import com.fullybelly.R;
import com.fullybelly.events.location.LocationChanged;
import com.fullybelly.events.location.LocationProviderStatusChanged;
import com.fullybelly.events.location.ProviderStatus;
import com.fullybelly.models.api.Offer;
import com.fullybelly.models.searcher.OrderingKind;
import com.fullybelly.models.searcher.SearcherSettings;
import com.fullybelly.services.internet.offers.OffersSearcherService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.LocationStateCheckerService;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.local.interfaces.SearcherSettingsService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.OffersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.viewModels.base.BaseViewModel;
import com.fullybelly.viewModels.fragments.OffersListItemViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class OffersListViewModel extends BaseViewModel implements OffersSearcherService.ServiceCallback {

    //region constants and enums

    private static int MIN_DISTANCE = 1;

    private static int MAX_DISTANCE = 50;

    private enum SearchState {
        WAITING_FOR_LOCATION,
        GOT_ANY_LOCATION,
        LOADING,
        IDLE
    }

    //endregion

    //region properties

    public ObservableBoolean loading = new ObservableBoolean();

    public ObservableBoolean showMessages = new ObservableBoolean();

    public ObservableBoolean showNoOffersMessage = new ObservableBoolean();

    public ObservableBoolean showLocationDisabledMessage = new ObservableBoolean();

    public ObservableField<String> distanceValue = new ObservableField<>();

    public ObservableField<String> hintText = new ObservableField<>();

    private ObservableList<OffersListItemViewModel> mOffers = new ObservableArrayList<>();

    @Bindable
    public int getMaxDistance() { return MAX_DISTANCE; }

    @Bindable
    public String getSearchTerm() { return mSearcherSettings.getSearchTerm(); }
    public void setSearchTerm(String searchTerm) { mSearcherSettings.setSearchTerm(searchTerm); }

    @Bindable
    public int getSearchDist() { return mSearcherSettings.getSearchDist(); }
    public void setSearchDist(int value) {
        if (value < MIN_DISTANCE) {
            value = MIN_DISTANCE;
        }

        mSearcherSettings.setSearchDist(value);
        notifyPropertyChanged(BR.searchDist);
    }

    @Bindable
    public boolean getFavorite() { return mSearcherSettings.getFavorite(); }
    public void setFavorite(boolean value) { mSearcherSettings.setFavorite(value); }

    @Bindable
    public boolean getOnlyAvailable() { return mSearcherSettings.getOnlyAvailable(); }
    public void setOnlyAvailable(boolean value) { mSearcherSettings.setOnlyAvailable(value); }

    @Bindable
    public boolean getCheapest() { return mSearcherSettings.getOrderingKind() == OrderingKind.CHEAPEST; }
    public void setCheapest(boolean value) {
        if (value) {
            mSearcherSettings.setOrderingKind(OrderingKind.CHEAPEST);

            notifyChange();
        } else {
            mSearcherSettings.setOrderingKind(OrderingKind.NONE);
        }
    }

    @Bindable
    public boolean getClosest() { return mSearcherSettings.getOrderingKind() == OrderingKind.CLOSEST; }
    public void setClosest(boolean value) {
        if (value) {
            mSearcherSettings.setOrderingKind(OrderingKind.CLOSEST);

            notifyChange();
        } else {
            mSearcherSettings.setOrderingKind(OrderingKind.NONE);
        }
    }

    @Bindable
    public boolean getPickupTime() { return mSearcherSettings.getOrderingKind() == OrderingKind.PICKUP; }
    public void setPickupTime(boolean value) {
        if (value) {
            mSearcherSettings.setOrderingKind(OrderingKind.PICKUP);

            notifyChange();
        } else {
            mSearcherSettings.setOrderingKind(OrderingKind.NONE);
        }
    }

    //endregion

    //region fields

    private DialogService mDialogService;

    private TextService mTextService;

    private NavigationService mNavigationService;

    private OffersSearcherService mOffersSearcherService;

    private SearcherSettingsService mSearcherSettingsService;

    private LocationStateCheckerService mLocationStateChecker;

    private SearcherSettings mSearcherSettings;

    private SearchState mCurrentSearchState;

    private Location mPendingChangedLocation;

    private Location mCurrentLocation;

    private boolean mSearchEnqueued;

    //endregion

    //region constructor

    @Inject
    public OffersListViewModel(DialogService dialogService,
                               TextService textService,
                               NavigationService navigationService,
                               OffersSearcherService offersSearcherService,
                               SearcherSettingsService searcherSettingsService,
                               LocationStateCheckerService locationStateCheckerService) {
        mDialogService = dialogService;
        mTextService = textService;
        mNavigationService = navigationService;
        mOffersSearcherService = offersSearcherService;
        mSearcherSettingsService = searcherSettingsService;
        mLocationStateChecker = locationStateCheckerService;
    }

    //endregion

    //region public methods

    public void init() {
        mSearcherSettings = mSearcherSettingsService.getSettings();
        mCurrentSearchState =
                mCurrentLocation != null || mPendingChangedLocation != null ?
                        SearchState.IDLE :
                        SearchState.WAITING_FOR_LOCATION;

        setSearchDist(MAX_DISTANCE);
        hintText.set(mTextService.get(R.string.searchTerm));

        updateDistanceValue();
        setUpMessaging();

        if (shouldSearch()) {
            search();
        }
    }

    @Override
    public void kill() {
        super.kill();
        mOffers.clear();
        mOffersSearcherService.tearDown();
    }

    @Override
    public void gotOffers(OffersServiceResult result) {
        addOffers(result);
        showMessages.set(false);
        showNoOffersMessage.set(false);
        loading.set(false);
        mSearcherSettingsService.saveSettings(mSearcherSettings);

        mCurrentSearchState = SearchState.IDLE;
    }

    @Override
    public void gotError(OffersServiceResult result) {
        ServiceResultCodes resultCode = result.getResultCode();
        mCurrentSearchState = SearchState.IDLE;

        mOffers.clear();
        loading.set(false);
        showNoOffersMessage.set(false);
        showLocationDisabledMessage.set(false);

        switch (resultCode) {
            case NO_DATA:
                showMessages.set(true);
                showNoOffersMessage.set(true);
                return;
            case NO_PERMISSIONS:
                mDialogService.showDialog(
                        mTextService.get(R.string.permissionsDenied),
                        mTextService.get(R.string.addPermissionsAndTryAgain),
                        DialogService.DialogLevel.WARN);
                return;
            case NO_PROVIDER_AVAILABLE:
                showMessages.set(true);
                showLocationDisabledMessage.set(true);
                return;
            case WRONG_ARGUMENT:
            case IMPROPERLY_CONFIGURED:
            case BAD_DATA_RECEIVED:
                mDialogService.showDialog(
                        mTextService.get(R.string.genericError),
                        mTextService.get(R.string.tryAgainLater),
                        DialogService.DialogLevel.WARN);
                return;
        }
    }

    public void loadNextPage(int page) {
        SearcherSettings settings = mSearcherSettings;

        if (mSearcherSettings.getSearchDist() >= MAX_DISTANCE) {
            settings = (SearcherSettings) mSearcherSettings.clone();

            if (settings != null) {
                settings.setSearchDist(-1);
            }
        }

        mOffersSearcherService.configure(
                settings,
                mCurrentLocation,
                page,
                this).run();
    }

    public ObservableList<OffersListItemViewModel> getOffers() {
        return mOffers;
    }

    public OffersListItemViewModel getOffer(int position) {
        return mOffers.get(position);
    }

    public void search() {
        if (mCurrentSearchState == SearchState.LOADING) {
            return;
        }

        updateCurrentLocation();
        tryRunSearch();
    }

    public void hideHintText() {
        hintText.set(null);
    }

    public void showHintText() {
        hintText.set(mTextService.get(R.string.searchTerm));
    }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onLocationChanged(LocationChanged event) {
        if (mCurrentSearchState == SearchState.WAITING_FOR_LOCATION) {
            mCurrentSearchState = SearchState.GOT_ANY_LOCATION;
            Location location = event.getLocation();

            if (location != null) {
                mCurrentLocation = location;
            }
        } else if (shouldSubstituteCurrentLocation(event.getLocation())) {
            mCurrentLocation = event.getLocation();
            mPendingChangedLocation = null;
        } else {
            mPendingChangedLocation = event.getLocation();
        }
        if (mSearchEnqueued) {
            search();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onLocationProviderStatusChanged(LocationProviderStatusChanged event) {
        if (event.getStatus() == ProviderStatus.ENABLED && mOffers.size() == 0) {
            search();
        }
    }

    //endregion

    //region private methods

    private boolean shouldSubstituteCurrentLocation(Location location) {
        boolean noOffers = mOffers.size() == 0 && mCurrentSearchState != SearchState.LOADING;
        boolean moreAccurate = false;

        if (mCurrentLocation != null && location != null) {
            moreAccurate = location.getAccuracy() > mCurrentLocation.getAccuracy();
        } else if (mCurrentLocation == null && location != null) {
            moreAccurate = true;
        }

        return noOffers && moreAccurate;
    }

    private void addOffers(OffersServiceResult result) {
        List<OffersListItemViewModel> viewModels = new ArrayList<>();

        for (Offer offer : result.getOffers()) {
            OffersListItemViewModel offerVm = new OffersListItemViewModel(mNavigationService, mTextService);

            offerVm.setOffer(offer);
            offerVm.setLocation(result.getLatitude(), result.getLongitude());
            viewModels.add(offerVm);
        }

        mOffers.addAll(viewModels);
    }

    private void loadOffers() {
        mCurrentSearchState = SearchState.LOADING;

        mOffers.clear();
        loadNextPage(0);
    }

    private void tryRunSearch() {
        if (mCurrentLocation != null) {
            mSearchEnqueued = false;

            loading.set(true);
            hideEnableLocationMessage();
            loadOffers();
        } else {
            if (mSearcherSettings.getSearchTerm() != null && !mSearcherSettings.getSearchTerm().equals("")) {
                mSearchEnqueued = false;

                loading.set(true);
                hideEnableLocationMessage();
                loadOffers();
            } else {
                if (!mLocationStateChecker.hasPermissions() || !mLocationStateChecker.canGetLocation()) {
                    loading.set(false);
                    mOffers.clear();
                    showEnableLocationMessage();
                } else {
                    if (mSearchEnqueued) {
                        mSearchEnqueued = false;

                        loading.set(false);
                        showEnableLocationMessage();
                    } else {
                        mSearchEnqueued = true;

                        loading.set(true);
                        hideEnableLocationMessage();
                    }
                }
            }
        }
    }

    private void updateCurrentLocation() {
        if (mPendingChangedLocation != null) {
            mCurrentLocation = mPendingChangedLocation;
            mPendingChangedLocation = null;
        }
    }

    public void updateDistanceValue() {
        int searchDist = getSearchDist();

        if (searchDist == MAX_DISTANCE) {
            distanceValue.set(mTextService.get(R.string.anyDistance));
        } else {
            distanceValue.set(String.valueOf(searchDist) + " km");
        }
    }

    private void showEnableLocationMessage() {
        showMessages.set(true);
        showLocationDisabledMessage.set(true);
        showNoOffersMessage.set(false);
    }

    private void hideEnableLocationMessage() {
        showMessages.set(false);
        showLocationDisabledMessage.set(false);
        showNoOffersMessage.set(false);
    }

    private boolean shouldSearch() {
        return mOffers.size() == 0;
    }

    //endregion

}
