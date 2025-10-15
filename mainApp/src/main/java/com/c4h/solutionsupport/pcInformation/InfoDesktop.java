package com.c4h.solutionsupport.pcInformation;

import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.List;

import org.json.JSONObject;

import com.c4h.solutionsupport.util.PlatformHelper;

public class InfoDesktop implements GeraeteInfo {

    // ========== BASIS ==========
	@Override
	public String getDeviceType() {
	    String os = System.getProperty("os.name").toLowerCase();
	    if (PlatformHelper.isAndroid()) {
	        return "Android";
	    } else if (os.contains("windows")) {
	        return "Windows-PC";
	    } else if (os.contains("linux")) {
	        return "Linux-PC";
	    } else if (os.contains("mac")) {
	        return "Mac";
	    }
	    return "Unbekanntes Betriebsystem";
	}
	//getHostnameforTickteting
	@Override
	public String getHostnameFor3S() {
	    try {
	        String hostname = InetAddress.getLocalHost().getHostName();
	        return hostname.length() >= 4 ? hostname.substring(0, 4) : hostname;
	    } catch (Exception e) {
	        return "Unbekannt";
	    }
	}
	
    @Override
    public String getDeviceModel() {
        return System.getProperty("os.name").contains("Windows") ? "Windows-PC" : "Linux-PC";
    }

