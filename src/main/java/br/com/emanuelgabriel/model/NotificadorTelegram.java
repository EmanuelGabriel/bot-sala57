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

    public void enviarNotificacaoTelegram(String title, String link, String dataPublicacao) {
        var publishedDateTime = OffsetDateTime.parse(dataPublicacao).toLocalDateTime();
        var dataPublicacaoFormatada = publishedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        var message = String.format("Novo vídeo publicado: %s (Publicado em: %s) %n%s", title, dataPublicacaoFormatada, link);
        var encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        var url = String.format("%s%s/sendMessage?chat_id=%s&text=%s", configEnv.getTelegramUrlApi(), configEnv.getTelegramBotToken(), configEnv.getTelegramChatId(), encodedMessage);

        logger.log(Level.INFO, "Enviando notificação para o Telegram: {0}", url);

        try {

            var httpClient = HttpClient.newHttpClient();

            httpClient.sendAsync(HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> System.out.println("Notificação enviada: " + response.body()));
        } catch (Exception e) {
            System.err.println(String.format("Erro ao enviar notificação: ", e.getMessage()));
            logger.log(Level.SEVERE, "Erro ao enviar notificação: {0}", e.getMessage());
        }
    }

}
