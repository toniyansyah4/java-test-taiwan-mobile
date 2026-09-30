# Java Spring Boot

## Registration and login (PostgreSQL)

Implements `Require.MD` with PostgreSQL persistence, as requested, instead of in-memory accounts.
Create the `setup-java` database and start PostgreSQL. `schema.sql` creates the `app_users` table automatically.
Override connection settings with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables.
Passwords are stored only as BCrypt hashes with cost 12. Emails are trimmed and normalized to lowercase;
a database unique constraint prevents duplicate accounts, including concurrent registrations.

```bash
curl -i http://localhost:8040/register --data-urlencode 'email=test@example.com' --data-urlencode 'password=mypassword'
curl -i http://localhost:8040/login --data-urlencode 'email=test@example.com' --data-urlencode 'password=mypassword'
```

Both endpoints accept `application/x-www-form-urlencoded` bodies. Registration returns 201 with
`User registered successfully.`; login returns 200 with `Login successful`.
Use `Accept: text/plain` for these plain-text responses or `Accept: application/json` for
JSON responses such as `{"message":"User registered successfully."}` (including error messages).
Invalid inputs return 400, duplicate email returns 409, and invalid credentials return 401 with
`Invalid email or password.`. Passwords require 8–72 characters and at most 72 UTF-8 bytes (BCrypt's limit).
Login verifies credentials; it does not issue a session or token or protect the existing task API.
Account data persists across application restarts. HTTP tests use isolated H2 storage in PostgreSQL mode.

## Contoh CRUD tasks (PostgreSQL Laragon)

Nyalakan PostgreSQL dan pastikan database `setup-java` tersedia. Koneksi mengikuti `src/main/resources/application.properties`. Saat startup, `schema.sql` membuat tabel `tasks` jika belum ada; Hibernate tidak mengubah skema karena `ddl-auto=none`.

Jalankan dari Git Bash Laragon:

```bash
./mvnw.cmd spring-boot:run
```

Port aplikasi saat ini adalah **8040**. Contoh request:

```bash
# Buat task (201 Created)
curl -i -X POST http://localhost:8040/tasks -H 'Content-Type: application/json' -d '{"title":"Belajar Spring Boot","description":"Membuat CRUD","completed":false}'

# Ambil semua task (200 OK)
curl -i http://localhost:8040/tasks

# Ambil satu task (200 OK)
curl -i http://localhost:8040/tasks/1

# Ganti isi task (200 OK)
curl -i -X PUT http://localhost:8040/tasks/1 -H 'Content-Type: application/json' -d '{"title":"Belajar Spring Boot","description":"CRUD selesai","completed":true}'

# Hapus task (204 No Content)
curl -i -X DELETE http://localhost:8040/tasks/1
```

Ganti `1` dengan ID dari respons POST. `title` wajib berisi teks, maksimal 200 karakter; `description` maksimal 2000 karakter. Untuk POST dan PUT, `description` yang tidak dikirim menjadi string kosong dan `completed` menjadi false. PUT mengganti seluruh isi task. ID yang tidak ditemukan menghasilkan 404; input tidak valid menghasilkan 400.

Test HTTP CRUD memakai H2 dalam mode PostgreSQL, bukan database Laragon. Jalankan `./mvnw.cmd verify` untuk menguji dan membuat JAR. Test ini tidak membuktikan koneksi atau kompatibilitas penuh dengan PostgreSQL Laragon.

Proyek awal Spring Boot 4.1.1 dengan Spring Web MVC dan Maven Wrapper.

## Menjalankan aplikasi

Java sudah terpasang. Cek versi:

```powershell
java --version
```

Jalankan dari folder proyek (Windows PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

Buka http://localhost:8040 untuk mendapatkan:

```json
{"message":"Hello, Spring Boot!"}
```

Tekan Ctrl+C untuk menghentikan aplikasi. Maven Wrapper mengunduh Maven dan dependency saat pertama dijalankan sehingga memerlukan internet.

## Test dan build

```powershell
.\mvnw.cmd clean verify
java -jar target/java-springboot-0.0.1-SNAPSHOT.jar
```

Di Linux/macOS, gunakan `./mvnw` sebagai pengganti `.\mvnw.cmd`. Di Git Bash Windows, gunakan `./mvnw.cmd`.

## Struktur

- `src/main/java/com/example/javaspringboot/JavaSpringbootApplication.java`: entry point.
- `src/main/java/com/example/javaspringboot/HelloController.java`: endpoint GET `/`.
- `src/main/resources/application.properties`: konfigurasi aplikasi.
- `src/test/java`: test aplikasi.

Target kompilasi adalah Java 17. Gunakan JDK 17 atau lebih baru untuk build.
