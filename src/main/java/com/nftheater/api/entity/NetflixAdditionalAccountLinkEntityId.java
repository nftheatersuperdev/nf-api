package com.nftheater.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class NetflixAdditionalAccountLinkEntityId implements Serializable {
    private static final long serialVersionUID = 7659113438751214904L;
    @NotNull
    @Column(name = "additional_account_id", nullable = false)
    private UUID additionalAccountId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

}