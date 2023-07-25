package com.nftheater.api.mapper;

import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.response.CustomerListResponse;
import com.nftheater.api.controller.customer.response.CustomerResponse;
import com.nftheater.api.dto.CustomerDto;
import com.nftheater.api.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDto, CustomerEntity> {

    CustomerDto toDto(CustomerEntity entity);

    CustomerResponse toResponse(CustomerDto dto);

    List<CustomerResponse> mapDtoToResponses(List<CustomerDto> dtos);

    CustomerDto mapRequestToDto(CreateCustomerRequest request);

}
