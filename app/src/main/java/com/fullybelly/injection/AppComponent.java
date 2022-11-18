package com.fullybelly.injection;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.background.LocationService;
import com.fullybelly.services.background.OrderMonitorService;
import com.fullybelly.views.activities.IntroActivity;
import com.fullybelly.views.activities.BaseActivity;
import com.fullybelly.views.activities.LoginActivity;
import com.fullybelly.views.activities.OfferDetailActivity;
import com.fullybelly.views.activities.OffersListActivity;
import com.fullybelly.views.activities.SettingsActivity;
import com.fullybelly.views.fragments.common.ToolbarFragment;
import com.fullybelly.views.fragments.offerDetailActivity.OfferDetailFragment;
import com.fullybelly.views.fragments.offerDetailActivity.PaymentMethodChooserFragment;
import com.fullybelly.views.fragments.offerDetailActivity.PurchaseDetailsDialogFragment;
import com.fullybelly.views.fragments.offerDetailActivity.UserDetailsFragment;
import com.fullybelly.views.fragments.ordersActivity.OrdersListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(LoginActivity activity);

    void inject(IntroActivity activity);

    void inject(OffersListActivity activity);

    void inject(OfferDetailActivity activity);

    void inject(SettingsActivity activity);

    void inject(LocationService service);

    void inject(OrderMonitorService service);

    void inject(ToolbarFragment fragment);

    void inject(OfferDetailFragment fragment);

    void inject(PurchaseDetailsDialogFragment fragment);

    void inject(PaymentMethodChooserFragment fragment);

    void inject(UserDetailsFragment fragment);

    void inject(OrdersListFragment fragment);

    final class Initializer {
        private Initializer() {}

        public static AppComponent init(FullyBellyApplication application) {
            return DaggerAppComponent.builder()
                                     .appModule(new AppModule(application))
                                     .build();
        }
    }

}
