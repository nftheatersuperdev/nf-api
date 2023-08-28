package com.nftheater.api.mapper;

import com.nftheater.api.constant.RoleEnum;
import com.nftheater.api.controller.adminuser.request.CreateAdminUserRequest;
import com.nftheater.api.controller.adminuser.response.AdminUserResponse;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.entity.AdminUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminUserMapper extends EntityMapper<AdminUserDto, AdminUserEntity> {

    @Mapping(source = "module", target = "account")
    AdminUserResponse toResponse(AdminUserEntity entity);

    @Mapping(source = "module", target = "account")
    @Mapping(source = "role", target = "role", qualifiedByName = "mapRoleName")
    AdminUserResponse toResponse(AdminUserDto dto);

    AdminUserEntity toEntity(String firebaseId, CreateAdminUserRequest request, boolean isActive);

    List<AdminUserResponse> mapDtoToResponses(List<AdminUserDto> dtos);

    @Named("mapRoleName")
    default String mapRoleName(String role) {
        return RoleEnum.valueOf(role).getRoleNameTh();
    }
}
