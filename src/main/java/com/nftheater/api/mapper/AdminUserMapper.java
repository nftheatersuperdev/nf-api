package com.nftheater.api.mapper;

import com.nftheater.api.controller.adminuser.request.CreateAdminUserRequest;
import com.nftheater.api.controller.adminuser.response.AdminUserResponse;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.entity.AdminUserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminUserMapper extends EntityMapper<AdminUserDto, AdminUserEntity> {

    AdminUserResponse toResponse(AdminUserEntity entity);

    AdminUserResponse toResponse(AdminUserDto dto);

    AdminUserEntity toEntity(String firebaseId, CreateAdminUserRequest request, boolean isActive);

    List<AdminUserResponse> mapDtoToResponses(List<AdminUserDto> dtos);

}
