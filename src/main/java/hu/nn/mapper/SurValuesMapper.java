package hu.nn.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.nn.dto.SurValuesDTO;
import hu.nn.entity.SurValues;
import hu.nn.util.Util;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SurValuesMapper {

	public static SurValues createFrom(final SurValuesDTO dto) {
		return updateEntity(new SurValues(), dto);
	}

	public static SurValuesDTO createFrom(final SurValues entity) {
		if (Util.isNotEmpty(entity)) {
			SurValuesDTO dto = new SurValuesDTO();
			updateDTO(dto, entity);
			return dto;
		}
		return null;
	}

	private static void updateDTO(final SurValuesDTO dto, final SurValues entity) {
		if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
			dto.setId(entity.getId());
			dto.setChdrnum(entity.getChdrnum());
			dto.setSurvalue(entity.getSurvalue());
			dto.setCompany(entity.getCompany());
			dto.setCurrency(entity.getCurrency());
			dto.setValidDate(entity.getValidDate());
		}
	}

	public static SurValues updateEntity(final SurValues entity, final SurValuesDTO dto) {
		if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
			entity.setId(dto.getId());
			entity.setChdrnum(dto.getChdrnum());
			entity.setSurvalue(dto.getSurvalue());
			entity.setCompany(dto.getCompany());
			entity.setCurrency(dto.getCurrency());
			entity.setValidDate(dto.getValidDate());
		}
		return entity;
	}

	public static List<SurValuesDTO> mapEntitiesIntoDTOs(final List<SurValues> entities) {
		return Util.isNotEmpty(entities) ? entities.stream().map(SurValuesMapper::createFrom).collect(Collectors.toList()) : Collections.emptyList();
	}

	public static List<SurValues> createFromDTOs(final Collection<SurValuesDTO> dtos) {
		return Util.isNotEmpty(dtos) ? dtos.stream().map(SurValuesMapper::createFrom).collect(Collectors.toList()) : Collections.emptyList();
	}

}