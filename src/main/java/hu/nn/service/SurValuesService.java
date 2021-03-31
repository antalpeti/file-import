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

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SurValuesService {

	@Autowired
	private SurValuesRepository surValuesRepository;

	public SurValuesDTO getById(Integer id) {
		return SurValuesMapper.createFrom(surValuesRepository.getById(id));
	}
	
	public List<SurValuesDTO> findAll() {
		List<SurValues> entities = surValuesRepository.findAll();
		return SurValuesMapper.mapEntitiesIntoDTOs(entities);
	}

}
