package com.commerce.Online.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    ANNULEE("Annulée");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

}
