package com.nftheater.api.controller.packages;

import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.packages.request.UpdatePackageRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.dto.PackageDto;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @GetMapping("/v1/packages")
    public GeneralResponse<List<PackageDto>> getAllPackage() {
        log.info("Start Get All Package");
        List<PackageDto> allPackage = packageService.getAllPackage();
        log.info("End Get All Package size = {}", allPackage.size());
        return new GeneralResponse<>(SUCCESS, allPackage);
    }

    @GetMapping("/v1/packages/modules/{module}")
    public GeneralResponse<List<PackageDto>> getPackageByModule(@PathVariable("module") String module) {
        log.info("Start get all package of module : {}", module);
        List<PackageDto> packageDtos = packageService.getPackageByModule(module);
        log.info("End get all package of module size : {}", packageDtos.size());
        return new GeneralResponse<>(SUCCESS, packageDtos);
    }

    @GetMapping("/v1/packages/{packageId}")
    public GeneralResponse<PackageDto> getPackageById(@PathVariable("packageId") UUID packageId) throws InvalidRequestException {
        log.info("Start get package with id : {}", packageId);
        PackageDto response = packageService.getPackageDetailById(packageId);
        log.info("End get package with id : {}", packageId);
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PatchMapping("/v1/packages/{packageId}")
    public GeneralResponse<Void> updatePackageById(@PathVariable("packageId") UUID packageId, @RequestBody UpdatePackageRequest updatePackageRequest) throws InvalidRequestException {
        log.info("Start update package with id : {}", packageId);
        packageService.updatePackageInfo(packageId, updatePackageRequest);
        log.info("End update package with id : {}", packageId);
        return new GeneralResponse<>(SUCCESS, null);
    }


}
