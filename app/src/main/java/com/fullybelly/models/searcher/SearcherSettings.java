package com.fullybelly.models.searcher;

import android.os.Parcel;
import android.os.Parcelable;

public final class SearcherSettings implements Parcelable, Cloneable {

    //region fields

    private String mSearchTerm;

    private int mSearchDist;

    private boolean mFavorite;

    private boolean mOnlyAvailable;

    private OrderingKind mOrderingKind;

    private boolean mForceSearch;

    //endregion

    //region getters and setters

    public String getSearchTerm() { return mSearchTerm; }
    public void setSearchTerm(String searchTerm) { mSearchTerm = searchTerm; }

    public int getSearchDist() { return mSearchDist; }
    public void setSearchDist(int searchDist) { mSearchDist = searchDist; }

    public boolean getFavorite() { return mFavorite; }
    public void setFavorite(boolean favorite) { mFavorite = favorite; }

    public boolean getOnlyAvailable() { return mOnlyAvailable; }
    public void setOnlyAvailable(boolean onlyAvailable) { mOnlyAvailable = onlyAvailable; }

    public OrderingKind getOrderingKind() { return mOrderingKind; }
    public void setOrderingKind(OrderingKind orderingKind) { mOrderingKind = orderingKind; }

    public boolean shouldForceSearch() { return mForceSearch; }
    public void setForceSearch(boolean isFresh) { mForceSearch = isFresh; }

    //endregion

    //region constructors

    public static final Parcelable.Creator<SearcherSettings> CREATOR
            = new Parcelable.Creator<SearcherSettings>() {
        public SearcherSettings createFromParcel(Parcel in) {
            return new SearcherSettings(in);
        }

        public SearcherSettings[] newArray(int size) {
            return new SearcherSettings[size];
        }
    };

    public SearcherSettings() {
        mSearchTerm = null;
        mSearchDist = 0;
        mFavorite = false;
        mOnlyAvailable = false;
        mOrderingKind = OrderingKind.NONE;
    }

    private SearcherSettings(Parcel in) {
        mSearchTerm = in.readString();
        mSearchDist = in.readInt();
        mFavorite = in.readInt() == 0 ? false : true;
        mOnlyAvailable = in.readInt() == 0 ? false : true;
        mOrderingKind = OrderingKind.valueOf(in.readString());
    }

    //endregion

    //region public methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSearchTerm);
        dest.writeInt(mSearchDist);
        dest.writeInt(mFavorite ? 1 : 0);
        dest.writeInt(mOnlyAvailable ? 1 : 0);
        dest.writeString(mOrderingKind.name());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //endregion

}
