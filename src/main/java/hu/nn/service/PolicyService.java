package hu.nn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.nn.dto.PolicyDTO;
import hu.nn.entity.Policy;
import hu.nn.mapper.PolicyMapper;
import hu.nn.repository.PolicyRepository;
import hu.nn.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public List<PolicyDTO> findAll() {
        List<Policy> entities = policyRepository.findAll();
        return PolicyMapper.mapEntitiesIntoDTOs(entities);
    }

    @Transactional
    public boolean save(PolicyDTO dto) {
        boolean saved = false;
        if (dto != null) {
            try {
                policyRepository.saveAndFlush(PolicyMapper.createFrom(dto));
                saved = true;
            } catch (Exception e) {
                log.error("Error in save: {}", e);
                dto.setCauseOfSaveFailure(ExceptionUtil.getRootCauseMessageInOneLine(e));
            }
        }
        return saved;
    }

}