package com.fullybelly.models.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class OrderSession {

    //region fields

    private final String mSessionId;

    private final Date mCreationDate;

    //endregion

    //region getters

    public String getSessionId() { return mSessionId; }

    public Date getCreationDate() { return mCreationDate; }

    //endregion

    //region constructors

    public OrderSession(String sessionId, Date creationDate) {
        mSessionId = sessionId;
        mCreationDate = creationDate;
    }

    public static OrderSession fromString(String serializedData) {
        String[] parts = serializedData.split(":::");
        String sessionId = parts[0];
        String date = parts.length >= 2 ? parts[1] : "";
        Date creationDate;

        try {
            creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
        } catch (ParseException e) {
            creationDate = null;
        }

        return new OrderSession(sessionId, creationDate);
    }

    //endregion

    //region public methods

    @Override
    public String toString() {
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        return String.format("%s:::%s", mSessionId, dateString);
    }

    //endregion

}
