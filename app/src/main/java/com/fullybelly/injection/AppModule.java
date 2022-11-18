package com.fullybelly.injection;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.internet.common.PartnersService;
import com.fullybelly.services.internet.common.PartnersServiceImpl;
import com.fullybelly.services.internet.offers.OfferDetailsService;
import com.fullybelly.services.internet.offers.OfferDetailsServiceImpl;
import com.fullybelly.services.internet.offers.OffersSearcherService;
import com.fullybelly.services.internet.offers.OffersSearcherServiceImpl;
import com.fullybelly.services.internet.orders.implementations.PaypalCheckoutServiceImpl;
import com.fullybelly.services.internet.orders.interfaces.BookOrderService;
import com.fullybelly.services.internet.orders.interfaces.CheckoutService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;
import com.fullybelly.services.internet.orders.implementations.ConfigurationServiceFactoryImpl;
import com.fullybelly.services.internet.orders.interfaces.OrderMonitorService;
import com.fullybelly.services.internet.orders.implementations.OrderMonitorServiceImpl;
import com.fullybelly.services.internet.orders.interfaces.OrdersListService;
import com.fullybelly.services.internet.orders.implementations.OrdersListServiceImpl;
import com.fullybelly.services.internet.orders.implementations.BookOrderServiceImpl;
import com.fullybelly.services.local.implementations.BookedOrderCacheServiceImpl;
import com.fullybelly.services.local.implementations.IntroServiceImpl;
import com.fullybelly.services.local.implementations.PaymentResultConsumerFactoryImpl;
import com.fullybelly.services.local.implementations.PaypalBackgroundServiceRunnerImpl;
import com.fullybelly.services.local.interfaces.BookedOrderCacheService;
import com.fullybelly.services.local.interfaces.IntroService;
import com.fullybelly.services.local.interfaces.PaymentResultConsumerFactory;
import com.fullybelly.services.local.interfaces.PaymentServiceFactory;
import com.fullybelly.services.local.implementations.PaymentServiceFactoryImpl;
import com.fullybelly.services.local.implementations.DialogServiceImpl;
import com.fullybelly.services.local.implementations.FavoritesServiceImpl;
import com.fullybelly.services.local.implementations.LocationStateCheckerServiceImpl;
import com.fullybelly.services.local.implementations.NavigationServiceImpl;
import com.fullybelly.services.local.implementations.NotificationServiceImpl;
import com.fullybelly.services.local.implementations.OrdersNotificationServiceImpl;
import com.fullybelly.services.local.implementations.OrdersSessionsServiceImpl;
import com.fullybelly.services.local.implementations.PaymentMethodServiceImpl;
import com.fullybelly.services.local.implementations.SearcherSettingsServiceImpl;
import com.fullybelly.services.local.implementations.TermsOfUseServiceImpl;
import com.fullybelly.services.local.implementations.TextServiceImpl;
import com.fullybelly.services.local.implementations.UserDetailsServiceImpl;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.FavoritesService;
import com.fullybelly.services.local.interfaces.LocationStateCheckerService;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.local.interfaces.NotificationService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.PaypalBackgroundServiceRunner;
import com.fullybelly.services.local.interfaces.SearcherSettingsService;
import com.fullybelly.services.local.interfaces.TermsOfUseService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.viewModels.activities.LoginViewModel;
import com.fullybelly.viewModels.activities.OfferDetailsViewModel;
import com.fullybelly.viewModels.activities.OffersListViewModel;
import com.fullybelly.viewModels.fragments.OffersListItemViewModel;
import com.fullybelly.viewModels.fragments.OrdersListViewModel;
import com.fullybelly.viewModels.fragments.PaymentMethodChooserViewModel;
import com.fullybelly.viewModels.fragments.PurchaseDetailsViewModel;
import com.fullybelly.viewModels.fragments.TopToolbarViewModel;
import com.fullybelly.viewModels.fragments.UserDetailsViewModel;
import com.fullybelly.viewModels.validators.PaymentMethodValidator;
import com.fullybelly.viewModels.validators.PaymentMethodValidatorImpl;
import com.fullybelly.viewModels.validators.UserDetailsValidator;
import com.fullybelly.viewModels.validators.UserDetailsValidatorImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    //region fields

    private FullyBellyApplication mApplication;

    //endregion

    //region constructor

    public AppModule(FullyBellyApplication application) { mApplication = application; }

    //endregion

    //region service providers

    @Provides
    @Singleton
    public DialogService provideDialogService() { return new DialogServiceImpl(mApplication); }

    @Provides
    @Singleton
    public FavoritesService provideFavoritesService() { return new FavoritesServiceImpl(mApplication); }

    @Provides
    @Singleton
    public LocationStateCheckerService provideLocationStateCheckerService() { return new LocationStateCheckerServiceImpl(mApplication); }

    @Provides
    @Singleton
    public NotificationService provideNotificationService() { return new NotificationServiceImpl(mApplication); }

    @Provides
    @Singleton
    public OrdersNotificationService provideOrdersNotificationService() { return new OrdersNotificationServiceImpl(mApplication); }

    @Provides
    @Singleton
    public OrdersSessionsService provideOrdersSessionService() { return new OrdersSessionsServiceImpl(mApplication); }

    @Provides
    @Singleton
    public PaymentMethodService providePaymentMethodService() { return new PaymentMethodServiceImpl(mApplication); }

    @Provides
    @Singleton
    public ConfigurationServiceFactory provideConfigurationServiceFactory() { return new ConfigurationServiceFactoryImpl(mApplication); }

    @Provides
    @Singleton
    public PaymentServiceFactory providePaymentServiceFactory(BookedOrderCacheService bookedOrderCacheService) {
        return new PaymentServiceFactoryImpl(mApplication,
                                             bookedOrderCacheService);
    }

    @Provides
    @Singleton
    public SearcherSettingsService provideSearcherSettingsService() { return new SearcherSettingsServiceImpl(mApplication); }

    @Provides
    @Singleton
    public TermsOfUseService provideTermsOfUseService() { return new TermsOfUseServiceImpl(mApplication); }

    @Provides
    @Singleton
    public TextService provideTextService() { return new TextServiceImpl(mApplication); }

    @Provides
    @Singleton
    public UserDetailsService provideUserDetailsService() { return new UserDetailsServiceImpl(mApplication); }

    @Provides
    @Singleton
    public com.fullybelly.services.internet.common.TermsOfUseService provideNetTermsOfUse() { return new com.fullybelly.services.internet.common.TermsOfUseServiceImpl(mApplication); }

    @Provides
    @Singleton
    public PartnersService providePartnersService() { return new PartnersServiceImpl(mApplication); }

    @Provides
    @Singleton
    public OfferDetailsService provideOfferDetailsService() { return new OfferDetailsServiceImpl(mApplication); }

    @Provides
    @Singleton
    public OffersSearcherService provideOffersSearcherService(FavoritesService favoritesService) { return new OffersSearcherServiceImpl(mApplication, favoritesService); }

    @Provides
    @Singleton
    public OrderMonitorService provideOrderMonitorService() { return new OrderMonitorServiceImpl(mApplication); }

    @Provides
    @Singleton
    public OrdersListService provideOrdersListService() { return new OrdersListServiceImpl(mApplication); }

    @Provides
    @Singleton
    public BookOrderService provideOrdersService() { return new BookOrderServiceImpl(mApplication); }

    @Provides
    @Singleton
    public NavigationService provideNavigationService() { return new NavigationServiceImpl(mApplication); }

    @Provides
    @Singleton
    public CheckoutService provideCheckoutService() { return new PaypalCheckoutServiceImpl(mApplication); }

    @Provides
    @Singleton
    public BookedOrderCacheService provideBookedOrderCacheService() { return new BookedOrderCacheServiceImpl(mApplication); }

    @Provides
    @Singleton
    public PaymentResultConsumerFactory providePaymentResultConsumerFactory(TextService textService,
                                                                            OrdersNotificationService ordersNotificationService,
                                                                            OrdersSessionsService ordersSessionsService,
                                                                            DialogService dialogService,
                                                                            CheckoutService checkoutService,
                                                                            BookedOrderCacheService bookedOrderCacheService) {
        return new PaymentResultConsumerFactoryImpl(mApplication,
                                                    textService,
                                                    ordersNotificationService,
                                                    ordersSessionsService,
                                                    dialogService,
                                                    checkoutService,
                                                    bookedOrderCacheService);
    }

    @Provides
    @Singleton
    public IntroService provideIntroService() {
        return new IntroServiceImpl(mApplication);
    }

    //endregion

    //region validation providers

    @Provides
    @Singleton
    public UserDetailsValidator provideUserDetailsValidator() { return new UserDetailsValidatorImpl(); }

    @Provides
    @Singleton
    public PaymentMethodValidator providePaymentMethodValidator() { return new PaymentMethodValidatorImpl(); }

    //endregion

    //region vm providers

    @Provides
    public LoginViewModel provideLoginViewModel(UserDetailsService detailsService,
                                                DialogService dialogService,
                                                TextService textService,
                                                NavigationService navigationService,
                                                PartnersService partnersService,
                                                IntroService introService,
                                                com.fullybelly.services.internet.common.TermsOfUseService externalTermsOfUseService,
                                                com.fullybelly.services.local.interfaces.TermsOfUseService localTermsOfUseService) {
        return new LoginViewModel(detailsService,
                                  dialogService,
                                  textService,
                                  navigationService,
                                  partnersService,
                                  introService,
                                  externalTermsOfUseService,
                                  localTermsOfUseService);
    }

    @Provides
    public OffersListViewModel provideOffersListViewModel(DialogService dialogService,
                                                          TextService textService,
                                                          NavigationService navigationService,
                                                          OffersSearcherService offersSearcherService,
                                                          SearcherSettingsService searcherSettingsService,
                                                          LocationStateCheckerService locationStateCheckerService) {
        return new OffersListViewModel(dialogService,
                                       textService,
                                       navigationService,
                                       offersSearcherService,
                                       searcherSettingsService,
                                       locationStateCheckerService);
    }

    @Provides
    public OffersListItemViewModel provideOffersListItemViewModel(NavigationService navigationService,
                                                                  TextService textService) {
        return new OffersListItemViewModel(navigationService, textService);
    }

    @Provides
    public OfferDetailsViewModel provideOfferDetailsViewModel() { return new OfferDetailsViewModel(); }

    @Provides
    public PaypalBackgroundServiceRunner providePaypalBgSvcRunner(ConfigurationServiceFactory configurationServiceFactory,
                                                                  DialogService dialogService,
                                                                  TextService textService) {
        return new PaypalBackgroundServiceRunnerImpl(mApplication,
                                                     configurationServiceFactory,
                                                     dialogService,
                                                     textService);
    }

    @Provides
    public TopToolbarViewModel provideTopToolbarViewModel(NavigationService navigationService,
                                                          OrdersSessionsService sessionsService,
                                                          OrdersNotificationService ordersNotificationService,
                                                          TextService textService,
                                                          FavoritesService favoritesService,
                                                          NotificationService notificationService) {
        return new TopToolbarViewModel(navigationService,
                                       sessionsService,
                                       ordersNotificationService,
                                       textService,
                                       favoritesService,
                                       notificationService);
    }

    @Provides
    public PurchaseDetailsViewModel providePurchaseDetailsViewModel(DialogService dialogService,
                                                                    TextService textService,
                                                                    UserDetailsService userDetailsService,
                                                                    PaymentMethodService paymentMethodService,
                                                                    OrdersSessionsService sessionsService,
                                                                    BookOrderService bookOrderService,
                                                                    ConfigurationServiceFactory configurationServiceFactory,
                                                                    PaymentServiceFactory paymentServiceFactory) {
        return new PurchaseDetailsViewModel(dialogService,
                                            textService,
                                            userDetailsService,
                                            paymentMethodService,
                                            sessionsService,
                                            bookOrderService,
                                            configurationServiceFactory,
                                            paymentServiceFactory);
    }

    @Provides
    public UserDetailsViewModel provideUserDetailsViewModel(DialogService dialogService,
                                                            TextService textService,
                                                            UserDetailsService userDetailsService,
                                                            PaymentMethodService paymentMethodService,
                                                            OrdersSessionsService sessionsService,
                                                            BookOrderService bookOrderService,
                                                            UserDetailsValidator userDetailsValidator,
                                                            ConfigurationServiceFactory configurationServiceFactory,
                                                            PaymentServiceFactory paymentServiceFactory) {
        return new UserDetailsViewModel(dialogService,
                                        textService,
                                        userDetailsService,
                                        paymentMethodService,
                                        sessionsService,
                                        bookOrderService,
                                        userDetailsValidator,
                                        configurationServiceFactory,
                                        paymentServiceFactory);
    }

    @Provides
    public PaymentMethodChooserViewModel providePaymentMethodChooserViewModel(DialogService dialogService,
                                                                              TextService textService,
                                                                              UserDetailsService userDetailsService,
                                                                              PaymentMethodService paymentMethodService,
                                                                              OrdersSessionsService sessionsService,
                                                                              BookOrderService bookOrderService,
                                                                              PaymentMethodValidator paymentMethodValidator,
                                                                              ConfigurationServiceFactory configurationServiceFactory,
                                                                              PaymentServiceFactory paymentServiceFactory) {
        return new PaymentMethodChooserViewModel(dialogService,
                                                 textService,
                                                 userDetailsService,
                                                 paymentMethodService,
                                                 sessionsService,
                                                 bookOrderService,
                                                 paymentMethodValidator,
                                                 configurationServiceFactory,
                                                 paymentServiceFactory);
    }

    @Provides
    public OrdersListViewModel provideOrdersListViewModel(UserDetailsService userDetailsService,
                                                          OrdersSessionsService sessionsService,
                                                          OrdersListService ordersListService,
                                                          OrdersNotificationService ordersNotificationService) {
        return new OrdersListViewModel(userDetailsService,
                                       sessionsService,
                                       ordersListService,
                                       ordersNotificationService);
    }

    //endregion

}
