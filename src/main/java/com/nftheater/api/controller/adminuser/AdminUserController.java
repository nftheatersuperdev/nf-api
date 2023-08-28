package com.nftheater.api.controller.adminuser;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.adminuser.request.CreateAdminUserRequest;
import com.nftheater.api.controller.adminuser.request.SearchAdminUserRequest;
import com.nftheater.api.controller.adminuser.response.AdminUserProfileResponse;
import com.nftheater.api.controller.adminuser.response.AdminUserResponse;
import com.nftheater.api.controller.adminuser.response.CreateAdminUserResponse;
import com.nftheater.api.controller.adminuser.response.SearchAdminUserResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.controller.response.PageableResponse;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.mapper.AdminUserMapper;
import com.nftheater.api.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminUserMapper adminUserMapper;

    @Secured({Module.ALL})
    @PostMapping("/v1/admin-users")
    public GeneralResponse<CreateAdminUserResponse> createAdminUser(@RequestBody CreateAdminUserRequest request) throws FirebaseAuthException {
        log.info("Start create new admin user with Email and Password");
        CreateAdminUserResponse response = adminUserService.createAdminUser(request);
        log.info("End create new admin user with user Id " + response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.YOUTUBE, Module.NETFLIX})
    @GetMapping("/v1/admin-user/profile")
    public GeneralResponse<AdminUserProfileResponse> getAdminUserProfile(HttpServletRequest request) throws FirebaseAuthException, DataNotFoundException {
        log.info("===== Start get my profile =====");
        String bearerToken = request.getHeader("Authorization");
        AdminUserDto adminUserDto = adminUserService.getAdminUserByFirebaseToken((bearerToken.substring(7, bearerToken.length())));
        AdminUserProfileResponse adminUserProfileResponse = new AdminUserProfileResponse();
        adminUserProfileResponse.setAdminUser(adminUserMapper.toResponse(adminUserDto));
        log.info("===== End get my profile with user Id {} =====", adminUserDto.getId());
        return new GeneralResponse<>(SUCCESS, adminUserProfileResponse);
    }

    @Secured({Module.ALL})
    @GetMapping("/v1/admin-user")
    public GeneralResponse<List<AdminUserResponse>> getAllAdminUser(){
        log.info("===== Start get all admin user =====");
        List<AdminUserResponse> allUser = adminUserService.getAllAdminUser();
        log.info("===== End get all admin user size {} =====", allUser.size());
        return new GeneralResponse<>(SUCCESS, allUser);
    }

    @Secured({Module.ALL})
    @PostMapping("/v1/admin-users/search")
    public GeneralResponse<SearchAdminUserResponse> searchAdminUser(
            @RequestBody(required = false) SearchAdminUserRequest searchAdminUserRequest,
            PageableRequest pageableRequest
    ) {
        log.info("Start search  admin users");
        SearchAdminUserResponse adminUserDtoPageableResponse = adminUserService.searchAdminUser(searchAdminUserRequest, pageableRequest);
        PageableResponse<AdminUserResponse> adminUserResponsePageableResponse = new PageableResponse<>();
        adminUserResponsePageableResponse.setPagination(adminUserDtoPageableResponse.getPagination());
        adminUserResponsePageableResponse.setRecords(adminUserDtoPageableResponse.getAdminUsers());
        log.info("End search admin user");
        return new GeneralResponse<>(SUCCESS,
                new SearchAdminUserResponse(adminUserResponsePageableResponse.getPagination(),
                        adminUserResponsePageableResponse.getRecords()));
    }

}
