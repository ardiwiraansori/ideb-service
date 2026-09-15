package com.skyworx.ideb.service;

import com.querydsl.core.BooleanBuilder;
import com.skyworx.ideb.entity.IdebReport;
import com.skyworx.ideb.entity.QIdebReport;
import com.skyworx.ideb.repository.IdebReportRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class IdebSearchService {

    private final IdebReportRepository idebReportRepository;

    public IdebSearchService(IdebReportRepository idebReportRepository) {
        this.idebReportRepository = idebReportRepository;
    }

    public Iterable<IdebReport> search(
            String nasabahName,
            String statusKredit,
            LocalDate startDate,
            LocalDate endDate) {

        QIdebReport idebReport = QIdebReport.idebReport;

        BooleanBuilder predicate = new BooleanBuilder();

        if (StringUtils.hasText(nasabahName)) {
            predicate.and(
                    idebReport.nasabahName.containsIgnoreCase(nasabahName.trim())
            );
        }

        if (StringUtils.hasText(statusKredit)) {
            predicate.and(
                    idebReport.statusKredit.equalsIgnoreCase(statusKredit.trim())
            );
        }

        if (startDate != null) {
            predicate.and(
                    idebReport.createdAt.goe(startDate.atStartOfDay())
            );
        }

        if (endDate != null) {
            predicate.and(
                    idebReport.createdAt.lt(
                            endDate.plusDays(1).atStartOfDay()
                    )
            );
        }

        return idebReportRepository.findAll(
                predicate,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }
}