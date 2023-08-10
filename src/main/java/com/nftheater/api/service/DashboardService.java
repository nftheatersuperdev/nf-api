package com.nftheater.api.service;

import com.nftheater.api.constant.NetflixAccountType;
import com.nftheater.api.controller.dashboard.response.NetflixChangeDateInfo;
import com.nftheater.api.controller.dashboard.response.NetflixCustomerInfo;
import com.nftheater.api.controller.dashboard.response.NetflixDashboardResponse;
import com.nftheater.api.controller.dashboard.response.NetflixDeviceInfo;
import com.nftheater.api.controller.netflix.response.NetflixAccountResponse;
import com.nftheater.api.dto.NetflixAccountDto;
import com.nftheater.api.entity.NetflixAccountEntity;
import com.nftheater.api.entity.NetflixAdditionalAccountEntity;
import com.nftheater.api.entity.NetflixLinkAdditionalEntity;
import com.nftheater.api.mapper.NetflixAccountMapper;
import com.nftheater.api.repository.NetflixAdditionalAccountRepository;
import com.nftheater.api.repository.NetflixRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.N;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.nftheater.api.specification.NetflixSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final NetflixAccountMapper netflixAccountMapper;
    private final NetflixRepository netflixRepository;
    private final NetflixAdditionalAccountRepository netflixAdditionalAccountRepository;
    private final NetflixService netflixService;

    @Transactional
    public NetflixDashboardResponse getNetflixDashboardInfo(){
        Specification<NetflixAccountEntity> specification = Specification.where(null);
        specification = specification.and(isActiveEqual(true));

        List<NetflixAccountEntity> netflixAccountEntity = netflixRepository.findAll(specification);
        List<NetflixAdditionalAccountEntity> netflixAdditionalAccountEntities = netflixAdditionalAccountRepository.findAll();
        List<NetflixAccountDto> netflixAccountDtoList = netflixAccountEntity.stream().map(netflixAccountMapper::toDto).collect(Collectors.toList());
        List<NetflixAccountResponse> netflixAccountResponse = netflixAccountMapper.mapDtoToResponses(netflixAccountDtoList);

        log.info("Get Change Date Account");
        int totalAccount = 0;
        totalAccount = netflixAccountEntity.size();

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 1);

        String todayString = df.format(now);
        int countToday = netflixAccountResponse.stream().filter(acct -> acct.getChangeDate().equalsIgnoreCase(todayString)).toList().size();

        Date tomorrowDate = calendar.getTime();
        String tomorrowString = df.format(tomorrowDate);
        int countTomorrow = netflixAccountResponse.stream().filter(acct -> acct.getChangeDate().equalsIgnoreCase(tomorrowString)).toList().size();;

        calendar.add(Calendar.DATE, 1);
        Date dayPlusTwo = calendar.getTime();
        String dayPlusTwoString = df.format(dayPlusTwo);
        int countDayPlusTwo = netflixAccountResponse.stream().filter(acct -> acct.getChangeDate().equalsIgnoreCase(dayPlusTwoString)).toList().size();;

        calendar.add(Calendar.DATE, 1);
        Date dayPlusThree = calendar.getTime();
        String dayPlusThreeString = df.format(dayPlusThree);
        int countDayPlusThree = netflixAccountResponse.stream().filter(acct -> acct.getChangeDate().equalsIgnoreCase(dayPlusThreeString)).toList().size();;

        log.info("ChangeDate today   : {} , size {}", todayString, countToday);
        log.info("ChangeDate today+1 : {} , size {}", tomorrowString, countTomorrow);
        log.info("ChangeDate today+2 : {} , size {}", dayPlusTwoString, countDayPlusTwo);
        log.info("ChangeDate today+3 : {} , size {}", dayPlusThreeString, countDayPlusThree);
        log.info("Netflix Total : {}", totalAccount);

        NetflixChangeDateInfo changeDateInfo = new NetflixChangeDateInfo();
        changeDateInfo.setChangeDateToday(todayString);
        changeDateInfo.setChangeDateTomorrow(tomorrowString);
        changeDateInfo.setChangeDateDayPlusTwo(dayPlusTwoString);
        changeDateInfo.setChangeDateDayPlusThree(dayPlusThreeString);
        changeDateInfo.setCountToday(countToday);
        changeDateInfo.setCountTomorrow(countTomorrow);
        changeDateInfo.setCountDayPlusTwo(countDayPlusTwo);
        changeDateInfo.setCountDayPlusThree(countDayPlusThree);
        changeDateInfo.setTotalAccount(totalAccount);

        for (NetflixAccountResponse netflixAccount : netflixAccountResponse) {
            netflixService.fillEmptyNetflixUser(netflixAccount);
        }

        log.info("Get Customer & Device");
        int totalCustomer = 0, waitingExpired = 0, waitingAsk2 = 0, waitingAsk1 = 0, waitingAsk = 0, totalActive = 0;
        int totalTV = 0, availableTV = 0;
        int totalOther = 0, availableOther = 0;
        int totalAdditional = 0, availableAdditional = 0;

        for(NetflixAccountResponse acct : netflixAccountResponse) {
            totalCustomer = totalCustomer + acct.getUsers().stream().filter(user -> user.getUser() != null).toList().size();
            waitingExpired = waitingExpired + acct.getUsers()
                    .stream()
                    .filter(user -> user.getUser() != null && user.getUser().getCustomerStatus().equalsIgnoreCase("รอ-หมดอายุ"))
                    .toList().size();
            waitingAsk2 = waitingAsk2 + acct.getUsers()
                    .stream()
                    .filter(user -> user.getUser() != null && user.getUser().getCustomerStatus().equalsIgnoreCase("รอ-ทวงซ้ำ 2"))
                    .toList().size();
            waitingAsk1 = waitingAsk1 + acct.getUsers()
                    .stream()
                    .filter(user -> user.getUser() != null && user.getUser().getCustomerStatus().equalsIgnoreCase("รอ-ทวงซ้ำ 1"))
                    .toList().size();
            waitingAsk = waitingAsk + acct.getUsers()
                    .stream()
                    .filter(user -> user.getUser() != null && user.getUser().getCustomerStatus().equalsIgnoreCase("รอ-เรียกเก็บ"))
                    .toList().size();
            totalActive = totalActive + acct.getUsers()
                    .stream()
                    .filter(user -> user.getUser() != null && user.getUser().getCustomerStatus().equalsIgnoreCase("กำลังใช้งาน"))
                    .toList().size();
            totalTV = totalTV + acct.getUsers().stream().filter(device -> device.getAccountType().equals(NetflixAccountType.TV)).toList().size();
            availableTV = availableTV + acct.getUsers()
                    .stream()
                    .filter(device -> device.getAccountType().equals(NetflixAccountType.TV) && device.getUser() == null)
                    .toList().size();
            totalOther = totalOther + acct.getUsers().stream().filter(device -> device.getAccountType().equals(NetflixAccountType.OTHER)).toList().size();
            availableOther = availableOther + acct.getUsers()
                    .stream()
                    .filter(device -> device.getAccountType().equals(NetflixAccountType.OTHER) && device.getUser() == null).toList().size();

        }

        NetflixCustomerInfo customerInfo = new NetflixCustomerInfo();
        customerInfo.setTotalCustomer(totalCustomer);
        customerInfo.setCountWaitingExpired(waitingExpired);
        customerInfo.setCountWaitingAsk2Status(waitingAsk2);
        customerInfo.setCountWaitingAsk1Status(waitingAsk1);
        customerInfo.setCountWaitingAskStatus(waitingAsk);
        customerInfo.setTotalActiveCustomer(totalActive);

        totalAdditional = netflixAdditionalAccountEntities.size();
        for(NetflixAdditionalAccountEntity add : netflixAdditionalAccountEntities) {
            if(add.getNetflixAdditionalAccountLink() == null) {
                availableAdditional = availableAdditional + 1;
            }
        }

        NetflixDeviceInfo deviceInfo = new NetflixDeviceInfo();
        deviceInfo.setAvailableTV(availableTV);
        deviceInfo.setTotalTV(totalTV);
        deviceInfo.setAvailableAdditional(availableAdditional);
        deviceInfo.setTotalAdditional(totalAdditional);
        deviceInfo.setAvailableOther(availableOther);
        deviceInfo.setTotalOther(totalOther);

        log.info("Customer status is รอ-หมดอายุ : {} คน", waitingExpired);
        log.info("Customer status is รอ-ทวงซ้ำ 2 : {} คน", waitingAsk2);
        log.info("Customer status is รอ-ทวงซ้ำ 1 : {} คน", waitingAsk1);
        log.info("Customer status is รอ-เรียกเก็บ : {} คน", waitingAsk);
        log.info("Total Customer : {}", totalCustomer);
        log.info("Total TV : {}", totalTV);
        log.info("Available TV : {}", availableTV);
        log.info("Total Other Device : {}", totalOther);
        log.info("Available Other Device : {}", availableOther);
        log.info("Total Additional Screen : {}", totalAdditional);
        log.info("Available Additional Screen : {}", availableAdditional);

        NetflixDashboardResponse response = new NetflixDashboardResponse();
        response.setChangeDateInfo(changeDateInfo);
        response.setCustomerInfo(customerInfo);
        response.setDeviceInfo(deviceInfo);
        return response;
    }
}