    @SuppressWarnings("resource")
	@Override
    public String getSerialNumber() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "Nicht verfügbar";
        }

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "bios", "get", "serialnumber"});
            process.getOutputStream().close();

            Scanner sc = new Scanner(process.getInputStream());
            sc.next(); // Überspringt die Kopfzeile "SerialNumber"
            if (sc.hasNext()) {
                String serial = sc.next().trim();
                if (serial.isEmpty()) {
					return "Nicht verfügbar";
				} else {
					return serial;
				}
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Nicht verfügbar";
    }

    // ========== SYSTEM ==========
    @Override
    public String getOSVersion() {
        return System.getProperty("os.name") + " " + System.getProperty("os.version");
    }

    @Override
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    @Override
    public String getSystemArchitecture() {
        return System.getProperty("os.arch");
    }

    @Override
    public long getSystemUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
    }

    @Override
    public String getBootTime() {
        long uptime = getSystemUptime();
        long bootMillis = System.currentTimeMillis() - (uptime * 1000);
        return new Date(bootMillis).toString();
    }

    // ========== BENUTZER ==========
    @Override
    public String getUsername() {
        return System.getProperty("user.name");
    }

    @Override
    public String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unbekannter Rechner";
        }
    }
    
	@Override
	public String getbetriebsNummer() {
		 String value = System.getenv("Betriebsnummer");
	        if (value == null) {
	            return "Betriebsnummer ist nicht gesetzt";
	        }
	        return value;
	}

    @Override
    public String getDomain() {
        return System.getenv().getOrDefault("USERDOMAIN", "Workgroup");
    }

    @Override
    public String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

    @Override
    public String getCurrentWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    @Override
    public String getInstalledLanguages() {
        return Locale.getDefault().toString();
    }

    @Override
    public String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    // ========== NETZWERK ==========
    @Override
    public String getIpAddress() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (Exception e) {
            return "Unbekannt";
        }
    }

    @Override
    public List<String> getAllIpAddresses() {
        List<String> ips = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (!addr.isLoopbackAddress()) {
                        ips.add(addr.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            ips.add("Fehler beim Auslesen");
        }
        return ips;
    }

    @Override
    public List<String> getNetworkAdapters() {
        List<String> adapters = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                adapters.add(ni.getDisplayName());
            }
        } catch (Exception e) {
            adapters.add("Fehler beim Auslesen");
        }
        return adapters;
    }

    @Override
    public String getDefaultGateway() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                if (!ni.isUp() || ni.isLoopback()) continue;
                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        // Hier könnte man z.B. die erste IP des Subnetzes zurückgeben
                        return addr.getHostAddress(); // als Näherung
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Nicht verfügbar";
    }

    @Override
    public String getDnsServers() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "Nicht verfügbar";
        }

        List<String> dnsList = new ArrayList<>();
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "ipconfig", "/all");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (Scanner sc = new Scanner(process.getInputStream())) {
                boolean dnsSection = false;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();

                    if (line.toLowerCase().startsWith("dns-server")) {
                        dnsSection = true;
                        String[] parts = line.split(":");
                        if (parts.length > 1) dnsList.add(parts[1].trim());
                    } else if (dnsSection && line.startsWith(" ")) {
                        dnsList.add(line.trim());
                    } else {
                        dnsSection = false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Nicht verfügbar";
        }

        return dnsList.isEmpty() ? "Nicht verfügbar" : String.join(", ", dnsList);
    }


    @Override
    public String getMacAddress() {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (ni != null) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) sb.append(String.format("%02X:", b));
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            return "Unbekannt";
        }
        return "Unbekannt";
    }

    @Override
    public String getWifiSSID() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "Nicht verfügbar";
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "netsh wlan show interfaces");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (Scanner sc = new Scanner(process.getInputStream())) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.toLowerCase().startsWith("ssid") && !line.toLowerCase().startsWith("bssid")) {
                        String[] parts = line.split(":", 2);
                        if (parts.length > 1) {
                            String ssid = parts[1].trim();
                            return ssid.isEmpty() ? "Nicht verfügbar" : ssid;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Nicht verfügbar";
    }
    
	@SuppressWarnings("deprecation")
	@Override
	public String getTeamViewerID() {
		 Process process = null;
		 try {
			process = Runtime.getRuntime().exec("reg query \"HKLM\\SOFTWARE\\WOW6432Node\\TeamViewer\" /v ClientID");
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		        java.io.BufferedReader reader = new java.io.BufferedReader(
		            new java.io.InputStreamReader(process.getInputStream())
		        );
		        String line;
		        try {
					while ((line = reader.readLine()) != null) {
					    if (line.contains("ClientID")) {
					        String[] parts = line.trim().split("\\s+");
					        String idHex = parts[parts.length - 1];
					        long id = Long.parseLong(idHex.replace("0x", ""), 16);
					        return "ID"+id;
					    }
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "TeamViewer nicht Installiert";
		    }

    // ========== HARDWARE ==========
    @Override
    public String getCpuInfo() {
        return System.getenv().getOrDefault("PROCESSOR_IDENTIFIER", "Unbekannt");
    }

    @Override
    public int getCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public double getCpuLoad() {
        try {
            com.sun.management.OperatingSystemMXBean osBean =
                    (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            @SuppressWarnings("deprecation")
            double load = osBean.getSystemCpuLoad();
            if (load >= 0) {
                return load * 100; // Prozent
            }
        } catch (Exception e) {
            // Ignorieren
        }

        // Fallback
        double avg = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        return avg < 0 ? 0.0 : avg;
    }

    @Override
    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    @Override
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    @Override
    public String getGpuInfo() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "Nicht verfügbar";
        }

        List<String> gpus = new ArrayList<>();
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "wmic path win32_VideoController get name");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (Scanner sc = new Scanner(process.getInputStream())) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (!line.isEmpty() && !line.toLowerCase().contains("name")) {
                        gpus.add(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gpus.isEmpty() ? "Nicht verfügbar" : String.join(", ", gpus);
    }


    @Override
    public long getDiskTotalSpace() {
        long total = 0;
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {
                total += store.getTotalSpace();
            } catch (Exception ignored) {}
        }
        return total;
    }

    @Override
    public long getDiskFreeSpace() {
        long free = 0;
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {
                free += store.getUsableSpace();
            } catch (Exception ignored) {}
        }
        return free;
    }

    @Override
    public String getMotherboardInfo() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "Nicht verfügbar";
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "wmic baseboard get product,manufacturer,serialnumber");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            StringBuilder sb = new StringBuilder();
            try (Scanner sc = new Scanner(process.getInputStream())) {
                boolean firstLine = true;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (firstLine) { 
                        firstLine = false; // Kopfzeile überspringen
                        continue;
                    }
                    if (!line.isEmpty()) {
                        sb.append(line).append(" | ");
                    }
                }
            }
            String result = sb.toString().trim();
            if (result.endsWith("|")) result = result.substring(0, result.length() - 1).trim();
            return result.isEmpty() ? "Nicht verfügbar" : result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Nicht verfügbar";
        }
    }


    @Override
    public String getBiosVersion() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "Nicht verfügbar";
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "wmic bios get smbiosbiosversion");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (Scanner sc = new Scanner(process.getInputStream())) {
                boolean firstLine = true;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (firstLine) { 
                        firstLine = false; // Kopfzeile überspringen
                        continue;
                    }
                    if (!line.isEmpty()) {
                        return line;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Nicht verfügbar";
    }


    @Override
    public String getScreenResolution() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return size.width + "x" + size.height;
    }

    // ========== SICHERHEIT ==========
    @Override
    public boolean isFirewallEnabled() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return false; // Andere OS nicht implementiert
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "netsh advfirewall show allprofiles");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (Scanner sc = new Scanner(process.getInputStream())) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim().toLowerCase();
                    if (line.contains("state") && line.contains("on")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean isAntivirusInstalled() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return false; // Andere OS nicht implementiert
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "wmic /namespace:\\\\root\\SecurityCenter2 path AntiVirusProduct get displayName");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (Scanner sc = new Scanner(process.getInputStream())) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (!line.isEmpty() && !line.toLowerCase().contains("displayname")) {
                        return true; // Mindestens ein Antivirus gefunden
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // ========== ZUSAMMENFASSUNG ==========
    @Override
    public String getSystemInfo() {
        return String.format("OS: %s | CPU: %s (%d Kerne) | RAM: %d MB frei von %d MB | IP: %s",
                getOSVersion(),
                getCpuInfo(),
                getCpuCores(),
                getFreeMemory() / (1024 * 1024),
                getTotalMemory() / (1024 * 1024),
                getIpAddress());
    }
    
    @Override
    public String toJson() {
        JSONObject json = new JSONObject();

        // Basis
        json.put("deviceType", getDeviceType());
        json.put("deviceModel", getDeviceModel());
        json.put("serialNumber", getSerialNumber());
        json.put("osVersion", getOSVersion());
        json.put("javaVersion", getJavaVersion());

        // User / Host
        json.put("username", getUsername());
        json.put("hostname", getHostname());
        json.put("domain", getDomain());
        json.put("Betriebsnummer", getbetriebsNummer());

        // Netzwerk
        json.put("ipAddress", getIpAddress());
        json.put("allIpAddresses", getAllIpAddresses());
        json.put("macAddress", getMacAddress());
        json.put("networkAdapters", getNetworkAdapters());
        json.put("wifiSSID", getWifiSSID());
        json.put("defaultGateway", getDefaultGateway());
        json.put("dnsServers", getDnsServers());
        json.put("TeamViewerId", getTeamViewerID());

        // Hardware
        json.put("cpuInfo", getCpuInfo());
        json.put("cpuCores", getCpuCores());
        json.put("cpuLoad", getCpuLoad());
        json.put("gpuInfo", getGpuInfo());
        json.put("totalMemory", getTotalMemory());
        json.put("freeMemory", getFreeMemory());
        json.put("diskTotalSpace", getDiskTotalSpace());
        json.put("diskFreeSpace", getDiskFreeSpace());
        json.put("motherboardInfo", getMotherboardInfo());
        json.put("biosVersion", getBiosVersion());
        json.put("screenResolution", getScreenResolution());

        // System
        json.put("systemArchitecture", getSystemArchitecture());
        json.put("systemUptime", getSystemUptime());
        json.put("bootTime", getBootTime());
        json.put("timeZone", getTimeZone());
        json.put("installedLanguages", getInstalledLanguages());

        // Security
        json.put("firewallEnabled", isFirewallEnabled());
        json.put("antivirusInstalled", isAntivirusInstalled());

        // User directories
        json.put("userHomeDirectory", getUserHomeDirectory());
        json.put("currentWorkingDirectory", getCurrentWorkingDirectory());

        return json.toString(4); // prettified JSON (indent = 4)
    }
    
    
 // --- Hardware-Infos ---
    public JSONObject getHardwareJson(GeraeteInfo geraeteInfo) {
        JSONObject json = new JSONObject();
        json.put("cpuInfo", geraeteInfo.getCpuInfo());
        json.put("cpuCores", geraeteInfo.getCpuCores());
        json.put("cpuLoad", geraeteInfo.getCpuLoad());
        json.put("gpuInfo", geraeteInfo.getGpuInfo());
        json.put("totalMemory", geraeteInfo.getTotalMemory());
        json.put("freeMemory", geraeteInfo.getFreeMemory());
        json.put("diskTotalSpace", geraeteInfo.getDiskTotalSpace());
        json.put("diskFreeSpace", geraeteInfo.getDiskFreeSpace());
        json.put("motherboardInfo", geraeteInfo.getMotherboardInfo());
        json.put("biosVersion", geraeteInfo.getBiosVersion());
        json.put("screenResolution", geraeteInfo.getScreenResolution());
        return json;
    }

    // --- System-Infos ---
    public JSONObject getSystemJson(GeraeteInfo geraeteInfo) {
        JSONObject json = new JSONObject();
        json.put("deviceType", geraeteInfo.getDeviceType());
        json.put("deviceModel", geraeteInfo.getDeviceModel());
        json.put("serialNumber", geraeteInfo.getSerialNumber());
        json.put("osVersion", geraeteInfo.getOSVersion());
        json.put("javaVersion", geraeteInfo.getJavaVersion());
        json.put("username", geraeteInfo.getUsername());
        json.put("hostname", geraeteInfo.getHostname());
        json.put("Betriebsnummer", geraeteInfo.getbetriebsNummer());
        json.put("domain", geraeteInfo.getDomain());
        json.put("systemArchitecture", geraeteInfo.getSystemArchitecture());
        json.put("systemUptime", geraeteInfo.getSystemUptime());
        json.put("bootTime", geraeteInfo.getBootTime());
        json.put("timeZone", geraeteInfo.getTimeZone());
        json.put("installedLanguages", geraeteInfo.getInstalledLanguages());
        json.put("userHomeDirectory", geraeteInfo.getUserHomeDirectory());
        json.put("currentWorkingDirectory", geraeteInfo.getCurrentWorkingDirectory());
        return json;
    }

    // --- Netzwerk-Infos ---
    public JSONObject getNetworkJson(GeraeteInfo geraeteInfo) {
        JSONObject json = new JSONObject();
        json.put("ipAddress", geraeteInfo.getIpAddress());
        json.put("allIpAddresses", geraeteInfo.getAllIpAddresses());
        json.put("macAddress", geraeteInfo.getMacAddress());
        json.put("networkAdapters", geraeteInfo.getNetworkAdapters());
        json.put("wifiSSID", geraeteInfo.getWifiSSID());
        json.put("defaultGateway", geraeteInfo.getDefaultGateway());
        json.put("dnsServers", geraeteInfo.getDnsServers());
        json.put("firewallEnabled", geraeteInfo.isFirewallEnabled());
        json.put("antivirusInstalled", geraeteInfo.isAntivirusInstalled());
        json.put("TeamViewer", geraeteInfo.getTeamViewerID());
        return json;
    }



    // ========== MAIN TEST ==========
    public static void main(String[] args) {
        InfoDesktop info = new InfoDesktop();
        System.out.println(info.toMarkdown()); // Schöne Ausgabe
        System.out.println(info.toJson());     // JSON für API
    }

}
