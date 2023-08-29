package com.nftheater.api.service;

import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.controller.customer.response.CustomerResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.controller.youtube.request.*;
import com.nftheater.api.controller.youtube.response.*;
import com.nftheater.api.dto.YoutubeAccountDto;
import com.nftheater.api.dto.YoutubePackageDto;
import com.nftheater.api.entity.*;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.mapper.YoutubeMapper;
import com.nftheater.api.mapper.YoutubePackageMapper;
import com.nftheater.api.repository.YoutubeAccountLinkRepository;
import com.nftheater.api.repository.YoutubePackageRepository;
import com.nftheater.api.repository.YoutubeRepository;
import com.nftheater.api.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nftheater.api.specification.YoutubeSpecification.*;
import static com.nftheater.api.utils.BusinessUtils.getColor;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YoutubeRepository youtubeRepository;
    private final YoutubePackageRepository youtubePackageRepository;
    private final YoutubeAccountLinkRepository youtubeAccountLinkRepository;
    private final YoutubeMapper youtubeMapper;
    private final YoutubePackageMapper youtubePackageMapper;
    private final CustomerService customerService;
    private final AdminUserService adminUserService;
    private final SystemConfigService systemConfigService;

    @Transactional(readOnly = true)
    public SearchYoutubeAccountResponse searchYoutubeAccount(SearchYoutubeAccountRequest request, PageableRequest pageableRequest) {
        final Pageable pageable = PageRequest.of(
                pageableRequest.getPageZeroIndex(),
                pageableRequest.getSize(),
                Sort.by(YoutubeAccountEntity_.CREATED_DATE).ascending()
        );

        Specification<YoutubeAccountEntity> specification = Specification.where(null);
        if (request != null) {
            if (!request.getChangeDate().equalsIgnoreCase("-")) {
                specification = specification.and(changeDateEqual(request.getChangeDate()));
            }
            if (!request.getUserId().isBlank() ) {
                specification = specification.and(userIdContain(request.getUserId()));
            }
            if (!request.getAccountName().isBlank()) {
                specification = specification.and(accountNameEqual(BusinessConstants.YOUTUBE_PREFIX + "-" + request.getAccountName()));
            }
            if (request.getCustomerStatus().size() != 0) {
                specification = specification.and(customerStatusIn(request.getCustomerStatus()));
            }
            if (request.getAccountStatus().size() != 0) {
                specification = specification.and(accountStatusIn(request.getAccountStatus()));
            }
        }
        Page<YoutubeAccountEntity> youtubeAccountEntityPage = youtubeRepository.findAll(specification, pageable);
        Page<YoutubeAccountDto> youtubeAccountDtoPage = youtubeAccountEntityPage.map(youtubeMapper::toDto);
        List<YoutubeAccountDto> youtubeAccountDtoList = youtubeAccountDtoPage.getContent();

        List<YoutubeAccountResponse> youtubeAccountResponse = youtubeMapper.mapDtoToResponses(youtubeAccountDtoList);
        YoutubeLinkUserResponse adminUser = new YoutubeLinkUserResponse();
        adminUser.setColor("#FF0000");
        adminUser.setAccountStatus("Admin");
        adminUser.setAccountType("ADMIN");
        adminUser.setUser(new CustomerResponse());
        adminUser.setSort(1);
        for(YoutubeAccountResponse acct : youtubeAccountResponse){
            acct.getUsers().add(adminUser);
            fillEmptyYoutubeUser(acct);
            acct.setAvailableDevice(acct.getUsers().stream()
                    .filter(u -> "ว่าง".equalsIgnoreCase(u.getAccountStatus()))
                    .toList().size());
        }

        PaginationResponse pagination = PaginationUtils.createPagination(youtubeAccountDtoPage);
        SearchYoutubeAccountResponse response = new SearchYoutubeAccountResponse();

        // Sort
        youtubeAccountResponse.sort(Comparator.comparingInt(YoutubeAccountResponse::getAvailableDevice).reversed());

        response.setPagination(pagination);
        response.setYoutube(youtubeAccountResponse);
        return response;
    }
    public CreateYoutubeAccountResponse createYoutubeAccount(CreateYoutubeAccountRequest createRequest) throws DataNotFoundException, InvalidRequestException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(createRequest.getCreatedBy());
        String adminUser = adminUserEntity.getAdminName();

        Long nextSeq = youtubeRepository.getYoutubeAccountNameSeq();

        YoutubeAccountEntity newYoutubeAccount = youtubeMapper.toEntity(createRequest);
        newYoutubeAccount.setAccountName(BusinessConstants.YOUTUBE_PREFIX.concat("-").concat(String.format("%05d", nextSeq)));
        newYoutubeAccount.setAccountStatus("กำลังใช้งานอยู่");
        newYoutubeAccount.setCreatedBy(adminUser);
        newYoutubeAccount.setUpdatedBy(adminUser);
        try {
            youtubeRepository.saveAndFlush(newYoutubeAccount);
        } catch(DataIntegrityViolationException ex) {
            log.error(ex.getMessage());
            if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
                throw new InvalidRequestException(" " + newYoutubeAccount.getYoutubeEmail() + " ซ้ำกับบัญชีอื่นในระบบ");
            }
        } catch (Exception ex) {
            throw new InvalidRequestException(" เกิดข้อผิดพลาดในระบบ กรุณาติดต่อผู้ดูแลระบบ");
        }

        return new CreateYoutubeAccountResponse(newYoutubeAccount.getId(), newYoutubeAccount.getAccountName());
    }

    @Transactional(readOnly = true)
    public YoutubeAccountResponse getYoutubeAccountById(UUID accountId) throws DataNotFoundException{
       final YoutubeAccountDto youtubeAccount = youtubeRepository.findById(accountId)
               .map(youtubeMapper::toDto)
               .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี " + accountId));

       YoutubeAccountResponse accountResponse = youtubeMapper.toResponse(youtubeAccount);
       return accountResponse;
    }

    public List<GetYoutubePackageResponse> getAllYoutubePackage(String packageType) {
        List<YoutubePackageDto> allPackageDtos = youtubePackageRepository.findByType(packageType)
                .stream().map(youtubePackageMapper::toDto)
                .toList();
        List<GetYoutubePackageResponse> allPackageResponse = allPackageDtos.stream()
                .map(youtubePackageMapper::toPackageResponse)
                .toList();
        return allPackageResponse;
    }

    public void fillEmptyYoutubeUser(YoutubeAccountResponse youtubeAccount) {
        // Get all config
        List<SystemConfigResponse> configs = systemConfigService.getAllConfig();
        String maxUserString = configs.stream().filter(
                config -> config.getConfigName().equalsIgnoreCase("YOUTUBE_MAX_USER"))
                .findFirst().orElse(null).getConfigValue();
        int maxUser = maxUserString != null ? Integer.valueOf(maxUserString) : 5;
        int countUser = youtubeAccount.getUsers().stream().collect(Collectors.toList()).size();
        while (countUser < maxUser) {
            YoutubeLinkUserResponse userLink = new YoutubeLinkUserResponse();
            userLink.setAccountType("USER");
            userLink.setAccountStatus("ว่าง");
            userLink.setUser(null);
            userLink.setColor("#008000");
            userLink.setSort(99);
            youtubeAccount.getUsers().add(userLink);
            countUser++;
        }
        // Set color
        youtubeAccount.getUsers().stream().forEach(user -> user.setColor(getColor(user.getAccountStatus())));

        // Sort ADMIN-1 USER-2
        youtubeAccount.getUsers().sort(Comparator.comparingInt(YoutubeLinkUserResponse::getSort));

    }

    @Transactional
    public void linkUserToYoutubeAccount(UUID accountId, UpdateLinkUserYoutubeRequest updateLinkUserYoutubeRequest, UUID adminId) throws DataNotFoundException, InvalidRequestException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        final YoutubeAccountEntity youtubeAccountEntity = youtubeRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Youtube : " + accountId));
        final CustomerEntity customerEntity = customerService.getCustomerByUserId(updateLinkUserYoutubeRequest.getUserId());

        // Validate
        final YoutubeAccountLinkEntity existingAccountLink = youtubeAccountLinkRepository
                .findByUserId(customerEntity.getId())
                .orElse(null);
        if (existingAccountLink != null) {
            throw new InvalidRequestException("ลูกค้าถูกเพิ่มในบัญชีนี้หรือบัญชีอื่นแล้ว");
        }

        // Check max other
        int existingUser = youtubeAccountEntity.getAccountLinks().stream()
                .filter(type -> "USER".equalsIgnoreCase(type.getAccountType()))
                .collect(Collectors.toList()).size();
        int maxOther = Integer.valueOf(systemConfigService.getSystemConfigByConfigName("YOUTUBE_MAX_USER").getConfigValue());
        if (maxOther <= existingUser) {
            throw new InvalidRequestException("จำนวนผู้ใช้งานของบัญชี Youtube " + youtubeAccountEntity.getAccountName() + " เต็มหมดแล้ว กรุณาเลือกบัญชีอื่น");
        }

        YoutubeAccountLinkEntity accountLinkEntity = new YoutubeAccountLinkEntity();
        YoutubeAccountLinkEntityId id = new YoutubeAccountLinkEntityId();
        id.setAccountId(accountId);
        id.setUserId(customerEntity.getId());
        accountLinkEntity.setId(id);
        accountLinkEntity.setAccountType("USER");
        accountLinkEntity.setAddedDate(ZonedDateTime.now());
        accountLinkEntity.setAddedBy(adminUser);
        accountLinkEntity.setAccount(youtubeAccountEntity);
        accountLinkEntity.setUser(customerEntity);

        youtubeAccountLinkRepository.save(accountLinkEntity);

        // Extend Customer day left.
        long newDayLeft = customerService.extendDayForUser(customerEntity, updateLinkUserYoutubeRequest.getExtendDay(), adminUser);
    }

    public void removeUserFromYoutubeAccount(UUID accountId, String userId) throws DataNotFoundException {
        final YoutubeAccountEntity youtubeAccountEntity = youtubeRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Youtube : " + accountId));
        final CustomerEntity customerEntity = customerService.getCustomerByUserId(userId);

        YoutubeAccountLinkEntityId id = new YoutubeAccountLinkEntityId();
        id.setAccountId(accountId);
        id.setUserId(customerEntity.getId());

        final YoutubeAccountLinkEntity removedEntity = youtubeAccountLinkRepository.findById(id)
                .orElse(null);
        if (removedEntity == null) {
            throw new DataNotFoundException("ไม่พบลูกค้า " + userId + " ในบัญชี Youtube : " + youtubeAccountEntity.getAccountName());
        }

        youtubeAccountLinkRepository.delete(removedEntity);
    }

    @Transactional
    public void updateYoutubeAccountStatus(UUID accountId, String status, UUID adminId) throws DataNotFoundException, InvalidRequestException {
        final YoutubeAccountEntity youtubeAccountEntity = youtubeRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Youtube : " + accountId));
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        if (!status.isBlank() && status != null) {
            if ("ปิดการใช้งานชั่วคราว".equalsIgnoreCase(status)) {
                List<YoutubeAccountLinkEntity> accountLink = youtubeAccountLinkRepository.findByAccountId(accountId);
                if (accountLink.size() != 1) {
                    throw new InvalidRequestException("ไม่สามารถปิดบัญชีชั่วคราวได้ เนื่องจากยังมีลูกค้าอยู่ในบัญชีนี้");
                }
            }
        }

        youtubeAccountEntity.setAccountStatus(status);
        youtubeAccountEntity.setUpdatedBy(adminUser);
    }

    @Transactional
    public YoutubeAccountResponse updateYoutubeAccount(UUID accountId, UUID adminId, UpdateYoutubeAccountRequest request) throws DataNotFoundException{
        final YoutubeAccountEntity youtubeAccountEntity = youtubeRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Youtube : " + accountId));
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        youtubeAccountEntity.setUpdatedBy(adminUser);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            youtubeAccountEntity.setYoutubePassword(request.getPassword());
        }
        if (request.getChangeDate() != null && !request.getChangeDate().isEmpty()) {
            youtubeAccountEntity.setChangeDate(request.getChangeDate());
        }
        YoutubeAccountResponse response = youtubeMapper.toResponse(youtubeMapper.toDto(youtubeAccountEntity));
        return response;
    }

    @Transactional
    public void transferUserToNewAccount(UUID toAccountId, TransferUserRequest transferUserRequest, UUID adminId) throws DataNotFoundException, InvalidRequestException {
        // Get Youtube Account
        final YoutubeAccountEntity toYoutubeAccountEntity = youtubeRepository.findById(toAccountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Youtube : " + toAccountId));
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        int existingUser = toYoutubeAccountEntity.getAccountLinks().size();
        int maxUser = Integer.valueOf(systemConfigService.getSystemConfigByConfigName("YOUTUBE_MAX_USER").getConfigValue());
        if (maxUser < transferUserRequest.getUserIds().size() + existingUser - 1) {
            throw new InvalidRequestException("ไม่สามารถย้ายลูกค้าได้ เนื่่องจากจำนวนลูกค้าในบัญชี "+ toYoutubeAccountEntity.getAccountName() + " เต็ม");
        }

        // Get Customer ID
        List<CustomerEntity> customerEntities = new ArrayList<>();
        for (String userId : transferUserRequest.getUserIds()) {
            customerEntities.add(customerService.getCustomerByUserId(userId));
        }
        // Get Customer Entity
        for(CustomerEntity customerEntity : customerEntities) {
            // Remove User
            removeUserFromYoutubeAccount(transferUserRequest.getFromAccountId(), customerEntity.getUserId());
            // Link to new Account
            UpdateLinkUserYoutubeRequest updateLinkUserYoutubeRequest = new UpdateLinkUserYoutubeRequest();
            updateLinkUserYoutubeRequest.setUserId(customerEntity.getUserId());
            updateLinkUserYoutubeRequest.setExtendDay(0);
            linkUserToYoutubeAccount(toAccountId, updateLinkUserYoutubeRequest, adminId);
        }
    }
}
