package mg.itu.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import mg.itu.models.base.BaseEntity;

public class Virement extends BaseEntity<Virement> {
    private Integer id;
    private BigDecimal montant;
    private LocalDateTime dateCreation;
    private LocalDateTime dateEffet;
    private Integer compteEnvoyeur;
    private Integer compteDestinataire;
    private Integer utilisateurId;
    private Integer statutCode;
    private Integer changeId;

    public Virement() {
    }

    @Override
    protected void copyFrom(Virement other) {
        if (other == null) return;
        this.id = other.id;
        this.montant = other.montant;
        this.dateCreation = other.dateCreation;
        this.dateEffet = other.dateEffet;
        this.compteEnvoyeur = other.compteEnvoyeur;
        this.compteDestinataire = other.compteDestinataire;
        this.utilisateurId = other.utilisateurId;
        this.statutCode = other.statutCode;
        this.changeId = other.changeId;
    }

    public Virement(BigDecimal montant, LocalDateTime dateCreation,
                    LocalDateTime dateEffet, Integer compteEnvoyeur,
                    Integer compteDestinataire, Integer utilisateurId,
                    Integer statutCode, Integer changeId) {
        setMontant(montant);
        setDateCreation(dateCreation);
        setDateEffet(dateEffet);
        setCompteEnvoyeur(compteEnvoyeur);
        setCompteDestinataire(compteDestinataire);
        setUtilisateurId(utilisateurId);
        setStatutCode(statutCode);
        setChangeId(changeId);
    }

