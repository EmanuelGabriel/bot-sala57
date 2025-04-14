package br.com.emanuelgabriel;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;

import br.com.emanuelgabriel.config.ConfigAmbiente;
import br.com.emanuelgabriel.model.NotificadorDiscord;
import br.com.emanuelgabriel.model.NotificadorTelegram;
import br.com.emanuelgabriel.utils.FileStorageUtil;

/**
 * Emanuel Gabriel Sousa
 *
 * @version 1.0
 *
 */
public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final ConfigAmbiente config = new ConfigAmbiente();
    private static final Set<String> notificaVideos = FileStorageUtil.carregarIds();

    public static void main(String[] args) {

        var scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            logger.info("Verificando novos vídeos...");
            verificarNovosVideosDaSala57();
            logger.info("Verificação concluída. Aguardando o próximo ciclo...");
        }, 0, config.getTempoThread(), TimeUnit.MILLISECONDS);

    }

    private static void verificarNovosVideosDaSala57() {
        try {
            // Conectar ao feed RSS e obter o documento
            var doc = Jsoup.connect(config.getFeedUrl()).get();

            var entry = doc.selectFirst("entry");
            if (entry == null) {
                logger.info("Nenhum vídeo encontrado no feed RSS.");
                return;
            }

            // Extrair informações do vídeo
            String videoId = entry.selectFirst("id").text();
            String title = entry.selectFirst("title").text();
            String link = entry.selectFirst("link").attr("href");
            String publishedDate = entry.selectFirst("published").text();

            var publishedDateTime = OffsetDateTime.parse(publishedDate).toLocalDateTime();
            var dataPublicacaoFormatada = publishedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if (!notificaVideos.contains(videoId)) {
                logger.info(String.format("Novo vídeo encontrado: %s (Publicado em: %s)", title, dataPublicacaoFormatada));

                enviarNotificacoes(title, link, publishedDate);

                notificaVideos.add(videoId);
                FileStorageUtil.salvarIds(notificaVideos);
            } else {
                logger.info(String.format("Nenhum novo vídeo encontrado na data %s.", dataPublicacaoFormatada));
            }

        } catch (IOException e) {
            System.err.println(String.format("Erro ao buscar feed RSS: %s", e.getMessage()));
            logger.log(Level.INFO, "Erro ao buscar feed RSS: {0}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static void enviarNotificacoes(String title, String link, String publishedDate) {
        var notificadorTelegram = new NotificadorTelegram();
        var notificadorDiscord = new NotificadorDiscord();

        notificadorTelegram.enviarNotificacaoTelegram(title, link, publishedDate);
        notificadorDiscord.enviarMensagem(title, link, publishedDate);
    }

}
