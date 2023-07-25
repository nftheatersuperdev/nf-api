package com.nftheater.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class NetflixAccountLinkEntityId implements Serializable {
    private static final long serialVersionUID = 8714806352607668069L;
    @NotNull
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

}