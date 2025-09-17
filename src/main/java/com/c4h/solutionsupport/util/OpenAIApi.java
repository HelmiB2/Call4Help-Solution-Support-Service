package com.c4h.solutionsupport.util;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

public class OpenAIApi {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static String API_KEY;

    static {
        try {
            if (PlatformHelper.isAndroid()) {
                // Unter Android – API_KEY aus Android-Kontext holen
                // In einem echten Android-Projekt musst du ihn z. B. über BuildConfig oder Resources setzen
                API_KEY = getAndroidApiKey();  // Dummy-Funktion, unten implementiert
            } else {
                // Unter Desktop – API_KEY aus .env lesen
                Dotenv dotenv = Dotenv.load();
                API_KEY = dotenv.get("OPENAI_API_KEY");
            }
        } catch (Exception e) {
            e.printStackTrace();
            API_KEY = null;
        }
    }

    public static String sendPrompt(String prompt) throws Exception {

        if (API_KEY == null || API_KEY.isEmpty()) {
            return "⚠️ Kein API-Key gefunden – bitte .env-Datei oder Android-Konfiguration prüfen.";
        }

        HttpClient client = HttpClient.newHttpClient();

        Map<String, Object> message = Map.of(
            "model", "gpt-3.5-turbo",
            "messages", List.of(
                Map.of("role", "system", "content", "Du bist ein IT-Helpdesk-Assistent."),
                Map.of("role", "user", "content", prompt)
            )
        );

        String json = new ObjectMapper().writeValueAsString(message);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Map<?, ?> map = new ObjectMapper().readValue(response.body(), Map.class);

        if (map.containsKey("error")) {
            Map<?, ?> error = (Map<?, ?>) map.get("error");
            return "❌ OpenAI API-Fehler: " + error.get("message");
        }

        List<?> choices = (List<?>) map.get("choices");
        if (choices == null || choices.isEmpty()) {
            return "⚠️ Keine Antwort von der KI erhalten.";
        }

        Map<?, ?> messageObj = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
        return (String) messageObj.get("content");
    }

    // Platzhalter: Ticket-Zusammenfassung durch KI (optional)
    public static String getTicketSummary(String userInput) {
        return null;
    }

    // Platzhalter: Chat-Antwort generieren (optional)
    public static String getChatReply(String userInput) {
        return null;
    }

    // Beispielhafte Hauptmethode für Desktop-Tests
    public static void main(String[] args) {
        try {
            String reply = OpenAIApi.sendPrompt("Wie starte ich meinen Drucker neu?");
            System.out.println("Antwort von der KI: " + reply);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dummy-Funktion für Android – hier muss du `API_KEY` aus deinem Android-Kontext laden.
     * Zum Beispiel über `BuildConfig.OPENAI_API_KEY` oder `context.getString(R.string.openai_api_key)`
     */
	private static String getAndroidApiKey() {
		// TODO: API Key aus Android-Kontext holen
		return System.getenv("OPENAI_API_KEY");  // oder leer lassen für Tests
	}
}
