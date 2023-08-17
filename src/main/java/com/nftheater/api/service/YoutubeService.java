package com.nftheater.api.service;

import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.controller.customer.response.CustomerResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.controller.youtube.request.CreateYoutubeAccountRequest;
import com.nftheater.api.controller.youtube.request.SearchYoutubeAccountRequest;
import com.nftheater.api.controller.youtube.response.*;
import com.nftheater.api.dto.YoutubeAccountDto;
import com.nftheater.api.dto.YoutubePackageDto;
import com.nftheater.api.entity.AdminUserEntity;
import com.nftheater.api.entity.YoutubeAccountEntity;
import com.nftheater.api.entity.YoutubeAccountEntity_;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.mapper.YoutubeMapper;
import com.nftheater.api.mapper.YoutubePackageMapper;
import com.nftheater.api.repository.YoutubePackageRepository;
import com.nftheater.api.repository.YoutubeRepository;
import com.nftheater.api.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

import static com.nftheater.api.specification.YoutubeSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YoutubeRepository youtubeRepository;
    private final YoutubePackageRepository youtubePackageRepository;
    private final YoutubeMapper youtubeMapper;
    private final YoutubePackageMapper youtubePackageMapper;
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
                specification = specification.and(accountNameContain(request.getAccountName()));
            }
            if (request.getCustomerStatus().size() != 0) {
                specification = specification.and(customerStatusIn(request.getCustomerStatus()));
            }
            if (!request.getAccountName().isBlank()) {
                specification = specification.and(accountStatusEqual(request.getAccountStatus()));
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
        for(YoutubeAccountResponse acct : youtubeAccountResponse){
            acct.getUsers().add(adminUser);
            fillEmptyYoutubeUser(acct);
        }

        PaginationResponse pagination = PaginationUtils.createPagination(youtubeAccountDtoPage);
        SearchYoutubeAccountResponse response = new SearchYoutubeAccountResponse();
        response.setPagination(pagination);
        response.setYoutube(youtubeAccountResponse);
        return response;
    }
    public CreateYoutubeAccountResponse createYoutubeAccount(CreateYoutubeAccountRequest createRequest) throws DataNotFoundException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(createRequest.getCreatedBy());
        String adminUser = adminUserEntity.getFirstName() + " " + adminUserEntity.getLastName();

        Long nextSeq = youtubeRepository.getYoutubeAccountNameSeq();

        YoutubeAccountEntity newYoutubeAccount = youtubeMapper.toEntity(createRequest);
        newYoutubeAccount.setAccountName(BusinessConstants.YOUTUBE_ACCOUNT_PREFIX.concat(nextSeq.toString()));
        newYoutubeAccount.setAccountStatus("กำลังใช้งาน");
        newYoutubeAccount.setCreatedBy(adminUser);
        newYoutubeAccount.setUpdatedBy(adminUser);
        youtubeRepository.saveAndFlush(newYoutubeAccount);

        return new CreateYoutubeAccountResponse(newYoutubeAccount.getId(), newYoutubeAccount.getAccountName());
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
            youtubeAccount.getUsers().add(userLink);
            countUser++;
        }

    }
}
