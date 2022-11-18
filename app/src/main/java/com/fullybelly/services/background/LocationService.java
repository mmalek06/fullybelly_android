package com.fullybelly.services.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fullybelly.events.location.LocationChanged;
import com.fullybelly.events.location.LocationProviderStatusChanged;
import com.fullybelly.events.location.LocationStatus;
import com.fullybelly.events.location.ProviderStatus;
import com.fullybelly.services.local.implementations.LocationStateCheckerServiceImpl;
import com.fullybelly.services.local.interfaces.LocationStateCheckerService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

public final class LocationService extends Service implements LocationListener {

    //region public fields

    @Inject
    public LocationStateCheckerService locationStateCheckerService;

    //endregion

    //region fields

    private LocationManager mLocationManager;

    private IBinder mBinder = new LocationSvcBinder();

    private ArrayList<String> mActiveProviders;

    //endregion

    //region constructor

    public LocationService() {
        super();

        mActiveProviders = new ArrayList<>();
    }

    //endregion

    //region public methods
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopListening();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        EventBus.getDefault().postSticky(new LocationChanged(location, LocationStatus.OK));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) {
        startListening();
        broadcastLastLocation();
        EventBus.getDefault().postSticky(new LocationProviderStatusChanged(ProviderStatus.ENABLED));
    }

    @Override
    public void onProviderDisabled(String provider) {
        mActiveProviders.remove(provider);
        EventBus.getDefault().postSticky(new LocationProviderStatusChanged(ProviderStatus.DISABLED));
    }

    public void startListening() {
        boolean hasPerms = locationStateCheckerService.hasPermissions();

        if ((locationStateCheckerService.isGpsLocUsable() || locationStateCheckerService.isNetLocUsable()) && hasPerms) {
            listen();
            broadcastLastLocation();
            return;
        }

        if (hasPerms) {
            waitForLocation();
        } else if (!hasPerms) {
            EventBus.getDefault().postSticky(new LocationChanged(null, LocationStatus.NO_PERMISSIONS));
        }
        if (!locationStateCheckerService.canGetLocation()) {
            EventBus.getDefault().postSticky(new LocationChanged(null, LocationStatus.LOCATION_UNAVAILABLE));
        }
    }

    @SuppressWarnings({"MissingPermission"})
    public void stopListening() {
        mLocationManager.removeUpdates(this);
        mActiveProviders.clear();
    }

    //endregion

    //region private methods

    private void listen() {
        stopListening();

        if (locationStateCheckerService.isNetLocUsable()) {
            mActiveProviders.add(LocationManager.NETWORK_PROVIDER);
        }
        if (locationStateCheckerService.isGpsLocUsable()) {
            mActiveProviders.add(LocationManager.GPS_PROVIDER);
        }

        for (String providerName : mActiveProviders) {
            requestLocationUpdates(providerName);
        }
    }

    private void waitForLocation() {
        mActiveProviders.add(LocationManager.NETWORK_PROVIDER);
        mActiveProviders.add(LocationManager.GPS_PROVIDER);

        for (String provider : mActiveProviders) {
            requestLocationUpdates(provider);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void broadcastLastLocation() {
        ArrayList<String> providers = new ArrayList<>();

        providers.add(LocationManager.NETWORK_PROVIDER);
        providers.add(LocationManager.GPS_PROVIDER);

        for (String providerName : providers) {
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(providerName);

            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation);
                return;
            }
        }

        EventBus.getDefault().postSticky(new LocationChanged(null, LocationStatus.LOCATION_UNAVAILABLE));
    }

    @SuppressWarnings({"MissingPermission"})
    private void requestLocationUpdates(String providerName) {
        mLocationManager.requestLocationUpdates(providerName, 30000, 100, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    //endregion

    //region classes

    public class LocationSvcBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }

    }

    //endregion

}
