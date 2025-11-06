package mg.itu.database.dao;

import mg.itu.model.*;
import mg.itu.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Exemple d'utilisation du système CRUD générique refactorisé
 * Architecture POO : Modèles héritent de GenericDAO
 */
public class DAOExample {
    
    public static void main(String[] args) {
        System.out.println("=== DÉMONSTRATION DU SYSTÈME CRUD REFACTORISÉ ===\n");
        
        try {
            demonstrationCodeStatutDevise();
            System.out.println("\n" + "=".repeat(60) + "\n");
            
            demonstrationDevise();
            System.out.println("\n" + "=".repeat(60) + "\n");
            
            demonstrationActionRole();
            System.out.println("\n" + "=".repeat(60) + "\n");
            
            demonstrationLogicMetier();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== DÉMONSTRATION TERMINÉE ===");
    }
    
    /**
     * Démonstration CRUD avec CodeStatutDevise
     */
    private static void demonstrationCodeStatutDevise() throws Exception {
        System.out.println("💱 DÉMONSTRATION : CODES STATUTS DEVISES");
        System.out.println("=" + "=".repeat(59));
        
        CodeStatutDeviseService service = new CodeStatutDeviseService();
        
        // CREATE
        System.out.println("\n1️⃣  CREATE - Création de codes statuts");
        CodeStatutDevise actif = new CodeStatutDevise("actif");
        CodeStatutDevise inactif = new CodeStatutDevise("inactif");
        
        CodeStatutDevise createdActif = service.create(actif);
        CodeStatutDevise createdInactif = service.create(inactif);
        
        System.out.println("   ✅ Créé : " + createdActif);
        System.out.println("   ✅ Créé : " + createdInactif);
        
        // GET ALL
        System.out.println("\n2️⃣  GET ALL - Tous les codes statuts");
        List<CodeStatutDevise> allCodes = service.getAll();
        System.out.println("   ✅ " + allCodes.size() + " code(s) statut(s)");
        for (CodeStatutDevise code : allCodes) {
            System.out.println("      - " + code);
        }
        
        // DELETE
        System.out.println("\n3️⃣  DELETE - Nettoyage");
        service.delete(createdActif.getId());
        service.delete(createdInactif.getId());
        System.out.println("   ✅ Codes statuts supprimés");
    }
    
    /**
     * Démonstration CRUD avec Devise
     */
    private static void demonstrationDevise() throws Exception {
        System.out.println("💵 DÉMONSTRATION : DEVISES");
        System.out.println("=" + "=".repeat(59));
        
        DeviseService service = new DeviseService();
        
        // CREATE
        System.out.println("\n1️⃣  CREATE - Création de devises");
        Devise euro = new Devise("EUR", LocalDate.now(), new BigDecimal("1.00"), 1);
        Devise dollar = new Devise("USD", LocalDate.now(), new BigDecimal("1.18"), 1);
        Devise yen = new Devise("JPY", LocalDate.now(), new BigDecimal("130.50"), 1);
        
        euro = service.create(euro);
        dollar = service.create(dollar);
        yen = service.create(yen);
        
        System.out.println("   ✅ Créé : " + euro);
        System.out.println("   ✅ Créé : " + dollar);
        System.out.println("   ✅ Créé : " + yen);
        
        // READ
        System.out.println("\n2️⃣  READ - Récupération par code");
        Devise foundEuro = service.getByCode("EUR");
        System.out.println("   ✅ Trouvé : " + foundEuro);
        System.out.println("   📊 Active : " + (foundEuro.isCurrentlyActive() ? "Oui" : "Non"));
        
        // GET ALL actives
        System.out.println("\n3️⃣  GET ALL - Devises actives");
        List<Devise> activeDevises = service.getCurrentlyActiveDevises();
        System.out.println("   ✅ " + activeDevises.size() + " devise(s) active(s)");
        
        // DELETE
        System.out.println("\n4️⃣  DELETE - Nettoyage");
        service.delete(euro.getId());
        service.delete(dollar.getId());
        service.delete(yen.getId());
        System.out.println("   ✅ Devises supprimées");
    }
    
    /**
     * Démonstration CRUD avec ActionRole
     */
    private static void demonstrationActionRole() throws Exception {
        System.out.println("🔐 DÉMONSTRATION : ACTIONS & RÔLES");
        System.out.println("=" + "=".repeat(59));
        
        ActionRoleService service = new ActionRoleService();
        
        // CREATE
        System.out.println("\n1️⃣  CREATE - Création de permissions");
        ActionRole adminCreate = new ActionRole("users", "CREATE", 1);
        ActionRole userRead = new ActionRole("users", "READ", 3);
        ActionRole adminDelete = new ActionRole("users", "DELETE", 1);
        
        adminCreate = service.create(adminCreate);
        userRead = service.create(userRead);
        adminDelete = service.create(adminDelete);
        
        System.out.println("   ✅ Créé : " + adminCreate);
        System.out.println("   ✅ Créé : " + userRead);
        System.out.println("   ✅ Créé : " + adminDelete);
        
        // CHECK PERMISSION
        System.out.println("\n2️⃣  CHECK - Vérification des permissions");
        boolean adminCanCreate = service.checkPermission(1, "users", "CREATE");
        boolean userCanCreate = service.checkPermission(3, "users", "CREATE");
        boolean userCanRead = service.checkPermission(3, "users", "READ");
        
        System.out.println("   📊 Admin (rôle 1) peut CREATE : " + (adminCanCreate ? "✅ Oui" : "❌ Non"));
        System.out.println("   📊 User (rôle 3) peut CREATE : " + (userCanCreate ? "✅ Oui" : "❌ Non"));
        System.out.println("   📊 User (rôle 3) peut READ : " + (userCanRead ? "✅ Oui" : "❌ Non"));
        
        // GET BY TABLE
        System.out.println("\n3️⃣  GET ALL - Permissions pour la table 'users'");
        List<ActionRole> userPermissions = service.getPermissionsByTable("users");
        System.out.println("   ✅ " + userPermissions.size() + " permission(s) pour 'users'");
        for (ActionRole perm : userPermissions) {
            System.out.println("      - " + perm.getPermissionDescription());
        }
        
        // DELETE
        System.out.println("\n4️⃣  DELETE - Nettoyage");
        service.delete(adminCreate.getId());
        service.delete(userRead.getId());
        service.delete(adminDelete.getId());
        System.out.println("   ✅ Permissions supprimées");
    }
    
    /**
     * Démonstration de la logique métier dans les modèles
     */
    private static void demonstrationLogicMetier() throws Exception {
        System.out.println("🧠 DÉMONSTRATION : LOGIQUE MÉTIER DANS LES MODÈLES");
        System.out.println("=" + "=".repeat(59));
        
        // Devise - Conversion
        System.out.println("\n💱 Devise - Conversion de devises:");
        Devise eur = new Devise("EUR", LocalDate.now(), new BigDecimal("1.00"), 1);
        Devise usd = new Devise("USD", LocalDate.now(), new BigDecimal("1.18"), 1);
        
        BigDecimal montantEUR = new BigDecimal("100.00");
        BigDecimal montantUSD = eur.convertTo(montantEUR, usd);
        System.out.println("   " + montantEUR + " EUR = " + montantUSD + " USD");
        
        System.out.println("\n💱 Devise - Vérification d'activité:");
        Devise deviseActive = new Devise("GBP", LocalDate.now().minusDays(10), new BigDecimal("0.85"), 1);
        deviseActive.setDateFin(LocalDate.now().plusDays(30));
        System.out.println("   Devise : " + deviseActive.getCode());
        System.out.println("   Active aujourd'hui : " + (deviseActive.isCurrentlyActive() ? "Oui" : "Non"));
        System.out.println("   Durée de validité : " + deviseActive.getDaysOfValidity() + " jours");
        
        // ActionRole - Permissions
        System.out.println("\n🔐 ActionRole - Gestion des permissions:");
        ActionRole permission = new ActionRole("products", "UPDATE", 2);
        System.out.println("   Permission : " + permission.getPermissionDescription());
        System.out.println("   Admin (1) peut UPDATE : " + (permission.hasPermission(1) ? "Oui" : "Non"));
        System.out.println("   Manager (2) peut UPDATE : " + (permission.hasPermission(2) ? "Oui" : "Non"));
        System.out.println("   User (3) peut UPDATE : " + (permission.hasPermission(3) ? "Oui" : "Non"));
        System.out.println("   Action CRUD standard : " + (permission.isStandardCrudAction() ? "Oui" : "Non"));
    }
}
