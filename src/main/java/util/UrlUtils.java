package util;

import config.Config;

/**
 * Utilidades para trabajar con las URLs que devuelve la API.
 * Ejemplo: "https://anapioficeandfire.com/api/houses/362" -> 362
 */
public final class UrlUtils {

    private UrlUtils() {
    }

    /** Devuelve el número final de la URL, o 0 si no es válida. */
    public static int extractId(String url) {
        if (url == null || url.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(url.substring(url.lastIndexOf('/') + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Reconstruye una URL a partir del recurso y el id: ("houses", 362). */
    public static String buildUrl(String resource, int id) {
        return Config.API_BASE_URL + "/" + resource + "/" + id;
    }
}