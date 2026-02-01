/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mvc_bateau.infra.dao;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author macbook
 */
public interface IBaseDBDAO<T> {
    
    /**
    * Crée un objet avec ID auto généré
    * @param model
    * @return l'ID généré par la BD
    */
   long create(T model) throws SQLException;

   /**
    * Crée un objet avec ID spécifié
    * @param id
    * @param model
    */
   long create(long id, T model) throws SQLException;

   /**
    * Crée des objets avec ID auto généré
    * @param models
    */
   void create(List<T> models) throws SQLException;

   void update(T model) throws SQLException;

   void update(List<T> models) throws SQLException;

   void update(long oldId, long newId);

   void update(long id, T model) throws SQLException;

   void delete(long id) throws SQLException;

   void delete(List<Long> id) throws SQLException ;

   List<T> selectAll();

   List<T> selectByIds(List<Long> ids);

   T selectById(long id);

   boolean existsById(long id);

   long countAll();

   boolean isTableEmpty();

   boolean isNew(T model);

   <X> void updateByIds(String col, X val, Long... ids) throws SQLException;

   <X> void update(String col, X oldVal, X newVal) throws SQLException;

   <X> List<Long> selectIdsBy(String col, X val);
    
}
