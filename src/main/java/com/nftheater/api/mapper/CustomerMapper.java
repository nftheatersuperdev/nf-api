package com.nftheater.api.mapper;

import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.response.CustomerResponse;
import com.nftheater.api.controller.member.response.CustomerProfileResponse;
import com.nftheater.api.dto.CustomerDto;
import com.nftheater.api.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDto, CustomerEntity> {

    CustomerDto toDto(CustomerEntity entity);

    @Mapping(source = "customerStatus", target = "sort", qualifiedByName = "getCustomerStatusSort")
    @Mapping(source = "expiredDate", target = "dayLeft", qualifiedByName = "calculateDayLeft")
    @Mapping(source = "actualPassword", target = "password")
    CustomerResponse toResponse(CustomerDto dto);

    List<CustomerResponse> mapDtoToResponses(List<CustomerDto> dtos);

    CustomerDto mapRequestToDto(CreateCustomerRequest request);

    CustomerProfileResponse toCustomerProfileResponse(CustomerDto dto);

    @Named("getCustomerStatusSort")
    public static int getCustomerStatusSort(String status) {
        if ("กำลังใช้งาน".equalsIgnoreCase(status)) {
            return 1;
        } else if ("รอ-เรียกเก็บ".equalsIgnoreCase(status)) {
            return 2;
        } else if ("รอ-ทวงซ้ำ 1".equalsIgnoreCase(status)) {
            return 3;
        } else if ("รอ-ทวงซ้ำ 2".equalsIgnoreCase(status)) {
            return 4;
        } else {
            return 99;
        }
    }
    @Named("calculateDayLeft")
    public static long calculateDayLeft(ZonedDateTime expireDate) {
        ZonedDateTime now = ZonedDateTime.now();
        now = now.with(LocalTime.of(1,0));
        long dayLeft = ChronoUnit.DAYS.between(now, expireDate);
        return dayLeft > 0 ? dayLeft : 0;
    }

}
