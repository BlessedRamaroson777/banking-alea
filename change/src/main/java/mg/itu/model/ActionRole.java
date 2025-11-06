package mg.itu.model;

import mg.itu.database.dao.Entity;
import mg.itu.database.dao.GenericDAO;

import java.sql.SQLException;

/**
 * Modèle ActionRole - Représente les permissions par rôle pour une action sur une table
 */
@Entity(tableName = "actions_roles")
public class ActionRole extends GenericDAO<ActionRole> {
    private int id;
    private String nomTable;       // Nom de la table concernée
    private String nomAction;      // Nom de l'action (CREATE, READ, UPDATE, DELETE)
    private int roleMinimum;       // Rôle minimum requis (1 = admin, 2 = user, etc.)
    
    // Constructeur par défaut
    public ActionRole() {
    }
    
    // Constructeur avec paramètres
    public ActionRole(String nomTable, String nomAction, int roleMinimum) {
        this.nomTable = nomTable;
        this.nomAction = nomAction;
        this.roleMinimum = roleMinimum;
    }
    
    // ========== GETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public String getNomTable() {
        return nomTable;
    }
    
    public String getNomAction() {
        return nomAction;
    }
    
    public int getRoleMinimum() {
        return roleMinimum;
    }
    
    // ========== SETTERS AVEC VALIDATION SIMPLE ==========
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Setter avec validation simple du nom de table
     */
    public void setNomTable(String nomTable) {
        if (nomTable == null || nomTable.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la table ne peut pas être vide");
        }
        if (nomTable.length() > 100) {
            throw new IllegalArgumentException("Le nom de la table ne peut pas dépasser 100 caractères");
        }
        this.nomTable = nomTable.trim();
    }
    
    /**
     * Setter avec validation simple du nom d'action
     */
    public void setNomAction(String nomAction) {
        if (nomAction == null || nomAction.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'action ne peut pas être vide");
        }
        if (nomAction.length() > 100) {
            throw new IllegalArgumentException("Le nom de l'action ne peut pas dépasser 100 caractères");
        }
        this.nomAction = nomAction.trim();
    }
    
    /**
     * Setter avec validation simple du rôle minimum
     */
    public void setRoleMinimum(int roleMinimum) {
        if (roleMinimum < 1) {
            throw new IllegalArgumentException("Le rôle minimum doit être au moins 1");
        }
        this.roleMinimum = roleMinimum;
    }
    
    // ========== LOGIQUE MÉTIER ==========
    
    /**
     * CONTRÔLE - Validation complexe
     */
    @Override
    protected void validate() throws SQLException {
        // Validation du nom de table
        if (nomTable == null || nomTable.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la table est obligatoire");
        }
        if (nomTable.length() > 100) {
            throw new IllegalArgumentException("Le nom de la table ne peut pas dépasser 100 caractères");
        }
        
        // Validation du nom d'action
        if (nomAction == null || nomAction.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'action est obligatoire");
        }
        if (nomAction.length() > 100) {
            throw new IllegalArgumentException("Le nom de l'action ne peut pas dépasser 100 caractères");
        }
        
        // Validation des actions standard
        String actionUpper = nomAction.toUpperCase();
        if (!actionUpper.matches("CREATE|READ|UPDATE|DELETE|SELECT|INSERT|.*")) {
            // On accepte les actions standard ou personnalisées
        }
        
        // Validation du rôle minimum
        if (roleMinimum < 1) {
            throw new IllegalArgumentException("Le rôle minimum doit être au moins 1 (1 = plus de permissions)");
        }
    }
    
    /**
     * MÉTIER - Normalisation avant création
     */
    @Override
    protected void beforeCreate() throws SQLException {
        // Normaliser le nom de la table
        if (nomTable != null) {
            nomTable = nomTable.trim().toLowerCase();
        }
        
        // Normaliser le nom de l'action en majuscules
        if (nomAction != null) {
            nomAction = nomAction.trim().toUpperCase();
        }
    }
    
    /**
     * MÉTIER - Normalisation avant mise à jour
     */
    @Override
    protected void beforeUpdate() throws SQLException {
        beforeCreate();
    }
    
    // ========== MÉTHODES MÉTIER ==========
    
    /**
     * Vérifie si un rôle donné a la permission pour cette action
     * @param userRole Le rôle de l'utilisateur (1 = admin, 2 = user, etc.)
     * @return true si l'utilisateur a la permission
     */
    public boolean hasPermission(int userRole) {
        // Plus le nombre est petit, plus le rôle a de permissions
        // Ex: role 1 (admin) peut tout faire, role 2 (user) a moins de permissions
        return userRole <= roleMinimum;
    }
    
    /**
     * Obtient le nom du rôle minimum requis
     */
    public String getRoleMinimumName() {
        switch (roleMinimum) {
            case 1: return "ADMIN";
            case 2: return "MANAGER";
            case 3: return "USER";
            case 4: return "GUEST";
            default: return "CUSTOM_" + roleMinimum;
        }
    }
    
    /**
     * Vérifie si c'est une action CRUD standard
     */
    public boolean isStandardCrudAction() {
        if (nomAction == null) return false;
        String action = nomAction.toUpperCase();
        return action.equals("CREATE") || action.equals("READ") || 
               action.equals("UPDATE") || action.equals("DELETE");
    }
    
    /**
     * Obtient une description complète de la permission
     */
    public String getPermissionDescription() {
        return String.format("Action '%s' sur la table '%s' requiert le rôle %s (niveau %d)", 
                             nomAction, nomTable, getRoleMinimumName(), roleMinimum);
    }
    
    @Override
    public String toString() {
        return "ActionRole{" +
                "id=" + id +
                ", nomTable='" + nomTable + '\'' +
                ", nomAction='" + nomAction + '\'' +
                ", roleMinimum=" + roleMinimum +
                " (" + getRoleMinimumName() + ")" +
                '}';
    }
}
