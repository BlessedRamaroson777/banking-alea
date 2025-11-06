package mg.itu.model;

import mg.itu.database.dao.Entity;
import mg.itu.database.dao.GenericDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle DeviseModification - Représente une proposition de modification d'une devise
 * Cette table intermédiaire permet de valider les modifications avant de les appliquer
 */
@Entity(tableName = "devises_modifications")
public class DeviseModification extends GenericDAO<DeviseModification> {
    private int id;
    private int deviseId;                    // ID de la devise à modifier
    private String code;                     // Nouvelle valeur du code (null si pas de modification)
    private LocalDate dateDebut;             // Nouvelle valeur de la date de début (null si pas de modification)
    private LocalDate dateFin;               // Nouvelle valeur de la date de fin (null si pas de modification)
    private BigDecimal cours;                // Nouvelle valeur du cours (null si pas de modification)
    private Integer statutCode;              // Nouvelle valeur du statut (null si pas de modification)
    private int statutValidation;            // Statut de la proposition (1=En attente, 2=Validé, 3=Refusé)
    private LocalDateTime dateProposition;   // Date de création de la proposition
    private LocalDateTime dateTraitement;    // Date de traitement (validation/refus)
    
    // Constructeur par défaut
    public DeviseModification() {
    }
    
    // Constructeur pour créer une proposition de modification
    public DeviseModification(int deviseId) {
        this.deviseId = deviseId;
        this.statutValidation = 1; // En attente par défaut
        this.dateProposition = LocalDateTime.now();
    }
    
    // ========== GETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public int getDeviseId() {
        return deviseId;
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
    
    public int getStatutValidation() {
        return statutValidation;
    }
    
