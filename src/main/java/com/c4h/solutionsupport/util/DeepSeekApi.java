package com.c4h.solutionsupport.util;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;

public class DeepSeekApi {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String API_KEY = dotenv.get("DEEPSEEK_API_KEY");
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, String> responseCache = new HashMap<>();

    /**
     * Sendet einen Prompt an die DeepSeek API (kostenoptimierte Version)
     */
    public static String sendPromptCostOptimized(String prompt) throws Exception {
        try {
            checkApiStatus();
            
            HttpClient client = HttpClient.newHttpClient();
            
            Map<String, Object> message = Map.of(
                "model", "deepseek-chat-lite",
                "messages", List.of(
                    Map.of("role", "system", "content", "Antworte kurz und präzise als IT-Support."),
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.5,
                "max_tokens", 300,
                "stream", false
            );

            String json = mapper.writeValueAsString(message);
            logRequest(json);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);

            if (response.statusCode() != 200) {
                handleErrorResponse(response);
            }

            Map<?, ?> responseMap = mapper.readValue(response.body(), Map.class);
            return extractContent(responseMap);
            
        } catch (Exception e) {
            System.err.println("Fehler in sendPromptCostOptimized: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Standard-Implementierung mit Caching
     */
    public static String sendPrompt(String prompt) throws Exception {
        return sendPrompt(prompt, true);
    }

    public static String sendPrompt(String prompt, boolean useCache) throws Exception {
        if (useCache && responseCache.containsKey(prompt)) {
            return responseCache.get(prompt);
        }

        String response = sendPromptCostOptimized(prompt);
        
        if (useCache) {
            responseCache.put(prompt, response);
        }
        
        return response;
    }

    /**
     * Erstellt eine Zusammenfassung für Support-Tickets
     */
    public static String getTicketSummary(String userInput) {
        try {
            String prompt = "Erstelle eine prägnante Zusammenfassung für ein Support-Ticket:\n" + userInput;
            return sendPrompt(prompt, false);
        } catch (Exception e) {
            System.err.println("Fehler bei Ticket-Zusammenfassung: " + e.getMessage());
            return implementLocalFallback(userInput);
        }
    }

    /**
     * Generiert eine Chat-Antwort für Support-Anfragen
     */
    public static String getChatReply(String userInput, String chatHistory) {
        try {
            String prompt = "Chat-Verlauf:\n" + chatHistory + "\n\nNeue Anfrage:\n" + userInput;
            return sendPrompt(prompt, false);
        } catch (Exception e) {
            System.err.println("Fehler bei Chat-Antwort: " + e.getMessage());
            return implementLocalFallback(userInput);
        }
    }

    /**
     * Prüft den API-Status und Guthaben
     */
    public static void checkApiStatus() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.deepseek.com/v1/dashboard/billing/credit_balance"))
            .header("Authorization", "Bearer " + API_KEY)
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            Map<?, ?> balanceMap = mapper.readValue(response.body(), Map.class);
            System.out.println("Verbleibendes Guthaben: $" + balanceMap.get("available_credit"));
        } else {
            throw new RuntimeException("Guthabenabfrage fehlgeschlagen: " + response.body());
        }
    }

    /**
     * Fallback-Implementierung bei API-Problemen
     */
    public static String sendPromptWithFallback(String prompt) {
        try {
            try {
				return sendPrompt(prompt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Insufficient Balance")) {
                return implementLocalFallback(prompt);
            }
            throw e;
        }
		return prompt;
    }

    private static String implementLocalFallback(String prompt) {
        System.err.println("API-Guthaben erschöpft - verwende lokale Fallback-Lösung");
        
        Map<String, String> commonSolutions = Map.of(
            "Drucker", "1. Strom prüfen 2. Kabelverbindung checken 3. Neustart versuchen",
            "Netzwerk", "1. Router neustarten 2. Kabelverbindung prüfen 3. Flugmodus deaktivieren",
            "Bluescreen", "1. Fehlercode notieren 2. Neustarten 3. Treiber aktualisieren"
        );
        
        for (Map.Entry<String, String> entry : commonSolutions.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                return "Standardlösung für " + entry.getKey() + " Probleme:\n" + entry.getValue();
            }
        }
        
        return "Entschuldigung, unsere KI ist aktuell nicht verfügbar. Bitte kontaktieren Sie info@3s-hamburg.de";
    }

    private static String extractContent(Map<?, ?> responseMap) {
        List<?> choices = (List<?>) responseMap.get("choices");
        Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
        return (String) message.get("content");
    }

    private static void handleErrorResponse(HttpResponse<String> response) throws Exception {
        Map<?, ?> errorMap = mapper.readValue(response.body(), Map.class);
        String errorMsg = "API-Fehler: " + errorMap.get("error");
        
        if (errorMsg.contains("Insufficient Balance")) {
            errorMsg += "\nBitte laden Sie Ihr API-Guthaben auf.";
        }
        
        throw new RuntimeException(errorMsg);
    }

    private static void logRequest(String json) {
        System.out.println("=== API Request ===");
        System.out.println("Endpoint: " + API_URL);
        System.out.println("Request Body Size: " + json.length() + " chars");
        System.out.println("Estimated Tokens: ~" + (json.length() / 4));
    }

    private static void logResponse(HttpResponse<String> response) {
        System.out.println("=== API Response ===");
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Size: " + response.body().length() + " chars");
    }

    public static void main(String[] args) {
        try {
            // Beispielaufrufe
            String antwort = sendPromptWithFallback("Wie behebe ich Netzwerkprobleme?");
            System.out.println("Antwort:\n" + antwort);

            String zusammenfassung = getTicketSummary("Ich kann mich nicht mit dem WLAN verbinden. Fehlermeldung: 'IP-Konflikt'");
            System.out.println("Zusammenfassung:\n" + zusammenfassung);

            String chatAntwort = getChatReply("Das Problem besteht weiter", 
                "Benutzer: Mein Drucker funktioniert nicht\nAssistent: Haben Sie den Drucker neu gestartet?");
            System.out.println("Chat-Antwort:\n" + chatAntwort);
            
            // Guthaben prüfen
            checkApiStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}