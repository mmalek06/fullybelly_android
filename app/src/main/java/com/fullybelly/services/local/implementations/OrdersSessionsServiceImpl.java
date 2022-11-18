package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.models.common.OrderSession;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OrdersSessionsServiceImpl extends BaseAppService implements OrdersSessionsService {

    //region constants

    private final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;

    //endregion

    //region constructor

    public OrdersSessionsServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public List<String> getSessions() { return getSessions(R.string.settingsBookedSessions); }

    @Override
    public List<String> getSessionsToCheck() { return getSessions(R.string.settingsSessionsToCheck); }

    @Override
    public List<String> getSessions(int resId) {
        Context context = getContext();

        if (context == null) {
            return new ArrayList<>();
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> serializedSessions = preferences.getStringSet(getContext().getString(resId), new HashSet<String>());
        Set<String> validSessions = new HashSet<>();
        List<String> sessions = new ArrayList<>();
        Date now = new Date();

        for (String serializedSession : serializedSessions) {
            OrderSession sessionWrapper = OrderSession.fromString(serializedSession);

            if (sessionWrapper.getCreationDate() == null) {
                continue;
            }

            if (Math.abs(now.getTime() - sessionWrapper.getCreationDate().getTime()) < MILLIS_PER_DAY) {
                sessions.add(sessionWrapper.getSessionId());
                validSessions.add(sessionWrapper.toString());
            }
        }

        editor.putStringSet(getContext().getString(resId), validSessions);
        editor.apply();

        return sessions;
    }

    @Override
    public String getLastSessionToCheck() {
        Context context = getContext();

        if (context == null) {
            return "";
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        Set<String> serializedSessions = preferences.getStringSet(getContext().getString(R.string.settingsSessionsToCheck), new HashSet<String>());
        String lastSession = null;
        Date date = new Date(Long.MIN_VALUE);

        for (String session : serializedSessions) {
            OrderSession sessionWrapper = OrderSession.fromString(session);

            if (sessionWrapper.getCreationDate().after(date)) {
                date = sessionWrapper.getCreationDate();
                lastSession = sessionWrapper.getSessionId();
            }
        }

        return lastSession;
    }

    @Override
    public void overwriteBookedSessions(List<String> sessions) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> newSessions = new HashSet<>();

        for (String session : sessions) {
            newSessions.add(new OrderSession(session, new Date()).toString());
        }

        editor.putStringSet(getContext().getString(R.string.settingsBookedSessions), newSessions);
        editor.apply();
    }

    @Override
    public void persistSession(String session) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> bookedSessions = preferences.getStringSet(getContext().getString(R.string.settingsBookedSessions), new HashSet<String>());
        Set<String> sessionsToCheck = preferences.getStringSet(getContext().getString(R.string.settingsSessionsToCheck), new HashSet<String>());
        OrderSession sessionWrapper = new OrderSession(session, new Date());

        bookedSessions.add(sessionWrapper.toString());
        sessionsToCheck.add(sessionWrapper.toString());
        editor.putStringSet(getContext().getString(R.string.settingsBookedSessions), bookedSessions);
        editor.putStringSet(getContext().getString(R.string.settingsSessionsToCheck), sessionsToCheck);
        editor.apply();
    }

    @Override
    public void removeSessionToCheck(String sessionId) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> sessionsToCheck = preferences.getStringSet(getContext().getString(R.string.settingsSessionsToCheck), new HashSet<String>());
        String foundSessionId = null;

        for (String savedSession : sessionsToCheck) {
            OrderSession sessionWrapper = OrderSession.fromString(savedSession);

            if (sessionWrapper.getSessionId().equals(sessionId)) {
                foundSessionId = savedSession;
            }
        }
        if (foundSessionId != null) {
            sessionsToCheck.remove(foundSessionId);
            editor.putStringSet(getContext().getString(R.string.settingsSessionsToCheck), sessionsToCheck);
        }

        editor.apply();
    }

    //endregion

}
