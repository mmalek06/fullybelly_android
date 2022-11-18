package com.fullybelly;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.facebook.FacebookSdk;
import com.fullybelly.injection.AppComponent;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class FullyBellyApplication extends Application {

    //region static fields

    private static AppComponent graph;

    private static FullyBellyApplication instance;

    private static String simCountryCode;

    private static String networkCountryCode;

    //endregion

    //region fields

    private Activity mCurrentActivity;

    //endregion

    //region getters

    public boolean hasCountryCode() {
        return (simCountryCode != null && !simCountryCode.equals("")) ||
                networkCountryCode != null && !networkCountryCode.equals("");
    }

    public String getCountryCode() {
        return "pl";
//        return simCountryCode == null || simCountryCode.equals("") ?
//                networkCountryCode :
//                simCountryCode;
    }

    //endregion

    //region lifecycle events

    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        simCountryCode = telephonyManager.getSimCountryIso();
        networkCountryCode = telephonyManager.getNetworkCountryIso();
        instance = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        buildComponentGraph();
    }

    //endregion

    //region public methods

    public Activity getCurrentActivity(){ return mCurrentActivity; }

    public void setCurrentActivity(Activity currentActivity){ mCurrentActivity = currentActivity; }

    public static AppComponent component() { return graph; }

    public static void buildComponentGraph() { graph = AppComponent.Initializer.init(instance); }

    public static FullyBellyApplication getInstance() { return instance; }

    //endregion

}
