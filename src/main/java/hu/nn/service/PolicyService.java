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

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public PolicyDTO getById(Integer id) {
        return PolicyMapper.createFrom(policyRepository.getById(id));
    }

    public List<PolicyDTO> findAll() {
        List<Policy> entities = policyRepository.findAll();
        return PolicyMapper.mapEntitiesIntoDTOs(entities);
    }

}