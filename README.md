# Rise To Be Champion

Rise To Be Champion adalah game pertarungan 2D yang dikembangkan menggunakan framework LibGDX (Java) untuk sisi Frontend (Client) dan Spring Boot (Java) beserta PostgreSQL untuk sisi Backend (Server).

## 🎮 Mainkan Game

👉 **[Mainkan Rise To Be Champion di sini!](https://toriqfathoni.itch.io/rise-to-be-champion)**

## Link Hack.md (untuk melihat gambar yang tidak te reload)

[hackmd](https://hackmd.io/@hibBia1FSKamsSSqsStSAA/rkmCgtxgfl)

## Struktur Repositori

Repositori ini terdiri dari beberapa komponen utama:

- **Frontend (`/Game`)**: Berisi kode sumber untuk game yang dibangun dengan LibGDX. Mengatur semua logika *rendering*, *physics*, input kontrol pemain (Singleplayer & Local Multiplayer), antarmuka (*UI*), dan integrasi API ke server.
- **Backend (`/Backend`)**: Berisi kode sumber untuk server API yang dibangun dengan Spring Boot. Menangani otentikasi pemain (Register/Login), penyimpanan data *progress* game, dan *leaderboard* ke dalam database PostgreSQL.
- **Database Dump (`/databasedump`)**: Berisi *file* cadangan (*dump*) database `.sql` yang memuat struktur tabel dan data awal untuk memudahkan instalasi di lingkungan pengembangan baru.

## Cuplikan Layar (Screenshots)

### 1. Main Menu
![Main Menu](path/to/main-menu.png)

### 2. Character Select
![Character Select](path/to/character-select.png)

### 3. Battle Gameplay
![Battle Gameplay](path/to/battle-gameplay.png)

### 4. Upgrade Screen
![Upgrade Screen](path/to/upgrade-screen.png)

## Fitur Utama

- **Story Mode**: Petualangan menyelesaikan 4 tahapan (*stage*) dengan musuh yang berbeda. Data *progress* (HP, Damage, Stage, Kematian, Waktu) disimpan secara berkala ke database.
- **Local Multiplayer**: Bermain melawan teman dalam 1 keyboard (Player 1 vs Player 2).
- **Sistem Upgrade**: Setelah berhasil menyelesaikan sebuah *stage*, pemain disajikan opsi untuk *upgrade* atribut karakter (HP, Basic Damage, Skill Damage, dll).
- **Pause & Resume**: Kemampuan untuk jeda permainan saat berada di pertempuran.
- **Save & Load Progress**: *Progress* pemain dapat dilanjutkan kapan saja berkat sinkronisasi langsung dengan server Backend.

## Teknologi yang Digunakan

### Frontend
- **Bahasa**: Java
- **Framework**: LibGDX
- **Build Tool**: Gradle
- **Architecture**: MVC & Observer Pattern

### Backend
- **Bahasa**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **ORM**: Hibernate (Spring Data JPA)
- **Build Tool**: Maven / Gradle

## Cara Menjalankan Aplikasi

### 1. Persiapan Database
1. Pastikan Anda telah menginstal **PostgreSQL**.
2. Buat database baru bernama `risetobechampion`.
3. *Import* file `risetobechampion.sql` yang ada di dalam folder `databasedump` ke dalam database yang baru dibuat.
4. (Opsional) Sesuaikan *username* dan *password* database pada file konfigurasi Spring Boot (`application.properties` di folder Backend).

### 2. Menjalankan Backend
1. Buka *terminal* atau *command prompt*.
2. Arahkan ke *directory* `Backend`.
3. Jalankan perintah berikut:
   ```bash
   ./gradlew bootRun
   ```
4. Pastikan backend berjalan dan *listen* di *port* default (biasanya 8080).

### 3. Menjalankan Frontend (Game)
1. Buka tab *terminal* baru.
2. Arahkan ke *directory* `Game`.
3. Jalankan perintah berikut:
   ```bash
   ./gradlew lwjgl3:run
   ```
4. Game akan terbuka dalam mode *Desktop* dan Anda bisa mulai memainkannya.

## Desain Diagram

### 1. Class Diagram
![Class Diagram](path/to/class-diagram.png)

### 2. Sequence Diagram
![Sequence Diagram](path/to/sequence-diagram.png)

### 3. Entity Relationship Diagram (ERD)
![ERD](path/to/erd.png)

### 4. Flowchart
![Flowchart](path/to/flowchart.png)

## Design Patterns yang Digunakan

Proyek ini secara aktif mengimplementasikan prinsip-prinsip arsitektur perangkat lunak modern untuk menjaga kode tetap bersih, tersusun, dan mudah dikembangkan. Berikut adalah daftar *design pattern* yang ada di dalam *game* ini:

### 1. Singleton Pattern
- **Penggunaan:** `SessionManager.java` dan `AudioManager.java`
- **Tujuan:** Memastikan bahwa hanya ada satu *instance* global yang mengelola status sesi pemain (seperti *progress stage*, atribut *upgrade*, karakter pilihan) serta sistem suara (*backsound* & *sound effects*). Ini mempermudah akses state dari berbagai layar (*screens*) yang berbeda.

### 2. Factory Method Pattern
- **Penggunaan:** `CombatantFactory.java`
- **Tujuan:** Memusatkan logika penciptaan petarung (`Combatant`). Menyediakan metode instan seperti `createRyu()`, `createKael()`, atau `createMrVan()` yang menyembunyikan kerumitan pengaturan animasi, *hitbox*, dan statistik dasar sehingga pembuatan karakter baru di pertempuran menjadi sangat rapi.

### 3. Observer Pattern
- **Penggunaan:** `Combatant.java` (Subject) dan `CombatantObserver.java` (Observer) pada `BattleHUD`.
- **Tujuan:** Memisahkan *business logic* dari UI. Ketika *Health Points* (HP) atau Energi karakter berubah akibat serangan, karakter akan memanggil `notifyObservers()`. UI akan langsung bereaksi dan memperbarui animasi *bar* tanpa perlu mengecek status HP setiap *frame* (menghindari *polling*).

### 4. Command Pattern
- **Penggunaan:** `Command.java` interface beserta turunan aslinya (`BasicAttackCommand`, `SkillCommand`, `DefendCommand`, dll) yang dieksekusi lewat `PlayerInputController.java`.
- **Tujuan:** Mengkapsulasi setiap aksi/serangan menjadi satu kelas objek mandiri. Pola ini membuat pemetaan kontrol (baik menggunakan *Keyboard* maupun *Controller/Gamepad*) menjadi sangat fleksibel dan modular tanpa mencemari logika input dengan kode serangan *hard-coded*.

### 5. Concurrent Finite State Machine (State Pattern)
- **Penggunaan:** `MovementState.java` (`GROUNDED`, `AIRBORNE`) dan `ActionState.java` (`ATTACK`, `HIT`, `DEFEND`, dll) di dalam `Combatant.java`.
- **Tujuan:** Mengelola kompleksitas logika animasi dan fisika. *Game* memisahkan FSM pergerakan dan aksi secara paralel (*concurrent*), memungkinkan pemain untuk melakukan manuver kompleks seperti **Jump Attack** (menyerang sambil melayang) tanpa konflik *state*.

### 6. Strategy Pattern
- **Penggunaan:** Kelas-kelas *AI* seperti `AggressiveAi.java` yang di-*inject* ke musuh.
- **Tujuan:** Menjadikan algoritma pengambilan keputusan musuh (*Artificial Intelligence*) dapat dibongkar pasang. Karakter musuh bisa diubah perilakunya (contoh: agresif, bertahan) di tengah permainan hanya dengan mengganti kelas strategi AI-nya.

### 7. Repository Pattern / Data Access Object (DAO) (Sisi Backend)
- **Penggunaan:** Paket `com.champion.backend.repository` pada *Spring Boot* (contoh: `UserRepository`, `GameRunRepository`).
- **Tujuan:** Mengkapsulasi dan menyembunyikan kerumitan logika akses *database* PostgreSQL. Melalui *Spring Data JPA*, kontroler di *backend* hanya perlu memanggil metode tingkat tinggi seperti `save()` atau `findById()` tanpa memikirkan sintaks SQL mentah.

### 8. Data Transfer Object (DTO) Pattern (Sisi Backend)
- **Penggunaan:** Paket `com.champion.backend.dto` (contoh: `AuthRequest`, `CombatSetupResponse`, `SaveProgressRequest`).
- **Tujuan:** Menjadi struktur data penengah yang dikirim lewat internet (JSON) antara *Frontend* dan *Backend*. Pola ini berguna untuk melindungi dan menyembunyikan struktur entitas asli di *database*, serta membatasi data spesifik apa saja yang boleh dikirim atau diterima.

### 9. Dependency Injection (DI) / Inversion of Control (IoC) (Sisi Backend)
- **Penggunaan:** Mekanisme injeksi dependensi bawaan *Spring Boot* (melewati kelas *Controller* yang membutuhkan *Repository*).
- **Tujuan:** Mencapai *Loose Coupling* (ketergantungan longgar). Objek-objek tidak perlu saling membuat *instance* secara manual dengan kata kunci `new`. *Spring Framework* bertanggung jawab penuh menciptakan objek (*bean*) dan menyuntikkannya otomatis ke kelas yang membutuhkan.

### 10. Model-View-Controller (MVC) Architectural Pattern
- **Penggunaan:** Digunakan sebagai fondasi arsitektur utama pada keseluruhan aplikasi (*Frontend* dan *Backend*).
- **Tujuan:** Memisahkan aplikasi menjadi 3 bagian logis agar kode rapi dan tidak bercampur aduk:
  - **Di Frontend:** Data petarung dan fisika (Model: `Combatant`, `SessionManager`) dipisahkan sepenuhnya dari kode *rendering* layar (View: `BattleScreen`, `BattleHUD`), serta input dari pengguna ditangkap oleh lapisan terpisah (Controller: `PlayerInputController`).
  - **Di Backend:** Menggunakan *Spring Web MVC* di mana tabel *database* diwakilkan oleh Model (Entitas JPA), data dirender keluar dalam bentuk JSON (View), dan *routing* permintaan web diatur oleh *REST Controllers* (seperti `GameController` dan `AuthController`).

## Pembuat
Dikembangkan oleh **Toriq Fathoni**.
