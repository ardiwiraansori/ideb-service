package com.skyworx.ideb.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.flowable.engine.delegate.BpmnError;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.flowable.engine.delegate.BpmnError;

@Component
public class ScrapeDataDelegate implements JavaDelegate {

    private static final Logger log =
            LoggerFactory.getLogger(ScrapeDataDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {

        String nik = (String) execution.getVariable("nik");
        String nasabahName = (String) execution.getVariable("nasabahName");

        log.info(
                "Scrape Data Eksternal dijalankan. processInstanceId={}, nik={}, nasabahName={}",
                execution.getProcessInstanceId(),
                nik,
                nasabahName
        );

        // Tetap dipertahankan untuk test Boundary Error
        if ("ERROR".equalsIgnoreCase(nik)) {

            execution.setVariable(
                    "failureStage",
                    "SCRAPE_DATA"
            );

            execution.setVariable(
                    "failureMessage",
                    "Simulasi kegagalan scraping"
            );

            throw new BpmnError(
                    "SCRAPE_ERROR",
                    "Simulasi kegagalan scraping"
            );
        }

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
            );

            try {
                Page page = browser.newPage();

                page.navigate(
                        "http://localhost:8080/mock/ideb.html"
                );

                String scrapedName =
                        page.locator("#nasabah-name")
                                .textContent()
                                .trim();

                String statusKredit =
                        page.locator("#status-kredit")
                                .textContent()
                                .trim();

                String nominalTagihanText =
                        page.locator("#nominal-tagihan")
                                .textContent()
                                .trim();

                Long nominalTagihan =
                        Long.parseLong(nominalTagihanText);

                execution.setVariable(
                        "scrapedName",
                        scrapedName
                );

                execution.setVariable(
                        "statusKredit",
                        statusKredit
                );

                execution.setVariable(
                        "nominalTagihan",
                        nominalTagihan
                );

                log.info(
                        "Hasil scraping Playwright: scrapedName={}, statusKredit={}, nominalTagihan={}",
                        scrapedName,
                        statusKredit,
                        nominalTagihan
                );

            } finally {
                browser.close();
            }

        } catch (BpmnError e) {
            throw e;

        } catch (Exception e) {

            execution.setVariable(
                    "failureStage",
                    "SCRAPE_DATA"
            );

            execution.setVariable(
                    "failureMessage",
                    e.getMessage()
            );

            throw new BpmnError(
                    "SCRAPE_ERROR",
                    "Gagal melakukan scraping"
            );
        }
    }
}
