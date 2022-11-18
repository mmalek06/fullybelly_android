package com.fullybelly.models.searcher;

public enum OrderingKind {

    NONE(""),
    CLOSEST("closest"),
    CHEAPEST("cheapest"),
    PICKUP("pickup");

    private final String mText;

    private OrderingKind(final String text) {
        this.mText = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return mText;
    }

}
