package mg.itu.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import mg.itu.models.base.BaseEntity;

public class Utilisateur extends BaseEntity<Utilisateur> {
    private Integer id;
    private String nom;
    private String motDePasse;
    private int niveau;

    public Utilisateur() {
    }

    public Utilisateur(Integer id, String nom, String motDePasse, int niveau) {
        setId(id);
        setNom(nom);
        setMotDePasse(motDePasse);
        setNiveau(niveau);
    }

    public Utilisateur(String nom, String motDePasse) {
        setNom(nom);
        setMotDePasse(motDePasse);
    }


    public boolean roleSuffisant(List<ActionRole> actionRoles, String nomTable, String action) {
        for (ActionRole actionRole : actionRoles) {
            if (actionRole.getNomTable().equalsIgnoreCase(nomTable) && actionRole.getNomAction().equalsIgnoreCase(action)
                    && niveau >= actionRole.getRoleMinimum())
                return true;
        }

        return false;
    }

    private List<Utilisateur> getByName(Connection conn, String nom) {
        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = "SELECT * FROM utilisateurs WHERE nom = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(
                            new Utilisateur(
                                    rs.getInt("id"),
                                    rs.getString("nom"),
                                    rs.getString("mot_de_passe"),
                                    rs.getInt("niveau")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return utilisateurs;
    }

    public void authenticate(Connection connection) {
        List<Utilisateur> utilisateurs = getByName(connection, this.nom);

        if (utilisateurs.isEmpty())
            return;

        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur.getMotDePasse().equals(motDePasse)) {
                setId(utilisateur.getId());
                setNom(utilisateur.getNom());
                setMotDePasse(utilisateur.getMotDePasse());
                setNiveau(utilisateur.getNiveau());

                break;
            }
        }
    }

    public boolean findById(Connection conn) {
        // Utilise un alias pour harmoniser le nom de colonne mot_de_passe -> motDePasse
        String sql = "SELECT id, nom, mot_de_passe, niveau FROM utilisateurs WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    setId(rs.getInt("id"));
                    setNom(rs.getString("nom"));
                    setMotDePasse(rs.getString("mot_de_passe"));
                    setNiveau(rs.getInt("niveau"));
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    protected String getTableName() {
        return "utilisateurs";
    }

    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    protected Utilisateur mapRow(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id"));
        utilisateur.setNom(rs.getString("nom"));
        try {
            utilisateur.setMotDePasse(rs.getString("motDePasse"));
        } catch (SQLException ex) {
            // fallback si la colonne est en snake_case
            utilisateur.setMotDePasse(rs.getString("mot_de_passe"));
        }
        utilisateur.setNiveau(rs.getInt("niveau"));

        return utilisateur;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("nom", nom);
        values.put("mot_de_passe", motDePasse);
        values.put("niveau", niveau);

        return values;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("nom", nom);
        values.put("mot_de_passe", motDePasse);
        values.put("niveau", niveau);

        return values;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
}