package br.com.emanuelgabriel;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import br.com.emanuelgabriel.utils.ConfigLoader;

/**
 * Unit test for simple App.
 */
public class AppTest extends TelegramLongPollingBot {

    private static final ConfigLoader configLoader = new ConfigLoader();

    @Override
    public void onUpdateReceived(Update update) {

        // Verifica se a atualização contém uma mensagem
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Obtém o chat-id
            long chatId = update.getMessage().getChatId();
            System.out.println("Chat-ID: " + chatId);

            // Opcional: Responda ao usuário com o chat-id
            String mensagem = "Seu Chat-ID é: " + chatId;
            try {
                enviarMensagem(chatId, mensagem);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return configLoader.getProperty("prop.telegram-name-bot");
    }

    @Override
    public String getBotToken() {
        return configLoader.getProperty("prop.telegram-bot-token");
    }

    private void enviarMensagem(long chatId, String mensagem) throws TelegramApiException {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(mensagem);
        execute(sendMessage);
    }

    public static void main(String[] args) {

        try {
            // Simula a execução do bot
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new AppTest());
            System.out.println("Bot iniciado!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
