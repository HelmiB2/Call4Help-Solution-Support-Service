package com.c4h.solutionsupport.pcInformation;

import java.util.List;

/**
 * Schnittstelle für geräteabhängige Systeminformationen.
 * Implementierungen z. B. für Desktop, Android, iOS.
 */
public interface GeraeteInfo {

    // ========== BASISINFORMATIONEN ==========
    String getDeviceType();       // z. B. Desktop, Android, iOS
    String getDeviceModel();      // z. B. "iPad Pro" oder "Dell XPS"
    String getSerialNumber();     // Seriennummer des Geräts

    // ========== SYSTEMINFORMATIONEN ==========
    String getOSVersion();        // Betriebssystem-Version
    String getJavaVersion();      // Java Runtime Version
    String getSystemArchitecture(); // 32-bit / 64-bit
    long getSystemUptime();       // Laufzeit seit Boot (Sekunden)
    String getBootTime();         // Zeitpunkt des letzten Systemstarts

    // ========== BENUTZERINFORMATIONEN ==========
    String getUsername();         // Aktueller Benutzername
    String getHostname();         // Rechnername
    String getDomain();           // Domäne oder Arbeitsgruppe
    String getUserHomeDirectory();// Home-Verzeichnis des Users
    String getCurrentWorkingDirectory(); // Arbeitsverzeichnis
    String getInstalledLanguages(); // Spracheinstellungen
    String getTimeZone();         // Zeitzone
    String getbetriebsNummer(); //Betriebsnummer

    // ========== NETZWERK ==========
    String getIpAddress();        // Lokale IP-Adresse
    String getMacAddress();       // MAC-Adresse
    String getWifiSSID();         // WLAN-SSID
    List<String> getAllIpAddresses();   // Alle IPs
    List<String> getNetworkAdapters();  // Alle Adapter
    String getDefaultGateway();   // Standard-Gateway
    String getDnsServers();       // DNS-Server
    String getTeamViewerID();	//TeamVier ID

    // ========== HARDWARE ==========
    String getCpuInfo();          // CPU-Name
    int getCpuCores();            // CPU-Kerne
    double getCpuLoad();          // CPU-Auslastung (%)
    long getTotalMemory();        // RAM gesamt (Bytes)
    long getFreeMemory();         // RAM frei (Bytes)
    String getGpuInfo();          // GPU/Grafikkarte
    long getDiskTotalSpace();     // Gesamtspeicher (Bytes)
    long getDiskFreeSpace();      // Freier Speicher (Bytes)
    String getMotherboardInfo();  // Mainboard
    String getBiosVersion();      // BIOS/Firmware
    String getScreenResolution(); // Bildschirmauflösung

    // ========== SICHERHEIT ==========
    boolean isFirewallEnabled();     // Firewall aktiv?
    boolean isAntivirusInstalled(); // Antivirus vorhanden?

    // ========== ZUSAMMENFASSUNG ==========
    String getSystemInfo();       // Alle Infos als Text
    String getHostnameFor3S();    //Ticketing

