# IDEB Service

Prototype microservice untuk proses Generate SLIK Report (IDEB) sebagai Backend Developer Test PT Skyworx Indonesia.

Project ini mengimplementasikan:

* Web Scraping menggunakan Playwright
* Workflow BPMN menggunakan Flowable
* Asynchronous background processing
* Real-time notification menggunakan WebSocket + STOMP
* PDF generation menggunakan Flying Saucer
* Penyimpanan report ke MariaDB
* Dynamic search menggunakan QueryDSL

## Tech Stack

* Java 21
* Spring Boot 4.1.1
* Flowable 8.0.0
* Playwright 1.62.0
* QueryDSL 5.1.0
* Flying Saucer 10.5.0
* MariaDB
* Maven

## Project Flow

Workflow utama IDEB:

```text
Start
  |
  v
Scrape Data Eksternal
  |
  v
Validasi Data
  |
  v
Generate PDF
  |
  v
Simpan ke Database
  |
  v
End
```

Jika terjadi error pada proses:

```text
Scrape Data Eksternal
        |
        +--> Boundary Error --> Log Failure

Generate PDF
        |
        +--> Boundary Error --> Log Failure
```

Proses scraping dijalankan secara asynchronous oleh Flowable sehingga request HTTP tidak perlu menunggu seluruh proses selesai.

Status akhir proses dikirim ke client secara real-time melalui WebSocket.

## Database Setup

Buat database MariaDB:

```sql
CREATE DATABASE ideb_test;
```

Konfigurasi default aplikasi:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/ideb_test
spring.datasource.username=root
spring.datasource.password=
```

Hibernate menggunakan:

```properties
spring.jpa.hibernate.ddl-auto=update
```

sehingga tabel aplikasi akan dibuat atau diperbarui saat aplikasi dijalankan.

## Running the Application

### Windows

Jalankan:

```bat
mvnw.cmd clean compile
mvnw.cmd spring-boot:run
```

Aplikasi akan berjalan di:

```text
http://localhost:8080
```

Untuk membuat executable JAR:

```bat
mvnw.cmd clean package
```

Hasil build:

```text
target/ideb-service-0.0.1-SNAPSHOT.jar
```

JAR dapat dijalankan dengan:

```bat
java -jar target/ideb-service-0.0.1-SNAPSHOT.jar
```

## Mock External Data

Untuk kebutuhan prototype, scraping dilakukan terhadap halaman HTML lokal.

URL:

```text
http://localhost:8080/mock/ideb.html
```

Contoh data yang tersedia:

```text
Nama            : Andi Pratama
Status Kredit   : MACET
Nominal Tagihan : 2750000
```

Playwright Chromium berjalan dalam mode headless untuk mengambil data dari halaman tersebut.

## API - Start IDEB Process

### POST `/api/ideb/scrape`

Request dapat menggunakan NIK atau nama nasabah.

Contoh menggunakan NIK:

```bat
curl -X POST http://localhost:8080/api/ideb/scrape ^
  -H "Content-Type: application/json" ^
  -d"{\"nik\":\"1371012345678901\"}"
```

Contoh response:

```json
{
  "processInstanceId": "b6661b80-b0ff-11f1-8ba6-7008944873da",
  "nik": "1371012345678901",
  "status": "ACCEPTED",
  "nasabahName": ""
}
```

API mengembalikan HTTP:

```text
202 Accepted
```

Response `202 Accepted` diberikan segera setelah workflow berhasil dimulai.

Proses scraping, validasi, generate PDF, dan penyimpanan database dilanjutkan oleh Flowable secara asynchronous.

## Flowable BPMN

File BPMN berada di:

```text
src/main/resources/processes/ideb-process.bpmn20.xml
```

Workflow:

```text
Start
-> Scrape Data Eksternal
-> Validasi Data
-> Generate PDF
-> Simpan ke DB
-> End
```

Task scraping menggunakan asynchronous execution Flowable.

## Error Handling

Untuk mempermudah pengujian error handling, tersedia trigger simulasi.

### Scraping Error

Gunakan:

```json
{
  "nik": "ERROR"
}
```

Contoh:

```bat
curl -X POST http://localhost:8080/api/ideb/scrape ^
  -H "Content-Type: application/json" ^
  -d"{\"nik\":\"ERROR\"}"
