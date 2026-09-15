package com.skyworx.ideb.repository;

import com.skyworx.ideb.entity.IdebReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdebReportRepository
        extends JpaRepository<IdebReport, Long> {
}
