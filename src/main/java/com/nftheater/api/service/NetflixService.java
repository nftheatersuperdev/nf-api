package com.nftheater.api.service;

import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.constant.NetflixAccountType;
import com.nftheater.api.constant.SystemConfigName;
import com.nftheater.api.controller.customer.response.UpdateCustomerRequest;
import com.nftheater.api.controller.netflix.request.*;
import com.nftheater.api.controller.netflix.response.*;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.dto.NetflixAccountDto;
import com.nftheater.api.dto.NetflixAdditionalAccountDto;
import com.nftheater.api.dto.NetflixLinkUserDto;
import com.nftheater.api.dto.NetflixPackageDto;
import com.nftheater.api.entity.*;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.mapper.NetflixAccountMapper;
import com.nftheater.api.mapper.NetflixPackageMapper;
import com.nftheater.api.repository.*;
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

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nftheater.api.mapper.NetflixAccountMapper.getCustomerStatusFromDayLeft;
import static com.nftheater.api.specification.NetflixSpecification.*;
import static com.nftheater.api.utils.BusinessUtils.getAccountStatus;
import static com.nftheater.api.utils.BusinessUtils.getColor;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetflixService {

    private final NetflixAccountMapper netflixAccountMapper;
    private final NetflixPackageMapper netflixPackageMapper;
    private final NetflixRepository netflixRepository;
    private final NetflixAccountLinkRepository netflixAccountLinkRepository;
    private final NetflixAdditionalAccountRepository netflixAdditionalAccountRepository;
    private final NetflixLinkAdditionalRepository netflixLinkAdditionalRepository;
    private final NetflixAdditionalAccountLinkRepository netflixAdditionalAccountLinkRepository;
    private final NetflixPackageRepository netflixPackageRepository;
    private final AdminUserService adminUserService;
    private final SystemConfigService systemConfigService;
    private final CustomerService customerService;

    @Transactional(readOnly = true)
    public SearchNetflixAccountResponse searchNetflixAccount(SearchNetflixAccountRequest request, PageableRequest pageableRequest) {
        final Pageable pageable = PageRequest.of(
                pageableRequest.getPageZeroIndex(),
                pageableRequest.getSize(),
                Sort.by(NetflixAccountEntity_.CREATED_DATE).ascending()
        );

        Specification<NetflixAccountEntity> specification = Specification.where(null);
        if (request != null) {
            if (!request.getChangeDate().equalsIgnoreCase("-")) {
                specification = specification.and(changeDateEqual(request.getChangeDate()));
            }
            if (!request.getBillDate().equalsIgnoreCase("-")) {
                specification = specification.and(billDateEqual(request.getBillDate()));
            }
            if (!request.getUserId().isBlank() ) {
                specification = specification.and(userIdContain(request.getUserId()));
            }
            if (!request.getAccountName().isBlank()) {
                specification = specification.and(accountNameEqual(BusinessConstants.NETFLIX_PREFIX + "-" + request.getAccountName()));
            }
            if (!request.getAccountEmail().isBlank()) {
                specification = specification.and(accountEmailContain(request.getAccountEmail()));
            }
            if (request.getCustomerStatus().size() != 0) {
                specification = specification.and(customerStatusIn(request.getCustomerStatus()));
            }
            specification = specification.and(isActiveEqual(request.getIsActive()));
        }

        Page<NetflixAccountEntity> netflixAccountEntityPage = netflixRepository.findAll(specification, pageable);
        Page<NetflixAccountDto> netflixAccountDtoPage = netflixAccountEntityPage.map(netflixAccountMapper::toDto);
        List<NetflixAccountDto> netflixAccountDtoList = netflixAccountDtoPage.getContent();

        PaginationResponse pagination = PaginationUtils.createPagination(netflixAccountDtoPage);
        SearchNetflixAccountResponse response = new SearchNetflixAccountResponse();
        response.setPagination(pagination);
        List<NetflixAccountResponse> netflixAccountResponse = netflixAccountMapper.mapDtoToResponses(netflixAccountDtoList);

        netflixAccountResponse.stream().forEach(acct -> {
                for (NetflixAdditionalAccountResponse add : acct.getAdditionalAccounts()) {
                    if (add.getUser() != null) {
                        add.getUser().setDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), add.getUser().getExpiredDate()));
                    }
                }
            }
        );

        List<NetflixAccountResponse> netflixResponse = new ArrayList<>();
        boolean added = false;
        for (NetflixAccountResponse netflixAccount : netflixAccountResponse) {
            added = false;
            fillEmptyNetflixUser(netflixAccount);
            netflixAccount.setAvailableDevice(generateAvailableDevice(netflixAccount.getUsers()));
            netflixAccount.setTotalAvailable(netflixAccount.getAvailableDevice().getAdditionalAvailable()
                    + netflixAccount.getAvailableDevice().getTvAvailable()
                    + netflixAccount.getAvailableDevice().getOtherAvailable());

            if (request.getFilterTVAvailable() && netflixAccount.getAvailableDevice().getTvAvailable() > 0) {
                added = true;
            }

            if (request.getFilterOtherAvailable() && netflixAccount.getAvailableDevice().getOtherAvailable() > 0) {
                added = true;
            }

            if (request.getFilterAdditionalAvailable() && netflixAccount.getAvailableDevice().getAdditionalAvailable() > 0) {
                added = true;
            }

            if (added) {
                netflixResponse.add(netflixAccount);
            }
        }

        // Sort
        netflixResponse.sort(Comparator.comparingInt(NetflixAccountResponse::getTotalAvailable).reversed());

        response.setNetflix(netflixResponse);
        return response;
    }

    public CreateNetflixAccountResponse createNetflixAccount(CreateNetflixAccountRequest createNetflixAccountRequest) throws DataNotFoundException, InvalidRequestException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(createNetflixAccountRequest.getCreatedBy());

        Long nextSeq = netflixRepository.getNetflixAccountNameSeq();
        String adminUser = adminUserEntity.getAdminName();

        NetflixAccountEntity newNetflixAccount = netflixAccountMapper.toEntity(createNetflixAccountRequest);
        newNetflixAccount.setAccountName(BusinessConstants.NETFLIX_PREFIX.concat("-").concat(String.format("%05d", nextSeq)));
        newNetflixAccount.setIsActive(true);
        newNetflixAccount.setCreatedBy(adminUser);
        newNetflixAccount.setUpdatedBy(adminUser);
        try {
            netflixRepository.saveAndFlush(newNetflixAccount);
        } catch(DataIntegrityViolationException ex) {
            log.error(ex.getMessage());
            if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
                throw new InvalidRequestException(" " + newNetflixAccount.getNetflixEmail() + " ซ้ำกับบัญชีอื่นในระบบ");
            }
        } catch (Exception ex) {
            throw new InvalidRequestException(" เกิดข้อผิดพลาดในระบบ กรุณาติดต่อผู้ดูแลระบบ");
        }

        return new CreateNetflixAccountResponse(newNetflixAccount.getId(), newNetflixAccount.getAccountName());
    }

    public CreateNetflixAdditionalAccountResponse createNetflixAdditionalAccount(UUID accountId, CreateNetflixAdditionalAccountRequest createNetflixAdditionalAccountRequest) throws DataNotFoundException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(UUID.fromString(createNetflixAdditionalAccountRequest.getCreatedBy()));
        String adminUser = adminUserEntity.getAdminName();
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));

        NetflixAdditionalAccountEntity savedEntity = new NetflixAdditionalAccountEntity();
        savedEntity.setCreatedBy(createNetflixAdditionalAccountRequest.getCreatedBy());
        savedEntity.setAdditionalEmail(createNetflixAdditionalAccountRequest.getEmail());
        savedEntity.setAdditionalPassword(createNetflixAdditionalAccountRequest.getPassword());
        savedEntity.setUpdatedBy(adminUser);

        netflixAdditionalAccountRepository.saveAndFlush(savedEntity);

        NetflixLinkAdditionalEntity netflixLinkAdditionalEntity = new NetflixLinkAdditionalEntity();
        netflixLinkAdditionalEntity.setAdditional(savedEntity);
        netflixLinkAdditionalEntity.setAccount(netflixAccountEntity);
        netflixLinkAdditionalEntity.setAddedBy(adminUser);
        netflixLinkAdditionalEntity.setUpdatedBy(adminUser);
        NetflixLinkAdditionalEntityId netflixLinkAdditionalEntityId = new NetflixLinkAdditionalEntityId();
        netflixLinkAdditionalEntityId.setAdditionalId(savedEntity.getId());
        netflixLinkAdditionalEntityId.setAccountId(netflixAccountEntity.getId());
        netflixLinkAdditionalEntity.setId(netflixLinkAdditionalEntityId);

        netflixLinkAdditionalRepository.save(netflixLinkAdditionalEntity);

        CreateNetflixAdditionalAccountResponse createNetflixAdditionalAccountResponse = new CreateNetflixAdditionalAccountResponse();
        createNetflixAdditionalAccountResponse.setId(savedEntity.getId());
        return createNetflixAdditionalAccountResponse;
    }

    @Transactional
    public NetflixAccountResponse getNetflixAccount(UUID accountId) throws DataNotFoundException {
        final NetflixAccountDto netflixAccountDto = netflixRepository.findById(accountId)
                .map(netflixAccountMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));

        // Add additional user to user
        for (NetflixAdditionalAccountDto additionalAccountDto : netflixAccountDto.getAdditionalAccounts()) {
            if (additionalAccountDto.getUser() != null) {
                NetflixLinkUserDto linkUserDto = netflixAccountMapper.toNetflixLinkUserDto(additionalAccountDto);
                linkUserDto.setAccountType(NetflixAccountType.ADDITIONAL.name());
                netflixAccountDto.getAccountLinks().add(linkUserDto);
            }
        }

        NetflixAccountResponse netflixAccountResponse = netflixAccountMapper.toResponse(netflixAccountDto);

        // Set color and Update customer status
        netflixAccountResponse.getUsers().stream().forEach(user -> {
            user.setColor(getColor(user.getAccountStatus()));
            if(user.getUser().getDayLeft() <= 3 && "กำลังใช้งาน".equalsIgnoreCase(user.getUser().getCustomerStatus())) {
                String userId = user.getUser().getUserId();
                String status = getCustomerStatusFromDayLeft(user.getUser().getExpiredDate());
                UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
                updateCustomerRequest.setCustomerStatus(status);
                try {
                    customerService.updateCustomer(userId, updateCustomerRequest, BusinessConstants.DEFAULT_SYSTEM_UUID);
                } catch (DataNotFoundException e) {}
                user.getUser().setCustomerStatus(status);
            }
        });
        netflixAccountResponse.getUsers().sort(Comparator.comparingInt(NetflixLinkUserResponse::getSort));
        return netflixAccountResponse;
    }

    @Transactional
    public void linkUserToNetflixAccount(UUID accountId, UpdateLinkUserNetflixRequest request, UUID userId, boolean isTransfer) throws DataNotFoundException, InvalidRequestException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(userId);
        String adminUser = adminUserEntity.getAdminName();
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final CustomerEntity customerEntity = customerService.getCustomerByUserId(request.getUserId());

        final NetflixPackageEntity netflixPackage = netflixPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new DataNotFoundException("ไม่พบแพ็คเก็ต"));

        if(!isTransfer){
            // Validate Existing
            final NetflixAdditionalAccountLinkEntity existingAdditionalLink = netflixAdditionalAccountLinkRepository
                    .findByUserId(customerEntity.getId())
                    .orElse(null);

            if (existingAdditionalLink != null) {
                throw new InvalidRequestException("ลูกค้าถูกเพิ่มในบัญชีนี้หรือบัญชีอื่นแล้ว");
            }

            final NetflixAccountLinkEntity existAccountLink = netflixAccountLinkRepository
                    .findByUserId(customerEntity.getId())
                    .orElse(null);

            if (existAccountLink != null) {
                throw new InvalidRequestException("ลูกค้าถูกเพิ่มในบัญชีนี้หรือบัญชีอื่นแล้ว");
            }
        }

        if (NetflixAccountType.ADDITIONAL.name().equalsIgnoreCase(request.getAccountType())) {
            List<NetflixLinkAdditionalEntity> additionalLink = netflixLinkAdditionalRepository.findByAccountId(accountId);
            if (additionalLink.size() == 0) {
                throw new InvalidRequestException("บัญชี Netflix " + netflixAccountEntity.getAccountName() + " ไม่มีจอเสริม กรุณาเพิ่มจอเสริมก่อนทำรายการ");
            }
            boolean allAdditionalHaveUser = false;
            UUID seletedAdditionalId = null;
            for (NetflixLinkAdditionalEntity additionalEntity : additionalLink) {
                NetflixAdditionalAccountLinkEntity userInAdditional = netflixAdditionalAccountLinkRepository
                        .findByAdditionalAccountId(additionalEntity.getId().getAdditionalId()).orElse(null);
                if(userInAdditional != null) {
                    allAdditionalHaveUser = true;
                } else {
                    allAdditionalHaveUser = false;
                    seletedAdditionalId = additionalEntity.getAdditional().getId();
                }
            }

            if (allAdditionalHaveUser) {
                throw new InvalidRequestException("จอเสริมของบัญชี Netflix " + netflixAccountEntity.getAccountName() + " เต็มหมดแล้ว กรุณาเลือกบัญชีอื่น");
            }

            final NetflixAdditionalAccountEntity netflixAdditionalAccountEntity = netflixAdditionalAccountRepository
                    .findById(seletedAdditionalId)
                    .orElseThrow((() -> new DataNotFoundException("ไม่พบบัญชีเสริม")));

            // Link User to Additional
            NetflixAdditionalAccountLinkEntity addedEntity = new NetflixAdditionalAccountLinkEntity();
            NetflixAdditionalAccountLinkEntityId id = new NetflixAdditionalAccountLinkEntityId();
            id.setAdditionalAccountId(netflixAdditionalAccountEntity.getId());
            id.setUserId(customerEntity.getId());

            addedEntity.setId(id);
            addedEntity.setUser(customerEntity);
            addedEntity.setAddedDate(ZonedDateTime.now());
            addedEntity.setAddedBy(adminUser);
            addedEntity.setAdditionalAccount(netflixAdditionalAccountEntity);
            addedEntity.setPackageName(netflixPackage.getName());

            netflixAdditionalAccountLinkRepository.save(addedEntity);
        } else if (NetflixAccountType.TV.name().equalsIgnoreCase(request.getAccountType())) {
            if (netflixAccountEntity.getAccountLinks().size() == 0) {
                NetflixAccountLinkEntity savedAccountLinkEntity = new NetflixAccountLinkEntity();
                NetflixAccountLinkEntityId id = new NetflixAccountLinkEntityId();
                id.setAccountId(accountId);
                id.setUserId(customerEntity.getId());
                savedAccountLinkEntity.setId(id);
                savedAccountLinkEntity.setAccountType(request.getAccountType());
                savedAccountLinkEntity.setAddedBy(adminUser);
                savedAccountLinkEntity.setAddedDate(ZonedDateTime.now());
                savedAccountLinkEntity.setUser(customerEntity);
                savedAccountLinkEntity.setAccount(netflixAccountEntity);
                savedAccountLinkEntity.setPackageName(netflixPackage.getName());

                netflixAccountLinkRepository.save(savedAccountLinkEntity);
            } else {
                for (NetflixAccountLinkEntity accountLinkEntity : netflixAccountEntity.getAccountLinks()) {
                    if (NetflixAccountType.TV.name().equalsIgnoreCase(accountLinkEntity.getAccountType())) {
                        throw new InvalidRequestException("ทีวีของบัญชี Netflix " + netflixAccountEntity.getAccountName() + " เต็มหมดแล้ว กรุณาเลือกบัญชีอื่น");
                    }
                    NetflixAccountLinkEntity savedAccountLinkEntity = new NetflixAccountLinkEntity();
                    NetflixAccountLinkEntityId id = new NetflixAccountLinkEntityId();
                    id.setAccountId(accountId);
                    id.setUserId(customerEntity.getId());
                    savedAccountLinkEntity.setId(id);
                    savedAccountLinkEntity.setAccountType(request.getAccountType());
                    savedAccountLinkEntity.setAddedBy(adminUser);
                    savedAccountLinkEntity.setAddedDate(ZonedDateTime.now());
                    savedAccountLinkEntity.setUser(customerEntity);
                    savedAccountLinkEntity.setAccount(netflixAccountEntity);
                    savedAccountLinkEntity.setPackageName(netflixPackage.getName());

                    netflixAccountLinkRepository.save(savedAccountLinkEntity);
                }
            }
        } else {
            // Check max other
            int existingUser = netflixAccountEntity.getAccountLinks().stream()
                    .filter(type -> "OTHER".equalsIgnoreCase(type.getAccountType()))
                    .collect(Collectors.toList()).size();
            int maxOther = Integer.valueOf(systemConfigService.getSystemConfigByConfigName("NETFLIX_MAX_OTHER_USER").getConfigValue());
            if (maxOther <= existingUser) {
                throw new InvalidRequestException("อุปกรณ์อื่นๆของบัญชี Netflix " + netflixAccountEntity.getAccountName() + " เต็มหมดแล้ว กรุณาเลือกบัญชีอื่น");
            }
            // Link User to Netflix account
            NetflixAccountLinkEntity accountLinkEntity = new NetflixAccountLinkEntity();
            NetflixAccountLinkEntityId id = new NetflixAccountLinkEntityId();
            id.setAccountId(accountId);
            id.setUserId(customerEntity.getId());
            accountLinkEntity.setId(id);
            accountLinkEntity.setAccountType(request.getAccountType());
            accountLinkEntity.setAddedBy(adminUser);
            accountLinkEntity.setAddedDate(ZonedDateTime.now());
            accountLinkEntity.setUser(customerEntity);
            accountLinkEntity.setAccount(netflixAccountEntity);
            accountLinkEntity.setPackageName(netflixPackage.getName());

            netflixAccountLinkRepository.save(accountLinkEntity);
        }

        // Extend Customer day left.
        long newDayLeft = customerService.extendDayForUser(customerEntity, netflixPackage.getDay(), adminUser);

    }

    public void updateNetflixAccountStatus(UUID accountId, Boolean status, UUID userId) throws DataNotFoundException, InvalidRequestException {
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(userId);
        String adminUser = adminUserEntity.getAdminName();

        if(!status) {
            List<NetflixAccountLinkEntity> accountLink = netflixAccountLinkRepository.findByAccountId(accountId);
            int additionalLink = netflixLinkAdditionalRepository.getCountUserByAccountId(accountId);
            if(accountLink.size() != 0 || additionalLink != 0) {
                throw new InvalidRequestException("ไม่สามารถปิดบัญชีชั่วคราวได้ เนื่องจากยังมีลูกค้าอยู่ในบัญชีนี้");
            }
        }

        netflixAccountEntity.setIsActive(status);
        netflixAccountEntity.setUpdatedBy(adminUser);

        netflixRepository.save(netflixAccountEntity);
    }

    public void removeUserFromNetflixAccount(UUID accountId, String userId) throws DataNotFoundException {
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final CustomerEntity customerEntity = customerService.getCustomerByUserId(userId);

        NetflixAccountLinkEntityId id = new NetflixAccountLinkEntityId();
        id.setAccountId(accountId);
        id.setUserId(customerEntity.getId());

        final NetflixAccountLinkEntity removedEntity = netflixAccountLinkRepository.findById(id)
                .orElse(null);
        if (removedEntity == null ) {
            throw new DataNotFoundException("ไม่พบลูกค้า " + userId + " ในบัญชี Netflix : " + netflixAccountEntity.getAccountName());
        }
        netflixAccountLinkRepository.delete(removedEntity);

    }

    public void removeUserFromAdditionalNetflixAccount(UUID accountId, UUID additionalId, String userId) throws DataNotFoundException {
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final CustomerEntity customerEntity = customerService.getCustomerByUserId(userId);

        NetflixLinkAdditionalEntityId netflixLinkAdditionalEntityId = new NetflixLinkAdditionalEntityId();
        netflixLinkAdditionalEntityId.setAccountId(accountId);
        netflixLinkAdditionalEntityId.setAdditionalId(additionalId);

        final NetflixLinkAdditionalEntity netflixLinkAdditionalEntity = netflixLinkAdditionalRepository.findById(netflixLinkAdditionalEntityId)
                .orElseThrow(() -> new DataNotFoundException("บัญชี Netflix ไม่่ถูกต้อง"));

        NetflixAdditionalAccountLinkEntityId removedId = new NetflixAdditionalAccountLinkEntityId();
        removedId.setAdditionalAccountId(additionalId);
        removedId.setUserId(customerEntity.getId());

        final NetflixAdditionalAccountLinkEntity removedEntity = netflixAdditionalAccountLinkRepository.findById(removedId)
                .orElseThrow(null);

        if (removedEntity == null) {
            throw new DataNotFoundException("ไม่พบลูกค้า " + userId + " ในบัญชี Netflix : " + netflixAccountEntity.getAccountName());
        }
        netflixAdditionalAccountLinkRepository.delete(removedEntity);
    }

    public List<GetAvailableAdditionAccountResponse> getAvailableAdditionAccount() {
        List<NetflixAdditionalAccountEntity> availableList = netflixAdditionalAccountRepository.getAvailableAccount();
        List<GetAvailableAdditionAccountResponse> responses = new ArrayList<>();
        for (NetflixAdditionalAccountEntity entity : availableList) {
            GetAvailableAdditionAccountResponse gaa = new GetAvailableAdditionAccountResponse();
            gaa.setAdditionalId(entity.getId());
            gaa.setEmail(entity.getAdditionalEmail());
            responses.add(gaa);
        }
        return responses;
    }

    public void unlinkAdditionAccount(UUID accountId, UUID additionalId, UUID adminId) throws DataNotFoundException {
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final NetflixAdditionalAccountEntity additionalAccountEntity = netflixAdditionalAccountRepository.findById(additionalId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชีเสริม : " + additionalId));

        NetflixLinkAdditionalEntityId checkId = new NetflixLinkAdditionalEntityId();
        checkId.setAdditionalId(additionalId);
        checkId.setAccountId(accountId);

        final NetflixLinkAdditionalEntity linkAdditionalEntity = netflixLinkAdditionalRepository.findById(checkId)
                .orElse(null);

        if( linkAdditionalEntity == null) {
            throw new DataNotFoundException("ไม่พบบัญชีเสริม : " + additionalId + " ภายใต้บัญชี Netflix : " + accountId);
        }

        netflixLinkAdditionalRepository.deleteById(checkId);
    }

    public void linkAdditionAccount(UUID accountId, UUID additionalId, UUID adminId) throws DataNotFoundException {
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final NetflixAdditionalAccountEntity additionalAccountEntity = netflixAdditionalAccountRepository.findById(additionalId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชีเสริม : " + additionalId));

        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();


        NetflixLinkAdditionalEntityId id = new NetflixLinkAdditionalEntityId();
        id.setAdditionalId(additionalId);
        id.setAccountId(accountId);

        NetflixLinkAdditionalEntity linkAdditionalEntity = new NetflixLinkAdditionalEntity();
        linkAdditionalEntity.setId(id);
        linkAdditionalEntity.setAddedBy(adminUser);
        linkAdditionalEntity.setUpdatedBy(adminUser);
        linkAdditionalEntity.setAccount(netflixAccountEntity);
        linkAdditionalEntity.setAdditional(additionalAccountEntity);
        netflixLinkAdditionalRepository.save(linkAdditionalEntity);
    }

    public UpdateNetflixAccountResponse updateNetflixAccount(UUID accountId, UUID adminId, UpdateNetflixAccountRequest request) throws DataNotFoundException{
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));

        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        NetflixAccountEntity savedfNetflixAccountEntity = netflixAccountEntity;
        savedfNetflixAccountEntity.setNetflixPassword(request.getPassword());
        savedfNetflixAccountEntity.setChangeDate(request.getChangeDate());
        savedfNetflixAccountEntity.setBillDate(request.getBillDate());
        savedfNetflixAccountEntity.setUpdatedBy(adminUser);

        savedfNetflixAccountEntity = netflixRepository.save(savedfNetflixAccountEntity);
        UpdateNetflixAccountResponse netflixAccountResponse = new UpdateNetflixAccountResponse();
        netflixAccountResponse.setId(savedfNetflixAccountEntity.getId());
        return  netflixAccountResponse;
    }

    public List<GetNetflixAccountResponse> getAllNetflixAccount() {
        List<NetflixAccountEntity> netflixAccountDtos = netflixRepository.findAll(Sort.by(NetflixAccountEntity_.CREATED_DATE).ascending());
        List<GetNetflixAccountResponse> netflixAccountResponses = new ArrayList<>();
        for(NetflixAccountEntity entity : netflixAccountDtos) {
            GetNetflixAccountResponse netflixAccountResponse = new GetNetflixAccountResponse();
            netflixAccountResponse.setAccountId(entity.getId());
            netflixAccountResponse.setAccountName(entity.getAccountName());
            netflixAccountResponses.add(netflixAccountResponse);
        }
        return netflixAccountResponses;
    }

    public UpdateAdditionalAccountResponse updateAdditionalAccount(
            UUID accountId,
            UUID additionalId,
            UUID adminId,
            UpdateAdditionalAccountRequest request) throws DataNotFoundException{
        final NetflixAccountEntity netflixAccountEntity = netflixRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + accountId));
        final NetflixAdditionalAccountEntity additionalAccountEntity = netflixAdditionalAccountRepository.findById(additionalId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชีเสริม : " + additionalId));

        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        NetflixLinkAdditionalEntityId checkId = new NetflixLinkAdditionalEntityId();
        checkId.setAdditionalId(additionalId);
        checkId.setAccountId(accountId);

        final NetflixLinkAdditionalEntity linkAdditionalEntity = netflixLinkAdditionalRepository.findById(checkId)
                .orElse(null);

        if( linkAdditionalEntity == null) {
            throw new DataNotFoundException("ไม่พบบัญชีเสริม : " + additionalId + " ภายใต้บัญชี Netflix : " + accountId);
        }

        NetflixAdditionalAccountEntity savedEntity = additionalAccountEntity;
        savedEntity.setUpdatedBy(adminUser);
        savedEntity.setAdditionalEmail(request.getEmail());
        savedEntity.setAdditionalPassword(request.getPassword());

        savedEntity = netflixAdditionalAccountRepository.save(savedEntity);
        UpdateAdditionalAccountResponse response = new UpdateAdditionalAccountResponse();
        response.setId(savedEntity.getId());
        return response;
    }

    @Transactional
    public void transferUserToNewAccount(UUID toAccountId, TransferUserRequest transferUserRequest, UUID adminId) throws DataNotFoundException, InvalidRequestException {
        // Get Netflix account
        final NetflixAccountEntity toNetflixAccountEntity = netflixRepository.findById(toAccountId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบบัญชี Netflix : " + toAccountId));

        int existingAdditionalUser = netflixRepository.getAdditionalUserFromAccount(toAccountId);
        int existingUser = netflixRepository.getUserFromAccount(toAccountId);
        int maxUser = Integer.valueOf(systemConfigService.getSystemConfigByConfigName("NETFFLIX_MAX_USER").getConfigValue());
        if (maxUser < transferUserRequest.getUserIds().size() + existingUser + existingAdditionalUser) {
            throw new InvalidRequestException("ไม่สามารถย้ายลูกค้าได้ เนื่่องจากจำนวนลูกค้าในบัญชี "+ toNetflixAccountEntity.getAccountName() + " เต็ม");
        }
        // Get Customer ID
        List<CustomerEntity> customerEntities = new ArrayList<>();
        for (String userId : transferUserRequest.getUserIds()) {
            customerEntities.add(customerService.getCustomerByUserId(userId));
        }
        // Get Customer Entity
        for(CustomerEntity customerEntity : customerEntities) {
            // Remove user
            String accountType = "";
            String packageName = "";
            String device = "";
            if (customerEntity.getNetflixAdditionalAccountLinks().size() == 0) {
                accountType = customerEntity.getNetflixAccountLinks().get(0).getAccountType();
                packageName = customerEntity.getNetflixAccountLinks().get(0).getPackageName();
                device = accountType;
                removeUserFromNetflixAccount(transferUserRequest.getFromAccountId(), customerEntity.getUserId());
            } else {
                accountType = "ADDITIONAL";
                NetflixAdditionalAccountLinkEntity additionalEntity = netflixAdditionalAccountLinkRepository.findByUserId(customerEntity.getId()).get();
                UUID additionalId = additionalEntity.getAdditionalAccount().getId();
                packageName = additionalEntity.getPackageName();
                device = "TV";
                removeUserFromAdditionalNetflixAccount(transferUserRequest.getFromAccountId(),additionalId, customerEntity.getUserId());
            }
            // Get Package ID
            NetflixPackageDto packageDto = netflixPackageRepository.findByNameAndDevice(packageName, device).map(netflixPackageMapper::toDto)
                    .orElse(new NetflixPackageDto());

            // Link to new Account
            UpdateLinkUserNetflixRequest updateLinkUserNetflixRequest = new UpdateLinkUserNetflixRequest();
            updateLinkUserNetflixRequest.setAccountType(accountType);
            updateLinkUserNetflixRequest.setUserId(customerEntity.getUserId());
            updateLinkUserNetflixRequest.setExtendDay(0);
            updateLinkUserNetflixRequest.setPackageId(packageDto.getId());
            linkUserToNetflixAccount(toAccountId, updateLinkUserNetflixRequest, adminId, true);
        }
        
    }

    public List<GetNetflixPackageResponse> getAllNetflixPackageByDevice(String device) {
        List<NetflixPackageDto> allPackageDtos = netflixPackageRepository.findByDevice(device)
                .stream().map(netflixPackageMapper::toDto)
                .toList();
        log.info("All Netflix package size : {}", allPackageDtos.size());
        List<GetNetflixPackageResponse> allPackageResponse = allPackageDtos.stream()
                .map(netflixPackageMapper::toPackageResponse)
                .toList();
        return allPackageResponse;
    }

    public Integer getTransactionToday() {
        ZonedDateTime today = ZonedDateTime.now();
        ZonedDateTime sod = today.toLocalDate().atStartOfDay(today.getZone());
        ZonedDateTime eod = today.with(LocalTime.of(23, 59, 59));
        log.info("Get Transaction between {} and {}", sod, eod);

        Specification<NetflixAccountLinkEntity> specification = Specification.where(null);
        specification = specification.and(overlapAddedDate(sod, eod));
        List<NetflixAccountLinkEntity> netflixAccountLinkEntity = netflixAccountLinkRepository.findAll(specification);
        log.info("Transaction of TV and Other : {}", netflixAccountLinkEntity.size());

        Specification<NetflixLinkAdditionalEntity> specification1 = Specification.where(null);
        specification1 = specification1.and(overlapAdditionalAddedDate(sod, eod));
        List<NetflixLinkAdditionalEntity> additionalLinkEntity = netflixLinkAdditionalRepository.findAll(specification1);
        log.info("Transaction of Additional Screen : {}", additionalLinkEntity.size());

        return netflixAccountLinkEntity.size() + additionalLinkEntity.size();
    }

    public void fillEmptyNetflixUser(NetflixAccountResponse netflixAccount) {
        // Get all config
        List<SystemConfigResponse> configs = systemConfigService.getAllConfig();
        String maxTvUserString = configs.stream().filter(
                        config -> config.getConfigName().equalsIgnoreCase(SystemConfigName.NETFLIX_MAX_TV_USER))
                .findFirst().orElse(null).getConfigValue();
        String maxOtherUserString = configs.stream().filter(
                        config -> config.getConfigName().equalsIgnoreCase(SystemConfigName.NETFLIX_MAX_OTHER_USER))
                .findFirst().orElse(null).getConfigValue();

        int maxTvUser = maxTvUserString != null ? Integer.valueOf(maxTvUserString) : 3;
        int maxOtherUser = maxOtherUserString != null ? Integer.valueOf(maxOtherUserString) : 4;
        // Add additional user to users
        for (NetflixAdditionalAccountResponse additionalAccount : netflixAccount.getAdditionalAccounts()) {
            NetflixLinkUserResponse linkUserResponse = new NetflixLinkUserResponse();
            linkUserResponse.setAccountType(NetflixAccountType.ADDITIONAL);
            linkUserResponse.setSort(2);
            if (additionalAccount.getUser() != null) {
                linkUserResponse.setAccountStatus(getAccountStatus(additionalAccount.getUser()));
                linkUserResponse.setUser(additionalAccount.getUser());
            } else {
                linkUserResponse.setAccountStatus(getAccountStatus(null));
                linkUserResponse.setUser(null);
            }
            netflixAccount.getUsers().add(linkUserResponse);
        }

        int countTvAccount = netflixAccount.getUsers().stream()
                .filter(acct -> acct.getAccountType().equals(NetflixAccountType.TV)).collect(Collectors.toList()).size();
        int countAdditionalAccount = netflixAccount.getUsers().stream()
                .filter(acct -> acct.getAccountType().equals(NetflixAccountType.ADDITIONAL)).collect(Collectors.toList()).size();
        int countOtherAccount = netflixAccount.getUsers().stream()
                .filter(acct -> acct.getAccountType().equals(NetflixAccountType.OTHER)).collect(Collectors.toList()).size();

        while (countTvAccount + countAdditionalAccount < maxTvUser) {
            NetflixLinkUserResponse linkTvUser = new NetflixLinkUserResponse();
            if (countTvAccount == 0) {
                linkTvUser.setAccountType(NetflixAccountType.TV);
                linkTvUser.setAccountStatus(getAccountStatus(null));
                linkTvUser.setColor("#008000");
                linkTvUser.setSort(1);

                linkTvUser.setUser(null);
                netflixAccount.getUsers().add(linkTvUser);
                countTvAccount++;
            } else {
                linkTvUser.setAccountType(NetflixAccountType.ADDITIONAL);
                linkTvUser.setAccountStatus("ยังไม่เปิดจอเสริม");
                linkTvUser.setColor("#000000");
                linkTvUser.setSort(2);

                linkTvUser.setUser(null);
                netflixAccount.getUsers().add(linkTvUser);
                countAdditionalAccount++;
            }
        }

        while (countOtherAccount < maxOtherUser) {
            NetflixLinkUserResponse linkTvUser = new NetflixLinkUserResponse();
            linkTvUser.setAccountType(NetflixAccountType.OTHER);
            linkTvUser.setSort(3);
            linkTvUser.setAccountStatus(getAccountStatus(null));
            linkTvUser.setUser(null);
            netflixAccount.getUsers().add(linkTvUser);
            linkTvUser.setColor("#008000");
            countOtherAccount++;
        }
        // Set color
        netflixAccount.getUsers().stream().forEach(user -> user.setColor(getColor(user.getAccountStatus())));
        // Sort TV-1 ADDITIONAL-2 OTHER-3
        netflixAccount.getUsers().sort(Comparator.comparingInt(NetflixLinkUserResponse::getSort));
    }

    private AvailableDeviceResponse generateAvailableDevice(List<NetflixLinkUserResponse> users) {
        AvailableDeviceResponse resp = new AvailableDeviceResponse();
        resp.setTvAvailable(users.stream()
                .filter(u -> u.getAccountType().equals(NetflixAccountType.TV) && u.getAccountStatus().equalsIgnoreCase("ว่าง"))
                .toList().size());
        resp.setOtherAvailable(users.stream()
                .filter(u -> u.getAccountType().equals(NetflixAccountType.OTHER) && u.getAccountStatus().equalsIgnoreCase("ว่าง"))
                .toList().size());
        resp.setAdditionalAvailable(users.stream()
                .filter(u -> u.getAccountType().equals(NetflixAccountType.ADDITIONAL) &&
                        (u.getAccountStatus().equalsIgnoreCase("ว่าง"))
                ).toList().size());
        return resp;
    }

}
