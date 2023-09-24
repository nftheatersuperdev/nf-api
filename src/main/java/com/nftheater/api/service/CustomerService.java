package com.nftheater.api.service;

import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.request.ExtendDayCustomerRequest;
import com.nftheater.api.controller.customer.request.SearchCustomerRequest;
import com.nftheater.api.controller.customer.response.*;
import com.nftheater.api.controller.member.request.VerifyCustomerRequest;
import com.nftheater.api.controller.member.response.CustomerProfileResponse;
import com.nftheater.api.controller.netflix.response.GetNetflixPackageResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.dto.CustomerDto;
import com.nftheater.api.dto.NetflixPackageDto;
import com.nftheater.api.dto.RewardDto;
import com.nftheater.api.entity.*;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.mapper.CustomerMapper;
import com.nftheater.api.mapper.NetflixPackageMapper;
import com.nftheater.api.repository.*;
import com.nftheater.api.security.SecurityUtils;
import com.nftheater.api.utils.JwtUtil;
import com.nftheater.api.utils.PaginationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.nftheater.api.constant.BusinessConstants.NETFLIX_PREFIX;
import static com.nftheater.api.constant.BusinessConstants.YOUTUBE_PREFIX;
import static com.nftheater.api.specification.CustomerSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final NetflixAccountLinkRepository netflixAccountLinkRepository;
    private final NetflixRepository netflixRepository;
    private final NetflixAdditionalAccountLinkRepository netflixAdditionalAccountLinkRepository;
    private final NetflixAdditionalAccountRepository netflixAdditionalAccountRepository;
    private final NetflixPackageRepository netflixPackageRepository;
    private final YoutubeAccountLinkRepository youtubeAccountLinkRepository;
    private final YoutubePackageRepository youtubePackageRepository;
    private final CustomerMapper customerMapper;
    private final AdminUserService adminUserService;
    private final SystemConfigService systemConfigService;
    private final RewardService rewardService;
    private final UserInfoService userInfoService;
    private final JwtUtil jwtUtil;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder encoder;
    private final NetflixPackageMapper netflixPackageMapper;

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
        customerDto.setUserId(generateUserId(createCustomerRequest.getAccount()));
        String generatedPassword = generatePassword();
        customerDto.setPassword(encoder.encode(generatedPassword));
        customerDto.setActualPassword(generatedPassword);
        customerDto.setExpiredDate(ZonedDateTime.now());
        customerDto.setCustomerStatus("กำลังใช้งาน");
        CustomerEntity customerEntity = customerMapper.toEntity(customerDto);
        customerRepository.saveAndFlush(customerEntity);

        CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        createCustomerResponse.setId(customerEntity.getId());
        createCustomerResponse.setUserId(customerEntity.getUserId());
        createCustomerResponse.setPassword(customerEntity.getActualPassword());
        return createCustomerResponse;
    }
    public List<CustomerListResponse> getCustomerList(String account) {
        List<CustomerEntity> customerEntities = customerRepository.findByAccount(account);
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
        String adminUser = adminUserEntity.getAdminName();

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
        String adminUser = adminUserEntity.getAdminName();

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

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataNotFoundException {
        CustomerEntity customerEntity = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userId + " is not found."));
        return new User(customerEntity.getUserId(), customerEntity.getPassword(), new ArrayList<>());
    }

    public CustomerProfileResponse getCustomerFromToken(HttpServletRequest httpServletRequest) throws DataNotFoundException {
        String customerToken = securityUtils.getTokenFromRequest(httpServletRequest);
        String username = jwtUtil.extractUsername(customerToken);
        UserDetails userDetails = userInfoService.loadUserByUsername(username);

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userDetails.getUsername() + " is not found."));

        CustomerDto customerDto = customerMapper.toDto(customerEntity);
        CustomerProfileResponse profileResponse = customerMapper.toCustomerProfileResponse(customerDto);
        NetflixAccountLinkEntity netflixAccountLinkEntity = netflixAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        if (netflixAccountLinkEntity != null) {
            NetflixAccountEntity netflix = netflixRepository.findById(netflixAccountLinkEntity.getAccount().getId()).get();
            profileResponse.setNetflixEmail(netflix.getNetflixEmail());
            profileResponse.setNetflixPassword(netflix.getNetflixPassword());
            profileResponse.setNetflixPackageName(netflixAccountLinkEntity.getPackageName());
            profileResponse.setNetflixDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), customerDto.getExpiredDate()));
        }
        NetflixAdditionalAccountLinkEntity additionalAccountLink = netflixAdditionalAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        if (additionalAccountLink != null) {
            NetflixAdditionalAccountEntity additionalAccount = netflixAdditionalAccountRepository.findById(additionalAccountLink.getId().getAdditionalAccountId()).get();
            profileResponse.setNetflixEmail(additionalAccount.getAdditionalEmail());
            profileResponse.setNetflixPassword(additionalAccount.getAdditionalPassword());
            profileResponse.setNetflixPackageName(netflixAccountLinkEntity.getPackageName());
            profileResponse.setNetflixDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), customerDto.getExpiredDate()));
        }
        YoutubeAccountLinkEntity youtubeAccountLinkEntity = youtubeAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        if (youtubeAccountLinkEntity != null) {
            profileResponse.setYoutubePackageName(youtubeAccountLinkEntity.getPackageName());
            profileResponse.setYoutubeDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), customerDto.getExpiredDate()));
        }

        return profileResponse;
    }

    public Boolean isUrlDuplicate(String url) {
        String uniqueUrl = url.substring(url.indexOf("/chat/"), url.length());
        log.info("Substring URL : {}", uniqueUrl);
        List<CustomerEntity> customer = customerRepository.findByLineUrl(uniqueUrl).stream().toList();
        if (customer.size() == 0) {
            return false;
        }
        return true;
    }

    public void deleteUserByUserId(String userId) throws DataNotFoundException {
        log.info("Delete Customer with Id : {}", userId);
        final CustomerEntity deltedCustomer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบข้อมูลลูคก้า"));
        // Check Netflix Link
        final NetflixAccountLinkEntity netflixLinkEntity = netflixAccountLinkRepository.findByUserId(deltedCustomer.getId()).orElse(null);
        if (netflixLinkEntity != null) {
            log.info("Unlink Netflix with {}", netflixLinkEntity.getId().getAccountId());
            netflixAccountLinkRepository.delete(netflixLinkEntity);
        }
        // Check Netflix Additional Link
        final NetflixAdditionalAccountLinkEntity additionalAccountLink = netflixAdditionalAccountLinkRepository.findByUserId(deltedCustomer.getId()).orElse(null);
        if (additionalAccountLink != null) {
            log.info("Unlink Addition account");
            netflixAdditionalAccountLinkRepository.delete(additionalAccountLink);
        }
        // Check Youtube Link
        final YoutubeAccountLinkEntity youtubeLink = youtubeAccountLinkRepository.findByUserId(deltedCustomer.getId()).orElse(null);
        if (youtubeLink != null) {
            log.info("Unlink Youtube with {}", youtubeLink.getId().getAccountId());
            youtubeAccountLinkRepository.delete(youtubeLink);
        }

        customerRepository.delete(deltedCustomer);
    }

    public void verifyCustomer(HttpServletRequest httpServletRequest, VerifyCustomerRequest verifyCustomerRequest) throws DataNotFoundException, InvalidRequestException {
        log.info("Verify customer");
        String customerToken = securityUtils.getTokenFromRequest(httpServletRequest);
        String username = jwtUtil.extractUsername(customerToken);
        UserDetails userDetails = userInfoService.loadUserByUsername(username);
        log.info("Verify customer {} with {}",userDetails.getUsername(), verifyCustomerRequest);

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userDetails.getUsername() + " is not found."));

        if ("ยืนยันสมาชิกแล้ว".equalsIgnoreCase(customerEntity.getVerifiedStatus())) {
            throw new InvalidRequestException("ลูกค้าทำการยืนยันสมาชิกเรียบร้อยแล้ว");
        }

        customerEntity.setCustomerName(verifyCustomerRequest.getCustomerName());
        customerEntity.setPhoneNumber(verifyCustomerRequest.getPhoneNumber());
        customerEntity.setLineId(verifyCustomerRequest.getLineId());
        customerEntity.setVerifiedStatus("ยืนยันสมาชิกแล้ว");

        SystemConfigResponse memberCollectPoint = systemConfigService.getSystemConfigByConfigName("NEW_MEMBER_COLLECT_POINT");
        int existingPoint = customerEntity.getMemberPoint();
        customerEntity.setMemberPoint(existingPoint + Integer.valueOf(memberCollectPoint.getConfigValue()));

        customerRepository.save(customerEntity);
    }

    @Transactional
    public void redeemReward(HttpServletRequest httpServletRequest, UUID rewardId) throws DataNotFoundException, InvalidRequestException {
        log.info("Redeem reward");
        RewardDto rewardDto = rewardService.getRewardById(rewardId);
        String customerToken = securityUtils.getTokenFromRequest(httpServletRequest);
        String username = jwtUtil.extractUsername(customerToken);
        UserDetails userDetails = userInfoService.loadUserByUsername(username);
        log.info("Redeem reward {} for customer {} ",rewardDto.getRewardName(), userDetails.getUsername());

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("ไม่พบข้อมูลลูกต้า " + userDetails.getUsername()));

        if (customerEntity.getMemberPoint() < rewardDto.getRedeemPoint()) {
            throw new InvalidRequestException("คะแนนของคุณไม่เพียงพอสำหรับการแลกรางวัลนี้");
        }

        int existingPoint = customerEntity.getMemberPoint();
        customerEntity.setMemberPoint(existingPoint - rewardDto.getRedeemPoint());
        customerEntity.setExpiredDate(
                customerEntity.getExpiredDate()
                        .plusDays(Long.valueOf(rewardDto.getRewardValue())));

        customerRepository.save(customerEntity);
    }

    public NetflixPackageDto getMemberCurrentNetflixPackage(HttpServletRequest httpServletRequest) throws DataNotFoundException, InvalidRequestException {
        String customerToken = securityUtils.getTokenFromRequest(httpServletRequest);
        String username = jwtUtil.extractUsername(customerToken);
        UserDetails userDetails = userInfoService.loadUserByUsername(username);
        log.info("Get Netflix package of customer {} ", userDetails.getUsername());

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("ไม่พบข้อมูลลูกต้า " + userDetails.getUsername()));

        NetflixAccountLinkEntity netflixAccountLinkEntity = netflixAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        NetflixAdditionalAccountLinkEntity additionalAccountLink = netflixAdditionalAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);

        if (netflixAccountLinkEntity == null && additionalAccountLink == null) {
            throw new InvalidRequestException("ลูกค้าไม่เคยสมัครแพ็คเกจ Netflix กรุณาติดต่อแอดมินเพื่อทำการสมัครแพ็คเกจ");
        }
        String packageName = "";
        String device = "";
        if (netflixAccountLinkEntity != null) {
            packageName = netflixAccountLinkEntity.getPackageName();
            device = netflixAccountLinkEntity.getAccountType();
        } else {
            packageName = additionalAccountLink.getPackageName();
            device = "TV";
        }
        log.info("Current package is {}", packageName);
        String finalPackageName = packageName;
        NetflixPackageDto packageDto = netflixPackageRepository.findByNameAndDevice(finalPackageName, device).map(netflixPackageMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบข้อมูลแพ็คเกจ "+ finalPackageName +" กรุณาติดต่อแอดมิน"));

        return packageDto;
    }

    private String generateUserId(String account) {
        Long userIdSeq = customerRepository.getUserIdSeq();
        if ("NETFLIX".equalsIgnoreCase(account)) {
            return NETFLIX_PREFIX.concat(String.format("%05d", userIdSeq));
        } else {
            return YOUTUBE_PREFIX.concat(String.format("%05d", userIdSeq));
        }
    }

    @Named("generatePassword")
    public static String generatePassword() {
        Random random = new Random();
        int num = random.nextInt(100000);
        return String.format("%05d", num);
    }
}