    // ========== FORMATTIERTE AUSGABE ==========
    default String toMarkdown() {
        return String.format("""
                ## Geräteinformationen

                - **Typ:** %s
                - **Modell:** %s
                - **Seriennummer:** %s
                - **OS-Version:** %s
                - **Java-Version:** %s
                - **Architektur:** %s
                - **Uptime:** %d Sekunden
                - **Boot-Zeit:** %s

                ### Benutzer
                - **Benutzername:** %s
                - **Hostname:** %s
                - **Domain:** %s
                - **Home-Verzeichnis:** %s
                - **Arbeitsverzeichnis:** %s
                - **Sprache:** %s
                - **Zeitzone:** %s
                - **BetriebsNummer:**%s

                ### Netzwerk
                - **IP-Adresse:** %s
                - **MAC-Adresse:** %s
                - **WLAN SSID:** %s
                - **Alle IPs:** %s
                - **Adapter:** %s
                - **Gateway:** %s
                - **DNS-Server:** %s
                - **TeamviewerID:** %s

                ### Hardware
                - **CPU:** %s
                - **Kerne:** %d
                - **CPU-Last:** %.2f %%
                - **RAM gesamt:** %d MB
                - **RAM frei:** %d MB
                - **GPU:** %s
                - **Disk gesamt:** %d MB
                - **Disk frei:** %d MB
                - **Mainboard:** %s
                - **BIOS:** %s
                - **Auflösung:** %s

                ### Sicherheit
                - **Firewall aktiv:** %b
                - **Antivirus installiert:** %b
                """,
                getDeviceType(),
                getDeviceModel(),
                getSerialNumber(),
                getOSVersion(),
                getJavaVersion(),
                getSystemArchitecture(),
                getSystemUptime(),
                getBootTime(),

                getUsername(),
                getHostname(),
                getDomain(),
                getUserHomeDirectory(),
                getCurrentWorkingDirectory(),
                getInstalledLanguages(),
                getTimeZone(),
                getbetriebsNummer(),

                getIpAddress(),
                getMacAddress(),
                getWifiSSID(),
                getAllIpAddresses(),
                getNetworkAdapters(),
                getDefaultGateway(),
                getDnsServers(),
                getTeamViewerID(),

                getCpuInfo(),
                getCpuCores(),
                getCpuLoad(),
                getTotalMemory() / (1024 * 1024),
                getFreeMemory() / (1024 * 1024),
                getGpuInfo(),
                getDiskTotalSpace() / (1024 * 1024),
                getDiskFreeSpace() / (1024 * 1024),
                getMotherboardInfo(),
                getBiosVersion(),
                getScreenResolution(),

                isFirewallEnabled(),
                isAntivirusInstalled()
        );
    }

    default String toJson() {
        return String.format("""
                {
                  "deviceType": "%s",
                  "deviceModel": "%s",
                  "serialNumber": "%s",
                  "osVersion": "%s",
                  "javaVersion": "%s",
                  "systemArchitecture": "%s",
                  "systemUptime": %d,
                  "bootTime": "%s",

                  "username": "%s",
                  "hostname": "%s",
                  "domain": "%s",
                  "homeDirectory": "%s",
                  "workingDirectory": "%s",
                  "languages": "%s",
                  "timeZone": "%s",
                  "Betriebsnummer: "%s",

                  "ipAddress": "%s",
                  "macAddress": "%s",
                  "wifiSSID": "%s",
                  "allIpAddresses": "%s",
                  "networkAdapters": "%s",
                  "defaultGateway": "%s",
                  "dnsServers": "%s",
                  "TeamViewerID": "%s",

                  "cpuInfo": "%s",
                  "cpuCores": %d,
                  "cpuLoad": %.2f,
                  "totalMemory": %d,
                  "freeMemory": %d,
                  "gpuInfo": "%s",
                  "diskTotalSpace": %d,
                  "diskFreeSpace": %d,
                  "motherboard": "%s",
                  "biosVersion": "%s",
                  "screenResolution": "%s",

                  "firewallEnabled": %b,
                  "antivirusInstalled": %b
                }
                """,
                escapeJson(getDeviceType()),
                escapeJson(getDeviceModel()),
                escapeJson(getSerialNumber()),
                escapeJson(getOSVersion()),
                escapeJson(getJavaVersion()),
                escapeJson(getSystemArchitecture()),
                getSystemUptime(),
                escapeJson(getBootTime()),

                escapeJson(getUsername()),
                escapeJson(getHostname()),
                escapeJson(getDomain()),
                escapeJson(getUserHomeDirectory()),
                escapeJson(getCurrentWorkingDirectory()),
                escapeJson(getInstalledLanguages()),
                escapeJson(getTimeZone()),
                escapeJson(getbetriebsNummer()),

                escapeJson(getIpAddress()),
                escapeJson(getMacAddress()),
                escapeJson(getWifiSSID()),
                escapeJson(String.valueOf(getAllIpAddresses())),
                escapeJson(String.valueOf(getNetworkAdapters())),
                escapeJson(getDefaultGateway()),
                escapeJson(getDnsServers()),
                escapeJson(getTeamViewerID()),

                escapeJson(getCpuInfo()),
                getCpuCores(),
                getCpuLoad(),
                getTotalMemory(),
                getFreeMemory(),
                escapeJson(getGpuInfo()),
                getDiskTotalSpace(),
                getDiskFreeSpace(),
                escapeJson(getMotherboardInfo()),
                escapeJson(getBiosVersion()),
                escapeJson(getScreenResolution()),

                isFirewallEnabled(),
                isAntivirusInstalled()
        );
    }

    // Kleine Hilfsmethode zum JSON-sicher machen
    private static String escapeJson(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
