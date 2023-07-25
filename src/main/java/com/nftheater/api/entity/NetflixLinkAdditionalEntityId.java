package com.nftheater.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class NetflixLinkAdditionalEntityId implements Serializable {
    private static final long serialVersionUID = 910008603588694343L;
    @NotNull
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @NotNull
    @Column(name = "additional_id", nullable = false)
    private UUID additionalId;

}