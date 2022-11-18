package com.fullybelly.viewModels;

import android.location.Location;

import com.fullybelly.events.location.LocationChanged;
import com.fullybelly.events.location.LocationProviderStatusChanged;
import com.fullybelly.events.location.LocationStatus;
import com.fullybelly.events.location.ProviderStatus;
import com.fullybelly.models.api.Offer;
import com.fullybelly.services.internet.offers.OffersSearcherService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.LocationStateCheckerService;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.local.interfaces.SearcherSettingsService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.OffersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.viewModels.activities.OffersListViewModel;
import com.fullybelly.viewModels.mocks.DialogServiceMock;
import com.fullybelly.viewModels.mocks.LocationStateCheckerServiceMock;
import com.fullybelly.viewModels.mocks.NavigationServiceMock;
import com.fullybelly.viewModels.mocks.OffersSearcherServiceMock;
import com.fullybelly.viewModels.mocks.SearcherSettingsServiceMock;
import com.fullybelly.viewModels.mocks.TextServiceMock;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OffersListViewModelTest {
    private OffersListViewModel sut;
    private DialogService dialogServiceMock;
    private TextService textServiceMock;
    private NavigationService navigationServiceMock;
    private OffersSearcherService searcherServiceMock;
    private SearcherSettingsService searcherSettingsServiceMock;
    private LocationStateCheckerService stateCheckerService;

    @Before
    public void initialize() {
        dialogServiceMock = new DialogServiceMock();
        textServiceMock = new TextServiceMock();
        navigationServiceMock = new NavigationServiceMock();
        searcherServiceMock = new OffersSearcherServiceMock();
        searcherSettingsServiceMock = new SearcherSettingsServiceMock();
        stateCheckerService = new LocationStateCheckerServiceMock();
        sut = new OffersListViewModel(
                dialogServiceMock,
                textServiceMock,
                navigationServiceMock,
                searcherServiceMock,
                searcherSettingsServiceMock,
                stateCheckerService);

        sut.init();
    }

    @Test
    public void whenRunningInit_thenDistanceShouldBeAnyDistanceAndVmShouldBeSearching() {
        assertEquals("sample text", sut.distanceValue.get());
        assertEquals(true, sut.loading.get());
    }

    @Test
    public void whenGotOffers_thenShouldNotBeSearchingAndShouldContainOffers() {
        List<Offer> offers = getOffers();

        sut.gotOffers(new OffersServiceResult(
                offers,
                0d,
                0d,
                ServiceResultCodes.SERVICE_OK));

        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.loading.get());
        assertEquals(offers.size(), sut.getOffers().size());
    }

    @Test
    public void whenGotNullOffers_thenShouldThrowException() {
        try {
            sut.gotOffers(new OffersServiceResult(
                    null,
                    0d,
                    0d,
                    ServiceResultCodes.SERVICE_OK
            ));

            fail("should throw exception");
        } catch (NullPointerException ex) { }
    }

    @Test
    public void whenGotNoDataError_thenShouldNotBeSearchingAndShouldShowMessages() {
        sut.gotError(new OffersServiceResult(null, 0d, 0d, ServiceResultCodes.NO_DATA));

        assertEquals(true, sut.showMessages.get());
        assertEquals(true, sut.showNoOffersMessage.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.loading.get());
    }

    @Test
    public void whenGotNoPermissionsError_thenShouldNotBeSearchingAndShouldNotShowMessages() {
        sut.gotError(new OffersServiceResult(null, 0d, 0d, ServiceResultCodes.NO_PERMISSIONS));

        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.loading.get());
    }

    @Test
    public void whenGotNoProviderAvailableError_thenShouldNotBeSearchingAndShouldShowMessages() {
        sut.gotError(new OffersServiceResult(null, 0d, 0d, ServiceResultCodes.NO_PROVIDER_AVAILABLE));

        assertEquals(true, sut.showMessages.get());
        assertEquals(true, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(false, sut.loading.get());
    }

    @Test
    public void whenGotWrongArgumentError_thenShouldNotBeSearchingAndShouldNotShowMessages() {
        sut.gotError(new OffersServiceResult(null, 0d, 0d, ServiceResultCodes.WRONG_ARGUMENT));

        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.loading.get());
    }

    @Test
    public void whenGotImproperlyConfiguredError_thenShouldNotBeSearchingAndShouldNotShowMessages() {
        sut.gotError(new OffersServiceResult(null, 0d, 0d, ServiceResultCodes.IMPROPERLY_CONFIGURED));

        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.loading.get());
    }

    @Test
    public void whenGotBadDataError_thenShouldNotBeSearchingAndShouldNotShowMessages() {
        sut.gotError(new OffersServiceResult(null, 0d, 0d, ServiceResultCodes.BAD_DATA_RECEIVED));

        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.loading.get());
    }

    @Test
    public void whenLoadingNextPageWithMaxDistance_thenDistanceShouldBeMinusOne() {
        sut.setSearchDist(1000);
        sut.loadNextPage(0);

        assertEquals(-1, ((OffersSearcherServiceMock)searcherServiceMock).settings.getSearchDist());
    }

    @Test
    public void whenLoadingNextPage_thenNextPageShouldBeAsRequested() {
        sut.loadNextPage(10);

        assertEquals(10, ((OffersSearcherServiceMock)searcherServiceMock).page);
    }

    @Test
    public void whenRequestingSearchWithNoLocationAndPersistedSettings_thenShouldNotBeLoadingAndShouldShowMessage() {
        ((LocationStateCheckerServiceMock)stateCheckerService).returnHasPerms = false;
        ((LocationStateCheckerServiceMock)stateCheckerService).returnCanGetLocation = false;

        sut.search();

        assertEquals(false, sut.loading.get());
        assertEquals(0, sut.getOffers().size());
        assertEquals(true, sut.showMessages.get());
        assertEquals(true, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showNoOffersMessage.get());
    }

    @Test
    public void whenRequestingSearchWhileWaitingForLocation_thenShouldNotSearch() {
        ((LocationStateCheckerServiceMock)stateCheckerService).returnHasPerms = true;
        ((LocationStateCheckerServiceMock)stateCheckerService).returnCanGetLocation = true;
        ((OffersSearcherServiceMock)searcherServiceMock).isSearching = false;

        sut.search();

        assertEquals(true, sut.loading.get());
        assertEquals(0, sut.getOffers().size());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(false, ((OffersSearcherServiceMock)searcherServiceMock).isSearching);
    }

    @Test
    public void whenRequestingSearchWithSearchPhrase_thenShouldSearch() {
        ((OffersSearcherServiceMock)searcherServiceMock).isSearching = false;

        sut.setSearchTerm("Poland");
        sut.search();

        assertEquals(true, sut.loading.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(true, ((OffersSearcherServiceMock)searcherServiceMock).isSearching);
    }

    @Test
    public void whenRequestingSearchWithLocationPresent_thenShouldSearch() {
        ((OffersSearcherServiceMock)searcherServiceMock).isSearching = false;
        Location location = new Location("network");

        location.setLatitude(0);
        location.setLongitude(0);
        sut.onLocationChanged(new LocationChanged(location, LocationStatus.OK));
        sut.search();

        assertEquals(true, sut.loading.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(true, ((OffersSearcherServiceMock)searcherServiceMock).isSearching);
    }

    @Test
    public void whenLocationProviderStatusIsChangedAndHasNoOffers_thenSearch() {
        ((OffersSearcherServiceMock)searcherServiceMock).isSearching = false;
        Location location = new Location("network");

        location.setLatitude(0);
        location.setLongitude(0);
        sut.onLocationChanged(new LocationChanged(location, LocationStatus.OK));
        sut.onLocationProviderStatusChanged(new LocationProviderStatusChanged(ProviderStatus.ENABLED));

        assertEquals(true, sut.loading.get());
        assertEquals(false, sut.showMessages.get());
        assertEquals(false, sut.showLocationDisabledMessage.get());
        assertEquals(false, sut.showNoOffersMessage.get());
        assertEquals(true, ((OffersSearcherServiceMock)searcherServiceMock).isSearching);
    }

    private List<Offer> getOffers() {
        return Arrays.asList(
                new Offer(1, 1, "donut in a butter", "nourl", 1d, 1, 1d, 50d, "PLN", "20:00", "20:59", "asdf", true),
                new Offer(2, 2, "butter in a donut", "nourl", 1d, 1, 1d, 20d, "PLN", "20:00", "21:59", "asdf", true),
                new Offer(3, 3, "butter with bacon", "nourl", 1d, 1, 1d, 1d, "PLN", "20:00", "22:59", "asdf", true)
        );
    }
}
