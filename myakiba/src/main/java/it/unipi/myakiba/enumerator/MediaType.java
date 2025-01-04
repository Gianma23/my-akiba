package it.unipi.myakiba.enumerator;

public enum MediaType {
    ANIME,
    MANGA;

    public static MediaType fromString(String type) {
        try {
            return MediaType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo di media non valido: " + type);
        }
    }
}
