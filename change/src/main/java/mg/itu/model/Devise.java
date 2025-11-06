package mg.itu.model;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

import mg.itu.database.dao.Entity;
import mg.itu.database.dao.GenericDAO;

/**
 * Modèle Devise - Représente une devise avec son cours
 */
@Entity(tableName = "devises")
public class Devise extends GenericDAO<Devise> {
    private int id;
    private String code;           // Code ISO de la devise (ex: EUR, USD)
    private LocalDate dateDebut;   // Date de début de validité
    private LocalDate dateFin;     // Date de fin de validité (nullable)
    private BigDecimal cours;      // Cours de la devise
    private Integer statutCode;    // Référence vers codes_statuts_devises
    private LocalDate dateModification; // Date de la dernière modification validée
    
    // Constructeur par défaut
    public Devise() {
    }
    
    // Constructeur avec paramètres
    public Devise(String code, LocalDate dateDebut, BigDecimal cours, Integer statutCode) {
        this.code = code;
        this.dateDebut = dateDebut;
        this.cours = cours;
        this.statutCode = statutCode;
    }
    
    // ========== GETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public String getCode() {
        return code;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public BigDecimal getCours() {
        return cours;
    }
    
    public Integer getStatutCode() {
        return statutCode;
    }
    
    public LocalDate getDateModification() {
        return dateModification;
    }
    
