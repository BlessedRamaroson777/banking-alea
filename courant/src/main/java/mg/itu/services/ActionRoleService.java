package mg.itu.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mg.itu.database.utils.DB;
import mg.itu.models.ActionRole;

public class ActionRoleService {

    /**
     * Récupérer tous les ActionRole accessibles pour un niveau d'utilisateur donné
     * (roleMinimum <= niveauUtilisateur)
     */
    public List<ActionRole> getByNiveauUtilisateur(Integer niveauUtilisateur) throws Exception {
        List<ActionRole> actionRoles = new ArrayList<>();
        String sql = "SELECT id, nom_table, nom_action, role_minimum FROM actions_roles WHERE role_minimum <= ? ORDER BY role_minimum";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, niveauUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActionRole role = new ActionRole();
                    role.setId(rs.getInt("id"));
                    role.setNomTable(rs.getString("nom_table"));
                    role.setNomAction(rs.getString("nom_action"));
                    role.setRoleMinimum(rs.getInt("role_minimum"));
                    actionRoles.add(role);
                }
            }
        }
        
        return actionRoles;
    }

    /**
     * Récupérer tous les ActionRole
     */
    public List<ActionRole> getAll() throws Exception {
        List<ActionRole> actionRoles = new ArrayList<>();
        String sql = "SELECT id, nom_table, nom_action, role_minimum FROM actions_roles ORDER BY role_minimum";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActionRole role = new ActionRole();
                    role.setId(rs.getInt("id"));
                    role.setNomTable(rs.getString("nom_table"));
                    role.setNomAction(rs.getString("nom_action"));
                    role.setRoleMinimum(rs.getInt("role_minimum"));
                    actionRoles.add(role);
                }
            }
        }
        
        return actionRoles;
    }
}
