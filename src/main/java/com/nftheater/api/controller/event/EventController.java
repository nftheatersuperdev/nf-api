package com.nftheater.api.controller.event;

import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.dto.EventRegisterDto;
import com.nftheater.api.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;


@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Secured({Module.ALL, Module.NETFLIX, Module.YOUTUBE})
    @GetMapping("/v1/event/register/members")
    public GeneralResponse<List<EventRegisterDto>> getAllRegisterData(
    ) throws IOException {
        log.info("Get all register data");
        List<EventRegisterDto> registerDtoList = eventService.getAllRegisterData();
        return new GeneralResponse<>(SUCCESS, registerDtoList);
    }

}
