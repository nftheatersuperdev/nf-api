package com.nftheater.api.service;

import com.nftheater.api.config.BusinessConfiguration;
import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.constant.SystemConfigName;
import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.request.ExtendDayCustomerRequest;
import com.nftheater.api.controller.customer.request.SearchCustomerRequest;
import com.nftheater.api.controller.customer.response.*;
import com.nftheater.api.controller.member.request.VerifyCustomerRequest;
import com.nftheater.api.controller.member.request.VerifyOtpRequest;
import com.nftheater.api.controller.member.response.CustomerProfileResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.dto.CustomerDto;
import com.nftheater.api.dto.NetflixPackageDto;
import com.nftheater.api.dto.RewardDto;
import com.nftheater.api.dto.client.sms.RequestOtpClientResponse;
import com.nftheater.api.dto.client.sms.VerifyOtpClientResponse;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
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

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.nftheater.api.constant.BusinessConstants.NETFLIX_PREFIX;
import static com.nftheater.api.constant.BusinessConstants.YOUTUBE_PREFIX;
import static com.nftheater.api.mapper.CustomerMapper.calculateDayLeft;
import static com.nftheater.api.mapper.NetflixAccountMapper.getCustomerStatusFromDayLeft;
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
    private final RequestOtpRepository requestOtpRepository;
    private final CustomerMapper customerMapper;
    private final AdminUserService adminUserService;
    private final SystemConfigService systemConfigService;
    private final RewardService rewardService;
    private final UserInfoService userInfoService;
    private final SmsService smsService;
    private final JwtUtil jwtUtil;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder encoder;
    private final NetflixPackageMapper netflixPackageMapper;
    private final BusinessConfiguration businessConfiguration;
    private final Clock clock;

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

        customerDtoList.stream().forEach(user -> {
            long dayLeft = calculateDayLeft(user.getExpiredDate());
            if(dayLeft <= 3) {
                String userId = user.getUserId();
                String status = getCustomerStatusFromDayLeft(user.getExpiredDate());
                UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
                updateCustomerRequest.setCustomerStatus(status);
                try {
                    updateCustomer(userId, updateCustomerRequest, BusinessConstants.DEFAULT_SYSTEM_UUID);
                } catch (DataNotFoundException e) {
                }
                user.setCustomerStatus(status);
            }
        });

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
            CustomerListResponse customerListResponse = getCustomerListResponse(dto);
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
        SystemConfigResponse statusFlowValues = systemConfigService.getSystemConfigByConfigName(SystemConfigName.CUSTOMER_STATUS_FLOW);
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
        UserDetails userDetails = this.getUserDetail(httpServletRequest);

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userDetails.getUsername() + " is not found."));

        CustomerDto customerDto = customerMapper.toDto(customerEntity);
        CustomerProfileResponse profileResponse = customerMapper.toCustomerProfileResponse(customerDto);
        NetflixAccountLinkEntity netflixAccountLinkEntity = netflixAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        if (netflixAccountLinkEntity != null) {
            NetflixAccountEntity netflix = netflixRepository.findById(netflixAccountLinkEntity.getAccount().getId())
                            .orElse(new NetflixAccountEntity());
            profileResponse.setNetflixEmail(netflix.getNetflixEmail());
            profileResponse.setNetflixPassword(netflix.getNetflixPassword());
            profileResponse.setNetflixPackageName(netflixAccountLinkEntity.getPackageName());
            profileResponse.setNetflixDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), customerDto.getExpiredDate()));
        }
        NetflixAdditionalAccountLinkEntity additionalAccountLink = netflixAdditionalAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        if (additionalAccountLink != null) {
            NetflixAdditionalAccountEntity additionalAccount = netflixAdditionalAccountRepository.findById(additionalAccountLink.getId().getAdditionalAccountId())
                            .orElse(new NetflixAdditionalAccountEntity());
            profileResponse.setNetflixEmail(additionalAccount.getAdditionalEmail());
            profileResponse.setNetflixPassword(additionalAccount.getAdditionalPassword());
            profileResponse.setNetflixPackageName(additionalAccountLink.getPackageName());
            profileResponse.setNetflixDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), customerDto.getExpiredDate()));
        }
        YoutubeAccountLinkEntity youtubeAccountLinkEntity = youtubeAccountLinkRepository.findByUserId(customerEntity.getId())
                .orElse(null);
        if (youtubeAccountLinkEntity != null) {
            profileResponse.setYoutubePackageName(youtubeAccountLinkEntity.getPackageName());
            profileResponse.setYoutubeDayLeft(ChronoUnit.DAYS.between(ZonedDateTime.now(), customerDto.getExpiredDate()));
        }

        RequestOtpEntity requestOtp = requestOtpRepository.findByUserId(customerEntity.getUserId())
                .orElse(null);

        if (requestOtp != null && requestOtp.getIsVerified()) {
            profileResponse.setIsPhoneVerified(requestOtp.getIsVerified());
        } else {
            profileResponse.setIsPhoneVerified(false);
        }

        if (!StringUtils.isEmpty(customerEntity.getLineUserId())) {
            profileResponse.setIsLineVerified(true);
        } else {
            profileResponse.setIsLineVerified(false);
        }

        profileResponse.setIsCustomerVerified( profileResponse.getIsLineVerified() && profileResponse.getIsPhoneVerified());

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

    @Transactional
    public void verifyOtp(HttpServletRequest httpServletRequest, VerifyOtpRequest verifyOtpRequest) throws DataNotFoundException, InvalidRequestException {
        UserDetails userDetails = this.getUserDetail(httpServletRequest);
        log.info("Verify OTP for customer : {} with refCode : {}", userDetails.getUsername(), verifyOtpRequest.getRefCode());

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userDetails.getUsername() + " is not found."));

        RequestOtpEntity requestOtp = requestOtpRepository.findByUserId(customerEntity.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found Otp request token of customer : {}", userDetails.getUsername()));

        if (!requestOtp.getRefNo().equals(verifyOtpRequest.getRefCode())) {
            throw new InvalidRequestException("RefNo is not same in system.");
        }

        if (requestOtp.getIsVerified()) {
            throw new InvalidRequestException("Cannot verify Otp for verified mobile no.");
        }

        VerifyOtpClientResponse isVerified;
        boolean isEnableRequestOTP = Boolean.getBoolean(systemConfigService.getSystemConfigByConfigName(SystemConfigName.ENABLE_REQUEST_OTP).getConfigValue());

        if (isEnableRequestOTP) {
            isVerified = smsService.verifyOtp(requestOtp.getRequestToken(), verifyOtpRequest.getPinCode());
        } else {
            isVerified = new VerifyOtpClientResponse();
            isVerified.setStatus("success");
            isVerified.setMessage("verified");
        }

        if ("success".equals(isVerified.getStatus())) {
            requestOtp.setIsVerified(true);
            requestOtp.setUpdatedDate(ZonedDateTime.now(clock));
            requestOtp.setMessage(isVerified.getMessage());

            customerEntity.setPhoneNumber(requestOtp.getPhoneNumber());
            customerEntity.setUpdatedDate(ZonedDateTime.now(clock));

        } else {
            requestOtp.setIsVerified(false);
            requestOtp.setUpdatedDate(ZonedDateTime.now(clock));
            requestOtp.setMessage(isVerified.getErrors().get(0).getMessage());

            throw new InvalidRequestException(isVerified.getErrors().get(0).getMessage());
        }
    }

    @Transactional
    public String requestOtp(HttpServletRequest httpServletRequest, String mobileNo) throws DataNotFoundException, InvalidRequestException {
        UserDetails userDetails = this.getUserDetail(httpServletRequest);
        log.info("Request OTP for customer : {} , with mobile no : {}", userDetails.getUsername(), mobileNo);
        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userDetails.getUsername() + " is not found."));

        RequestOtpEntity requestOtp = requestOtpRepository.findByUserId(customerEntity.getUserId())
                .orElse(new RequestOtpEntity());

        if (requestOtp.getRetryCount() == null) {
            requestOtp.setRetryCount(0);
        }

        if (requestOtp.getIsVerified() != null && requestOtp.getIsVerified()) {
            throw new InvalidRequestException("Cannot request Otp for verified mobile no.");
        }

        ZonedDateTime coolDownTime = ZonedDateTime.now(clock).plusMinutes(businessConfiguration.getSmsCoolDownTime());
        if (requestOtp.getRetryCount() >= businessConfiguration.getSmsMaxRetry()
                && requestOtp.getUpdatedDate().isAfter(coolDownTime)) {
            throw new InvalidRequestException("You are reached maximum OTP request, Please try again in next 1 hour.");
        }

        boolean isEnableRequestOTP = Boolean.getBoolean(systemConfigService.getSystemConfigByConfigName(SystemConfigName.ENABLE_REQUEST_OTP).getConfigValue());

        RequestOtpClientResponse clientResponse;
        if (isEnableRequestOTP) {
            clientResponse = smsService.requestOtp(mobileNo);
        } else {
            log.info("Mockup Request OTP.");
            clientResponse = new RequestOtpClientResponse();
            Random gen = new Random();
            clientResponse.setRefNo("GF83T"+ gen.nextInt());
            clientResponse.setToken("TOKEN");
        }

        int newCount = requestOtp.getRetryCount() + 1;
        requestOtp.setRequestedDate(ZonedDateTime.now(clock));
        requestOtp.setRequestToken(clientResponse.getToken());
        requestOtp.setUserId(userDetails.getUsername());
        requestOtp.setPhoneNumber(mobileNo);
        requestOtp.setRefNo(clientResponse.getRefNo());
        requestOtp.setMessage(clientResponse.getStatus());
        requestOtp.setRetryCount(newCount);
        requestOtp.setIsVerified(false);
        requestOtp.setUpdatedDate(ZonedDateTime.now(clock));

        requestOtp = requestOtpRepository.save(requestOtp);

        return clientResponse.getRefNo();
    }

    public void verifyCustomer(HttpServletRequest httpServletRequest, VerifyCustomerRequest verifyCustomerRequest) throws DataNotFoundException, InvalidRequestException {
        log.info("Verify customer");
        UserDetails userDetails = this.getUserDetail(httpServletRequest);
        log.info("Verify customer {} with {}",userDetails.getUsername(), verifyCustomerRequest);

        final CustomerEntity customerEntity = customerRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Customer ID " + userDetails.getUsername() + " is not found."));

        if ("ยืนยันสมาชิกแล้ว".equalsIgnoreCase(customerEntity.getVerifiedStatus())) {
            throw new InvalidRequestException("ลูกค้าทำการยืนยันสมาชิกเรียบร้อยแล้ว");
        }

        customerEntity.setPhoneNumber(verifyCustomerRequest.getPhoneNumber());
        customerEntity.setLineId(verifyCustomerRequest.getLineId());
        customerEntity.setLineUserId(verifyCustomerRequest.getLineUserId());
        customerEntity.setVerifiedStatus("ยืนยันสมาชิกแล้ว");

        SystemConfigResponse memberCollectPoint = systemConfigService.getSystemConfigByConfigName(SystemConfigName.NEW_MEMBER_COLLECT_POINT);
        int existingPoint = customerEntity.getMemberPoint() == null ? 0 : customerEntity.getMemberPoint();
        customerEntity.setMemberPoint(existingPoint + Integer.valueOf(memberCollectPoint.getConfigValue()));

        customerRepository.save(customerEntity);
    }

    @Transactional
    public void redeemReward(HttpServletRequest httpServletRequest, UUID rewardId) throws DataNotFoundException, InvalidRequestException {
        log.info("Redeem reward");
        RewardDto rewardDto = rewardService.getRewardById(rewardId);
        UserDetails userDetails = this.getUserDetail(httpServletRequest);
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
//            throw new InvalidRequestException("ลูกค้าไม่เคยสมัครแพ็คเกจ Netflix กรุณาติดต่อแอดมินเพื่อทำการสมัครแพ็คเกจ");
            return null;
        } else {
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
                    .orElseThrow(() -> new DataNotFoundException("ไม่พบข้อมูลแพ็คเกจ " + finalPackageName + " กรุณาติดต่อแอดมิน"));
            return packageDto;
        }
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

    public UserDetails getUserDetail(HttpServletRequest httpServletRequest) {
        String customerToken = securityUtils.getTokenFromRequest(httpServletRequest);
        String username = jwtUtil.extractUsername(customerToken);
        return userInfoService.loadUserByUsername(username);
    }

    private static CustomerListResponse getCustomerListResponse(CustomerDto dto) {
        CustomerListResponse customerListResponse = new CustomerListResponse();
        customerListResponse.setValue(dto.getUserId());
        customerListResponse.setLabel(dto.getLineId());
        customerListResponse.setFilterLabel(dto.getUserId() == null ? "" : dto.getUserId()
                .concat("|")
                .concat(dto.getEmail() == null ? "" : dto.getEmail())
                .concat("|")
                .concat(dto.getLineId() == null ? "" : dto.getLineId()));
        return customerListResponse;
    }
}
