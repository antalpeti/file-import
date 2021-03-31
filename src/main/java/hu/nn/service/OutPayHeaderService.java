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

}