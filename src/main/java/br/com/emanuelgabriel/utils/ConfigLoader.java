package br.com.emanuelgabriel.utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigLoader {

    private static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());
    private final Properties properties = new Properties();

    /**
     * Carrega as propriedades de um arquivo .properties especificado.
     *
     * @param propertiesFile O nome do arquivo .properties a ser carregado.
     */
    public void loadPropertiesFromFile(String propertiesFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null) {
                throw new RuntimeException(String.format("Arquivo %s não encontrado.", propertiesFile));
            }
            properties.load(input);
            logger.info(String.format("Arquivo %s carregado com sucesso.", propertiesFile));
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    String.format("Falha ao carregar o arquivo %s: %s", propertiesFile, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtém o valor de uma propriedade pelo nome da chave.
     *
     * @param key O nome da chave.
     * @return O valor associado à chave.
     */
    public String getProperty(String key) {
        var value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(String.format("A chave %s não foi encontrada no arquivo de propriedades.", key));
        }
        return value;
    }

    /**
     * Obtém o valor de uma propriedade pelo nome da chave, com valor padrão.
     *
     * @param key          O nome da chave.
     * @param defaultValue O valor padrão caso a chave não seja encontrada.
     * @return O valor associado à chave ou o valor padrão.
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

}
