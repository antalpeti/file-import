package hu.nn.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.nn.dto.PolicyDTO;
import hu.nn.entity.Policy;
import hu.nn.util.CSVUtil;
import hu.nn.util.Util;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PolicyMapper {

    public static Policy createFrom(final PolicyDTO dto) {
        return updateEntity(new Policy(), dto);
    }

    public static PolicyDTO createFrom(final Policy entity) {
        if (Util.isNotEmpty(entity)) {
            PolicyDTO dto = new PolicyDTO();
            updateDTO(dto, entity);
            return dto;
        }
        return null;
    }

    private static void updateDTO(final PolicyDTO dto, final Policy entity) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
            dto.setId(entity.getId());
            dto.setChdrnum(entity.getChdrnum());
            dto.setCownnum(entity.getCownnum());
            dto.setOwnerName(entity.getOwnerName());
            dto.setLifcNum(entity.getLifcNum());
            dto.setLifcName(entity.getLifcName());
            dto.setAracde(entity.getAracde());
            dto.setAgntnum(entity.getAgntnum());
            dto.setMailAddress(entity.getMailAddress());
        }
    }

    public static PolicyDTO updateDTO(final PolicyDTO dto, final String[] csvRow) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(csvRow)) {
            int i = 0;
            dto.setChdrnum(CSVUtil.getElement(csvRow, i++));
            dto.setCownnum(CSVUtil.getElement(csvRow, i++));
            dto.setOwnerName(CSVUtil.getElement(csvRow, i++));
            dto.setLifcNum(CSVUtil.getElement(csvRow, i++));
            dto.setLifcName(CSVUtil.getElement(csvRow, i++));
            dto.setAracde(CSVUtil.getElement(csvRow, i++));
            dto.setAgntnum(CSVUtil.getElement(csvRow, i++));
            dto.setMailAddress(CSVUtil.getElement(csvRow, i));
        }
        return dto;
    }

    public static Policy updateEntity(final Policy entity, final PolicyDTO dto) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
            entity.setId(dto.getId());
            entity.setChdrnum(dto.getChdrnum());
            entity.setCownnum(dto.getCownnum());
            entity.setOwnerName(dto.getOwnerName());
            entity.setLifcNum(dto.getLifcNum());
            entity.setLifcName(dto.getLifcName());
            entity.setAracde(dto.getAracde());
            entity.setAgntnum(dto.getAgntnum());
            entity.setMailAddress(dto.getMailAddress());
        }
        return entity;
    }

    public static List<PolicyDTO> mapEntitiesIntoDTOs(final List<Policy> entities) {
        return Util.isNotEmpty(entities) ? entities.stream().map(PolicyMapper::createFrom).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static List<Policy> createFromDTOs(final Collection<PolicyDTO> dtos) {
        return Util.isNotEmpty(dtos) ? dtos.stream().map(PolicyMapper::createFrom).collect(Collectors.toList()) : Collections.emptyList();
    }

}