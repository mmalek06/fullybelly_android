package com.fullybelly.services.local.implementations;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.LocationStateCheckerService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class LocationStateCheckerServiceImpl extends BaseAppService implements LocationStateCheckerService {

    //region fields

    private final LocationManager mLocationManager;

    //endregion

    //region constructor

    @Inject
    public LocationStateCheckerServiceImpl(FullyBellyApplication application) {
        super(application);

        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    //endregion

    //region public methods

    @Override
    public boolean hasPermissions() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        return ActivityCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean canGetLocation() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        return isNetLocUsable() || isGpsLocUsable();
    }

    @Override
    public boolean isGpsLocUsable() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean isNetLocUsable() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        return isNetLocEnabled() && confirmAirplaneModeOff() && confirmWiFiAvailable();
    }

    @Override
    public boolean isNetLocEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //endregion

    //region private methods

    private boolean confirmWiFiAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            List<NetworkInfo> tmpInfos = new ArrayList<>();
            networkInfos = new NetworkInfo[networks.length];

            for (Network net : networks) {
                tmpInfos.add(cm.getNetworkInfo(net));
            }

            tmpInfos.toArray(networkInfos);
        } else {
            //noinspection deprecation
            networkInfos = cm.getAllNetworkInfo();
        }

        for (NetworkInfo info : networkInfos) {
            if (info != null && info.isAvailable()) {
                return true;
            }
        }

        return false;
    }

    private boolean confirmAirplaneModeOff() {
        int airplaneSetting;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            airplaneSetting = Settings.System.getInt(getContext().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        } else {
            //noinspection deprecation
            airplaneSetting = Settings.System.getInt(getContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        }

        return airplaneSetting == 0;
    }

    //endregion

}
