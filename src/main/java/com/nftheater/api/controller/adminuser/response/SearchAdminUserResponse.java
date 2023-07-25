package com.nftheater.api.controller.adminuser.response;

import com.nftheater.api.controller.response.PaginationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAdminUserResponse {

    private PaginationResponse pagination;
    private List<AdminUserResponse> adminUsers;
}