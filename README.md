# Nyimbo Cia Agendi

An offline-first Java Android hymnal and a Spring Boot/PostgreSQL content backend. The Android app opens directly to its song list, includes the 299-song offline catalog and lyrics reader, local search, favorites, Light/Dark appearance choices, and the event features described below. The supplied Agendi artwork is used as the app icon and on app headers.

## Content and production status

The Android app includes the titles and lyrics from the MySongbooks Nyimbo cia Agendi pages at the user's direction. The source directory has 299 entries and one repeated title. The app presents this catalog in source order, numbered 1–299; those are app list positions, not verified printed hymnal numbers. Keys and keyboard scores are not provided on those pages and are not shown in the app. The Old/New numbered collections still contain clearly labeled demo rows in the data layer. The server seeds clearly marked demo events and an announcement.

The admin import for the Old/New numbered collections accepts only human-verified rows with a documented license name and reference. A version/number cross-reference is accepted only when the target hymn is verified in the other collection. Only verified hymns are sent to mobile clients. The source import remains separate from the verified Old/New collection model. The display-number mapping does not modify original source order or infer official numbers, keys, scores, or cross-references.

The first Play release is an offline hymnal: it includes the 299-song catalog, local search, favorites, and appearance settings. Its release variant omits network permissions and does not include events, online synchronization, maps, push notifications, or advertising. The debug build retains the event/backend tooling for development. Those network features should only be enabled in a later release after their content and services are ready.

## Repository layout

- `android/` — Android Studio Java/XML app, Room database, Retrofit API client, WorkManager sync, Firebase message receiver, event map and admin import-compatible content model.
- `backend/` — Spring Boot REST API, PostgreSQL/Flyway schema, role-protected admin API, browser admin dashboard, input validation, image upload, and append-only audit rows.
- `docker-compose.yml` — local PostgreSQL and API stack.
- `.env.example` — development configuration names and placeholders; replace passwords and keep `.env` out of version control.

## Start the backend locally

1. Copy `.env.example` to `.env` and set fresh local values for `DATABASE_PASSWORD` and `NYIMBO_ADMIN_PASSWORD`.
2. Run `docker compose up --build` from this directory.
3. Open `http://localhost:8080/admin`. The page asks for the admin username/password. The dashboard keeps credentials only in page memory and uses a CSRF token on writes. Use HTTPS behind a reverse proxy outside local development.
4. The public read API is `GET /api/v1/events`, `GET /api/v1/news`, and `GET /api/v1/hymns?version=0|1`.

The database starts with labeled demo events and a demo announcement. Event categories are restricted to Camp Meetings, Sabbath Worship, Crusades, Youth Events, Choir Events, Conferences, and Special Programs. Admin writes require the `ADMIN` role, requests are validated, and actions are written to `admin_audit`.

You can also run the backend without Docker when PostgreSQL is available:

```sh
cd backend
export DATABASE_URL=jdbc:postgresql://localhost:5432/nyimbo
export DATABASE_USERNAME=nyimbo
export DATABASE_PASSWORD='your-local-database-password'
export NYIMBO_ADMIN_USERNAME=admin
export NYIMBO_ADMIN_PASSWORD='your-long-local-admin-password'
mvn spring-boot:run
```

Flyway owns database schema changes. Hibernate runs in `validate` mode and does not mutate production schemas.

## Offline MySongbooks catalog

The Android asset `android/app/src/main/assets/mysongbooks_nyimbo_cia_agendi.json` contains the titles, lyrics, directory order, and source page links. `tools/import_mysongbooks.py` refreshes this file from the source catalog. The app displays entries in source order with list positions 1–299; these numbers do not claim to be the printed hymnal numbers. Keys and keyboard scores were not present in the linked pages. The duplicate title at directory positions 191 and 192 is retained to match the source list.

## Hymn import format

Upload a UTF-8 JSON array from the dashboard. The example below is only a schema sample and contains no hymn text:

```json
[
  {
    "version": 0,
    "number": 123,
    "title": "Verified Kikuyu title",
    "lyrics": "Paste complete, authorized verses here, preserving verse breaks.",
    "musicKey": null,
    "crossReferenceNumber": null,
    "notationUrl": null,
    "contentLicense": "Rights holder / license name",
    "licenseReference": "https://rights-holder.example/authorization",
    "verified": true
  }
]
```

