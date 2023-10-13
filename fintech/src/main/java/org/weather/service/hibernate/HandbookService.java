package org.weather.service.hibernate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.weather.entity.Handbook;
import org.weather.exception.handbook.HandbookTypeNotFoundException;
import org.weather.repository.hibernate.HandbookRepository;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HandbookService {
    private final HandbookRepository handbookRepository;
    private final MessageSourceWrapper messageSourceWrapper;
    public List<Handbook> findAll() {
        return handbookRepository.findAll();
    }

    public Handbook findById(Integer id) {
        Optional<Handbook> handbook = handbookRepository.findById(id);
        if(handbook.isEmpty()) {
            throw new HandbookTypeNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.HANDBOOK_NOT_FOUND));
        }
        return handbook.get();
    }
}
