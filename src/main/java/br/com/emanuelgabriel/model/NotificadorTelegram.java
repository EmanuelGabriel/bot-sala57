package br.com.emanuelgabriel.model;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import br.com.emanuelgabriel.config.ConfigAmbiente;

/**
 * @author Emanuel Gabriel Sousa
 * @version 1.0.0
 */
public class NotificadorTelegram extends TelegramLongPollingBot implements Notificador {

    private static final Logger logger = Logger.getLogger(NotificadorTelegram.class.getName());
    private static final ConfigAmbiente configEnv = new ConfigAmbiente();

    public NotificadorTelegram() {
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Método chamado quando uma atualização é recebida
        if (update.hasMessage() && update.getMessage().hasText()) {
            var chatId = update.getMessage().getChatId();
            logger.log(Level.INFO, "Mensagem recebida: {0}", chatId);
        }

    }

    @Override
    public String getBotUsername() {
        return configEnv.getTelegramBotUsername();
    }

    @Override
    public String getBotToken() {
        return configEnv.getTelegramBotToken();
    }

    @Override
    public void enviarMensagem(String titulo, String link, String dataPublicacao) {
        try {

            var publishedDateTime = OffsetDateTime.parse(dataPublicacao).toLocalDateTime();
            var dataFormatada = publishedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            var texto = String.format("Novo vídeo: **%s** (Publicado em: %s)\\n🔗 %s\\n\\n", titulo, dataFormatada, link);

            var sendMessage = new SendMessage();
            sendMessage.setChatId(configEnv.getTelegramChatId());
            sendMessage.setText(texto);
            sendMessage.enableMarkdown(true);
            execute(sendMessage);

            logger.log(Level.INFO, "Mensagem enviada com sucesso para o Telegram.");

        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Erro: {0}", e.getMessage());
        }
    }

    /**
     * Envia uma mensagem de notificação para um chat do Telegram.
     *
     * @param title O título da notificação, geralmente o título de um vídeo ou
     * conteúdo.
     * @param link O link associado à notificação, geralmente uma URL para o
     * conteúdo.
     * @param dataPublicacao A data de publicação do conteúdo no formato
     * ISO-8601 (ex.: "2023-10-01T10:15:30+01:00").
     *
     * Este método formata a data de publicação, constrói uma mensagem,
     * codifica-a para transmissão via URL e a envia para um chat específico do
     * Telegram usando a API do Bot do Telegram.
     *
     * A URL da API do Telegram, o token do bot e o ID do chat são recuperados
     * da configuração da aplicação. Logs são gerados para indicar o status do
     * processo de notificação.
     *
     * Em caso de erro durante o processo de notificação, uma mensagem de erro é
     * registrada nos logs.
     */
    public void enviarNotificacaoTelegram(String title, String link, String dataPublicacao) {
        var publishedDateTime = OffsetDateTime.parse(dataPublicacao).toLocalDateTime();
        var dataPublicacaoFormatada = publishedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        var message = String.format("Novo vídeo publicado: %s (Publicado em: %s) %n%s", title, dataPublicacaoFormatada, link);
        var encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        var url = String.format("%s%s/sendMessage?chat_id=%s&text=%s", configEnv.getTelegramUrlApi(), configEnv.getTelegramBotToken(), configEnv.getTelegramChatId(), encodedMessage);

        try {

            var httpClient = HttpClient.newHttpClient();

            httpClient.sendAsync(HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> logger.log(Level.INFO, "Notificação enviada com sucesso para o Telegram. Status: {0}", response.statusCode()));
        } catch (Exception e) {
            System.err.println(String.format("Erro ao enviar notificação: ", e.getMessage()));
            logger.log(Level.SEVERE, "Erro ao enviar notificação: {0}", e.getMessage());
        }
    }

}
