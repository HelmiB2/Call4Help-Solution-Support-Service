package com.c4h.solutionsupport.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AntwortManager {

    private static final Map<String, String> antworten = new HashMap<>();

    public static void ladeAntworten() {
    	 InputStream in = AntwortManager.class.getResourceAsStream("/kiAntwort/antworten.csv");
    	 if (in == null) {
             System.err.println("Ressource antworten.csv nicht gefunden!");
             return;
         }
    	 
    	 
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String zeile;
            while ((zeile = reader.readLine()) != null) {
                if (zeile.trim().isEmpty() || zeile.startsWith("#")) continue;
                String[] teile = zeile.split(",", 2);
                if (teile.length == 2) {
                    String key = teile[0].trim().toLowerCase();
                    String antwort = teile[1].trim();
                    antworten.put(key, antwort);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String findeAntwort(String eingabe) {
        String lower = eingabe.toLowerCase();
        for (String key : antworten.keySet()) {
            if (lower.contains(key)) {
                return antworten.get(key);
            }
        }
        return null;
    }
    
    
    public static void main(String[] args) {
        ladeAntworten();
        System.out.println("Antwort für 'hilfe': " + findeAntwort("hallo"));
    }
}
