package hu.nn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.nn.dto.SurValuesDTO;
import hu.nn.entity.SurValues;
import hu.nn.mapper.SurValuesMapper;
import hu.nn.repository.SurValuesRepository;
import hu.nn.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SurValuesService {

    @Autowired
    private SurValuesRepository surValuesRepository;

    public List<SurValuesDTO> findAll() {
        List<SurValues> entities = surValuesRepository.findAll();
        return SurValuesMapper.mapEntitiesIntoDTOs(entities);
    }

    @Transactional
    public boolean save(SurValuesDTO dto) {
        boolean saved = false;
        if (dto != null) {
            try {
                surValuesRepository.saveAndFlush(SurValuesMapper.createFrom(dto));
                saved = true;
            } catch (Exception e) {
                log.error("Error in save: {}", e);
                dto.setCauseOfSaveFailure(ExceptionUtil.getRootCauseMessageInOneLine(e));
            }
        }
        return saved;
    }
}
