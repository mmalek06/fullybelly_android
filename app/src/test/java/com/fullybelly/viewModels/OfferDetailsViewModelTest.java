package com.fullybelly.viewModels;

import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.services.internet.offers.OfferDetailsService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.OfferDetailsServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.viewModels.fragments.OfferDetailsViewModel;
import com.fullybelly.viewModels.mocks.DialogServiceMock;
import com.fullybelly.viewModels.mocks.OfferDetailsServiceMock;
import com.fullybelly.viewModels.mocks.OrdersNotificationServiceMock;
import com.fullybelly.viewModels.mocks.OrdersSessionsServiceMock;
import com.fullybelly.viewModels.mocks.TextServiceMock;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OfferDetailsViewModelTest {
    private OfferDetailsViewModel sut;
    private DialogService dialogServiceMock;
    private TextService textServiceMock;
    private OfferDetailsService detailsServiceMock;
    private OrdersSessionsService sessionsServiceMock;
    private OrdersNotificationService notificationServiceMock;

    @Before
    public void initialize() {
        dialogServiceMock = new DialogServiceMock();
        textServiceMock = new TextServiceMock();
        detailsServiceMock = new OfferDetailsServiceMock();
        sessionsServiceMock = new OrdersSessionsServiceMock();
        notificationServiceMock = new OrdersNotificationServiceMock();
        sut = new OfferDetailsViewModel(
                textServiceMock,
                dialogServiceMock,
                detailsServiceMock,
                null);
    }

    @Test
    public void whenOfferIsReceived_thenVmHasItsData() {
        DetailedOffer offer = getOffer();

        sut.gotOffer(
                new OfferDetailsServiceResult(
                        offer,
                        ServiceResultCodes.SERVICE_OK
                )
        );

        assertEquals(offer.getAddress(), sut.getAddress());
        assertEquals(offer.getDescription(), sut.getDescription());
        assertEquals(offer.getMealDescription(), sut.getMealDescription());
    }

    private DetailedOffer getOffer() {
        //return new DetailedOffer(1, 1, "offer", "", "", 2d, "district", "address", 9, 20d, 1d, "PLN", "20:00", "23:59", "description", 0d, 0d, "meal description", true);
        return null;
    }
}
