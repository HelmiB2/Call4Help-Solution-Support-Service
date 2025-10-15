package com.c4h.solutionsupport.pcInformation;

import com.c4h.solutionsupport.util.PlatformHelper;

public class GeraeteInfoFactory {

    public static GeraeteInfo create() {
        if (PlatformHelper.isAndroid()) {
            System.out.println("Ich bin in Android");
            try {
                return (GeraeteInfo) Class.forName("com.c4h.solutionsupport.pcInformation.InfoAndroid")
                        .getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.err.println("Android-Implementierung konnte nicht geladen werden, verwende Desktop: " + e);
            }
        }

        if (PlatformHelper.isIOS()) {
            System.out.println("Ich bin in iOS");
            try {
                return (GeraeteInfo) Class.forName("com.c4h.solutionsupport.pcInformation.InfoIOS")
                        .getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.err.println("iOS-Implementierung konnte nicht geladen werden, verwende Desktop: " + e);
            }
        }

        System.out.println("Ich bin in Desktop");
        return new InfoDesktop(); // Rückfall auf Desktop
    }
}
