package com.c4h.solutionsupport.pcInformation;

import java.util.List;

public class InfoAndroid implements GeraeteInfo {

	@Override
	public String getDeviceType() {
		// TODO Auto-generated method stub
		return "Pixel 7";
	}

	@Override
	public String getDeviceModel() {
		// TODO Auto-generated method stub
		return "Dell";
	}

	@Override
	public String getSerialNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOSVersion() {
		// TODO Auto-generated method stub
		return "OS-Version";
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIpAddress() {
		// TODO Auto-generated method stub
		return "192.168.1.1";
	}

	@Override
	public String getMacAddress() {
		// TODO Auto-generated method stub
		return "192.178.168.1";
	}

	@Override
	public String getWifiSSID() {
		// TODO Auto-generated method stub
		return "CA_1920";
	}

	@Override
	public String getCpuInfo() {
		// TODO Auto-generated method stub
		return "CPU-Info";
	}

	@Override
	public long getTotalMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFreeMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSystemInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	//Plattfomabhängig nicht vererbt.
	private static String escapeJson(String value) {
	    return value == null ? "" : value.replace("\"", "\\\"");
	}

	@Override
	public String toJson() {
	    return String.format("""
	            {
	              "deviceType": "%s",
	              "deviceModel": "%s",
	              "serialNumber": "%s",
	              "ipAddress": "%s",
	              "macAddress": "%s",
	              "wifiSSID": "%s",
	              "cpuInfo": "%s",
	              "OS-Version": "%s"
	            }
	            """,
	            escapeJson(getDeviceType()),
	            escapeJson(getDeviceModel()),
	            escapeJson(getSerialNumber()),
	            escapeJson(getIpAddress()),
	            escapeJson(getMacAddress()),
	            escapeJson(getWifiSSID()),
	            escapeJson(getCpuInfo()), 
	            escapeJson(getOSVersion())
	    );
	    }
	 public static void main(String[] args) {
	        GeraeteInfo androidInfo = new InfoAndroid();
	        
	        System.out.println("Android Device Info:");
	        System.out.println("Device Model: " + androidInfo.getDeviceModel());
	        System.out.println("Wifi Version: " + androidInfo.getWifiSSID());
	        
	        
	        System.out.println(androidInfo.toJson());
	        // Weitere Felder testen
	    }

	 @Override
	 public String getJavaVersion() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getSystemArchitecture() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public long getSystemUptime() {
		// TODO Auto-generated method stub
		return 0;
	 }

	 @Override
	 public String getBootTime() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getUserHomeDirectory() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getCurrentWorkingDirectory() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getInstalledLanguages() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getTimeZone() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public List<String> getAllIpAddresses() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public List<String> getNetworkAdapters() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getDefaultGateway() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getDnsServers() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public int getCpuCores() {
		// TODO Auto-generated method stub
		return 0;
	 }

	 @Override
	 public double getCpuLoad() {
		// TODO Auto-generated method stub
		return 0;
	 }

	 @Override
	 public String getGpuInfo() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public long getDiskTotalSpace() {
		// TODO Auto-generated method stub
		return 0;
	 }

	 @Override
	 public long getDiskFreeSpace() {
		// TODO Auto-generated method stub
		return 0;
	 }

	 @Override
	 public String getMotherboardInfo() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getBiosVersion() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getScreenResolution() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public boolean isFirewallEnabled() {
		// TODO Auto-generated method stub
		return false;
	 }

	 @Override
	 public boolean isAntivirusInstalled() {
		// TODO Auto-generated method stub
		return false;
	 }

	 @Override
	 public String getHostnameFor3S() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getTeamViewerID() {
		// TODO Auto-generated method stub
		return null;
	 }

	 @Override
	 public String getbetriebsNummer() {
		// TODO Auto-generated method stub
		return null;
	 }

}
