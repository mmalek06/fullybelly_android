package com.fullybelly.events.offerDetail;

public final class DetailsLoadCompleted {

    //region fields

    private String name;

    //endregion

    //region getters

    public String getName() { return name; }

    //endregion

    //region constructor

    public DetailsLoadCompleted(String name) {
        this.name = name;
    }

    //endregion

}
