package br.com.emanuelgabriel.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.emanuelgabriel.config.ConfigAmbiente;

public class NotificadorDiscord implements Notificador {

    private static final Logger logger = Logger.getLogger(NotificadorDiscord.class.getName());
    private static final ConfigAmbiente config = new ConfigAmbiente();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String CONTENT_TYPE = "application/json";

    @Override
    public void enviarMensagem(String titulo, String link, String dataPublicacao) {

        var publishedDateTime = OffsetDateTime.parse(dataPublicacao).toLocalDateTime();
        var dataFormatada = publishedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        var jsonPayload = String.format("{\"content\": \"Novo vídeo publicado: **%s** (Publicado em: %s)\\n🔗 %s\\n\\n\"}", titulo, dataFormatada, link);

        try {

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getWebhookDiscordUrl()))
                    .header("Content-Type", CONTENT_TYPE)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                logger.info("Mensagem enviada com sucesso para o Discord.");
            } else {
                logger.warning(String.format("Falha ao enviar mensagem para o Discord: ", response.statusCode(), response.body()));
            }

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Erro ao enviar notificação para Discord: {0}", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

}
