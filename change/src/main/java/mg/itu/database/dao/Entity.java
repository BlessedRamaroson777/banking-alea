package mg.itu.database.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour marquer une classe comme entité de base de données
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    /**
     * Nom de la table dans la base de données
     * Si non spécifié, utilise le nom de la classe converti en snake_case
     */
    String tableName() default "";
}
