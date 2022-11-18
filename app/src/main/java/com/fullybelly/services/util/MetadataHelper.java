package com.fullybelly.services.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public final class MetadataHelper {

    //region public methods

    public static Bundle getMetadata(Context context) {
        ApplicationInfo app;

        try { app = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA); }
        catch (PackageManager.NameNotFoundException e) { return null; }

        return app.metaData;
    }

    //endregion

}
