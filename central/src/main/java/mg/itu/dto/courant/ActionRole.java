package mg.itu.dto.courant;

/**
 * DTO pour l'entité ActionRole du module Courant
 */
public class ActionRole {
    private int id;
    private String nomTable;
    private String nomAction;
    private Integer roleMinimum;

    public ActionRole() {
    }

    public ActionRole(int id, String nomTable, String nomAction, Integer roleMinimum) {
        this.id = id;
        this.nomTable = nomTable;
        this.nomAction = nomAction;
        this.roleMinimum = roleMinimum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomTable() {
        return nomTable;
    }

    public void setNomTable(String nomTable) {
        this.nomTable = nomTable;
    }

    public String getNomAction() {
        return nomAction;
    }

    public void setNomAction(String nomAction) {
        this.nomAction = nomAction;
    }

    public Integer getRoleMinimum() {
        return roleMinimum;
    }

    public void setRoleMinimum(Integer roleMinimum) {
        this.roleMinimum = roleMinimum;
    }

    @Override
    public String toString() {
        return "ActionRole{" +
                "id=" + id +
                ", nomTable='" + nomTable + '\'' +
                ", nomAction='" + nomAction + '\'' +
                ", roleMinimum=" + roleMinimum +
                '}';
    }
}
