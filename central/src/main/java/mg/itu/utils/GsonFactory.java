package mg.itu.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * Factory pour créer des instances Gson avec support des types Java 8+ (LocalDate, LocalDateTime)
 */
public class GsonFactory {
    
    /**
     * Crée une instance Gson configurée pour gérer LocalDate, LocalDateTime et BigDecimal
     * Note: Les champs null ne sont PAS sérialisés (comportement par défaut de Gson)
     */
    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // Ne pas utiliser .serializeNulls() pour éviter d'envoyer "id":null qui ne peut pas être désérialisé en int
        
        // Deserializer pour lire LocalDate depuis JSON
        gsonBuilder.registerTypeAdapter(LocalDate.class,
            (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));
        
        // Serializer pour écrire LocalDate en JSON
        gsonBuilder.registerTypeAdapter(LocalDate.class,
            (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        
        // Deserializer pour lire LocalDateTime depuis JSON
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
            (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Serializer pour écrire LocalDateTime en JSON
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        
        // Deserializer pour lire BigDecimal depuis JSON
        gsonBuilder.registerTypeAdapter(BigDecimal.class,
            (JsonDeserializer<BigDecimal>) (json, typeOfT, context) ->
                new BigDecimal(json.getAsString()));
        
        // Serializer pour écrire BigDecimal en JSON
        gsonBuilder.registerTypeAdapter(BigDecimal.class,
            (JsonSerializer<BigDecimal>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src));
        
        return gsonBuilder.create();
    }
}
