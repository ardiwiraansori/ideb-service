package com.skyworx.ideb.delegate;

import com.skyworx.ideb.entity.IdebReport;
import com.skyworx.ideb.repository.IdebReportRepository;
import com.skyworx.ideb.service.WebSocketNotificationService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SaveReportDelegate implements JavaDelegate {

    private static final Logger log =
            LoggerFactory.getLogger(SaveReportDelegate.class);

    private final IdebReportRepository repository;
    private final WebSocketNotificationService notificationService;

    public SaveReportDelegate(
            IdebReportRepository repository,
            WebSocketNotificationService notificationService
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        String nik =
                (String) execution.getVariable("nik");

        String scrapedName =
                (String) execution.getVariable("scrapedName");

        String statusKredit =
                (String) execution.getVariable("statusKredit");

        Long nominalTagihan =
                (Long) execution.getVariable("nominalTagihan");

        String pdfFileName =
                (String) execution.getVariable("pdfFileName");

        IdebReport report = new IdebReport();

        report.setNik(nik);
        report.setNasabahName(scrapedName);
        report.setStatusKredit(statusKredit);
        report.setNominalTagihan(nominalTagihan);
        report.setPdfFileName(pdfFileName);

        IdebReport savedReport =
                repository.save(report);

        execution.setVariable(
                "reportId",
                savedReport.getId()
        );

        notificationService.sendSuccess(
                execution.getProcessInstanceId(),
                savedReport.getId(),
                savedReport.getPdfFileName()
        );

        log.info(
                "Report berhasil disimpan ke database. reportId={}, nasabahName={}",
                savedReport.getId(),
                savedReport.getNasabahName()
        );
    }
}