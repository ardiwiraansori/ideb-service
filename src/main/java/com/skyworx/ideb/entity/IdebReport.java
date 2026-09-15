package com.skyworx.ideb.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ideb_reports")
public class IdebReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nik;

    @Column(name = "nasabah_name")
    private String nasabahName;

    @Column(name = "status_kredit")
    private String statusKredit;

    @Column(name = "nominal_tagihan")
    private Long nominalTagihan;

    @Column(name = "pdf_file_name")
    private String pdfFileName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNasabahName() {
        return nasabahName;
    }

    public void setNasabahName(String nasabahName) {
        this.nasabahName = nasabahName;
    }

    public String getStatusKredit() {
        return statusKredit;
    }

    public void setStatusKredit(String statusKredit) {
        this.statusKredit = statusKredit;
    }

    public Long getNominalTagihan() {
        return nominalTagihan;
    }

    public void setNominalTagihan(Long nominalTagihan) {
        this.nominalTagihan = nominalTagihan;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
