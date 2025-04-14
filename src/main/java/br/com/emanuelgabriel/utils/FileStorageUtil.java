/**
 * Utility class for handling file storage operations related to a JSON file.
 * This class provides methods to load and save a set of IDs to a JSON file.
 *
 * <p>
 * The JSON file is used to persist a set of unique IDs, and the utility ensures
 * that the file is read and written in a structured manner using the Gson
 * library.
 * </p>
 *
 * <p>
 * If the file does not exist or cannot be read, a new empty set of IDs will be
 * created. Similarly, any errors during saving will be logged to the standard
 * error output.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * {@code
 * Set<String> ids = FileStorageUtil.carregarIds();
 * ids.add("newId");
 * FileStorageUtil.salvarIds(ids);
 * }
 * </pre>
 *
 * <p>
 * Note: The file path is hardcoded as "notificaVideos.json". Ensure that the
 * application has the necessary permissions to read and write to this file.
 * </p>
 *
 * @see java.nio.file.Files
 * @see com.google.gson.Gson
 */
package br.com.emanuelgabriel.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class FileStorageUtil {

    private static final System.Logger logger = System.getLogger(FileStorageUtil.class.getName());
    private static final String IDS_FILE_PATH = "notificaVideos.json";
    private static final Gson gson = new Gson();

    /**
     * Carrega os IDs do arquivo JSON.
     *
     * @return Um conjunto de IDs carregados do arquivo ou um conjunto vazio se
     * o arquivo não existir.
     */
    public static Set<String> carregarIds() {
        try (Reader reader = Files.newBufferedReader(Paths.get(IDS_FILE_PATH))) {
            return gson.fromJson(reader, new TypeToken<Set<String>>() {
            }.getType());
        } catch (IOException e) {
            logger.log(Level.ERROR, "Erro ao carregar IDs do arquivo ou arquivo não encontrado. Criando novo conjunto. {0}", e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Salva os IDs no arquivo JSON.
     *
     * @param ids O conjunto de IDs a ser salvo.
     */
    public static void salvarIds(Set<String> ids) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(IDS_FILE_PATH))) {
            gson.toJson(ids, writer);
        } catch (IOException e) {
            logger.log(Level.ERROR, "Erro ao salvar IDs no arquivo: {0}", e.getMessage());
        }
    }

}
