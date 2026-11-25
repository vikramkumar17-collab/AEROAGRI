# AEROAGRI – Simple Java Backend

Spring Boot backend for the AEROAGRI drone monitoring website. **All sensor data is SIMULATED** (no real drone hardware).

## Technologies
Java 17, Spring Boot 3.2 (Spring Web, JDBC), Maven, H2 database (file), JUnit 5, HTML/CSS/JS + fetch() frontend.

## Project structure
```
pom.xml
src/main/java/com/aeroagri/
  AeroAgriApplication.java      starts the app
  model/        Sensor, LiDARSensor, GPSSensor, IMUSensor, Drone, Field, Sector, Scan, User
  exception/    SensorException, ScanNotFoundException
  repository/   AeroRepository   (all SQL, JdbcTemplate)
  service/      SensorService, ScanService, CsvExporter
  controller/   ApiController    (REST endpoints)
src/main/resources/
  static/index.html             your frontend (served at http://localhost:8080)
  schema.sql, data.sql          tables + 3 sensors
  application.properties
src/test/java/com/aeroagri/     SensorTest, ScanServiceTest
```

## Run (PowerShell)
Needs JDK 17+ and Maven (`java -version`, `mvn -v`).
```powershell
cd path\to\aeroagri-backend
mvn test
mvn spring-boot:run
```
Then open **http://localhost:8080** in the browser. (Opening index.html directly also works; it calls http://localhost:8080.)
If the backend is not running, the page shows "Backend disconnected" at the bottom-left and keeps its built-in demo values.

## Database
H2 file database in `./data/aeroagri.mv.db` (kept after restart). Tables: `sensors`, `sensor_readings`, `scans`, `scan_results`.
Delete the `data` folder to reset.

## API endpoints
| Method | URL | Purpose |
|---|---|---|
| GET | /api/dashboard | drone, field, mode, latest readings |
| GET | /api/sensors | new simulated readings (saved to DB) |
| GET | /api/sensors/latest | last saved readings |
| POST | /api/demo/mode | `{"mode":"healthy"}` or `{"mode":"problem"}` |
| GET | /api/fields | field with sectors |
| POST | /api/scans/start | start a scan (finishes after 10 s, `aeroagri.scan-seconds`) |
| GET | /api/scans/{id} | status, progress %, stage |
| GET | /api/scans/{id}/results | sector-wise results + warning |
| GET | /api/scans/history | last 20 scans |
| GET | /api/reports/scans/{id}/csv | CSV download (also saved in `./reports`) |

## Where each OOP concept is
| Concept | Where |
|---|---|
| Encapsulation | private fields + getters/setters in `Sensor`, `Scan`, `User`, `Sector` |
| Constructors | `Drone(name)` / `Drone(name,status,battery)`, `GPSSensor` (2 versions), `Scan()` / `Scan(field,mode)` |
| Inheritance | `LiDARSensor`, `GPSSensor`, `IMUSensor` extend `Sensor` |
| Overriding | `takeReading()`, `getUnit()`, `getType()`, `getDetails()` in the 3 sensors |
| Polymorphism | `Drone.sensors` is `List<Sensor>`; `SensorService.readAll()` loops over `Sensor` |
| Abstraction | `abstract class Sensor` with abstract methods |
| Collections/Generics | `ArrayList<Sensor>`, `List<Sector>`, `HashMap<String,Object>` |
| Exceptions | `SensorException`, `ScanNotFoundException`, try-catch in `SensorService.readAll()` |
| File handling | `CsvExporter` |
| JDBC | `AeroRepository` (JdbcTemplate) |
| Networking | `ApiController` + `fetch()` in index.html |
