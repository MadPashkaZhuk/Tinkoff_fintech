package org.weather.dao.hibernate;

import org.springframework.http.HttpStatus;
import org.weather.dto.HandbookDTO;
import org.weather.entity.HandbookEntity;
import org.weather.exception.handbook.HandbookAlreadyExistsException;
import org.weather.exception.handbook.HandbookTypeNotFoundException;
import org.weather.repository.HandbookRepository;
import org.weather.service.HandbookService;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;
import java.util.Optional;

public class HandbookServiceImpl implements HandbookService {
    private final HandbookRepository handbookRepository;
    private final MessageSourceWrapper messageSourceWrapper;
    private final EntityMapper entityMapper;

    public HandbookServiceImpl(HandbookRepository handbookRepository, MessageSourceWrapper messageSourceWrapper, EntityMapper entityMapper) {
        this.handbookRepository = handbookRepository;
        this.messageSourceWrapper = messageSourceWrapper;
        this.entityMapper = entityMapper;
    }

    @Override
    public HandbookDTO save(String weatherType) {
        if(hasHandbookWithName(weatherType)) {
            throw new HandbookAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.HANDBOOK_ALREADY_EXISTS));
        }
        HandbookEntity handbookEntity = new HandbookEntity();
        handbookEntity.setWeatherType(weatherType);
        handbookRepository.save(handbookEntity);
        return mapHandbookEntityToDTO(handbookEntity);
    }

    @Override
    public HandbookDTO getOrCreateByTypeName(String weatherType) {
        if(hasHandbookWithName(weatherType)) {
            return getHandbookDTOByType(weatherType);
        }
        return save(weatherType);
    }

    public List<HandbookDTO> findAll() {
        return mapHandbookEntityListToDtoList(handbookRepository.findAll());
    }

    public HandbookDTO findById(Integer id) {
        return mapHandbookEntityToDTO(getHandbookEntityById(id));
    }

    public HandbookEntity getHandbookEntityById(Integer id) {
        Optional<HandbookEntity> handbook = handbookRepository.findById(id);
        if(handbook.isEmpty()) {
            throw new HandbookTypeNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.HANDBOOK_NOT_FOUND));
        }
        return handbook.get();
    }

    public HandbookDTO mapHandbookEntityToDTO(HandbookEntity handbookEntity) {
        return entityMapper.mapHandbookEntityToDTO(handbookEntity);
    }

    public HandbookDTO getHandbookDTOByType(String weatherType) {
        return entityMapper.mapHandbookEntityToDTO(handbookRepository.getHandbookEntityByWeatherType(weatherType));
    }

    public List<HandbookDTO> mapHandbookEntityListToDtoList(List<HandbookEntity> handbookEntityList) {
        return entityMapper.mapHandbookEntityListToDtoList(handbookEntityList);
    }

    public boolean hasHandbookWithName(String weatherType) {
        return handbookRepository.getHandbookEntityByWeatherType(weatherType) != null;
    }
}
