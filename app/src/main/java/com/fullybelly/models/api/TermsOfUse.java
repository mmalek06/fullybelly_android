package com.fullybelly.models.api;

public final class TermsOfUse {

    //region fields

    private int mId;

    private String mText;

    private String mImpressum;

    private boolean mAccepted;

    //endregion

    //region getters

    public int getId() { return mId; }

    public String getText() { return mText; }

    public String getImpressum() { return mImpressum; }

    public boolean isAccepted() { return mAccepted; }

    //endregion

    //region constructor

    public TermsOfUse(int id, String text, String impressum, boolean accepted) {
        mId = id;
        mText = text;
        mImpressum = impressum;
        mAccepted = accepted;
    }

    //endregion

}