    private Virement changerStatut(Connection conn, Integer utilisateurId,
                                   LocalDateTime dateValidation,
                                   Integer nouveauStatut, List<ActionRole> actionRoles) throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurId);
        utilisateur.findById(conn);

        if (!utilisateur.roleSuffisant(actionRoles, "Virement", "VALIDER"))
            throw new Exception("Vous n'avez pas les privilèges pour effectuer cette action");

        // Récupération des comptes
        Compte envoyeur = chargerCompte(conn, compteEnvoyeur);
        Compte destinataire = chargerCompte(conn, compteDestinataire);

        // Calcul des frais
        BigDecimal frais = calculerFrais(envoyeur.getTypeId());

        // Mise à jour des soldes
        mettreAJourSoldes(envoyeur, destinataire, frais);

        // Mise à jour du statut des virements et transactions
        this.setStatutCode(nouveauStatut);
        this.save(conn);

        List<Transaction> transactions = Transaction.getByVirementId(this.getId());
        for (Transaction transaction : transactions) {
            transaction.setStatutCode(nouveauStatut);
            transaction.save(conn);
        }

        // Création des validations
        creerValidations(conn, utilisateurId, dateValidation, nouveauStatut, transactions);

        // Sauvegarde des comptes
        envoyeur.save(conn);
        destinataire.save(conn);

        return this;
    }

    public Virement valider(Connection conn, Integer utilisateurId,
                            LocalDateTime dateValidation, List<ActionRole> actionRoles) throws Exception {
        if (this.statutCode > 1) {
            throw new IllegalArgumentException(
                    "Le virement a déjà été validé"
            );
        }

        return changerStatut(conn, utilisateurId, dateValidation, 11, actionRoles);
    }

    public Virement refuser(Connection conn, Integer utilisateurId,
                            LocalDateTime dateValidation, List<ActionRole> actionRoles) throws Exception {
        return changerStatut(conn, utilisateurId, dateValidation, -11, actionRoles);
    }

    private Compte chargerCompte(Connection conn, Integer compteId)
            throws SQLException {
        Compte compte = new Compte();
        compte.setId(compteId);
        compte.findById(conn);
        return compte;
    }

    private BigDecimal calculerFrais(Integer typeCompte) {
        return new ConfigFrais()
                .getByTypeCompteAndMontant(typeCompte, montant)
                .calculerFrais(montant);
    }

    private void mettreAJourSoldes(Compte envoyeur, Compte destinataire,
                                   BigDecimal frais) {
        envoyeur.setSolde(envoyeur.getSolde().subtract(montant));
        destinataire.setSolde(
                destinataire.getSolde().add(montant).subtract(frais)
        );
    }

    private void creerValidations(Connection conn, Integer utilisateurId,
                                  LocalDateTime dateValidation,
                                  Integer nouveauStatut) throws SQLException {
        // Validation des transactions associées
        List<Transaction> transactions = Transaction.getByVirementId(this.getId());

        creerValidations(conn, utilisateurId, dateValidation, nouveauStatut, transactions);
    }

    private void creerValidations(Connection conn, Integer utilisateurId,
                                  LocalDateTime dateValidation,
                                  Integer nouveauStatut, List<Transaction> transactions) throws SQLException {
        // Validation du virement
        Validation validationVirement = new Validation(
                Validation.TypeValidation.VIREMENT,
                this.getId(),
                dateValidation,
                utilisateurId,
                nouveauStatut
        );
        validationVirement.save(conn);

        // validation des transactions
        for (Transaction transaction : transactions) {
            Validation validationTransaction = new Validation(
                    Validation.TypeValidation.TRANSACTION,
                    transaction.getId(),
                    dateValidation,
                    utilisateurId,
                    nouveauStatut
            );
            validationTransaction.save(conn);
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        if (montant == null) {
            throw new IllegalArgumentException("Le montant ne doit pas être null");
        }
        if (BigDecimal.ZERO.compareTo(montant) >= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        this.montant = montant;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation != null ? dateCreation : LocalDateTime.now();
    }

    public LocalDateTime getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDateTime dateEffet) {
        if (dateEffet == null) {
            dateEffet = dateCreation;
        }
        if (dateEffet.isBefore(dateCreation)) {
            throw new IllegalArgumentException(
                    "La date d'effet ne doit pas être antérieure à la date de création"
            );
        }
        this.dateEffet = dateEffet;
    }

    public Integer getCompteEnvoyeur() {
        return compteEnvoyeur;
    }

    public void setCompteEnvoyeur(Integer compteEnvoyeur) {
        if (compteEnvoyeur == null) {
            throw new IllegalArgumentException(
                    "Le compte envoyeur ne doit pas être null"
            );
        }
        this.compteEnvoyeur = compteEnvoyeur;
    }

    public Integer getCompteDestinataire() {
        return compteDestinataire;
    }

    public void setCompteDestinataire(Integer compteDestinataire) {
        if (compteDestinataire != null && compteDestinataire.equals(compteEnvoyeur)) {
            throw new IllegalArgumentException(
                    "Le compte destinataire doit être différent du compte envoyeur"
            );
        }
        this.compteDestinataire = compteDestinataire;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        if (utilisateurId == null) {
            throw new IllegalArgumentException(
                    "Un utilisateur est requis pour la création d'un virement"
            );
        }
        this.utilisateurId = utilisateurId;
    }

    public Integer getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(Integer statutCode) {
        this.statutCode = statutCode;
    }

    public Integer getChangeId() {
        return changeId;
    }

    public void setChangeId(Integer changeId) {
        if (changeId == null) {
            this.changeId = 1; // 1 doit être MGA dans la bdd
            return;
        }

        this.changeId = changeId;
    }

    @Override
    protected String getTableName() {
        return "virements";
    }

    @Override
    protected Virement mapRow(ResultSet rs) throws SQLException {
        Virement v = new Virement();
        v.setId(rs.getInt("id"));
        v.setMontant(rs.getBigDecimal("montant"));
        Timestamp dc = rs.getTimestamp("date_creation");
        Timestamp de = rs.getTimestamp("date_effet");
        v.setDateCreation(dc != null ? dc.toLocalDateTime() : null);
        v.setDateEffet(de != null ? de.toLocalDateTime() : null);
        v.setCompteEnvoyeur(rs.getInt("compte_envoyeur"));
        v.setCompteDestinataire(rs.getInt("compte_destinataire"));
        v.setUtilisateurId(rs.getInt("utilisateur_id"));
        v.setStatutCode(rs.getInt("statut_code"));
        v.setChangeId(rs.getInt("change_id"));
        return v;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (montant != null) map.put("montant", montant);
        if (dateCreation != null) map.put("date_creation", dateCreation);
        if (dateEffet != null) map.put("date_effet", dateEffet);
        if (compteEnvoyeur != null) map.put("compte_envoyeur", compteEnvoyeur);
        if (compteDestinataire != null) map.put("compte_destinataire", compteDestinataire);
        if (utilisateurId != null) map.put("utilisateur_id", utilisateurId);
        if (statutCode != null) map.put("statut_code", statutCode);
        if (changeId != null) map.put("change_id", changeId);

        return map;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        return getInsertValues(); // Même logique
    }

    public static Optional<Virement> findById(Connection conn, int id)
            throws SQLException {
        return BaseEntity.findById(conn, Virement::new, id);
    }

    public static List<Virement> findAll(Connection conn) throws SQLException {
        return BaseEntity.findAll(conn, Virement::new);
    }
}