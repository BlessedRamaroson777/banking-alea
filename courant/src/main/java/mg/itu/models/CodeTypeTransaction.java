package mg.itu.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import mg.itu.database.utils.DB;
import mg.itu.models.base.BaseEntity;

public class CodeTypeTransaction extends BaseEntity<CodeTypeTransaction> {
    private Integer id;
    private String libelle;

    public CodeTypeTransaction() {
    }

    public CodeTypeTransaction(int id, String libelle) {
        setId(id);
        setLibelle(libelle);
    }

    public CodeTypeTransaction(String libelle) {
        setLibelle(libelle);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public CodeTypeTransaction findByLibelle() throws SQLException {
        String sql = "SELECT * FROM codes_types_transactions WHERE libelle = ? LIMIT 1";

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql)) {
            ps.setString(1, this.libelle);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.setId(rs.getInt("id"));
                    this.setLibelle(rs.getString("libelle"));

                    return this;
                }
            }
        }

        return null;
    }

    @Override
    protected String getTableName() {
        return "codes_types_transactions";
    }

    @Override
    protected CodeTypeTransaction mapRow(ResultSet rs) throws SQLException {
        CodeTypeTransaction codeType = new CodeTypeTransaction();
        codeType.setId(rs.getInt("id"));
        codeType.setLibelle(rs.getString("libelle"));

        return codeType;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("libelle", libelle);

        return values;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("libelle", libelle);

        return values;
    }

    @Override
    protected void copyFrom(CodeTypeTransaction other) {
        this.id = other.id;
        this.libelle = other.libelle;
    }
}