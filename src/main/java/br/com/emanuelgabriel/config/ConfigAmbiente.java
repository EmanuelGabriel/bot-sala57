/**
 * Classe responsável por carregar e gerenciar as configurações de ambiente do sistema.
 *
 * <p>
 * Esta classe detecta o ambiente (desenvolvimento ou produção) com base na
 * variável de ambiente {@code ENV_BOT_NOTIFICADOR} e carrega o arquivo de
 * propriedades correspondente. As propriedades são utilizadas para configurar
 * diversas informações necessárias para o funcionamento do sistema, como URLs,
 * tokens e identificadores.
 * </p>
 *
 * <p>
 * Propriedades esperadas nos arquivos de configuração:
 * <ul>
 * <li>{@code prop.channel-id} - Identificador do canal.</li>
 * <li>{@code prop.feed-url} - URL do feed, concatenada com o
 * {@code channel-id}.</li>
 * <li>{@code prop.tempo-thread} - Tempo de execução da thread em
 * milissegundos.</li>
 * <li>{@code prop.webhook-discord-url} - URL do webhook do Discord.</li>
 * <li>{@code prop.telegram-bot-token} - Token do bot do Telegram.</li>
 * <li>{@code prop.telegram-bot-username} - Nome de usuário do bot do
 * Telegram.</li>
 * <li>{@code prop.telegram-url-api} - URL da API do Telegram.</li>
 * <li>{@code prop.telegram-chat-id} - Identificador do chat do Telegram.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Em caso de falha ao carregar as configurações, uma exceção
 * {@link RuntimeException} será lançada.
 * </p>
 *
 * <p>
 * Exemplo de uso:
 * <pre>
 * {@code
 * ConfigAmbiente config = new ConfigAmbiente();
 * String channelId = config.getChannelId();
 * String webhookUrl = config.getWebhookDiscordUrl();
 * }
 * </pre>
 * </p>
 *
 * @author Emanuel Gabriel
 * @version 1.0
 */
package br.com.emanuelgabriel.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.emanuelgabriel.utils.ConfigLoader;

public class ConfigAmbiente {

    private static final Logger logger = Logger.getLogger(ConfigAmbiente.class.getName());
    private final ConfigLoader configLoader = new ConfigLoader();

    private String channelId;
    private String feedUrl;
    private Long tempoThread;
    private String webhookDiscordUrl;
    private String telegramBotToken;
    private String telegramBotUsername;
    private String telegramUrlApi;
    private String telegramChatId;

    public ConfigAmbiente() {
        logger.info("Iniciando configuração de ambiente...");

        try {
            // Detectar o ambiente (DEV ou PROD)
            final String ENV = System.getenv("ENV_BOT_NOTIFICADOR");
            String propertiesFile;

            if (ENV != null && ENV.equalsIgnoreCase("PROD")) {
                logger.info("Ambiente de produção detectado.");
                propertiesFile = "config-prod.properties";
            } else {
                logger.info("Ambiente de desenvolvimento detectado.");
                propertiesFile = "config-dev.properties";
            }

            // Carregar o arquivo .properties correspondente
            configLoader.loadPropertiesFromFile(propertiesFile);

            if (propertiesFile.equals("config-prod.properties")) {
                logger.info("Carregando configurações de produção...");
                this.channelId = System.getenv("ENV_YOUTUBE_CHANNEL_ID");
                this.feedUrl = System.getenv("ENV_YOUTUBE_FEED_URL").concat("=").concat(this.channelId);
                logger.log(Level.INFO, "URL do feed: {0}", this.feedUrl);
                this.tempoThread = Long.valueOf(System.getenv("ENV_YOUTUBE_TEMPO_THREAD"));
                this.webhookDiscordUrl = System.getenv("ENV_DISCORD_WEBHOOK_URL");
                this.telegramBotToken = System.getenv("ENV_TELEGRAM_BOT_TOKEN");
                this.telegramBotUsername = System.getenv("ENV_TELEGRAM_BOT_USERNAME");
                this.telegramUrlApi = System.getenv("ENV_TELEGRAM_URL_API");
                this.telegramChatId = System.getenv("ENV_TELEGRAM_CHAT_ID");
            } else {
                logger.info("Carregando configurações de desenvolvimento...");
                this.channelId = configLoader.getProperty("prop.channel-id");
                this.feedUrl = configLoader.getProperty("prop.feed-url").concat("=").concat(this.channelId);
                logger.log(Level.INFO, "URL do feed: {0}", this.feedUrl);
                this.tempoThread = Long.valueOf(configLoader.getProperty("prop.tempo-thread"));
                this.webhookDiscordUrl = configLoader.getProperty("prop.webhook-discord-url");
                this.telegramBotToken = configLoader.getProperty("prop.telegram-bot-token");
                this.telegramBotUsername = configLoader.getProperty("prop.telegram-name-bot");
                this.telegramUrlApi = configLoader.getProperty("prop.telegram-url-api");
                this.telegramChatId = configLoader.getProperty("prop.telegram-chat-id");
            }

        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Erro ao carregar as configurações de ambiente: {0}", e.getMessage());
            throw new RuntimeException("Falha ao inicializar as configurações de ambiente.", e);
        }
    }

    public String getChannelId() {
        return channelId;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public Long getTempoThread() {
        return tempoThread;
    }

    public String getWebhookDiscordUrl() {
        return webhookDiscordUrl;
    }

    public void setWebhookDiscordUrl(String webhookDiscordUrl) {
        this.webhookDiscordUrl = webhookDiscordUrl;
    }

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }

    public String getTelegramBotUsername() {
        return telegramBotUsername;
    }

    public void setTelegramBotUsername(String telegramBotUsername) {
        this.telegramBotUsername = telegramBotUsername;
    }

    public String getTelegramUrlApi() {
        return telegramUrlApi;
    }

    public void setTelegramUrlApi(String telegramUrlApi) {
        this.telegramUrlApi = telegramUrlApi;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

}
