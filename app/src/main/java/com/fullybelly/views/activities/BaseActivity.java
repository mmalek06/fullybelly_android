package com.fullybelly.views.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.events.activities.ActivityInitializationResultCodes;
import com.fullybelly.services.background.OrderMonitorService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.results.OrdersMonitorServiceResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    //region constants

    private final static int PERMS_REQUEST_CODE = 1000;

    //endregion

    //region static fields

    protected static HashSet<String> runningSessionIdsChecks = new HashSet<>();

    //endregion

    //region public fields

    @Inject
    public OrdersSessionsService ordersSessionsService;

    //endregion

    //region fields

    protected FullyBellyApplication mApplication;

    //endregion

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullyBellyApplication.component().inject(this);

        mApplication = (FullyBellyApplication) getApplicationContext();

        PermissionsWrapper info = getPermissionsInfo();

        if (info.permissionsList.size() == 0 && info.neededPermissions.size() == 0) {
            permissionsChecked(ActivityInitializationResultCodes.INIT_OK);
        } else {
            askForPermissions(info);
        }

        mApplication.setCurrentActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mApplication.getCurrentActivity() == null) {
            mApplication.setCurrentActivity(this);
        }

        EventBus.getDefault().register(this);
        monitorOrders();
    }

    @Override
    protected void onPause() {
        clearReferences();
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    //endregion

    //region public methods

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMS_REQUEST_CODE:
                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    permissionsChecked(ActivityInitializationResultCodes.INIT_OK);
                } else {
                    permissionsChecked(ActivityInitializationResultCodes.INIT_NO_PERMS);
                    Toast.makeText(this, getString(R.string.permissionsDenied), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrdersMonitorServiceResult(OrdersMonitorServiceResult result) {
        runningSessionIdsChecks.remove(result.getOrder().getSessionId());
    }

    //endregion

    //endregion

    //region protected methods

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void permissionsChecked(ActivityInitializationResultCodes code) {}

    protected void monitorOrders(String sessionId) {
        if (runningSessionIdsChecks.contains(sessionId)) {
            return;
        }

        Intent intent = new Intent(this, OrderMonitorService.class);

        intent.putExtra(OrderMonitorService.SESSION_ID_KEY, sessionId);
        startService(intent);
        runningSessionIdsChecks.add(sessionId);
    }

    private void monitorOrders() {
        List<String> sessionsToCheck = ordersSessionsService.getSessionsToCheck();

        for (String session : sessionsToCheck) {
            monitorOrders(session);
        }
    }

    //endregion

    //region private methods

    private PermissionsWrapper getPermissionsInfo() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new PermissionsWrapper();
        }

        final List<String> neededPermissions = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        if (!addPermission(permissionsList, Manifest.permission.INTERNET)) {
            neededPermissions.add(getString(R.string.internet2));
        }
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            neededPermissions.add(getString(R.string.location2));
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            neededPermissions.add(getString(R.string.memory));
        }

        return new PermissionsWrapper(neededPermissions, permissionsList);
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return false;
        }
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);

            if (!shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }

        return true;
    }

    private void askForPermissions(final PermissionsWrapper info) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        if (info.neededPermissions.size() > 0) {
            String message = getString(R.string.permissionsExplanation) + " " + TextUtils.join(", ", info.neededPermissions);

            showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                @Override
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(info.permissionsList.toArray(new String[info.permissionsList.size()]), PERMS_REQUEST_CODE);
                }
            });

            return;
        }

        requestPermissions(info.permissionsList.toArray(new String[info.permissionsList.size()]), PERMS_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                       .setMessage(message)
                       .setPositiveButton("OK", okListener)
                       .setNegativeButton(getString(R.string.cancelButtonText), null)
                       .create()
                       .show();
    }

    private void clearReferences(){
        Activity currActivity = mApplication.getCurrentActivity();

        if (this.equals(currActivity)) {
            mApplication.setCurrentActivity(null);
        }
    }

    //endregion

    //region private classes

    private class PermissionsWrapper {

        public List<String> neededPermissions;

        public List<String> permissionsList;

        public PermissionsWrapper() {
            neededPermissions = new ArrayList<>();
            permissionsList = new ArrayList<>();
        }

        public PermissionsWrapper(List<String> neededPermissions, List<String> permissionsList) {
            this.neededPermissions = neededPermissions;
            this.permissionsList = permissionsList;
        }

    }

    //endregion

}
