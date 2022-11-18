package com.fullybelly.viewModels;

import com.fullybelly.models.api.TermsOfUse;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.services.results.TermsOfUseServiceResult;
import com.fullybelly.viewModels.activities.LoginViewModel;
import com.fullybelly.viewModels.mocks.DialogServiceMock;
import com.fullybelly.viewModels.mocks.InternetTermsOfUseMock;
import com.fullybelly.viewModels.mocks.LocalTermsOfUseMock;
import com.fullybelly.viewModels.mocks.NavigationServiceMock;
import com.fullybelly.viewModels.mocks.TextServiceMock;
import com.fullybelly.viewModels.mocks.UserDetailsServiceMock;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewModelTest {
    private LoginViewModel sut;
    private UserDetailsServiceMock detailsServiceMock;
    private DialogServiceMock dialogServiceMock;
    private TextServiceMock textServiceMock;
    private NavigationServiceMock navigationServiceMock;
    private InternetTermsOfUseMock internetTermsOfUseMock;
    private LocalTermsOfUseMock localTermsOfUseMock;

    @Before
    public void initialize() {
        detailsServiceMock = new UserDetailsServiceMock();
        dialogServiceMock = new DialogServiceMock();
        textServiceMock = new TextServiceMock();
        navigationServiceMock = new NavigationServiceMock();
        internetTermsOfUseMock = new InternetTermsOfUseMock();
        localTermsOfUseMock = new LocalTermsOfUseMock();
        localTermsOfUseMock.terms = new TermsOfUse(1, "asdf", "asdf", true);
        sut = new LoginViewModel(
                detailsServiceMock,
                dialogServiceMock,
                textServiceMock,
                navigationServiceMock,
                null,
                internetTermsOfUseMock,
                localTermsOfUseMock);
    }

    @Test
    public void whenRunningInit_thenVmStartsLoading() throws Exception {
        sut.init();

        assertTrue(sut.isLoading.get());
    }

    @Test
    public void givenTermsOfUseAreNotAccepted_whenGuestLoginIsRequested_thenNavigationIsNotCalled() {
        localTermsOfUseMock.terms = new TermsOfUse(1, "asdf", "asdf", false);

        sut.guestLogin();

        assertEquals(NavigationServiceMock.NavigationChoice.LOGIN, navigationServiceMock.currentView);
    }

    @Test
    public void givenTermsOfUseAreAccepted_whenGuestLoginIsRequested_thenNavigationIsCalled() {
        sut.guestLogin();

        assertEquals(NavigationServiceMock.NavigationChoice.OFFERS_LIST, navigationServiceMock.currentView);
    }

    @Test
    public void whenTOUDialogIsRequested_thenTOUDialogShouldAppear() {
        sut.showTermsAndConditionsPopup();

        assertEquals(DialogServiceMock.DialogKind.REQUIRED_INFO, dialogServiceMock.currentDialog);
    }

    @Test
    public void whenCallingAfterLogin_thenNavigationSwitchesToOffersList() {
        sut.afterLogin();

        assertEquals(NavigationServiceMock.NavigationChoice.OFFERS_LIST, navigationServiceMock.currentView);
    }

    @Test
    public void whenTermsAndConditionsIsLoaded_thenVmStopsLoading() {
        sut.gotTermsAndConditions(
                new TermsOfUseServiceResult(
                        localTermsOfUseMock.terms,
                        ServiceResultCodes.SERVICE_OK));

        assertFalse(sut.isLoading.get());
    }

    @Test
    public void whenTermsAndConditionsAreNullAndWrongResultCodeIsSet_thenVmThrowsException() {
        try {
            sut.gotTermsAndConditions(new TermsOfUseServiceResult(
                    null,
                    ServiceResultCodes.SERVICE_OK));
            fail("should throw exception");
        } catch (NullPointerException ex) { }
    }

    @Test
    public void whenTermsAndConditionsAreNullAndCorrectResultCodeIsSet_thenVmShouldNotThrowException() {
        try {
            sut.gotTermsAndConditions(new TermsOfUseServiceResult(
                    null,
                    ServiceResultCodes.BAD_DATA_RECEIVED));
        } catch (NullPointerException ex) {
            fail("should not throw exception");
        }
    }
}