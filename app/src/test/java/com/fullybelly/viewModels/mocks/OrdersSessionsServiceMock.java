package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.local.interfaces.OrdersSessionsService;

import java.util.List;

public final class OrdersSessionsServiceMock implements OrdersSessionsService {
    @Override
    public List<String> getSessions() {
        return null;
    }

    @Override
    public List<String> getSessionsToCheck() {
        return null;
    }

    @Override
    public List<String> getSessions(int resId) {
        return null;
    }

    @Override
    public String getLastSessionToCheck() {
        return null;
    }

    @Override
    public void overwriteBookedSessions(List<String> sessions) {

    }

    @Override
    public void persistSession(String session) {

    }

    @Override
    public void removeSessionToCheck(String sessionId) {

    }
}
