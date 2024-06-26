package com.nftheater.api.service;

import com.nftheater.api.constant.NetflixAccountType;
import com.nftheater.api.controller.packages.request.UpdatePackageRequest;
import com.nftheater.api.dto.PackageDto;
import com.nftheater.api.constant.Module;
import com.nftheater.api.entity.NetflixPackageEntity;
import com.nftheater.api.entity.YoutubePackageEntity;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.mapper.PackageMapper;
import com.nftheater.api.repository.NetflixPackageRepository;
import com.nftheater.api.repository.YoutubePackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageService {

    private final YoutubePackageRepository youtubePackageRepository;
    private final NetflixPackageRepository netflixPackageRepository;
    private final PackageMapper packageMapper;

    public List<PackageDto> getAllPackage() {
        log.info("Get All Package.");
        List<NetflixPackageEntity> netflixTvPackageEntities = netflixPackageRepository.findByDevice(NetflixAccountType.TV.name());
        List<NetflixPackageEntity> netflixOtherPackageEntities = netflixPackageRepository.findByDevice(NetflixAccountType.OTHER.name());
        List<YoutubePackageEntity> youtubeNewPackageEntities = youtubePackageRepository.findByType("NEW");
        List<YoutubePackageEntity> youtubeExtendPackageEntities = youtubePackageRepository.findByType("EXTEND");
        log.info("Netflix TV package size : {}", netflixTvPackageEntities.size());
        log.info("Netflix Other package size : {}", netflixOtherPackageEntities.size());
        log.info("Youtube package size : {}", youtubeNewPackageEntities.size());
        List<PackageDto> allPackage = new ArrayList<>();
        List<PackageDto> netflixTvPackage = netflixTvPackageEntities
                .stream()
                .map(packageMapper::toPackageDto)
                .sorted(Comparator.comparing(PackageDto::getDay))
                .collect(Collectors.toList());
        netflixTvPackage.forEach(n -> n.setModule(Module.NETFLIX));
        List<PackageDto> netflixOtherPackage = netflixOtherPackageEntities
                .stream()
                .map(packageMapper::toPackageDto)
                .sorted(Comparator.comparing(PackageDto::getDay))
                .collect(Collectors.toList());
        netflixOtherPackage.forEach(n -> n.setModule(Module.NETFLIX));

        List<PackageDto> youtubeNewPackage = youtubeNewPackageEntities
                .stream()
                .map(packageMapper::toPackageDto)
                .sorted(Comparator.comparing(PackageDto::getDay))
                .collect(Collectors.toList());
        youtubeNewPackage.forEach(y -> y.setModule(Module.YOUTUBE));
        List<PackageDto> youtubeExtendPackage = youtubeExtendPackageEntities
                .stream()
                .map(packageMapper::toPackageDto)
                .sorted(Comparator.comparing(PackageDto::getDay))
                .collect(Collectors.toList());
        youtubeExtendPackage.forEach(y -> y.setModule(Module.YOUTUBE));

        allPackage.addAll(netflixTvPackage);
        allPackage.addAll(netflixOtherPackage);
        allPackage.addAll(youtubeNewPackage);
        allPackage.addAll(youtubeExtendPackage);
        return allPackage;
    }

    public List<PackageDto> getPackageByModule(String module) {
        log.info("Get All package for type : {}", module);
        if (Module.NETFLIX.equalsIgnoreCase(module)) {
            List<NetflixPackageEntity> entities = netflixPackageRepository.findAll();
            return entities
                    .stream()
                    .map(packageMapper::toPackageDto)
                    .collect(Collectors.toList());
        } else if (Module.YOUTUBE.equalsIgnoreCase(module)) {
            List<YoutubePackageEntity> entities = youtubePackageRepository.findAll();
            return entities
                    .stream()
                    .map(packageMapper::toPackageDto)
                    .collect(Collectors.toList());
        } else {
            log.info("Cannot found package of module : {}", module);
            return new ArrayList<>();
        }
    }

    public PackageDto getPackageDetailById(UUID packageId) throws InvalidRequestException {
        log.info("Get package detail with id : {}", packageId);

        NetflixPackageEntity netflixPackageEntity = netflixPackageRepository.findById(packageId)
                .orElse(null);
        if (netflixPackageEntity != null) {
            return packageMapper.toPackageDto(netflixPackageEntity);
        }

        YoutubePackageEntity youtubePackageEntity = youtubePackageRepository.findById(packageId)
                .orElse(null);
        if (youtubePackageEntity != null) {
            return packageMapper.toPackageDto(youtubePackageEntity);
        }

        log.info("Cannot found package with id : {}", packageId);
        throw new InvalidRequestException("Cannot found package with id : " + packageId);
    }

    public void updatePackageInfo(UUID packageId, UpdatePackageRequest updatePackageRequest) throws InvalidRequestException {
        log.info("Update package with id : {}", packageId);

        NetflixPackageEntity netflixPackageEntity = netflixPackageRepository.findById(packageId)
                .orElse(null);
        if (netflixPackageEntity != null) {
            log.info("Update package with id : {}, module : {}", packageId, Module.NETFLIX);
            netflixPackageEntity.setDay(updatePackageRequest.getDay());
            netflixPackageEntity.setPrice(String.valueOf(updatePackageRequest.getPrice()));
            netflixPackageEntity.setName(updatePackageRequest.getName());
            netflixPackageEntity.setIsActive(updatePackageRequest.getIsActive());
            netflixPackageEntity.setUpdatedBy(updatePackageRequest.getUpdatedBy());
            netflixPackageRepository.save(netflixPackageEntity);
            return;
        }

        YoutubePackageEntity youtubePackageEntity = youtubePackageRepository.findById(packageId)
                .orElse(null);
        if (youtubePackageEntity != null) {
            log.info("Update package with id : {}, module : {}", packageId, Module.YOUTUBE);
            youtubePackageEntity.setPrice(String.valueOf(updatePackageRequest.getPrice()));
            youtubePackageEntity.setName(updatePackageRequest.getName());
            youtubePackageEntity.setIsActive(updatePackageRequest.getIsActive());
            youtubePackageEntity.setUpdatedBy(updatePackageRequest.getUpdatedBy());
            youtubePackageRepository.save(youtubePackageEntity);
            return;
        }

        log.info("Cannot found package with id : {}", packageId);
        throw new InvalidRequestException("Cannot found package with id : " + packageId);
    }
}
