package com.nftheater.api.service;

import com.nftheater.api.constant.SystemConfigName;
import com.nftheater.api.controller.member.request.EventRegisterRequest;
import com.nftheater.api.dto.EventRegisterDto;
import com.nftheater.api.entity.CustomerEntity;
import com.nftheater.api.entity.EventRegisterEntity;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.mapper.EventRegisterMapper;
import com.nftheater.api.repository.EventRegisterRepository;
import com.nftheater.api.utils.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final SystemConfigService systemConfigService;
    private final CustomerService customerService;
    private final EventRegisterRepository eventRegisterRepository;
    private final EventRegisterMapper eventRegisterMapper;
    @Cacheable(cacheNames = "eventAvailable")
    public boolean isEventAvailable(String eventName) throws DataNotFoundException {
        Integer startTime = Integer.valueOf(systemConfigService.getSystemConfigByConfigName(SystemConfigName.EVENT_START_TIME).getConfigValue());
        Integer endTime = Integer.valueOf(systemConfigService.getSystemConfigByConfigName(SystemConfigName.EVENT_END_TIME).getConfigValue());

        LocalDateTime today = LocalDateTime.now(DateUtil.getTimeZone());
        LocalDateTime firstDayOfMonth = LocalDateTime
                .of(today.getYear(), today.getMonth(), 1, startTime, 0,0);

        LocalDateTime lastDayOfMonth = LocalDateTime
                .of(today.getYear(), today.getMonth(), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH), endTime, 0,0);

        log.info("Today : {}", today);
        log.info("First day of month : {}", firstDayOfMonth);
        log.info("Last day of month : {}", lastDayOfMonth);

        if (today.isAfter(lastDayOfMonth) || today.isBefore(firstDayOfMonth)) {
            return false;
        }
        return true;
    }

    @Transactional
    public UUID registerEvent(String eventName, EventRegisterRequest registerRequest, HttpServletRequest httpServletRequest) throws DataNotFoundException, InvalidRequestException {
        UserDetails userDetails = customerService.getUserDetail(httpServletRequest);
        CustomerEntity customerEntity = customerService.getCustomerByUserId(userDetails.getUsername());
        log.info("===== Start register Event: {} for customer: {} =====", eventName, customerEntity.getUserId());

        boolean isExisting = eventRegisterRepository.findByUserIdAndFacebookName(customerEntity.getUserId(), registerRequest.getFacebookName())
                .isPresent();

        if (isExisting) {
            throw new InvalidRequestException("เนื่องจากท่านได้ลงทะเบียนกิจกรรมนีี้แล้ว ไม่สามารถลงทะเบียนซ้ำได้");
        }

        EventRegisterEntity registerEntity = new EventRegisterEntity();
        registerEntity.setFacebookName(registerRequest.getFacebookName());
        registerEntity.setUserId(customerEntity.getUserId());

        registerEntity = eventRegisterRepository.save(registerEntity);
        return registerEntity.getId();
    }

    public void clearAllRegister() {
        log.info("Clear All Register data");
        int totalRecord = eventRegisterRepository.findAll().size();
        log.info("Clear {} records from event_register", totalRecord);
        eventRegisterRepository.deleteAll();
    }
    public List<EventRegisterDto> getAllRegisterData() {
        log.info("Get All Registered Data");
        List<EventRegisterEntity> eventRegisterEntities = eventRegisterRepository.findAll();
        log.info("Get All Registered Data size : {}", eventRegisterEntities.size());
        return eventRegisterMapper.toDto(eventRegisterEntities);
    }

}