```

Proses akan masuk ke Boundary Error pada tahap scraping dan kemudian menjalankan failure handler.

Failure stage:

```text
SCRAPE_DATA
```

### PDF Generation Error

Gunakan:

```json
{
  "nik": "PDF_ERROR"
}
```

Proses scraping dan validasi akan berjalan terlebih dahulu, kemudian proses Generate PDF akan menghasilkan simulated error.

Proses akan masuk ke Boundary Error Generate PDF.

Failure stage:

```text
GENERATE_PDF
```

## WebSocket Notification

WebSocket menggunakan STOMP.

Endpoint:

```text
ws://localhost:8080/ws
```

Topic:

```text
/topic/ideb
```

Halaman test WebSocket tersedia di:

```text
http://localhost:8080/ws-test.html
```

### Success Notification

Contoh event:

```json
{
  "message": "Proses IDEB berhasil",
  "processInstanceId": "b6661b80-b0ff-11f1-8ba6-7008944873da",
  "reportId": 11,
  "status": "SUCCESS",
  "pdfFileName": "ideb-report-b6661b80-b0ff-11f1-8ba6-7008944873da.pdf"
}
```

### Failed Notification

Contoh scraping failure:

```json
{
  "status": "FAILED",
  "message": "Simulasi kegagalan scraping",
  "processInstanceId": "...",
  "failureStage": "SCRAPE_DATA"
}
```

Contoh PDF failure:

```json
{
  "status": "FAILED",
  "message": "Simulasi kegagalan generate PDF",
  "processInstanceId": "...",
  "failureStage": "GENERATE_PDF"
}
```

## PDF Generation

PDF dibuat menggunakan Flying Saucer.

Data hasil scraping diubah menjadi XHTML dan kemudian dirender menjadi PDF.

Proses PDF menggunakan:

```java
ByteArrayOutputStream
```

sehingga PDF terlebih dahulu diproses di memory sebelum disimpan ke filesystem.

File hasil generate disimpan di:

```text
generated-reports/
```

Folder tersebut tidak dimasukkan ke Git repository.

## Database Report

Hasil workflow yang berhasil disimpan ke tabel:

```text
ideb_reports
```

Field utama:

```text
id
nik
nasabah_name
status_kredit
nominal_tagihan
pdf_file_name
created_at
```

## Dynamic Search - QueryDSL

### GET `/api/ideb/search`

Endpoint pencarian menggunakan QueryDSL dengan parameter yang seluruhnya bersifat optional:

```text
nasabahName
statusKredit
startDate
endDate
```

### Search All

```bat
curl "http://localhost:8080/api/ideb/search"
```

### Filter Nama Nasabah

```bat
curl "http://localhost:8080/api/ideb/search?nasabahName=Andi"
```

`nasabahName` menggunakan pencarian LIKE / contains secara case-insensitive.

Contoh:

```text
Andi
```

dapat menemukan:

```text
Andi Pratama
```

### Filter Status Kredit

```bat
curl "http://localhost:8080/api/ideb/search?statusKredit=MACET"
```

### Filter Start Date

Format tanggal:

```text
yyyy-MM-dd
```

Contoh:

```bat
curl "http://localhost:8080/api/ideb/search?startDate=2026-09-01"
```

### Filter End Date

```bat
curl "http://localhost:8080/api/ideb/search?endDate=2026-09-30"
```

### Kombinasi Semua Filter

```bat
curl "http://localhost:8080/api/ideb/search?nasabahName=Andi&statusKredit=MACET&startDate=2026-09-01&endDate=2026-09-30"
```

QueryDSL hanya menambahkan kondisi untuk parameter yang diberikan.

Secara konsep:

```sql
WHERE lower(nasabah_name) LIKE '%andi%'
  AND lower(status_kredit) = 'macet'
  AND created_at >= '2026-09-01 00:00:00'
  AND created_at < '2026-10-01 00:00:00'
```

## Example End-to-End Test

Buka terlebih dahulu:

```text
http://localhost:8080/ws-test.html
```

Kemudian trigger workflow:

```bat
curl -X POST http://localhost:8080/api/ideb/scrape ^
  -H "Content-Type: application/json" ^
  -d"{\"nik\":\"1371012345678901\"}"
```

API akan segera mengembalikan:

```text
HTTP 202 Accepted
```

Flowable kemudian menjalankan:

```text
Scraping
-> Validation
-> PDF Generation
-> Database Insert
```

Setelah selesai, browser yang subscribe ke:

```text
/topic/ideb
```

akan menerima status:

```text
SUCCESS
```

Data yang telah disimpan kemudian dapat dicari melalui:

```bat
curl "http://localhost:8080/api/ideb/search?nasabahName=Andi"
```

## Project Structure

```text
src/main/java/com/skyworx/ideb/
├── controller/
├── delegate/
├── dto/
├── entity/
├── repository/
├── service/
└── config/

src/main/resources/
├── processes/
│   └── ideb-process.bpmn20.xml
├── static/
│   ├── mock/
│   │   └── ideb.html
│   └── ws-test.html
└── application.properties
```

## Main Requirements Implemented

| Requirement                             | Status      |
| --------------------------------------- | ----------- |
| POST `/api/ideb/scrape`                 | Implemented |
| Playwright Web Scraping                 | Implemented |
| Flowable BPMN Workflow                  | Implemented |
| Scraping Boundary Error                 | Implemented |
| PDF Boundary Error                      | Implemented |
| Async Background Processing             | Implemented |
| WebSocket SUCCESS / FAILED Notification | Implemented |
| PDF Generation                          | Implemented |
| `ByteArrayOutputStream` PDF Processing  | Implemented |
| Save IDEB Report to Database            | Implemented |
| GET `/api/ideb/search`                  | Implemented |
| QueryDSL Dynamic Filtering              | Implemented |
