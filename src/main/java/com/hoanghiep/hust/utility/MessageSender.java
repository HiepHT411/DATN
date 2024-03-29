package com.hoanghiep.hust.utility;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class MessageSender {

    private static final String CHAT_ID = "1248834056";
    private static final String TOKEN = "6129046820:AAHHBvs3HkwdEo51K6JBTPZIe2YenZPVLqo";

    public static void sendAccountCreatedToMyTelegramRoom(String username, String email) throws IOException, InterruptedException {

        String message = "User " + username + " with email: " + email + " have just created account!!!";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", CHAT_ID)
                .queryParam("text", message);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + TOKEN))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
