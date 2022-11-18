package com.fullybelly.models.api;

public final class P24Configuration {

    //region fields

    private final String p24crc;

    private final String p24merchant;

    //endregion

    //region getters

    public String getP24crc() { return p24crc; }

    public String getP24merchant() { return p24merchant; }

    //endregion

    //region constructor

    public P24Configuration(String p24crc, String p24merchant) {
        this.p24crc = p24crc;
        this.p24merchant = p24merchant;
    }

    //endregion

}
