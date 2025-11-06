package mg.itu.database.dao;

import mg.itu.database.utils.DB;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe générique pour les opérations CRUD sur n'importe quelle entité
 * Couche Persistance (DAO - Data Access Object)
 * Les entités héritent directement de cette classe
 * 
 * @param <T> Type de l'entité
 */
public abstract class GenericDAO<T> {
    protected Class<T> entityClass;
    protected String tableName;
    
    @SuppressWarnings("unchecked")
    public GenericDAO() {
        this.entityClass = (Class<T>) getClass();
        this.tableName = getTableName();
    }
    
    /**
     * Récupère le nom de la table depuis l'annotation @Entity ou depuis le nom de la classe
     */
    private String getTableName() {
        if (entityClass.isAnnotationPresent(Entity.class)) {
            Entity entity = entityClass.getAnnotation(Entity.class);
            String name = entity.tableName();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return NamingUtils.camelToSnake(entityClass.getSimpleName());
    }
    
    /**
     * CREATE - Insère une ligne et retourne l'objet créé avec l'ID généré
     * Structure : Contrôle → Calcul/Métier → Persistance
     * Note: Les noms de colonnes sont automatiquement convertis en snake_case
     */
    public T create() throws SQLException {
        // 1. CONTRÔLE - Validation des données
        this.validate();
        
        // 2. MÉTIER - Préparation avant sauvegarde
        this.beforeCreate();
        
        // 3. PERSISTANCE - Insertion en base
        @SuppressWarnings("unchecked")
        T obj = (T) this;
        Connection conn = DB.getConnection();
        // getFieldValues() retourne déjà les noms en snake_case
        Map<String, Object> fieldValues = getFieldValues(obj);
        
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String columnName = entry.getKey(); // Déjà en snake_case
            Object value = entry.getValue();
            
            // Ignorer l'ID lors de l'insertion (auto-généré)
            // columnName est déjà en snake_case, donc "id" reste "id"
            if (columnName.equalsIgnoreCase("id") && (value == null || (value instanceof Number && ((Number) value).intValue() == 0))) {
                continue;
            }
            
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(columnName); // Utilise directement le nom en snake_case
            placeholders.append("?");
            values.add(value);
        }
        
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING *", 
                                   tableName, columns, placeholders);
        
