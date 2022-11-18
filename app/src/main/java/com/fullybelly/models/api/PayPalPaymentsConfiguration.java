package com.fullybelly.models.api;

public final class PayPalPaymentsConfiguration {

    //region fields

    private final String clientId;

    //endregion

    //region getters

    public String getClientId() { return clientId; }

    //endregion

    //region constructor

    public PayPalPaymentsConfiguration(String clientId) {
        this.clientId = clientId;
    }

    //endregion

}
