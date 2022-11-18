package com.fullybelly.viewModels.activities;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.events.activities.NotificationRequiredAccepted;
import com.fullybelly.events.activities.NotificationRequiredDeclined;
import com.fullybelly.models.api.TermsOfUse;
import com.fullybelly.services.internet.common.PartnersService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.IntroService;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.services.results.PartnersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.services.results.TermsOfUseServiceResult;
import com.fullybelly.viewModels.base.BaseViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

public final class LoginViewModel extends BaseViewModel implements
        FacebookCallback<LoginResult>,
        PartnersService.ServiceCallback,
        com.fullybelly.services.internet.common.TermsOfUseServiceImpl.ServiceCallback {

    //region properties

    public ObservableBoolean isLoading = new ObservableBoolean();

    public ObservableBoolean showImpressum = new ObservableBoolean();

    //endregion

    //region private fields

    private ObservableArrayList<String> mPartnersLogos = new ObservableArrayList<>();

    private boolean mShowListActionEnqueued;

    private UserDetailsService mDetailsService;

    private DialogService mDialogService;

    private TextService mTextService;

    private NavigationService mNavigationService;

    private PartnersService mPartnersService;

    private IntroService mIntroService;

    private com.fullybelly.services.internet.common.TermsOfUseService mExternalTermsOfUseService;

    private com.fullybelly.services.local.interfaces.TermsOfUseService mLocalTermsOfUseService;

    //endregion

    //region constructor

    @Inject
    public LoginViewModel(UserDetailsService detailsService,
                          DialogService dialogService,
                          TextService textService,
                          NavigationService navigationService,
                          PartnersService partnersService,
                          IntroService introService,
                          com.fullybelly.services.internet.common.TermsOfUseService externalTermsOfUseService,
                          com.fullybelly.services.local.interfaces.TermsOfUseService localTermsOfUseService) {
        mDetailsService = detailsService;
        mDialogService = dialogService;
        mTextService = textService;
        mNavigationService = navigationService;
        mPartnersService = partnersService;
        mIntroService = introService;
        mExternalTermsOfUseService = externalTermsOfUseService;
        mLocalTermsOfUseService = localTermsOfUseService;
    }

    //endregion

    //region public methods

    public void init() {
        setUpMessaging();
        isLoading.set(true);

        if (validateCountryCode()) {
            if (FullyBellyApplication.getInstance().getCountryCode().equalsIgnoreCase("de")) {
                showImpressum.set(true);
            }

            mExternalTermsOfUseService.configure(
                    this,
                    FullyBellyApplication.getInstance().getCountryCode()).run();
            mPartnersService.configure(
                    this,
                    FullyBellyApplication.getInstance().getCountryCode()).run();
        }
    }

    public ObservableList<String> getPartnersLogotypes() { return mPartnersLogos; }

    @Override
    public void kill() {
        super.kill();
        mExternalTermsOfUseService.tearDown();
        mPartnersService.tearDown();
    }

    public void guestLogin() {
        if (!validateCountryCode()) {
            return;
        }

        if (shouldShowTermsOfUsePopup()) {
            mShowListActionEnqueued = true;
            String text = mLocalTermsOfUseService.getTermsOfUse().getText();

            if (text != null) {
                showTermsAndConditionsPopupWithText(text);
            } else {
                afterLogin();
            }
        } else {
            afterLogin();
        }
    }

    public void restaurantLogin() {
        if (!validateCountryCode()) {
            return;
        }

        mNavigationService.navigateToRestaurantPanel();
    }

    public void showTermsAndConditionsPopup() {
        String terms = mLocalTermsOfUseService.getTermsOfUse().getText();

        if (terms != null) {
            showTermsAndConditionsPopupWithText(terms);
        }
    }

    public void showImpressumPopup() {
        String impressum = mLocalTermsOfUseService.getTermsOfUse().getImpressum();

        if (impressum != null) {
            showImpressumWithText(impressum);
        }
    }

    public void afterLogin() {
        String country = FullyBellyApplication.getInstance().getCountryCode();

//        if (country.equals("pl") && !mIntroService.userSawIntro()) {
//            mIntroService.setUserSawIntro();
//            mNavigationService.navigateToIntro();
//        } else {
            mNavigationService.navigateToOffersList();
//        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    if (object.has("email")) {
                        mDetailsService.setEmailAddress(object.getString("email"));
                    }
                }
                catch(JSONException e) { }
                finally { afterLogin(); }
            }
        });
        Bundle parameters = new Bundle();

        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() { }

    @Override
    public void onError(FacebookException exception) {
        mDialogService.showToast(mTextService.get(R.string.fbLoginError));
    }

    @Override
    public void gotTermsAndConditions(TermsOfUseServiceResult result) {
        isLoading.set(false);

        if (result.getResultCode() != ServiceResultCodes.SERVICE_OK) {
            return;
        }

        int currentTermsId = mLocalTermsOfUseService.getLastIdentifier();
        int newId = result.getTerms().getId();

        if (newId != currentTermsId) {
            mLocalTermsOfUseService.setTermsAndConditions(result.getTerms());
        }
    }

    @Override
    public void gotTermsAndConditionsError(TermsOfUseServiceResult result) {
        isLoading.set(false);
    }

    @Override
    public void gotPartners(PartnersServiceResult result) {
        if (result.getResultCode() != ServiceResultCodes.SERVICE_OK) {
            return;
        }

        mPartnersLogos.clear();
        mPartnersLogos.addAll(result.getPartners());
    }

    @Override
    public void gotPartnersError(PartnersServiceResult result) { }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionAccept(NotificationRequiredAccepted event) {
        TermsOfUse terms = mLocalTermsOfUseService.getTermsOfUse();
        TermsOfUse newTerms = new TermsOfUse(terms.getId(), terms.getText(), terms.getImpressum(), true);

        mLocalTermsOfUseService.setTermsAndConditions(newTerms);

        if (mShowListActionEnqueued) {
            mShowListActionEnqueued = false;

            afterLogin();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionDecline(NotificationRequiredDeclined event) {
        TermsOfUse terms = mLocalTermsOfUseService.getTermsOfUse();
        TermsOfUse newTerms = new TermsOfUse(terms.getId(), terms.getText(), terms.getImpressum(), false);

        mLocalTermsOfUseService.setTermsAndConditions(newTerms);
        mDialogService.showToast(mTextService.get(R.string.acceptTermsPrompt));
    }

    //endregion

    //region private methods

    private boolean shouldShowTermsOfUsePopup() {
        TermsOfUse terms = mLocalTermsOfUseService.getTermsOfUse();

        return terms.getId() > 0 && !terms.isAccepted();
    }

    private void showTermsAndConditionsPopupWithText(String terms) {
        mDialogService.showRequiredInfoDialog(
                null,
                terms,
                true,
                true);
    }

    private void showImpressumWithText(String text) {
        mDialogService.showRequiredInfoDialog(
                null,
                text,
                true,
                false);
    }

    private boolean validateCountryCode() {
        if (!FullyBellyApplication.getInstance().hasCountryCode()) {
            mDialogService.showToast(mTextService.get(R.string.countryUnknown));

            return false;
        }

        return true;
    }

    //endregion

}
