package hu.nn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.nn.dto.OutPayHeaderDTO;
import hu.nn.entity.OutPayHeader;
import hu.nn.mapper.OutPayHeaderMapper;
import hu.nn.repository.OutPayHeaderRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class OutPayHeaderService {

    @Autowired
    private OutPayHeaderRepository outPayHeaderRepository;

    public OutPayHeaderDTO getByOutpayHeaderId(Integer outpayHeaderId) {
        return OutPayHeaderMapper.createFrom(outPayHeaderRepository.getByOutpayHeaderId(outpayHeaderId));
    }

    public List<OutPayHeaderDTO> findAll() {
        List<OutPayHeader> entities = outPayHeaderRepository.findAll();
        return OutPayHeaderMapper.mapEntitiesIntoDTOs(entities);
    }

    @Transactional
    public boolean save(OutPayHeaderDTO dto) {
        boolean saved = false;
        if (dto != null) {
            try {
                outPayHeaderRepository.save(OutPayHeaderMapper.createFrom(dto));
                saved = true;
            } catch (Exception e) {
                log.error("Error in save: {}", e);
                dto.setCauseOfSaveFailure(e.getMessage());
            }
        }
        return saved;
    }
}