package com.fullybelly.views.fragments.offerDetailActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.fullybelly.events.activities.OfferMapActivityInitialized;
import com.fullybelly.R;

import com.fullybelly.models.api.DetailedOffer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class OfferMapFragment extends MapFragment implements OnMapReadyCallback {

    //region private fields

    private boolean mMapDataReady;

    private boolean mMapReady;

    private String mRestaurantName;

    private double mLatitude;

    private double mLongitude;

    private GoogleMap mMap;

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};

    private int curMapTypeIndex = 1;

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOfferLoaded(OfferMapActivityInitialized result) {
        DetailedOffer offer = result.getOffer();

        mRestaurantName = offer.getName();
        mLatitude = offer.getLatitude();
        mLongitude = offer.getLongitude();

        if (!mMapDataReady && mMapReady) {
            initMarker();
        }

        mMapDataReady = true;
    }

    //endregion

    //region lifecycle events

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapReady = true;

        googleMap.setTrafficEnabled(false);
        setCameraPosition(mLatitude, mLongitude);

        if (mMapDataReady) {
            initMarker();
        }
    }

    //endregion

    //region private methods

    private void initMarker() {
        MarkerOptions options = new MarkerOptions().position(new LatLng(mLatitude, mLongitude));

        options.title(mRestaurantName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fullybelly_logo_small));

        Marker marker = mMap.addMarker(options);

        marker.showInfoWindow();
    }

    private void setCameraPosition(double latitude, double longitude) {
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(latitude, longitude))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
        mMap.setTrafficEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //endregion

}
