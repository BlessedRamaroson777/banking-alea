package mg.itu.models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import mg.itu.database.utils.DB;
import mg.itu.models.base.BaseEntity;

public class ConfigFrais extends BaseEntity<ConfigFrais> {
    private Integer id;
    private Integer typeCompteId;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal fraisMontant;
    private BigDecimal fraisPourcentage;

    public ConfigFrais() {
    }

    public ConfigFrais(int id, Integer typeCompteId, BigDecimal min, BigDecimal max, BigDecimal fraisMontant, BigDecimal fraisPourcentage) {
        setId(id);
        setTypeCompteId(typeCompteId);
        setMin(min);
        setMax(max);
        setFraisMontant(fraisMontant);
        setFraisPourcentage(fraisPourcentage);
    }

    public ConfigFrais getByTypeCompteAndMontant(Integer typeCompteId, BigDecimal montant) {
        String sql = "SELECT * FROM config_frais WHERE type_compte_id = ? AND ? BETWEEN min AND max LIMIT 1";

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, typeCompteId);
            ps.setBigDecimal(2, montant);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.setId(rs.getInt("id"));
                    this.setTypeCompteId(rs.getInt("type_compte_id"));
                    this.setMin(rs.getBigDecimal("min"));
                    this.setMax(rs.getBigDecimal("max"));
                    this.setFraisMontant(rs.getBigDecimal("frais_montant"));
                    this.setFraisPourcentage(rs.getBigDecimal("frais_pourcentage"));

                    return this;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BigDecimal calculerFrais(BigDecimal montant) {
        return fraisMontant.add(montant.multiply(fraisPourcentage));
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeCompteId() {
        return typeCompteId;
    }

    public void setTypeCompteId(Integer typeCompteId) {
        this.typeCompteId = typeCompteId;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        if (min.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("La valeur minimale de la plage doit être positive");

        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        if (max.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("La valeur maximale de la plage doit être positive");

        this.max = max;
    }

    public BigDecimal getFraisMontant() {
        return fraisMontant;
    }

    public void setFraisMontant(BigDecimal fraisMontant) {
        if (fraisMontant.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Le frais en montant doit être positive");

        this.fraisMontant = fraisMontant;
    }

    public BigDecimal getFraisPourcentage() {
        return fraisPourcentage;
    }

    public void setFraisPourcentage(BigDecimal fraisPourcentage) {
        if (fraisPourcentage.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Le frais en montant doit être positive");

        this.fraisPourcentage = fraisPourcentage;
    }

    @Override
    protected String getTableName() {
        return "config_frais";
    }

    @Override
    protected ConfigFrais mapRow(ResultSet rs) throws SQLException {
        ConfigFrais config = new ConfigFrais();
        config.setId(rs.getInt("id"));
        config.setTypeCompteId(rs.getInt("type_compte_id"));
        config.setMin(rs.getBigDecimal("min"));
        config.setMax(rs.getBigDecimal("max"));
        config.setFraisMontant(rs.getBigDecimal("frais_montant"));
        config.setFraisPourcentage(rs.getBigDecimal("frais_pourcentage"));
        return config;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("type_compte_id", typeCompteId);
        values.put("min", min);
        values.put("max", max);
        values.put("frais_montant", fraisMontant);
        values.put("frais_pourcentage", fraisPourcentage);
        return values;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("type_compte_id", typeCompteId);
        values.put("min", min);
        values.put("max", max);
        values.put("frais_montant", fraisMontant);
        values.put("frais_pourcentage", fraisPourcentage);
        return values;
    }

    @Override
    protected void copyFrom(ConfigFrais other) {
        this.id = other.id;
        this.typeCompteId = other.typeCompteId;
        this.min = other.min;
        this.max = other.max;
        this.fraisMontant = other.fraisMontant;
        this.fraisPourcentage = other.fraisPourcentage;
    }
}