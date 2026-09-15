package com.skyworx.ideb.delegate;

import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.ByteArrayOutputStream;

@Component
public class GeneratePdfDelegate implements JavaDelegate {

    private static final Logger log =
            LoggerFactory.getLogger(GeneratePdfDelegate.class);

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

        String validationStatus =
                (String) execution.getVariable("validationStatus");

        // Tetap dipertahankan untuk test PDF Error Boundary
        if ("PDF_ERROR".equalsIgnoreCase(nik)) {

            execution.setVariable(
                    "failureStage",
                    "GENERATE_PDF"
            );

            execution.setVariable(
                    "failureMessage",
                    "Simulasi kegagalan generate PDF"
            );

            throw new BpmnError(
                    "PDF_ERROR",
                    "Simulasi kegagalan generate PDF"
            );
        }

        try {

            String html = """
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                        <style>
                            @page {
                                size: A4;
                                margin: 30px;
                            }

                            body {
                                font-family: sans-serif;
                                font-size: 12px;
                            }

                            h1 {
                                text-align: center;
                            }

                            table {
                                width: 100%%;
                                border-collapse: collapse;
                            }

                            td {
                                border: 1px solid #000;
                                padding: 8px;
                            }

                            .label {
                                font-weight: bold;
                                width: 35%%;
                            }
                        </style>
                    </head>
                    <body>

                        <h1>SLIK Report / IDEB</h1>

                        <table>
                            <tr>
                                <td class="label">NIK</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td class="label">Nama Nasabah</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td class="label">Status Kredit</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td class="label">Nominal Tagihan</td>
                                <td>%d</td>
                            </tr>
                            <tr>
                                <td class="label">Status Validasi</td>
                                <td>%s</td>
                            </tr>
                        </table>

                    </body>
                    </html>
                    """.formatted(
                    nik,
                    scrapedName,
                    statusKredit,
                    nominalTagihan,
                    validationStatus
            );

            try (ByteArrayOutputStream outputStream =
                         new ByteArrayOutputStream()) {

                ITextRenderer renderer = new ITextRenderer();

                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);

                byte[] pdfBytes =
                        outputStream.toByteArray();

                Path reportDirectory =
                        Paths.get("generated-reports");

                Files.createDirectories(reportDirectory);

                String pdfFileName =
                        "ideb-report-"
                                + execution.getProcessInstanceId()
                                + ".pdf";

                Path pdfPath =
                        reportDirectory.resolve(pdfFileName);

                Files.write(
                        pdfPath,
                        pdfBytes
                );

                execution.setVariable(
                        "pdfStatus",
                        "GENERATED"
                );

                execution.setVariable(
                        "pdfSizeBytes",
                        pdfBytes.length
                );

                execution.setVariable(
                        "pdfFileName",
                        pdfFileName
                );

                log.info(
                        "PDF berhasil dibuat. nama={}, size={} bytes, file={}",
                        scrapedName,
                        pdfBytes.length,
                        pdfPath.toAbsolutePath()
                );
            }

        } catch (BpmnError e) {
            throw e;

        } catch (Exception e) {

            execution.setVariable(
                    "failureStage",
                    "GENERATE_PDF"
            );

            execution.setVariable(
                    "failureMessage",
                    e.getMessage()
            );

            throw new BpmnError(
                    "PDF_ERROR",
                    "Gagal melakukan generate PDF"
            );
        }
    }
}