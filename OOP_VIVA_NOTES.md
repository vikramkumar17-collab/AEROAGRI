# OOP Viva Notes – AEROAGRI

**Encapsulation** – `Sensor` keeps `id`, `name`, `status` private; other classes use `getStatus()` / `setStatus()`. `Sector` also hides its status and recalculates it inside `evaluate()`.

**Constructors** – `new Drone("AEROAGRI-D1")` calls `this(name, "Hovering / Idle", 100)` (constructor chaining). `GPSSensor(id,name)` calls the 4-argument one (overloading). `Scan()` is empty for JDBC, `Scan(field, mode)` sets RUNNING.

**Abstraction** – `Sensor` is abstract: it declares `takeReading()`, `getUnit()`, `getType()` without code. You cannot write `new Sensor(...)`.

**Inheritance** – `class LiDARSensor extends Sensor` reuses id/name/status and `checkOnline()`.

**Overriding** – each child writes its own `takeReading()` (LiDAR = distance cm, GPS = latitude, IMU = tilt) and `@Override getDetails()`.

**Polymorphism** – in `SensorService.readAll()`: `for (Sensor s : drone.getSensors()) s.takeReading();`. Same call, different result depending on the real object.

**Collections & generics** – `List<Sensor>` in `Drone`, `List<Sector>` in `Field`, `Map<String,Object>` (`HashMap`) for JSON responses.

**Exception handling** – custom `SensorException` is thrown when a sensor is OFFLINE or LiDAR gives an impossible value; `readAll()` catches it so one broken sensor does not stop the others. `ScanNotFoundException` gives a 404.

**File handling** – `CsvExporter.export()` writes `reports/scan_<id>.csv` with `PrintWriter` inside try-with-resources.

**JDBC** – `AeroRepository` uses `JdbcTemplate.update()` to INSERT/UPDATE and `query()` with a RowMapper to SELECT. Tables: sensors, sensor_readings, scans, scan_results.

**Networking** – browser `fetch()` calls REST URLs like `/api/scans/start`; `ApiController` returns JSON.

**Scan flow** – POST start → row in `scans` (RUNNING) → frontend polls `GET /api/scans/{id}` → after 10 s the service generates sector results, saves them in `scan_results`, marks COMPLETED.

**Honesty point** – every reading is simulated with `Random`; the UI labels it "SIMULATED".
