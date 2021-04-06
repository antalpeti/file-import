package hu.nn.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.nn.constant.SurValuesConstant;
import hu.nn.dto.SurValuesDTO;
import hu.nn.entity.SurValues;
import hu.nn.util.CSVUtil;
import hu.nn.util.DateUtil;
import hu.nn.util.NumberUtil;
import hu.nn.util.Util;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            dto.setCompany(entity.getCompany());
            dto.setChdrnum(entity.getChdrnum());
            dto.setSurvalue(entity.getSurvalue());
            dto.setCurrency(entity.getCurrency());
            dto.setValidDate(entity.getValidDate());
        }
    }

    public static SurValuesDTO updateDTO(final SurValuesDTO dto, final String[] row) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(row)) {
            int i = 0;
            dto.setCompany(CSVUtil.getElement(row, i++));
            dto.setChdrnum(CSVUtil.getElement(row, i++));
            dto.setSurvalue(NumberUtil.parseNumber(CSVUtil.getElement(row, i), BigDecimal.class));
            if (SurValuesConstant.COMPANY_EUR.equals(dto.getCompany())) {
                dto.setCurrency(SurValuesConstant.CURRENCY_EUR);
            } else if (SurValuesConstant.COMPANY_HUF.equals(dto.getCompany())) {
                dto.setCurrency(SurValuesConstant.CURRENCY_HUF);
            }
            String element = CSVUtil.getElement(row, row.length - 1);
            if (Util.isNotEmpty(element) && element.length() >= DateUtil.DATE_PATTERN_10_HU_HYPHEN.length()) {
                dto.setValidDate(element.substring(0, DateUtil.DATE_PATTERN_10_HU_HYPHEN.length()));
            }
        }
        return dto;
    }

    public static SurValues updateEntity(final SurValues entity, final SurValuesDTO dto) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
            entity.setId(dto.getId());
            entity.setCompany(dto.getCompany());
            entity.setChdrnum(dto.getChdrnum());
            entity.setSurvalue(dto.getSurvalue());
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