package mg.itu.models.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * BaseEntity fournit des opérations CRUD génériques basées sur JDBC pour les modèles.
 * Les sous-classes doivent fournir le nom de la table, le mapping ResultSet -> entité
 * et les valeurs à insérer/mettre à jour via des LinkedHashMap (ordre conservé).
 * <p>
 * Contrat à implémenter dans les sous-classes:
 * - getTableName(): nom de la table (ex: "comptes")
 * - getId()/setId(): identifiant primaire (par défaut colonne "id")
 * - mapRow(ResultSet): construit l'entité à partir d'une ligne
 * - getInsertValues(): colonnes/valeurs pour INSERT (sans id)
 * - getUpdateValues(): colonnes/valeurs pour UPDATE (sans id)
 */
public abstract class BaseEntity<T extends BaseEntity<T>> {

    protected String getIdColumn() {
        return "id";
    }

    protected abstract String getTableName();

    public abstract Integer getId();

    public abstract void setId(Integer id);

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract LinkedHashMap<String, Object> getInsertValues();

    protected abstract LinkedHashMap<String, Object> getUpdateValues();

    /**
     * Insère (si id null) ou met à jour (si id non null) l'entité.
     */
    @SuppressWarnings("unchecked")
    public T save(Connection conn) throws SQLException {
        if (getId() == null) {
            // INSERT ... RETURNING id
            LinkedHashMap<String, Object> values = getInsertValues();

            if (values == null || values.isEmpty())
                throw new IllegalStateException("getInsertValues() doit retourner au moins une colonne");

            StringBuilder cols = new StringBuilder();
            StringBuilder qs = new StringBuilder();
            int i = 0;
            for (String col : values.keySet()) {
                if (i++ > 0) {
                    cols.append(", ");
                    qs.append(", ");
                }
                cols.append(col);
                qs.append("?");
            }
            String sql = "INSERT INTO " + getTableName() + " (" + cols + ") VALUES (" + qs + ") RETURNING " + getIdColumn();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bindAll(ps, values);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        setId(rs.getInt(1));
                }
            }

            return (T) this;
        } else {
            // UPDATE ... WHERE id=?
            LinkedHashMap<String, Object> values = getUpdateValues();
            if (values == null || values.isEmpty())
                throw new IllegalStateException("getUpdateValues() doit retourner au moins une colonne");

            StringBuilder set = new StringBuilder();
            int i = 0;
            for (String col : values.keySet()) {
                if (i++ > 0) set.append(", ");
                set.append(col).append("=?");
            }
            String sql = "UPDATE " + getTableName() + " SET " + set + " WHERE " + getIdColumn() + " = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int idx = bindAll(ps, values);
                ps.setObject(idx, getId());
                ps.executeUpdate();
            }
            return (T) this;
        }
    }

    /**
     * Supprime l'entité par son id. Retourne true si une ligne a été affectée.
     */
    public boolean delete(Connection conn) throws SQLException {
        if (getId() == null) {
            throw new IllegalStateException("Impossible de supprimer: id null");
        }
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, getId());
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    /**
     * Charge l'entité depuis la base de données en utilisant l'id déjà défini dans l'objet.
     * Les attributs de l'objet courant sont mis à jour avec les valeurs de la base.
     * Retourne true si l'entité a été trouvée et chargée, false sinon.
     */
    @SuppressWarnings("unchecked")
    public boolean findById(Connection conn) throws SQLException {
        if (getId() == null)
            throw new IllegalStateException("Impossible de charger: id null");

        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    T loaded = mapRow(rs);
                    // Copier les attributs de l'entité chargée vers this
                    copyFrom(loaded);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Méthode statique utilitaire pour charger une entité par id.
     * Retourne un Optional contenant l'entité si trouvée.
     */
    public static <T extends BaseEntity<T>> Optional<T> findById(Connection conn, Supplier<T> factory, int id) throws SQLException {
        T proto = factory.get();
        String sql = "SELECT * FROM " + proto.getTableName() + " WHERE " + proto.getIdColumn() + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(proto.mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Copie les attributs d'une autre entité vers celle-ci.
     * Les sous-classes peuvent surcharger cette méthode pour une copie personnalisée.
     * Par défaut, cette méthode suppose que mapRow() retourne une instance correctement initialisée
     * et que les sous-classes implémenteront la copie si nécessaire.
     */
    @SuppressWarnings("unchecked")
    protected void copyFrom(T other) {
        if (other == null) return;
        Class<?> cls = this.getClass();
        while (cls != null && BaseEntity.class.isAssignableFrom(cls)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                int mods = f.getModifiers();
                if (Modifier.isStatic(mods)) continue;
                if (Modifier.isFinal(mods)) continue;
                try {
                    f.setAccessible(true);
                    Object val = f.get(other);
                    f.set(this, val);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Impossible de copier le champ '" + f.getName() + "' de " + cls.getSimpleName(), e);
                }
            }
            cls = cls.getSuperclass();
        }
    }

    /**
     * Retourne toutes les lignes de la table.
     */
    public static <T extends BaseEntity<T>> List<T> findAll(Connection conn, Supplier<T> factory) throws SQLException {
        T proto = factory.get();
        String sql = "SELECT * FROM " + proto.getTableName();
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(proto.mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Recherche avec un WHERE arbitraire et paramètres.
     */
    public static <T extends BaseEntity<T>> List<T> findWhere(Connection conn, Supplier<T> factory, String whereClause, Object... params) throws SQLException {
        T proto = factory.get();
        String sql = "SELECT * FROM " + proto.getTableName() + (whereClause == null || whereClause.isBlank() ? "" : " WHERE " + whereClause);
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    bindParam(ps, i + 1, params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(proto.mapRow(rs));
                }
            }
        }
        return list;
    }

    // Helpers
    private static int bindAll(PreparedStatement ps, LinkedHashMap<String, Object> values) throws SQLException {
        int idx = 1;
        for (Object v : values.values()) {
            bindParam(ps, idx++, v);
        }

        return idx;
    }

    private static void bindParam(PreparedStatement ps, int index, Object v) throws SQLException {
        if (v == null)
            ps.setObject(index, null);
        else if (v instanceof BigDecimal bd)
            ps.setBigDecimal(index, bd);
        else if (v instanceof Integer i)
            ps.setInt(index, i);
        else if (v instanceof Long l)
            ps.setLong(index, l);
        else if (v instanceof String s)
            ps.setString(index, s);
        else if (v instanceof Boolean b)
            ps.setBoolean(index, b);
        else if (v instanceof LocalDateTime ldt)
            ps.setTimestamp(index, Timestamp.valueOf(ldt));
        else if (v instanceof java.sql.Timestamp ts)
            ps.setTimestamp(index, ts);
        else if (v instanceof java.sql.Date d)
            ps.setDate(index, d);
        else if (v instanceof java.util.Date ud)
            ps.setTimestamp(index, new Timestamp(ud.getTime()));
        else
            ps.setObject(index, v);
    }
}
