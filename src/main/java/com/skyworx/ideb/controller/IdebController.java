package com.skyworx.ideb.controller;

import com.skyworx.ideb.dto.IdebScrapeRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ideb")
public class IdebController {

    @PostMapping("/scrape")
    public Map<String, String> scrape(@RequestBody IdebScrapeRequest request) {

        boolean nikKosong = request.getNik() == null || request.getNik().isBlank();
        boolean namaKosong = request.getNasabahName() == null || request.getNasabahName().isBlank();

        if (nikKosong && namaKosong) {
            return Map.of(
                    "status", "ERROR",
                    "message", "NIK atau Nama Nasabah wajib diisi"
            );
        }

        return Map.of(
                "status", "ACCEPTED",
                "nik", request.getNik() != null ? request.getNik() : "",
                "nasabahName", request.getNasabahName() != null ? request.getNasabahName() : ""
        );
    }
}

