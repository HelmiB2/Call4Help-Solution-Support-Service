# Call4Help Solutions Support (C4H)

## 1. Einführung
Call4Help Solutions Support (C4H) ist eine plattformübergreifende Support-App für Windows.
Sie vereinfacht den Zugriff auf IT-Support, stellt Geräteinformationen dar, bietet einen Ticketing-Modus und ermöglicht Ticket- sowie Chat-Funktionen.

---

## 2. Projektstruktur

### Wichtige Dateien im Root-Verzeichnis
- `.classpath`, `.project`, `.settings/` → Eclipse Projektkonfiguration  
- `pom.xml` → Maven Build-Konfiguration  
- `runPom.xml` → Alternative Build-/Run-Konfiguration  
- `index.html` → Start-/Info-Seite für das Projekt  
- `doc/` → Projektdokumentation  

### `src/`
- **android/** → Android-spezifische Konfiguration und Ressourcen  
  - `AndroidManifest.xml`  
  - `res/mipmap-*` → App Icons für verschiedene DPI-Stufen  

- **main/**  
  - **java/com/c4h/solutionsupport/**  
    - `MainC4H.java` → Haupteinstiegspunkt der Anwendung  
    - **chat/** → Chat-Funktionalitäten (`ChatControler.java`)  
    - **kiosk/** → Kiosk-Modus (`KioskControler.java`)  
    - **mitarbeiter/** → Mitarbeiter-Modul (`MitarbeiterControler.java`)  
    - **pcInformation/** → System- und Netzwerk-Infos  
      - `GeraeteInfo.java`, `InfoDesktop.java`, `InfoAndroid.java`  
      - `PcInformationControler.java`  
    - **startView/** → Navigation & Startseite (`StartViewControler.java`)  
    - **ticket/** → Ticketmodul (`TicketControler.java`)  
    - **update/** → Updatefunktionalität (`UpdateChecker.java`, `UpdateControler.java`)  
    - **util/** → Hilfsklassen (Logging, Navigation, APIs, PlatformHelper etc.)  

  - **resources/**  
    - `*.fxml` → UI Layouts (Chat, Kiosk, Ticket, PcInfo usw.)  
    - `logback.xml` → Logging-Konfiguration  
    - **Control/** → UI-Icons  
    - **kiAntwort/** → vordefinierte Antworten (`antworten.csv`)  
    - **Logo/** → Projektlogos  
    - **META-INF/native-image/** → Konfiguration für GraalVM / Native Image  
    - **Style/** → CSS Stylesheets  

- **test/** → Testcode und Testressourcen  

### `target/`
- Kompilierte Artefakte (JAR-Dateien, Klassen, Ressourcen)  
- Beispiel:  
  - `C4HSolutionsSupport-2.0.0.jar`  
  - `C4HSolutionsSupport-2.0.0-jar-with-dependencies.jar`  

---

## 3. Funktionen
- Support-Chat & Ticket-Erstellung 
- Geräteinformationen (Modell, Seriennummer, IP, MAC, RAM usw.)  
- Update-Checker für automatische Updates  
- Logging für Desktop 

---

## 4. Installation & Ausführung
### Voraussetzungen
- Java 17 oder neuer  
- Maven 3.9+  
- Auf Android: APK-Build über Gluon/Maven  

### Build
```bash
mvn clean package
```

### Start Desktop
```bash
java -jar target/C4HSolutionsSupport-2.0.0-jar-with-dependencies.jar
```

### Start Android
- APK wird mit Maven/Gluon erstellt und auf Gerät installiert  

---

## 5. Architektur
- **Frontend:** JavaFX (FXML, CSS)  
- **Backend-Integration (optional):** REST-APIs für Tickets & Support  
- **Modularer Aufbau:**  
  - Start-View Navigation  
  - Kiosk-Modul( in Arbeit )
  - Chat- & Ticket-Modul  
  - Geräteinformation (plattformabhängig Desktop/Android)  
  - Update-Mechanismus  

---

## 6. Tests
- In Arbeit.
-- Unit Tests in `src/test/java`  
-- Automatisierte UI-Tests möglich mit Ranorex  

---

## 7. Updates
- Deployment via Baramundi (für Windows-Clients)  
- APK-Updates für Android über GitHub Release oder MDM  

---

## 8. FAQ
- **Kann die App ohne Internet genutzt werden?**  
  Ja, lokale Module funktionieren. Für Support-Chat und Tickets ist Internet notwendig.  

---

## 9. Roadmap
- Remote-Control Integration  
- Erweiterte Geräteanalyse  
