package com.nftheater.api.service;

import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.request.ExtendDayCustomerRequest;
import com.nftheater.api.controller.customer.request.SearchCustomerRequest;
import com.nftheater.api.controller.customer.response.*;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.dto.CustomerDto;
import com.nftheater.api.entity.AdminUserEntity;
import com.nftheater.api.entity.CustomerEntity;
import com.nftheater.api.entity.CustomerEntity_;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.mapper.CustomerMapper;
import com.nftheater.api.repository.CustomerRepository;
import com.nftheater.api.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.nftheater.api.specification.CustomerSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    public static final String NF_PREFIX = "NF";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AdminUserService adminUserService;
    private final SystemConfigService systemConfigService;

    public SearchCustomerResponse searchCustomer(SearchCustomerRequest request, PageableRequest pageableRequest) {
        final Pageable pageable = PageRequest.of(
                pageableRequest.getPageZeroIndex(),
                pageableRequest.getSize(),
                Sort.by(CustomerEntity_.UPDATED_DATE).descending()
        );
        Specification<CustomerEntity> specification = Specification.where(null);
        if (request != null) {
            if (!request.getUserId().isBlank()) {
                specification = specification.and(userIdContain(request.getUserId()));
            }
            if (!request.getEmail().isBlank()) {
                specification = specification.and(emailContain(request.getEmail()));
            }
            if (!request.getLineId().isBlank()) {
                specification = specification.and(lineIdContain(request.getLineId()));
            }
            if (request.getStatus().size() != 0) {
                specification = specification.and(customerStatusIn(request.getStatus()));
            }
            if (!"ALL".equalsIgnoreCase(request.getAccount())) {
                specification = specification.and(accountEqual(request.getAccount()));
            }
        }

        Page<CustomerEntity> customerEntityPage = customerRepository.findAll(specification, pageable);
        Page<CustomerDto> customerDtoPage = customerEntityPage.map(customerMapper::toDto);
        List<CustomerDto> customerDtoList = customerDtoPage.getContent();

        PaginationResponse pagination = PaginationUtils.createPagination(customerDtoPage);

        List<CustomerResponse> customerResponse = customerMapper.mapDtoToResponses(customerDtoList);
        customerResponse.sort(Comparator.comparingInt(CustomerResponse::getSort));

        SearchCustomerResponse response = new SearchCustomerResponse();
        response.setCustomer(customerResponse);
        response.setPagination(pagination);

        return response;
    }

    public CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
        CustomerDto customerDto = customerMapper.mapRequestToDto(createCustomerRequest);
        customerDto.setUserId(generateUserId());
        customerDto.setPassword(generatePassword());
        customerDto.setExpiredDate(ZonedDateTime.now());
        customerDto.setCustomerStatus("ปกติ");
        CustomerEntity customerEntity = customerMapper.toEntity(customerDto);
        customerRepository.saveAndFlush(customerEntity);

        CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        createCustomerResponse.setId(customerEntity.getId());
        createCustomerResponse.setUserId(customerEntity.getUserId());
        createCustomerResponse.setPassword(customerEntity.getPassword());
        return createCustomerResponse;
    }
    public List<CustomerListResponse> getCustomerList() {
        List<CustomerEntity> customerEntities = customerRepository.findAll();
        List<CustomerDto> customerDtoList = customerEntities.stream().map(customerMapper::toDto).collect(Collectors.toList());
        List<CustomerListResponse> customerListResponses = new ArrayList<>();
        for(CustomerDto dto : customerDtoList) {
            CustomerListResponse customerListResponse = new CustomerListResponse();
            customerListResponse.setValue(dto.getUserId());
            customerListResponse.setLabel(dto.getLineId());
            customerListResponse.setFilterLabel(dto.getUserId() == null ? "" : dto.getUserId()
                    .concat("|")
                    .concat(dto.getEmail() == null ? "" : dto.getEmail())
                    .concat("|")
                    .concat(dto.getLineId() == null ? "" : dto.getLineId()));
            customerListResponses.add(customerListResponse);
        }
        return customerListResponses;
    }

    public CustomerResponse extendExpiredDateForCustomer(String userId, ExtendDayCustomerRequest request, UUID adminId) throws DataNotFoundException {
        final CustomerEntity customerEntity = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userId + " is not found."));
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getFirstName() + " " + adminUserEntity.getLastName();

        long availableDay = this.extendDayForUser(customerEntity, request.getExtendDay(), adminUser);
        CustomerDto customerDto = customerMapper.toDto(customerEntity);
        CustomerResponse customerResponse = customerMapper.toResponse(customerDto);
        customerResponse.setDayLeft(availableDay);
        return customerResponse;
    }

    public CustomerEntity getCustomerByUserId(String userId) throws DataNotFoundException {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() ->new DataNotFoundException("ไม่พบลูกค้า " + userId));
    }

    public long extendDayForUser(CustomerEntity customerEntity, int extendDay, String adminUser) {
        ZonedDateTime newExpiredDateTime = customerEntity.getExpiredDate().plusDays(extendDay);
        customerEntity.setExpiredDate(newExpiredDateTime);
        customerEntity.setCustomerStatus("กำลังใช้งาน");
        customerEntity.setUpdatedBy(adminUser);
        customerRepository.save(customerEntity);

        return ChronoUnit.DAYS.between(ZonedDateTime.now(), newExpiredDateTime);
    }

    public CustomerResponse updateCustomer(String userId, UpdateCustomerRequest updateCustomerRequest, UUID adminId) throws DataNotFoundException {
        final CustomerEntity customerEntity = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบข้อมูลลูกค้า"));

        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getFirstName() + " " + adminUserEntity.getLastName();

        CustomerEntity savedEntity = customerEntity;
        savedEntity.setCustomerStatus(updateCustomerRequest.getCustomerStatus());
        savedEntity.setUpdatedBy(adminUser);
        savedEntity = customerRepository.save(savedEntity);

        CustomerDto customerDto = customerMapper.toDto(savedEntity);

        return customerMapper.toResponse(customerDto);
    }

    public String getNextStatusForCustomer(String currentStatus) throws DataNotFoundException {
        SystemConfigResponse statusFlowValues = systemConfigService.getSystemConfigByConfigName("CUSTOMER_STATUS_FLOW");
        List<String> statusFlow = Arrays.stream(statusFlowValues.getConfigValue().split(",")).toList();
        int indexOfCurrentStatus = statusFlow.indexOf(currentStatus);
        if (indexOfCurrentStatus != -1 && indexOfCurrentStatus+1 != statusFlow.size()) {
            return statusFlow.get(indexOfCurrentStatus+1);
        } else {
            throw new DataNotFoundException("ไม่พบสถานะถัดไป");
        }
    }

    private String generateUserId() {
        Long userIdSeq = customerRepository.getUserIdSeq();
        return NF_PREFIX.concat(String.format("%05d", userIdSeq));
    }

    @Named("generatePassword")
    public static String generatePassword() {
        Random random = new Random();
        int num = random.nextInt(100000);
        return String.format("%05d", num);
    }
}
