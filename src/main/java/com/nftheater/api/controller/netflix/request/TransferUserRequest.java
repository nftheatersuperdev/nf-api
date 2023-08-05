package com.nftheater.api.controller.netflix.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TransferUserRequest {
    private UUID fromAccountId;
    private List<String> userIds;
}
