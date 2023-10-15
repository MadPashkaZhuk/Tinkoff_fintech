package org.weather.dao.hibernate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.weather.entity.HandbookEntity;
import org.weather.exception.handbook.HandbookTypeNotFoundException;
import org.weather.repository.HandbookRepository;
import org.weather.service.HandbookService;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "true")
public class HandbookServiceImpl implements HandbookService {
    private final HandbookRepository handbookRepository;
    private final MessageSourceWrapper messageSourceWrapper;

    public HandbookServiceImpl(HandbookRepository handbookRepository, MessageSourceWrapper messageSourceWrapper) {
        this.handbookRepository = handbookRepository;
        this.messageSourceWrapper = messageSourceWrapper;
    }

    public List<HandbookEntity> findAll() {
        return handbookRepository.findAll();
    }

    public HandbookEntity findById(Integer id) {
        Optional<HandbookEntity> handbook = handbookRepository.findById(id);
        if(handbook.isEmpty()) {
            throw new HandbookTypeNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.HANDBOOK_NOT_FOUND));
        }
        return handbook.get();
    }
}
