/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import mvc_bateau.infra.exception.DBException;
import mvc_bateau.infra.jdbc.JdbcHelper;
import mvc_bateau.infra.misc.StringUtils;
import mvc_bateau.infra.model.BaseModel;

/**
 *
 * @author macbook
 * @param <T>
 */
public class BaseDBDAO<T extends BaseModel> implements IBaseDBDAO<T> {
    
    public final static String DATE_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private final String tableName;
    
    private String DB_SELECT;
    private String DB_INSERT;
    private String DB_UPDATE;
    private String DB_UPDATE_ID;
    private String DB_DELETE;
    
    public BaseDBDAO(String pTableName) {
        tableName = pTableName;
    }
    
    protected Connection getConn() {
        return JdbcHelper.getInstance().getConn();
    }
    
    @Override
    public boolean existsById(long id) {
        boolean result = Boolean.FALSE;
        try {
            String query = String.format("SELECT COUNT(*) > 0 FROM %s ", getTableName());
            Statement stmt =  getConn().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                    result = rs.getBoolean(1);
            }
            // Close ressources
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL : "+ e.getMessage());
            throw new DBException(e);
        }
        return result;
    }
    
    @Override
    public <X> List<Long> selectIdsBy(String col, X val) {
        // Concaténation des IDs. Ex 1,2
        List<Long> res = new ArrayList<>();
        try {
            String query = String.format(
                            "SELECT id FROM %s WHERE %s = ?", 
                            tableName, 
                            col);
            PreparedStatement pstmt =  getConn().prepareStatement(query);
            setParameter(pstmt, 1, val);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                    res.add(rs.getLong(1));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL : "+ e.getMessage());
        } 
        return res;
    }
    
    @Override
    public long create(T model) throws SQLException {
        PreparedStatement pstmt = getConn().prepareStatement(getDbInsert());
        pstmt = setParameters(pstmt, model);
        pstmt.executeUpdate();
        pstmt.close();
        model.setId(getLastId());
        return model.getId();
    }
    
    @Override
    public long create(long id, T model) throws SQLException {
        update(create(model), id);
        return id;
    }
    
    @Override
    public void create(List<T> models) throws SQLException {
        for(T model : models) {
                model.setId(create(model));
        }
    }
	
    @Override
    public void update(long id, T model) throws SQLException {
        PreparedStatement pstmt = getConn().prepareStatement(getDbUpdate());
        pstmt = setParameters(pstmt, model);
        Field[] fields = model.getPersistFields();
        pstmt.setLong(fields.length + 1, id);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    @Override
    public <X> void updateByIds(String col, X val, Long... ids) throws SQLException {
        String query = String.format(
                        "UPDATE %s SET %s = ? WHERE id IN (%s)",
                        tableName,
                        col,
                        createInElements(Arrays.asList(ids)));
        PreparedStatement pstmt = getConn().prepareStatement(query);
        pstmt = setParameter(pstmt, 1, val);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    @Override
    public <X> void update(String col, X oldVal, X newVal) throws SQLException {
            updateByIds(col, newVal, selectIdsBy(col, oldVal).toArray(new Long[0]));
    }
    
    @Override
    public void update(long oldId, long newId) {
        // MAJ l'id
        try {
            PreparedStatement pstmt = getConn().prepareStatement(getDbUpdateId());
            pstmt.setLong(1, newId);
            pstmt.setLong(2, oldId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(String.format("--> oldId = %s, newId = %s", oldId, newId));
    }
    
    @Override
    public void update(T model) throws SQLException {
        update(model.getId(), model);
    }
    
    @Override
    public void update(List<T> models) throws SQLException {
        for(T model : models) {
            update(model);
        }
    }
    
    @Override
    public void delete(long id) throws SQLException {
        PreparedStatement pstmt = getConn().prepareStatement(getDbDelete());
        pstmt.setLong(1, id);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    @Override
    public void delete(List<Long> ids) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE id IN (%s)", getTableName(), createInElements(ids));
        Statement stmt = getConn().createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    @Override
    public boolean isNew(T model) {
            return selectById(model.getId()) == null;
    }
    
    @Override
    public T selectById(long id) {
            List<T> list = selectByIds(Arrays.asList(id));
            return list.isEmpty()?null:list.get(0);
    }
    
    @Override
    public List<T> selectByIds(List<Long> ids) {
        // Concaténation des IDs. Ex 1,2
        String stringIds = createInElements(ids);
        List<T> result = new ArrayList<>();
        try {
            if (!stringIds.isEmpty()) {
                String query = String.format("%s WHERE id IN (%s)", getDbSelect(), stringIds);
                Statement stmt =  getConn().createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                        result.add(resultSetToModel(rs));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : {}"+ e.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
    
    @Override
    public long countAll() {
        long result = 0L;
        try {
            String query = String.format("SELECT COUNT(*) FROM %s ", getTableName());
            Statement stmt =  getConn().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                    result = rs.getLong(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL : {}"+ e.getMessage());
        }
        return result;
    }
    
    @Override
    public List<T> selectAll() {
        List<T> result = new ArrayList<>();
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery(getDbSelect()+" ORDER BY "+defaultSort());
            // Boucle sur le set de résultat
            while (rs.next()) {
                result.add(resultSetToModel(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL : {}"+ e.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
    
    protected String defaultSort() {
        return "id DESC";
    }
    
    @Override
    public boolean isTableEmpty() {
        return countAll() == 0L;
    }
    
    /*
    *
    * TOOLS
    *
    */
    public Long getLastId() throws SQLException {
        System.out.println(getLastIdQuery());
        PreparedStatement pstmt = getConn().prepareStatement(getLastIdQuery());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            long newId = rs.getLong("newid");
            rs.close();
            pstmt.close();
            return newId;
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T resultSetToModel(ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
        T model = getModelClass().newInstance();
        model.setId((long) rs.getInt("id"));
        Field[] persistFields = model.getPersistFields();
        Field f;
        for (Field persistField : persistFields) {
            f = persistField;
            if (Integer.class == f.getType()) {
                setFieldVal(f, model, rs.getInt(getFieldColumn(f)));
                //model.getFields()[i] = rs.getInt(fieldsNames[i]);
            } else if (Long.class == f.getType()) {
                setFieldVal(f, model, rs.getLong(getFieldColumn(f)));
                //model.getFields()[i] = rs.getLong(fieldsNames[i]);
            } else if (Double.class == f.getType()) {
                setFieldVal(f, model, rs.getDouble(getFieldColumn(f)));
                //model.getFields()[i] = rs.getDouble(fieldsNames[i]);
            } else if (Float.class == f.getType()) {
                setFieldVal(f, model, rs.getFloat(getFieldColumn(f)));
                //model.getFields()[i] = rs.getFloat(fieldsNames[i]);
            } else if (String.class == f.getType()) {
                setFieldVal(f, model, rs.getString(getFieldColumn(f)));
                //model.getFields()[i] = rs.getString(fieldsNames[i]);
            } else if (Date.class == f.getType()) {
                java.sql.Date d = rs.getDate(getFieldColumn(f));
                setFieldVal(f, model, new Date(d.getTime()));
//                if(!StringUtils.isBlank(s)) {
//                    setFieldVal(f, model, stringToDate(s));
//                    //model.getFields()[i] = stringToDate(s);
//                }
            } else if (Boolean.class == f.getType()) {
                setFieldVal(f, model, rs.getBoolean(getFieldColumn(f)));
                //model.getFields()[i] = rs.getBoolean(fieldsNames[i]);
            } else if (f.getType().isEnum()) {
                String s = rs.getString(getFieldColumn(f));
                if(!StringUtils.isBlank(s)) {
                    setFieldVal(f, model, Enum.valueOf((Class) f.getType(), s));
                    //model.getFields()[i] = Enum.valueOf((Class)fieldsClasses[i], s);
                }
            }
            if(rs.wasNull()) {
                // Null value
                setFieldVal(f, model, null);
                //model.getFields()[i] = null;
            }
        }
        System.out.println(String.format("Id = %s, Fields = %s", model.getId(), Arrays.toString(model.getPersistFieldsVal())));
        return model;
    }
    
    protected String getFieldColumn(Field f) {
        if(f.isAnnotationPresent(Column.class)) {
            return f.getAnnotation(Column.class).value();
        } else {
            return f.getName();
        }
    }
    
    private Object getFieldVal(Field f, Object ref) {
        f.setAccessible(true);
        try {
            Object res = f.get(ref);
            f.setAccessible(false);
            return res;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(BaseDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void setFieldVal(Field f, Object ref, Object val) {
        f.setAccessible(true);
        try {
            f.set(ref, val);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(BaseDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        f.setAccessible(false);
    }
    
    @SuppressWarnings("unchecked")
    public Class<T> getModelClass() {
            return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }
        
    private PreparedStatement setParameters(PreparedStatement pstmt, T model) throws SQLException {
        Field[] fields = model.getPersistFields();
        //Object[] fields = model.getFields();
        for (int i = 0; i < fields.length; i++) {
                pstmt = setParameter(pstmt, i + 1, fields[i].getType(), getFieldVal(fields[i], model));
        }
        return pstmt;
    }
    
    private <X> PreparedStatement setParameter(PreparedStatement pstmt, int index, X field) throws SQLException {
        return setParameter(
                        pstmt, 
                        index, 
                        field != null ? field.getClass() : null,
                        field);
    }
	
    private <X> PreparedStatement setParameter(PreparedStatement pstmt, int index, Class<?> fieldClass, X field) throws SQLException {
        if(field == null) {
                //pstmt.setObject(i + 1, null);
                pstmt.setNull(index, Types.NULL);
        } else {
            if (Integer.class == fieldClass) {
                pstmt.setInt(index, (Integer) field);
            } else if (Long.class == fieldClass) {
                pstmt.setLong(index, (Long) field);
            } else if (Double.class == fieldClass) {
                pstmt.setDouble(index, (Double) field);
            } else if (Float.class == fieldClass) {
                pstmt.setFloat(index, (Float) field);
            } else if (String.class == fieldClass) {
                pstmt.setString(index, (String) field);
            } else if (Date.class == fieldClass) {
                //pstmt.setString(index, dateToString((Date) field));
                pstmt.setTimestamp(index, new java.sql.Timestamp(((Date)field).getTime()));
                //pstmt.setDate(index, new java.sql.Date(((Date)field).getTime()));
            } else if (Boolean.class == fieldClass) {
                pstmt.setBoolean(index, (Boolean)field);
            } else if (fieldClass.isEnum()) {
                pstmt.setString(index, ((Enum<?>)field).name());
            }
        }
        return pstmt;
    }
    
//    protected String dateToString(Date d) {
//           return new SimpleDateFormat(DATE_STRING_FORMAT).format(d);
//    }
//    
//    protected Date stringToDate(String s) {
//        try {
//            return StringUtils.isBlank(s) ? null : new SimpleDateFormat(DATE_STRING_FORMAT).parse(s);
//        } catch (ParseException e) {
//            System.err.println(e.getMessage());
//            return null;
//        }
//    }
    
    public String getLastIdQuery() {
        return "SELECT MAX(id) newid FROM " + tableName;
    }
    
    public String getDbSelect(String alias) {
        if (DB_SELECT == null) {
            String alias2 = StringUtils.isBlank(alias) ? "" :  alias+".";
            String fields = "";
            try {
                T model = getModelClass().newInstance();
                Field[] fieldsReflect = model.getPersistFields();
                for (int i = 0; i < fieldsReflect.length; i++) {
                    fields += alias2 + getFieldColumn(fieldsReflect[i]); //fieldsNames[i];

                    if (i != fieldsReflect.length - 1) {
                            fields += ", ";
                    }
                }

                //DB_SELECT = "SELECT id, " + fields + " FROM " + tableName + ";";
                DB_SELECT = String.format("SELECT %sid, ", alias2) + fields + " FROM " + tableName + " "+alias+" ";
                System.out.println(DB_SELECT);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(BaseDBDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return DB_SELECT;
    }
    
    /**
    * Requête d'insertion dans la BD
    * @return
    */
    public String getDbInsert() {
        if (DB_INSERT == null) {
            String fields = "";
            String values = "";
            
            try {
                T model = getModelClass().newInstance();
                Field[] fieldsReflect = model.getPersistFields();
                for (int i = 0; i < fieldsReflect.length; i++) {
                    fields += getFieldColumn(fieldsReflect[i]);
                    values += "?";
                    if (i != fieldsReflect.length - 1) {
                        fields += ",";
                        values += ",";
                    }
                }

                DB_INSERT = "INSERT INTO " + tableName + "(" + fields + ") VALUES(" + values + ")";
                System.out.println(DB_INSERT);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(BaseDBDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return DB_INSERT;
    }
    
    /**
    * Requête de MAJ de l'ID dans la table avec ID spécifié
    * @return
    */
    public String getDbUpdateId() {
        //if (DB_UPDATE == null) {
            DB_UPDATE = "UPDATE " + tableName + " SET id=? WHERE id=?";
            System.out.println(DB_UPDATE);
        //}
        return DB_UPDATE;
    }
    
    /**
        * Requête de sélection des données de la BD
        * @return
    */
   public String getDbSelect() {
        return getDbSelect("");
   }
    
    /**
        * Requête de MAJ des données dans la table avec ID spécifié
        * @return
    */
   public String getDbUpdate() {
       if (DB_UPDATE_ID == null) {
           String fields = "";
           
           try {
                T model = getModelClass().newInstance();
                Field[] fieldsReflect = model.getPersistFields();
                for (int i = 0; i < fieldsReflect.length; i++) {
                        fields += " " + getFieldColumn(fieldsReflect[i]) + "=?";
                        if (i != fieldsReflect.length - 1) {
                                fields += ",";
                        }
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(BaseDBDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
           DB_UPDATE_ID = "UPDATE " + tableName + " SET " + fields + " WHERE id=?";
           System.out.println(DB_UPDATE_ID);
       }
       return DB_UPDATE_ID;
   }
   
   public String getDbDelete() {
        if (DB_DELETE == null) {
            DB_DELETE = "DELETE FROM " + tableName + " WHERE id=?";
            System.out.println(DB_DELETE);
        }
        return DB_DELETE;
    }
   
    protected String createInElements(List<Long> elts) {
        return String.join(",", elts.stream().map((id)-> id.toString()).collect(Collectors.toList()));
    }
    
    protected String createInWildCardElements(int nb) {
        if(nb > 0) {
                String s = "?";
                for(int i = 1; i < nb; i++) {
                        s += ", ?";
                }
                return s;
        }
        return "";
    }
    
    /*
    *
    * GETTERS & SETTERS
    *
    */
    public String getTableName() {
        return tableName;
    }
    
    
}
