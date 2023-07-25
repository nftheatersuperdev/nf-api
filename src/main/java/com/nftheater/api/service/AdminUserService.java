package com.nftheater.api.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.nftheater.api.controller.adminuser.request.CreateAdminUserRequest;
import com.nftheater.api.controller.adminuser.request.SearchAdminUserRequest;
import com.nftheater.api.controller.adminuser.response.AdminUserResponse;
import com.nftheater.api.controller.adminuser.response.CreateAdminUserResponse;
import com.nftheater.api.controller.adminuser.response.SearchAdminUserResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.entity.AdminUserEntity;
import com.nftheater.api.entity.AdminUserEntity_;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.mapper.AdminUserMapper;
import com.nftheater.api.repository.AdminUserRepository;
import com.nftheater.api.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nftheater.api.specification.AdminUserSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    public static final String NOT_FOUND = " not found.";
    private final FirebaseService firebaseService;
    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;

    public List<AdminUserResponse> getAllAdminUser(){
        List<AdminUserEntity> adminUserEntityList = adminUserRepository.findAll();
        return adminUserEntityList.stream()
                .map(adminUserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "adminUsers")
    public List<AdminUserDto> getAllAdminUserDto() {
        return adminUserRepository.findAll().stream()
                .map(adminUserMapper::toDto).collect(Collectors.toList());
    }

    public AdminUserEntity getAdminUserEntityById(UUID id) throws DataNotFoundException {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Admin user " + id + " is not found."));
    }

    public CreateAdminUserResponse createAdminUser(CreateAdminUserRequest request) throws FirebaseAuthException {
        UserRecord firebaseUser = firebaseService.registerEmailAndPassword(request.getEmail(), request.getPassword());
        AdminUserEntity adminUserEntity = adminUserMapper.toEntity(firebaseUser.getUid(), request, true);
        AdminUserEntity newUser = adminUserRepository.save(adminUserEntity);
        return new CreateAdminUserResponse(newUser.getId());
    }


    @Transactional(readOnly = true)
    public AdminUserDto getAdminUserByFirebaseToken(String bearerToken) throws FirebaseAuthException, DataNotFoundException {
        FirebaseToken decodedToken = firebaseService.verifyToken(bearerToken);
        String firebaseId = decodedToken.getUid();

        AdminUserEntity adminUserEntity = adminUserRepository.findByFirebaseId(firebaseId)
                .orElseThrow(() -> new DataNotFoundException("Admin user with firebaseId " + firebaseId + NOT_FOUND));;
        return adminUserMapper.toDto(adminUserEntity);
    }

    @Transactional(readOnly = true)
    public SearchAdminUserResponse searchAdminUser(SearchAdminUserRequest criteriaRequest, PageableRequest pageableRequest) {
        log.info("Finding admin users with criteria : {}, page :{}, size : {}", criteriaRequest, pageableRequest.getPage(), pageableRequest.getSize());
        final Pageable pageable = PageRequest.of(
                pageableRequest.getPageZeroIndex(),
                pageableRequest.getSize(),
                Sort.by(AdminUserEntity_.CREATED_DATE).descending()
        );

        Specification<AdminUserEntity> specification = Specification.where(null);
        if (criteriaRequest != null) {
            specification = criteriaIdEqual(criteriaRequest, specification);
            specification = criteriaFirstNameContain(criteriaRequest, specification);
            specification = criteriaLastNameContain(criteriaRequest, specification);
            specification = criteriaEmailContain(criteriaRequest, specification);
            specification = criteriaRoleEqual(criteriaRequest, specification);
            specification = criteriaModuleEqual(criteriaRequest, specification);
        }

        Page<AdminUserEntity> adminUserEntityPage = adminUserRepository.findAll(specification, pageable);
        Page<AdminUserDto> adminUserDtoPage = adminUserEntityPage.map(adminUserMapper::toDto);

        PaginationResponse pagination = PaginationUtils.createPagination(adminUserDtoPage);
        SearchAdminUserResponse response = new SearchAdminUserResponse();
        response.setPagination(pagination);
        response.setAdminUsers(adminUserMapper.mapDtoToResponses(adminUserDtoPage.getContent()));

        log.info("Found {} customers with criteria.", adminUserDtoPage);
        return response;
    }


    private Specification<AdminUserEntity> criteriaEmailContain(SearchAdminUserRequest criteriaRequest,
                                                                Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getEmail() != null) {
            specification = specification.and(emailContain(criteriaRequest.getEmail()));
        }
        return specification;
    }

    private Specification<AdminUserEntity> criteriaLastNameContain(SearchAdminUserRequest criteriaRequest,
                                                                   Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getLastName() != null) {
            specification = specification.and(lastNameContain(criteriaRequest.getLastName()));
        }
        return specification;
    }

    private Specification<AdminUserEntity> criteriaFirstNameContain(SearchAdminUserRequest criteriaRequest,
                                                                    Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getFirstName() != null) {
            specification = specification.and(firstNameContain(criteriaRequest.getFirstName()));
        }
        return specification;
    }

    private Specification<AdminUserEntity> criteriaIdEqual(SearchAdminUserRequest criteriaRequest, Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getId() != null) {
            specification = specification.and(idEqual(criteriaRequest.getId()));
        }
        return specification;
    }

    private Specification<AdminUserEntity> criteriaRoleEqual(SearchAdminUserRequest criteriaRequest,
                                                                      Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getRole() != null) {
            specification = specification.and(roleEqual(criteriaRequest.getRole()));
        }
        return specification;
    }

    private Specification<AdminUserEntity> criteriaModuleEqual(SearchAdminUserRequest criteriaRequest,
                                                                      Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getRole() != null) {
            specification = specification.and(moduleEqual(criteriaRequest.getModule().name()));
        }
        return specification;
    }

    private Specification<AdminUserEntity> criteriaActiveStatusEqual(SearchAdminUserRequest criteriaRequest,
                                                               Specification<AdminUserEntity> specification) {
        if (criteriaRequest.getRole() != null) {
            specification = specification.and(activeStatusEqual(criteriaRequest.isActive()));
        }
        return specification;
    }

}