Set `version` to `0` for Old or `1` for New. The sample values are placeholders, not authentic numbered-hymnal data. Leave the key, mapping, and notation URL empty unless verified. Notation references must use HTTPS. A mapped hymn must exist in the opposite version and be verified. The importer does not convert or infer keys, lyrics, numbers, or mappings.

## Open/build the Android app

Open the project root (`NyimboCiaAgendi`) in Android Studio. It uses native Java and XML, `minSdk 26`, `targetSdk 36`, AGP 8.13.2, and Gradle 8.13. For Play submissions from 31 August 2026, [Google Play requires new apps and updates to target API 36 or higher](https://support.google.com/googleplay/android-developer/answer/11926878); verify the policy again before release. AGP 8.13 officially supports API 36 and uses Gradle 8.13 as its default version ([Android Gradle Plugin 8.13 release notes](https://developer.android.com/build/releases/agp-8-13-0-release-notes)).

Build a debug APK in Android Studio, or with the generated wrapper:

```sh
./gradlew :android:app:assembleDebug
```

The APK is written to `android/app/build/outputs/apk/debug/app-debug.apk`. The current installable debug build is also copied to `../nyimbo-cia-agendi-debug.apk`. Debug builds default to the Android emulator host (`10.0.2.2:8080`) and allow cleartext traffic only in the debug manifest. For a physical device, set the API URL to a reachable host:

```sh
./gradlew :android:app:assembleDebug -PNYIMBO_API_BASE_URL=http://192.168.1.10:8080/api/v1/
```

For the Play upload, build the signed Android App Bundle with:

```sh
./gradlew :android:app:bundleRelease
```

The bundle is written to `android/app/build/outputs/bundle/release/app-release.aab`. The release signing key and `release-signing.properties` are local, ignored files. Keep them backed up securely; the same upload key is needed to sign later updates. Do not upload the debug APK to Play.

The Play Console also requires a public privacy-policy URL. The offline first-release policy is supplied at `../nyimbo-cia-agendi-privacy-policy.html`; host it on a public HTTPS page and use that URL both in Play Console and in the developer contact settings before submitting.

### Development-only events and notifications

- Events, maps, push delivery, and reminders are available only in the debug build, not the first Play release.
- To enable these in a later release, add a restricted Google Maps key, Firebase Android configuration, backend HTTPS hosting, and matching Firebase Admin credentials, then update the Play Data safety declaration and privacy policy.
- Set `FCM_ENABLED=true` and `FIREBASE_PROJECT_ID` on the backend and provide Google Application Default Credentials (for local Docker, mount the service-account file read-only and set `GOOGLE_APPLICATION_CREDENTIALS` inside the container; on Google Cloud, use workload identity). The admin dashboard can then send to the `church_updates` topic. The Android app subscribes only when the user enables notifications and a Firebase config is present; it unsubscribes when switched off. Do not commit service-account JSON.
- `POST_NOTIFICATIONS` is requested only when the user enables notifications. Notes, bookmarks, favorites, and reading history are stored locally; personal notes are never sent by sync.

## Tests

Backend unit tests cover authorized/verified hymn import requirements and duplicate-number rejection. PostgreSQL/Testcontainers integration tests cover Flyway startup, public content visibility, admin authentication, and draft privacy:

```sh
cd backend && mvn test
```

The Testcontainers integration tests need Docker running.

The Android module has Room DAO instrumented tests for local notes/favorites and separated hymn numbering, plus Android UI-test dependencies. Run instrumented tests from Android Studio with an emulator or connected device.

## Security and deployment checklist

- Set strong database and admin passwords from a secret manager, not the repository.
- Put the API behind HTTPS; use a reverse proxy with request limits and backups.
- Set upload storage to durable private storage and add retention/virus-scanning controls appropriate to deployment.
- Restrict the Maps key and configure Firebase for the exact Android application ID and signing certificate.
- Import only authorized hymnal data and keep its license reference with the content record.
- Review privacy disclosures for analytics, FCM, and third-party services before Play release.
