package com.nftheater.api.mapper;

import com.nftheater.api.constant.NetflixAccountType;
import com.nftheater.api.controller.netflix.request.CreateNetflixAccountRequest;
import com.nftheater.api.controller.netflix.response.NetflixAccountResponse;
import com.nftheater.api.controller.netflix.response.NetflixLinkUserResponse;
import com.nftheater.api.dto.NetflixAccountDto;
import com.nftheater.api.dto.NetflixAdditionalAccountDto;
import com.nftheater.api.dto.NetflixLinkUserDto;
import com.nftheater.api.entity.NetflixAccountEntity;
import com.nftheater.api.entity.NetflixAccountLinkEntity;
import com.nftheater.api.entity.NetflixLinkAdditionalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NetflixAccountMapper extends EntityMapper<NetflixAccountDto, NetflixAccountEntity> {

    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "netflixEmail", target = "email")
    @Mapping(source = "netflixPassword", target = "password")
    @Mapping(source = "accountLinks", target = "users")
    @Mapping(source = "additionalAccounts", target = "additionalAccounts")
    NetflixAccountResponse toResponse(NetflixAccountDto dto);

    List<NetflixAccountResponse> mapDtoToResponses(List<NetflixAccountDto> dtos);

    @Mapping(source = "email", target = "netflixEmail")
    @Mapping(source = "password", target = "netflixPassword")
    @Mapping(source = "createdBy", target = "updatedBy")
    NetflixAccountEntity toEntity(CreateNetflixAccountRequest request);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "addedDate", target = "addedDate")
    @Mapping(source = "addedBy", target = "addedBy")
    NetflixLinkUserDto toNetflixLinkUserDto(NetflixAccountLinkEntity entity);

    List<NetflixLinkUserDto> toNetflixLinkUserDtos(List<NetflixAccountLinkEntity> entities);

    @Mapping(source = "user.expiredDate", target = "user.dayLeft", qualifiedByName = "calculateDayLeft")
    @Mapping(source = "user.expiredDate", target = "accountStatus", qualifiedByName = "getCustomerStatus")
    @Mapping(source = "accountType", target = "sort", qualifiedByName = "getTypeSort")
    NetflixLinkUserResponse toNetflixLinkUserResponse(NetflixLinkUserDto dto);

    List<NetflixLinkUserResponse> toNetflixLinkUserResponses(List<NetflixLinkUserDto> dtos);

    @Mapping(source = "additional.additionalEmail", target = "email")
    @Mapping(source = "additional.additionalPassword", target = "password")
    @Mapping(source = "additional.netflixAdditionalAccountLink.user" , target = "user")
    @Mapping(source = "additional.id", target = "additionalId")
    NetflixAdditionalAccountDto toNetflixAdditionalAccountDto(NetflixLinkAdditionalEntity entity);

    List<NetflixAdditionalAccountDto> toNetflixAdditionalAccountDtos(List<NetflixLinkAdditionalEntity> entities);

    @Mapping(source = "user", target = "user")
    NetflixLinkUserDto toNetflixLinkUserDto(NetflixAdditionalAccountDto dto);

    @Named("calculateDayLeft")
    public static long calculateDayLeft(ZonedDateTime expireDate) {
        long dayLeft = ChronoUnit.DAYS.between(ZonedDateTime.now(), expireDate);
        return dayLeft > 0 ? dayLeft : 0;
    }

    @Named("getCustomerStatus")
    public static String getCustomerStatusFromDayLeft(ZonedDateTime expireDate) {
        long dayLeft = calculateDayLeft(expireDate);
        if (dayLeft > 3) {
            return "กำลังใช้งาน";
        } else if (dayLeft == 3) {
            return "รอ-เรียกเก็บ";
        } else if (dayLeft == 2) {
            return "รอ-ทวงซ้ำ 1";
        } else if (dayLeft == 1) {
            return "รอ-ทวงซ้ำ 2";
        } else if (dayLeft == 0) {
            return "หมดอายุ";
        } else {
            return "รอ-หมดอายุ";
        }
    }

    @Named("getTypeSort")
    public static int getSortByAccountType(String type) {
        if (type.equalsIgnoreCase(NetflixAccountType.TV.name())) {
            return 1;
        } else if (type.equalsIgnoreCase(NetflixAccountType.ADDITIONAL.name())) {
            return 2;
        }
        return 3;
    }

}