    public LocalDateTime getDateProposition() {
        return dateProposition;
    }
    
    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }
    
    // ========== SETTERS ==========
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDeviseId(int deviseId) {
        if (deviseId <= 0) {
            throw new IllegalArgumentException("L'ID de la devise doit être positif");
        }
        this.deviseId = deviseId;
    }
    
    public void setCode(String code) {
        if (code != null && !code.trim().isEmpty()) {
            if (code.length() != 3) {
                throw new IllegalArgumentException("Le code doit contenir exactement 3 caractères");
            }
            this.code = code.trim().toUpperCase();
        } else {
            this.code = code;
        }
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public void setCours(BigDecimal cours) {
        if (cours != null && cours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le cours doit être strictement positif");
        }
        this.cours = cours;
    }
    
    public void setStatutCode(Integer statutCode) {
        this.statutCode = statutCode;
    }
    
    public void setStatutValidation(int statutValidation) {
        this.statutValidation = statutValidation;
    }
    
    public void setDateProposition(LocalDateTime dateProposition) {
        this.dateProposition = dateProposition;
    }
    
    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
    
    // ========== LOGIQUE MÉTIER ==========
    
    /**
     * CONTRÔLE - Validation en utilisant la validation de Devise
     * Crée une devise temporaire avec les modifications proposées et utilise sa validation
     */
    @Override
    protected void validate() throws SQLException {
        if (deviseId <= 0) {
            throw new IllegalArgumentException("L'ID de la devise est obligatoire");
        }
        
        // Vérifier qu'au moins un champ de modification est défini
        if (code == null && dateDebut == null && dateFin == null && cours == null && statutCode == null) {
            throw new IllegalArgumentException("Au moins un champ doit être modifié");
        }
        
        // Récupérer la devise originale
        Devise deviseOriginale = Devise.getById(Devise.class, deviseId);
        if (deviseOriginale == null) {
            throw new SQLException("Devise non trouvée avec l'ID: " + deviseId);
        }
        
        // Créer une devise temporaire avec les modifications proposées
        Devise deviseTest = new Devise();
        deviseTest.setId(deviseOriginale.getId());
        deviseTest.setCode(code != null ? code : deviseOriginale.getCode());
        deviseTest.setDateDebut(dateDebut != null ? dateDebut : deviseOriginale.getDateDebut());
        deviseTest.setDateFin(dateFin != null ? dateFin : deviseOriginale.getDateFin());
        deviseTest.setCours(cours != null ? cours : deviseOriginale.getCours());
        deviseTest.setStatutCode(statutCode != null ? statutCode : deviseOriginale.getStatutCode());
        
        // Utiliser la validation de Devise pour vérifier la cohérence
        deviseTest.validate();
    }
    
    /**
     * MÉTIER - Normalisation avant création
     */
    @Override
    protected void beforeCreate() throws SQLException {
        // Normaliser le code en majuscules si présent
        if (code != null) {
            code = code.trim().toUpperCase();
        }
        
        // Arrondir le cours à 2 décimales si présent
        if (cours != null) {
            cours = cours.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        
        // Définir le statut à "En attente" par défaut
        if (statutValidation == 0) {
            statutValidation = 1;
        }
        
        // Définir la date de proposition si non définie
        if (dateProposition == null) {
            dateProposition = LocalDateTime.now();
        }
    }
    
    /**
     * Valider la modification proposée et l'appliquer à la devise
     * @param dateValidation Date de validation à appliquer (optionnel)
     */
    public DeviseModification validerModification(LocalDate dateValidation) throws SQLException {
        // Vérifier que la modification est en attente
        if (statutValidation != 1) {
            throw new IllegalStateException("Seules les modifications en attente peuvent être validées");
        }
        
        // Récupérer la devise originale
        Devise devise = Devise.getById(Devise.class, deviseId);
        if (devise == null) {
            throw new SQLException("Devise non trouvée avec l'ID: " + deviseId);
        }
        
        // Appliquer les modifications
        if (code != null) {
            devise.setCode(code);
        }
        if (dateDebut != null) {
            devise.setDateDebut(dateDebut);
        }
        if (dateFin != null) {
            devise.setDateFin(dateFin);
        }
        if (cours != null) {
            devise.setCours(cours);
        }
        if (statutCode != null) {
            devise.setStatutCode(statutCode);
        }
        
        // Définir la date de modification
        if (dateValidation != null) {
            devise.setDateModification(dateValidation);
        }
        
        // Mettre à jour la devise
        devise.update();
        
        // Marquer la modification comme validée
        this.statutValidation = 2; // Validé
        this.dateTraitement = LocalDateTime.now();
        
        return this.update();
    }
    
    /**
     * Refuser la modification proposée
     */
    public DeviseModification refuserModification() throws SQLException {
        // Vérifier que la modification est en attente
        if (statutValidation != 1) {
            throw new IllegalStateException("Seules les modifications en attente peuvent être refusées");
        }
        
        // Marquer la modification comme refusée
        this.statutValidation = 3; // Refusé
        this.dateTraitement = LocalDateTime.now();
        
        return this.update();
    }
    
    /**
     * Vérifie si la modification est en attente
     */
    public boolean isEnAttente() {
        return statutValidation == 1;
    }
    
    /**
     * Vérifie si la modification est validée
     */
    public boolean isValidee() {
        return statutValidation == 2;
    }
    
    /**
     * Vérifie si la modification est refusée
     */
    public boolean isRefusee() {
        return statutValidation == 3;
    }
    
    /**
     * MÉTIER - Créer une proposition de modification à partir d'une devise et des modifications
     * Cette méthode encapsule la logique métier de proposition de modification
     * 
     * @param deviseId ID de la devise à modifier
     * @param propositionData Objet DeviseModification contenant les nouvelles valeurs
     * @return La proposition de modification créée
     * @throws SQLException si erreur lors de la création
     */
    public static DeviseModification proposeModification(int deviseId, DeviseModification propositionData) throws SQLException {
        // Vérifier que la devise existe
        Devise deviseOriginale = Devise.getById(Devise.class, deviseId);
        if (deviseOriginale == null) {
            throw new SQLException("Devise non trouvée avec l'ID: " + deviseId);
        }
        
        // Créer la proposition
        DeviseModification modification = new DeviseModification(deviseId);
        
        // Copier uniquement les champs qui sont différents de l'original
        if (propositionData.getCode() != null && !propositionData.getCode().equals(deviseOriginale.getCode())) {
            modification.setCode(propositionData.getCode());
        }
        
        if (propositionData.getDateDebut() != null && !propositionData.getDateDebut().equals(deviseOriginale.getDateDebut())) {
            modification.setDateDebut(propositionData.getDateDebut());
        }
        
        if (propositionData.getDateFin() != null) {
            if (deviseOriginale.getDateFin() == null || !propositionData.getDateFin().equals(deviseOriginale.getDateFin())) {
                modification.setDateFin(propositionData.getDateFin());
            }
        }
        
        if (propositionData.getCours() != null && !propositionData.getCours().equals(deviseOriginale.getCours())) {
            modification.setCours(propositionData.getCours());
        }
        
        if (propositionData.getStatutCode() != null && !propositionData.getStatutCode().equals(deviseOriginale.getStatutCode())) {
            modification.setStatutCode(propositionData.getStatutCode());
        }
        
        // Vérifier qu'au moins un champ a été modifié
        if (modification.getCode() == null && 
            modification.getDateDebut() == null && 
            modification.getDateFin() == null && 
            modification.getCours() == null && 
            modification.getStatutCode() == null) {
            throw new IllegalArgumentException("Aucune modification détectée");
        }
        
        // Créer la proposition en base
        return modification.create();
    }
    
    @Override
    public String toString() {
        return "DeviseModification{" +
                "id=" + id +
                ", deviseId=" + deviseId +
                ", code='" + code + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", cours=" + cours +
                ", statutCode=" + statutCode +
                ", statutValidation=" + statutValidation +
                ", dateProposition=" + dateProposition +
                ", dateTraitement=" + dateTraitement +
                '}';
    }
}
