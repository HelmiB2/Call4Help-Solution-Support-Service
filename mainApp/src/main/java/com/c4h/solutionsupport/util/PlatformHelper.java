package com.c4h.solutionsupport.util;

public class PlatformHelper {

    public static boolean isAndroid() {
        String buildUser = System.getProperty("os.version");
        System.out.println("OS:= "+buildUser+".");
        return buildUser != null && buildUser.toLowerCase().contains("android");
    }

	public static boolean isIOS() {
		 try {
	            // Bei Gluon iOS Apps könnte das z. B. eine iOS-spezifische Klasse sein:
	            Class.forName("com.gluonhq.ios.iosPlatform"); // Beispiel
	            return true;
	        } catch (ClassNotFoundException e) {
	            return false;
	        }
	}
	public static String geStateOs() {
		if (isAndroid()){
			return "android";
		}else
			return "ios";
	}
}