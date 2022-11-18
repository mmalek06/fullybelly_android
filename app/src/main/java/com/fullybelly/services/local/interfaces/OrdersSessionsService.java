package com.fullybelly.services.local.interfaces;

import java.util.List;

public interface OrdersSessionsService {

    List<String> getSessions();

    List<String> getSessionsToCheck();

    List<String> getSessions(int resId);

    String getLastSessionToCheck();

    void overwriteBookedSessions(List<String> sessions);

    void persistSession(String session);

    void removeSessionToCheck(String sessionId);

}