        System.out.println("[DEBUG CREATE] SQL: " + sql);
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                T created = mapResultSetToObject(rs);
                // Hook après création
                if (created instanceof GenericDAO) {
                    ((GenericDAO<?>) created).afterCreate();
                }
                return created;
            }
        }
        
        return null;
    }
    
    /**
     * READ - Récupère une ligne par son identifiant
     * Structure : Contrôle → Persistance
     */
    public static <T extends GenericDAO<T>> T getById(Class<T> clazz, int id) throws SQLException {
        // 1. CONTRÔLE
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID doit être positif");
        }
        
        // 2. PERSISTANCE
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Connection conn = DB.getConnection();
            String sql = String.format("SELECT * FROM %s WHERE id = ?", instance.tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return instance.mapResultSetToObject(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erreur lors de la récupération", e);
        }
        
        return null;
    }
    
    /**
     * UPDATE - Met à jour une ligne et retourne l'objet mis à jour
     * Structure : Contrôle → Métier → Persistance
     * Note: Les noms de colonnes sont automatiquement convertis en snake_case
     */
    public T update() throws SQLException {
        // 1. CONTRÔLE - Validation
        this.validate();
        
        // 2. MÉTIER - Préparation avant mise à jour
        this.beforeUpdate();
        
        // 3. PERSISTANCE
        @SuppressWarnings("unchecked")
        T obj = (T) this;
        Connection conn = DB.getConnection();
        // getFieldValues() retourne déjà les noms en snake_case
        Map<String, Object> fieldValues = getFieldValues(obj);
        
        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();
        Object idValue = null;
        
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String columnName = entry.getKey(); // Déjà en snake_case
            Object value = entry.getValue();
            
            // Extraire l'ID (columnName est déjà en snake_case, donc "id" reste "id")
            if (columnName.equalsIgnoreCase("id")) {
                idValue = value;
                continue;
            }
            
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(columnName).append(" = ?"); // Utilise directement le nom en snake_case
            values.add(value);
        }
        
        if (idValue == null || (idValue instanceof Number && ((Number) idValue).intValue() <= 0)) {
            throw new SQLException("L'objet doit avoir un ID valide pour être mis à jour");
        }
        
        values.add(idValue);
        
        String sql = String.format("UPDATE %s SET %s WHERE id = ? RETURNING *", 
                                   tableName, setClause);
        
        System.out.println("[DEBUG UPDATE] SQL: " + sql);
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                T updated = mapResultSetToObject(rs);
                // Hook après mise à jour
                if (updated instanceof GenericDAO) {
                    ((GenericDAO<?>) updated).afterUpdate();
                }
                return updated;
            }
        }
        
        return null;
    }
    
    /**
     * DELETE - Supprime une ligne
     * Structure : Contrôle → Métier → Persistance
     */
    public boolean delete() throws SQLException {
        // 1. CONTRÔLE
        @SuppressWarnings("unchecked")
        T obj = (T) this;
        Field idField = getIdField();
        if (idField == null) {
            throw new SQLException("Aucun champ 'id' trouvé");
        }
        
        idField.setAccessible(true);
        try {
            Object idValue = idField.get(obj);
            if (idValue == null || (idValue instanceof Number && ((Number) idValue).intValue() <= 0)) {
                throw new IllegalArgumentException("L'ID doit être valide pour supprimer");
            }
            
            // 2. MÉTIER - Hook avant suppression
            this.beforeDelete();
            
            // 3. PERSISTANCE
            Connection conn = DB.getConnection();
            String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, idValue);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Hook après suppression
                    this.afterDelete();
                    return true;
                }
                return false;
            }
        } catch (IllegalAccessException e) {
            throw new SQLException("Erreur d'accès au champ ID", e);
        }
    }
    
    /**
     * DELETE statique - Supprime par ID
     */
    public static <T extends GenericDAO<T>> boolean deleteById(Class<T> clazz, int id) throws SQLException {
        T instance = getById(clazz, id);
        if (instance != null) {
            return instance.delete();
        }
        return false;
    }
    
    /**
     * GET ALL - Récupère toutes les lignes avec des filtres optionnels
     * Structure : Contrôle → Persistance
     * 
     * Exemples d'utilisation des filtres :
     * - Égalité simple : Map.of("code", "USD") → WHERE code = 'USD'
     * - Avec opérateur : Map.of("num <=", 4) → WHERE num <= 4
     * - LIKE : Map.of("code LIKE", "%USD%") → WHERE code LIKE '%USD%'
     * - Comparaison : Map.of("cours >", 100.0) → WHERE cours > 100.0
     * - IN : Map.of("id IN", "(1,2,3)") → WHERE id IN (1,2,3)
     * - IS NULL : Map.of("date_fin IS", null) ou Map.of("date_fin IS NULL", null)
     * 
     * @param filters Map où la clé peut contenir le nom du champ + opérateur (ex: "num <=", "code LIKE")
     *                Si pas d'opérateur, utilise = par défaut
     */
    public static <T extends GenericDAO<T>> List<T> getAll(Class<T> clazz, Map<String, Object> filters, 
                                                             String orderBy, Integer limit, Integer offset) throws SQLException {
        // 1. CONTRÔLE
        if (limit != null && limit < 0) {
            throw new IllegalArgumentException("La limite doit être positive");
        }
        if (offset != null && offset < 0) {
            throw new IllegalArgumentException("L'offset doit être positif");
        }
        
        // 2. PERSISTANCE
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Connection conn = DB.getConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM " + instance.tableName);
            List<Object> values = new ArrayList<>();
            
            // Ajouter les filtres WHERE
            if (filters != null && !filters.isEmpty()) {
                sql.append(" WHERE ");
                boolean first = true;
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    if (!first) {
                        sql.append(" AND ");
                    }
                    
                    String key = entry.getKey().trim();
                    Object value = entry.getValue();
                    
                    // Split la clé par le premier espace pour séparer nom de colonne et opérateur
                    String[] parts = key.split("\\s+", 2);
                    String columnName = NamingUtils.camelToSnake(parts[0]);
                    String operator = parts.length > 1 ? parts[1].toUpperCase() : "=";
                    
                    // Construire la condition SQL
                    sql.append(columnName).append(" ").append(operator);
                    
                    // Ajouter la valeur si nécessaire (sauf pour IS NULL / IS NOT NULL)
                    if (!operator.equals("IS NULL") && !operator.equals("IS NOT NULL")) {
                        if (operator.equals("IN")) {
                            // Pour IN, la valeur doit être directement insérée (ex: "(1,2,3)")
                            sql.append(" ").append(value);
                        } else {
                            sql.append(" ?");
                            values.add(value);
                        }
                    }
                    
                    first = false;
                }
            }
            
            // Ajouter ORDER BY
            if (orderBy != null && !orderBy.isEmpty()) {
                sql.append(" ORDER BY ").append(NamingUtils.camelToSnake(orderBy));
            }
            
            // Ajouter LIMIT
            if (limit != null && limit > 0) {
                sql.append(" LIMIT ").append(limit);
            }
            
            // Ajouter OFFSET
            if (offset != null && offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
            
            System.out.println("[DEBUG GETALL] SQL: " + sql.toString());
            
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                ResultSet rs = stmt.executeQuery();
                List<T> results = new ArrayList<>();
                
                while (rs.next()) {
                    results.add(instance.mapResultSetToObject(rs));
                }
                
                return results;
            }
        } catch (Exception e) {
            throw new SQLException("Erreur lors de la récupération", e);
        }
    }
    
    /**
     * GET ALL - Version simplifiée sans filtres
     */
    public static <T extends GenericDAO<T>> List<T> getAll(Class<T> clazz) throws SQLException {
        return getAll(clazz, null, null, null, null);
    }
    
    // ========== HOOKS MÉTIER (à surcharger dans les entités) ==========
    
    /**
     * Validation des données - à surcharger dans chaque entité
     */
    protected void validate() throws SQLException {
        // À implémenter dans les classes filles
    }
    
    /**
     * Hook avant création - logique métier avant insertion
     */
    protected void beforeCreate() throws SQLException {
        // À surcharger si nécessaire
    }
    
    /**
     * Hook après création
     */
    protected void afterCreate() throws SQLException {
        // À surcharger si nécessaire
    }
    
    /**
     * Hook avant mise à jour
     */
    protected void beforeUpdate() throws SQLException {
        // À surcharger si nécessaire
    }
    
    /**
     * Hook après mise à jour
     */
    protected void afterUpdate() throws SQLException {
        // À surcharger si nécessaire
    }
    
    /**
     * Hook avant suppression
     */
    protected void beforeDelete() throws SQLException {
        // À surcharger si nécessaire
    }
    
    /**
     * Hook après suppression
     */
    protected void afterDelete() throws SQLException {
        // À surcharger si nécessaire
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Récupère les valeurs des champs de l'objet
     */
    private Map<String, Object> getFieldValues(T obj) {
        Map<String, Object> fieldValues = new HashMap<>();
        Field[] fields = entityClass.getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String columnName = NamingUtils.camelToSnake(field.getName());
                Object value = field.get(obj);
                fieldValues.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return fieldValues;
    }
    
    /**
     * Récupère le champ ID
     */
    private Field getIdField() {
        try {
            return entityClass.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
    
    /**
     * Récupère l'ID de l'objet
     */
    public int getId() throws SQLException {
        Field idField = getIdField();
        if (idField == null) {
            throw new SQLException("Aucun champ 'id' trouvé");
        }
        idField.setAccessible(true);
        try {
            Object value = idField.get(this);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return 0;
        } catch (IllegalAccessException e) {
            throw new SQLException("Erreur d'accès au champ ID", e);
        }
    }
    
    /**
     * Convertit un ResultSet en objet de type T
     * Conversion automatique camelCase (Java) -> snake_case (SQL)
     */
    protected T mapResultSetToObject(ResultSet rs) throws SQLException {
        try {
            T obj = entityClass.getDeclaredConstructor().newInstance();
            Field[] fields = entityClass.getDeclaredFields();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Récupérer tous les noms de colonnes disponibles dans le ResultSet
            Map<String, String> availableColumns = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i).toLowerCase();
                availableColumns.put(columnName, columnName);
            }
            
            for (Field field : fields) {
                field.setAccessible(true);
                // Conversion camelCase -> snake_case
                String columnName = NamingUtils.camelToSnake(field.getName());
                
                // Vérifier si la colonne existe dans le ResultSet
                if (!availableColumns.containsKey(columnName.toLowerCase())) {
                    continue; // Ignorer les champs qui n'ont pas de colonne correspondante
                }
                
                try {
                    Object value = rs.getObject(columnName);
                    if (value != null) {
                        // Conversion de type si nécessaire
                        if (field.getType() == int.class || field.getType() == Integer.class) {
                            field.set(obj, rs.getInt(columnName));
                        } else if (field.getType() == long.class || field.getType() == Long.class) {
                            field.set(obj, rs.getLong(columnName));
                        } else if (field.getType() == double.class || field.getType() == Double.class) {
                            field.set(obj, rs.getDouble(columnName));
                        } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                            field.set(obj, rs.getBoolean(columnName));
                        } else if (field.getType() == String.class) {
                            field.set(obj, rs.getString(columnName));
                        } else if (field.getType() == java.time.LocalDate.class) {
                            java.sql.Date sqlDate = rs.getDate(columnName);
                            field.set(obj, sqlDate != null ? sqlDate.toLocalDate() : null);
                        } else if (field.getType() == java.time.LocalDateTime.class) {
                            java.sql.Timestamp timestamp = rs.getTimestamp(columnName);
                            field.set(obj, timestamp != null ? timestamp.toLocalDateTime() : null);
                        } else if (field.getType() == java.math.BigDecimal.class) {
                            field.set(obj, rs.getBigDecimal(columnName));
                        } else {
                            field.set(obj, value);
                        }
                    }
                } catch (SQLException e) {
                    // Erreur lors de la récupération de la colonne, on ignore
                    System.err.println("Erreur lors de la récupération de la colonne '" + columnName + "': " + e.getMessage());
                }
            }
            
            return obj;
        } catch (Exception e) {
            throw new SQLException("Erreur lors de la conversion du ResultSet", e);
        }
    }
}
