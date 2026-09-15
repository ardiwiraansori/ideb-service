package com.skyworx.ideb.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidateDataDelegate implements JavaDelegate {

    private static final Logger log =
            LoggerFactory.getLogger(ValidateDataDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {

        String scrapedName =
                (String) execution.getVariable("scrapedName");

        String statusKredit =
                (String) execution.getVariable("statusKredit");

        Long nominalTagihan =
                (Long) execution.getVariable("nominalTagihan");

        boolean valid =
                scrapedName != null && !scrapedName.isBlank()
                        && statusKredit != null && !statusKredit.isBlank()
                        && nominalTagihan != null;

        execution.setVariable(
                "validationStatus",
                valid ? "VALID" : "INVALID"
        );

        log.info(
                "Validasi Data dijalankan. scrapedName={}, statusKredit={}, nominalTagihan={}, hasil={}",
                scrapedName,
                statusKredit,
                nominalTagihan,
                valid ? "VALID" : "INVALID"
        );
    }
}
