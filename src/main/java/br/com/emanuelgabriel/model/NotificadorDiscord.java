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

    /**
     * Envia uma mensagem de notificação para um webhook do Discord.
     *
     * @param titulo O título do vídeo ou mensagem a ser notificada.
     * @param link O link associado ao vídeo ou mensagem.
     * @param dataPublicacao A data de publicação no formato ISO-8601 (ex.:
     * "2023-10-01T10:15:30+01:00").
     *
     * O método formata a data de publicação para o formato "dd/MM/yyyy" e
     * constrói um payload JSON contendo o título, a data formatada e o link. Em
     * seguida, envia uma requisição HTTP POST para o webhook do Discord
     * configurado.
     *
     * Em caso de sucesso, um log informativo é gerado. Caso contrário, um log
     * de erro é registrado com o código de status e a resposta do servidor.
     *
     * Exceções de entrada/saída (IOException) e interrupção de thread
     * (InterruptedException) são tratadas, e um log de erro é gerado em caso de
     * falha.
     */
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
                logger.log(Level.INFO, "Notificação enviada com sucesso para o Discord. Status: {0}", response.statusCode());
            } else {
                logger.log(Level.SEVERE, String.format("Falha ao enviar mensagem para o Discord: %s - %s", response.statusCode(), response.body()));
            }

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Erro ao enviar notificação para Discord: {0}", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

}
