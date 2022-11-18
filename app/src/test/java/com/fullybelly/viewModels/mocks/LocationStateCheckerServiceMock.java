package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.local.interfaces.LocationStateCheckerService;

public final class LocationStateCheckerServiceMock implements LocationStateCheckerService {
    public boolean returnHasPerms = true;
    public boolean returnCanGetLocation = true;
    public boolean returnIsGpsLocUsable = true;
    public boolean returnIsNetLocUsable = true;
    public boolean returnIsNetLocEnabled = true;

    @Override
    public boolean hasPermissions() { return returnHasPerms; }

    @Override
    public boolean canGetLocation() { return returnCanGetLocation; }

    @Override
    public boolean isGpsLocUsable() { return returnIsGpsLocUsable; }

    @Override
    public boolean isNetLocUsable() { return returnIsNetLocUsable; }

    @Override
    public boolean isNetLocEnabled() { return returnIsNetLocEnabled; }
}
