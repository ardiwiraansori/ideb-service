package com.skyworx.ideb.controller;

import com.skyworx.ideb.dto.IdebScrapeRequest;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

import java.util.Map;

@RestController
@RequestMapping("/api/ideb")
public class IdebController {

    private final RuntimeService runtimeService;

    public IdebController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/scrape")
    public ResponseEntity<?> scrape(@RequestBody IdebScrapeRequest request) {

        boolean nikEmpty =
                request.getNik() == null || request.getNik().isBlank();

        boolean nasabahNameEmpty =
                request.getNasabahName() == null || request.getNasabahName().isBlank();

        if (nikEmpty && nasabahNameEmpty) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", "NIK atau Nama Nasabah wajib diisi"
                    )
            );
        }

        Map<String, Object> variables = new HashMap<>();

        variables.put(
                "nik",
                request.getNik() != null ? request.getNik() : ""
        );

        variables.put(
                "nasabahName",
                request.getNasabahName() != null ? request.getNasabahName() : ""
        );

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(
                        "idebProcess",
                        variables
                );

        return ResponseEntity.accepted().body(
                Map.of(
                        "status", "ACCEPTED",
                        "processInstanceId", processInstance.getId(),
                        "nik", request.getNik() != null ? request.getNik() : "",
                        "nasabahName", request.getNasabahName() != null
                                ? request.getNasabahName()
                                : ""
                )
        );
    }
}