    // ========== SETTERS AVEC VALIDATION SIMPLE ==========
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Setter avec validation simple du code
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code ne peut pas être vide");
        }
        if (code.length() != 3) {
            throw new IllegalArgumentException("Le code doit contenir exactement 3 caractères");
        }
        this.code = code.trim().toUpperCase();
    }
    
    /**
     * Setter avec validation simple de la date de début
     */
    public void setDateDebut(LocalDate dateDebut) {
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début ne peut pas être nulle");
        }
        this.dateDebut = dateDebut;
    }
    
    /**
     * Setter de la date de fin (peut être null)
     */
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    /**
     * Setter avec validation simple du cours
     */
    public void setCours(BigDecimal cours) {
        if (cours == null || cours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le cours doit être positif");
        }
        this.cours = cours;
    }
    
    /**
     * Setter du statut
     */
    public void setStatutCode(Integer statutCode) {
        this.statutCode = statutCode;
    }
    
    /**
     * Setter de la date de modification
     */
    public void setDateModification(LocalDate dateModification) {
        this.dateModification = dateModification;
    }
    
    // ========== LOGIQUE MÉTIER ==========
    
    /**
     * CONTRÔLE - Validation complexe
     */
    @Override
    protected void validate() throws SQLException {
        // Validation du code
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code de la devise est obligatoire");
        }
        if (code.length() != 3) {
            throw new IllegalArgumentException("Le code doit être au format ISO (3 caractères)");
        }
        
        // Validation de la date de début
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        
        // Validation des dates
        if (dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        
        // Validation du cours
        if (cours == null || cours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le cours doit être strictement positif");
        }
        
        // Validation du cours (15 chiffres max, 2 décimales)
        if (cours.precision() > 15) {
            throw new IllegalArgumentException("Le cours ne peut pas avoir plus de 15 chiffres");
        }
    }

    protected void beforePersistance() throws SQLException{
        // Normaliser le code en majuscules
        if (code != null) {
            code = code.trim().toUpperCase();
        }
        
        // Si pas de date de début, utiliser la date du jour
        if (dateDebut == null) {
            dateDebut = LocalDate.now();
        }
        
        // Vérifier que la date de début n'est pas dans le futur
        if (dateDebut.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le futur");
        }
        
        // Arrondir le cours à 2 décimales
        if (cours != null) {
            cours = cours.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    /**
     * MÉTIER - Normalisation avant création
     */
    @Override
    protected void beforeCreate() throws SQLException {
        beforePersistance();
        
        // Définir le statut à "En attente" (id=1) lors de la création
        if (statutCode == null || statutCode == 0) {
            statutCode = 1; // 1 = "En attente"
        }
    }
    
    /**
     * MÉTIER - Normalisation avant mise à jour
     */
    @Override
    protected void beforeUpdate() throws SQLException {
        beforePersistance();
    }
    
    // ========== MÉTHODES MÉTIER ==========
    
    /**
     * Vérifie si la devise est active à une date donnée
     * Une devise est active si:
     * 1. Son statut est "Valide" (statutCode = 2)
     * 2. La date est dans sa période de validité (dateDebut <= date <= dateFin)
     */
    public boolean isActiveAt(LocalDate date) {
        // Vérifier d'abord le statut : doit être "Valide" (2)
        if (statutCode == null || statutCode != 2) {
            return false;
        }
        
        // Si dateDebut est null, impossible de déterminer si actif
        if (dateDebut == null) {
            return false;
        }
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        boolean afterStart = !date.isBefore(dateDebut);
        boolean beforeEnd = (dateFin == null) || !date.isAfter(dateFin);
        
        return afterStart && beforeEnd;
    }
    
    /**
     * Vérifie si la devise est actuellement active
     * (Statut "Valide" et dans la période de validité actuelle)
     */
    public boolean isCurrentlyActive() {
        return isActiveAt(LocalDate.now());
    }
    
    /**
     * Calcule le montant converti en une autre devise
     */
    public BigDecimal convertTo(BigDecimal montant, Devise autreDevise) {
        if (montant == null || autreDevise == null) {
            throw new IllegalArgumentException("Les paramètres ne peuvent pas être null");
        }
        
        // Conversion : montant * (coursAutreDevise / coursActuel)
        BigDecimal taux = autreDevise.getCours().divide(this.cours, 4, java.math.RoundingMode.HALF_UP);
        return montant.multiply(taux).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Obtient le nombre de jours de validité
     */
    public long getDaysOfValidity() {
        if (dateFin == null) {
            return -1; // Validité illimitée
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
    }
    
    /**
     * MÉTIER - Valider la création d'une devise
     * Change le statut de "En attente" (1) à "Valide" (2)
     * 
     * @param dateValidation Date de validation à appliquer (date de début)
     * @return La devise mise à jour avec le statut "Valide"
     * @throws SQLException si erreur lors de la mise à jour
     * @throws IllegalStateException si la devise n'est pas en attente
     * @throws IllegalArgumentException si la date de validation est invalide
     */
    public Devise validerCreation(LocalDate dateValidation) throws SQLException {
        // Vérifier que la devise est en attente
        if (statutCode == null || statutCode != 1) {
            throw new IllegalStateException("Seules les devises en attente peuvent être validées");
        }
        
        // Vérifier qu'elle n'a pas déjà été traitée
        if (dateModification != null) {
            throw new IllegalStateException("Cette devise a déjà été validée ou refusée");
        }
        
        // Enregistrer la date de modification (date de validation)
        // La date de création (dateDebut) reste inchangée
        if (dateValidation != null) {
            this.dateModification = dateValidation;
        }
        
        // Changer le statut à "Valide" (id=2)
        this.statutCode = 2;
        
        // Mettre à jour en base de données
        return this.update();
    }
    
    /**
     * MÉTIER - Refuser la création d'une devise
     * Change le statut de "En attente" (1) à "Refuse" (3)
     * 
     * @return La devise mise à jour avec le statut "Refuse"
     * @throws SQLException si erreur lors de la mise à jour
     * @throws IllegalStateException si la devise n'est pas en attente
     */
    public Devise refuserCreation() throws SQLException {
        // Vérifier que la devise est en attente
        if (statutCode == null || statutCode != 1) {
            throw new IllegalStateException("Seules les devises en attente peuvent être refusées");
        }
        
        // Vérifier qu'elle n'a pas déjà été traitée
        if (dateModification != null) {
            throw new IllegalStateException("Cette devise a déjà été validée ou refusée");
        }
        
        // Enregistrer la date de refus (date de modification)
        this.dateModification = LocalDate.now();
        
        // Changer le statut à "Refuse" (id=3)
        this.statutCode = 3;
        
        // Mettre à jour en base de données
        return this.update();
    }
    
    /**
     * Vérifie si la devise est en attente de validation
     */
    public boolean isEnAttente() {
        return statutCode != null && statutCode == 1;
    }
    
    /**
     * Vérifie si la devise est validée
     */
    public boolean isValidee() {
        return statutCode != null && statutCode == 2;
    }
    
    /**
     * Vérifie si la devise est refusée
     */
    public boolean isRefusee() {
        return statutCode != null && statutCode == 3;
    }
    
    @Override
    public String toString() {
        return "Devise{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", cours=" + cours +
                ", statutCode=" + statutCode +
                ", active=" + isCurrentlyActive() +
                '}';
    }
}
