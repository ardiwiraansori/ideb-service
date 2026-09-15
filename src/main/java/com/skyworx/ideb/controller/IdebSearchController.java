package com.skyworx.ideb.controller;

import com.skyworx.ideb.entity.IdebReport;
import com.skyworx.ideb.service.IdebSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ideb")
public class IdebSearchController {

    private final IdebSearchService idebSearchService;

    public IdebSearchController(IdebSearchService idebSearchService) {
        this.idebSearchService = idebSearchService;
    }

    @GetMapping("/search")
    public Iterable<IdebReport> search(
            @RequestParam(required = false) String nasabahName,
            @RequestParam(required = false) String statusKredit,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return idebSearchService.search(
                nasabahName,
                statusKredit,
                startDate,
                endDate
        );
    }
}