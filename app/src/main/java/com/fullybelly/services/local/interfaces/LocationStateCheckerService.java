package com.fullybelly.services.local.interfaces;

public interface LocationStateCheckerService {

    boolean hasPermissions();

    boolean canGetLocation();

    boolean isGpsLocUsable();

    boolean isNetLocUsable();

    boolean isNetLocEnabled();

}
