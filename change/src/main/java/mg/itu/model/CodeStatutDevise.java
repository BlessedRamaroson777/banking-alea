package mg.itu.model;

import mg.itu.database.dao.Entity;
import mg.itu.database.dao.GenericDAO;

import java.sql.SQLException;

/**
 * Modèle CodeStatutDevise - Représente un code de statut pour une devise
 */
@Entity(tableName = "codes_statuts_devises")
public class CodeStatutDevise extends GenericDAO<CodeStatutDevise> {
    private int id;
    private String libelle;
    
    // Constructeur par défaut
    public CodeStatutDevise() {
    }
    
    // Constructeur avec paramètres
    public CodeStatutDevise(String libelle) {
        this.libelle = libelle;
    }
    
    // ========== GETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    // ========== SETTERS AVEC VALIDATION SIMPLE ==========
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Setter avec validation simple du libellé
     */
    public void setLibelle(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé ne peut pas être vide");
        }
        if (libelle.length() > 20) {
            throw new IllegalArgumentException("Le libellé ne peut pas dépasser 20 caractères");
        }
        this.libelle = libelle.trim();
    }
    
    // ========== LOGIQUE MÉTIER ==========
    
    /**
     * CONTRÔLE - Validation complexe
     */
    @Override
    protected void validate() throws SQLException {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé est obligatoire");
        }
        if (libelle.length() > 20) {
            throw new IllegalArgumentException("Le libellé ne peut pas dépasser 20 caractères");
        }
    }
    
    /**
     * MÉTIER - Normalisation avant création
     */
    @Override
    protected void beforeCreate() throws SQLException {
        if (libelle != null) {
            libelle = libelle.trim().toUpperCase();
        }
    }
    
    /**
     * MÉTIER - Normalisation avant mise à jour
     */
    @Override
    protected void beforeUpdate() throws SQLException {
        beforeCreate();
    }
    
    @Override
    public String toString() {
        return "CodeStatutDevise{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
