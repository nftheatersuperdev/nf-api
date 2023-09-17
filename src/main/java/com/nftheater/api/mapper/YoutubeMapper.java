package com.nftheater.api.mapper;

import com.nftheater.api.controller.youtube.request.CreateYoutubeAccountRequest;
import com.nftheater.api.controller.youtube.response.YoutubeAccountResponse;
import com.nftheater.api.controller.youtube.response.YoutubeLinkUserResponse;
import com.nftheater.api.dto.YoutubeAccountDto;
import com.nftheater.api.dto.YoutubeLinkUserDto;
import com.nftheater.api.entity.YoutubeAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface YoutubeMapper extends EntityMapper<YoutubeAccountDto, YoutubeAccountEntity> {

    @Mapping(source = "email", target = "youtubeEmail")
    @Mapping(source = "password", target = "youtubePassword")
    @Mapping(source = "createdBy", target = "updatedBy")
    YoutubeAccountEntity toEntity(CreateYoutubeAccountRequest request);

    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "youtubeEmail", target = "email")
    @Mapping(source = "youtubePassword", target = "password")
    @Mapping(source = "accountLinks", target = "users")
    YoutubeAccountResponse toResponse(YoutubeAccountDto dto);

    List<YoutubeAccountResponse> mapDtoToResponses(List<YoutubeAccountDto> dtos);

    @Mapping(source = "user.expiredDate", target = "user.dayLeft", qualifiedByName = "calculateDayLeft")
    @Mapping(source = "user.expiredDate", target = "accountStatus", qualifiedByName = "getCustomerStatus")
    @Mapping(source = "accountType", target = "sort", qualifiedByName = "getTypeSort")
    YoutubeLinkUserResponse toYoutubeLinkUserResponse(YoutubeLinkUserDto dto);

    List<YoutubeLinkUserResponse> toYoutubeLinkUserResponses(List<YoutubeLinkUserDto> dtos);

    @Named("calculateDayLeft")
    public static long calculateDayLeft(ZonedDateTime expireDate) {
        return ChronoUnit.DAYS.between(ZonedDateTime.now(), expireDate);
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
        } else {
            return "รอ-หมดอายุ";
        }
    }

    @Named("getTypeSort")
    public static int getSortByAccountType(String type) {
        if ("USER".equalsIgnoreCase(type)) {
            return 2;
        }
        return 99;
    }

}
