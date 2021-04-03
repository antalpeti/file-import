package hu.nn.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.nn.dto.OutPayHeaderDTO;
import hu.nn.entity.OutPayHeader;
import hu.nn.util.CsvUtil;
import hu.nn.util.DateUtil;
import hu.nn.util.NumberUtil;
import hu.nn.util.Util;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OutPayHeaderMapper {

    public static OutPayHeader createFrom(final OutPayHeaderDTO dto) {
        return updateEntity(new OutPayHeader(), dto);
    }

    public static OutPayHeaderDTO createFrom(final OutPayHeader entity) {
        if (Util.isNotEmpty(entity)) {
            OutPayHeaderDTO dto = new OutPayHeaderDTO();
            updateDTO(dto, entity);
            return dto;
        }
        return null;
    }

    private static void updateDTO(final OutPayHeaderDTO dto, final OutPayHeader entity) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
            dto.setOutpayHeaderId(entity.getOutpayHeaderId());
            dto.setClntnum(entity.getClntnum());
            dto.setChdrnum(entity.getChdrnum());
            dto.setLetterType(entity.getLetterType());
            dto.setPrintDate(entity.getPrintDate());
            dto.setDataId(entity.getDataId());
            dto.setClntName(entity.getClntName());
            dto.setClntAddress(entity.getClntAddress());
            dto.setRegDate(entity.getRegDate());
            dto.setBenPercent(entity.getBenPercent());
            dto.setRole1(entity.getRole1());
            dto.setRole2(entity.getRole2());
            dto.setCownNum(entity.getCownNum());
            dto.setCownName(entity.getCownName());
            dto.setNotice01(entity.getNotice01());
            dto.setNotice02(entity.getNotice02());
            dto.setNotice03(entity.getNotice03());
            dto.setNotice04(entity.getNotice04());
            dto.setNotice05(entity.getNotice05());
            dto.setNotice06(entity.getNotice06());
            dto.setClaimId(entity.getClaimId());
            dto.setTp2processDate(entity.getTp2processDate());
        }
    }

    public static OutPayHeaderDTO updateDTO(final OutPayHeaderDTO dto, final String[] csvRow) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(csvRow)) {
            int i = 0;
            dto.setClntnum(CsvUtil.getElement(csvRow, i++));
            dto.setChdrnum(CsvUtil.getElement(csvRow, i++));
            dto.setLetterType(CsvUtil.getElement(csvRow, i++));
            dto.setPrintDate(DateUtil.parseDate(CsvUtil.getElement(csvRow, i++)));
            dto.setDataId(CsvUtil.getElement(csvRow, i++));
            dto.setClntName(CsvUtil.getElement(csvRow, i++));
            dto.setClntAddress(CsvUtil.getElement(csvRow, i++));
            dto.setRegDate(DateUtil.parseDate(CsvUtil.getElement(csvRow, i++)));
            dto.setBenPercent(NumberUtil.parseNumber(CsvUtil.getElement(csvRow, i++), BigDecimal.class));
            dto.setRole1(CsvUtil.getElement(csvRow, i++));
            dto.setRole2(CsvUtil.getElement(csvRow, i++));
            dto.setCownNum(CsvUtil.getElement(csvRow, i++));
            dto.setCownName(CsvUtil.getElement(csvRow, i++));
            dto.setNotice01(CsvUtil.getElement(csvRow, i++));
            dto.setNotice02(CsvUtil.getElement(csvRow, i++));
            dto.setNotice03(CsvUtil.getElement(csvRow, i++));
            dto.setNotice04(CsvUtil.getElement(csvRow, i++));
            dto.setNotice05(CsvUtil.getElement(csvRow, i++));
            dto.setNotice06(CsvUtil.getElement(csvRow, i++));
            dto.setClaimId(CsvUtil.getElement(csvRow, i++));
            dto.setTp2processDate(DateUtil.parseDate(CsvUtil.getElement(csvRow, i)));
        }
        return dto;
    }

    public static OutPayHeader updateEntity(final OutPayHeader entity, final OutPayHeaderDTO dto) {
        if (Util.isNotEmpty(dto) && Util.isNotEmpty(entity)) {
            entity.setOutpayHeaderId(dto.getOutpayHeaderId());
            entity.setClntnum(dto.getClntnum());
            entity.setChdrnum(dto.getChdrnum());
            entity.setLetterType(dto.getLetterType());
            entity.setPrintDate(dto.getPrintDate());
            entity.setDataId(dto.getDataId());
            entity.setClntName(dto.getClntName());
            entity.setClntAddress(dto.getClntAddress());
            entity.setRegDate(dto.getRegDate());
            entity.setBenPercent(dto.getBenPercent());
            entity.setRole1(dto.getRole1());
            entity.setRole2(dto.getRole2());
            entity.setCownNum(dto.getCownNum());
            entity.setCownName(dto.getCownName());
            entity.setNotice01(dto.getNotice01());
            entity.setNotice02(dto.getNotice02());
            entity.setNotice03(dto.getNotice03());
            entity.setNotice04(dto.getNotice04());
            entity.setNotice05(dto.getNotice05());
            entity.setNotice06(dto.getNotice06());
            entity.setClaimId(dto.getClaimId());
            entity.setTp2processDate(dto.getTp2processDate());
        }
        return entity;
    }

    public static List<OutPayHeaderDTO> mapEntitiesIntoDTOs(final List<OutPayHeader> entities) {
        return Util.isNotEmpty(entities) ? entities.stream().map(OutPayHeaderMapper::createFrom).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static List<OutPayHeader> createFromDTOs(final Collection<OutPayHeaderDTO> dtos) {
        return Util.isNotEmpty(dtos) ? dtos.stream().map(OutPayHeaderMapper::createFrom).collect(Collectors.toList()) : Collections.emptyList();
    }

}