/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.model;

import mvc_bateau.infra.dao.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author macbook
 */
public abstract class BaseModel {
    
    protected Long id;
    //protected Object[] fields;

    public BaseModel() {
    }

    public BaseModel(Long pId) {
        id = pId;
    }

//    public void setFieldValueAt(int index, Object value) {
//        fields[index] = value;
//    }
//
//    public Object getFieldValueAt(int index) {
//        return fields[index];
//    }

    public boolean isNew() {
        return id == null;
    }

    public Long getId() {
        return id;
    }
    
    public Object[] getPersistFieldsVal() {
        return getFieldsVal(false);
    }
    
    public Object[] getFieldsVal() {
        return getFieldsVal(true);
    }
    
    public Object[] getFieldsVal(boolean takeTransients) {
        //return fields;
        List<Object> fieldValues = new ArrayList<>();

        // Parcours des champs déclarés dans la classe (dans l'ordre de déclaration)
        for (Field field : getFields(takeTransients)) {
            // Rendre accessible les champs privés
            field.setAccessible(true);

            try {
                // Ajoute la valeur du champ à la liste
                fieldValues.add(field.get(this));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erreur lors de l'accès au champ : " + field.getName(), e);
            }
            field.setAccessible(false);
        }

        return fieldValues.toArray();
    }
    
    public Field[] getPersistFields() {
        return getFields(false);
    }
    
    public Field[] getFields() {
        return getFields(true);
    }
    
    public Field[] getFields(boolean takeTransients) {
        //return fields;
        List<Field> fields = new ArrayList<>();

        // Récupère la classe de l'objet courant (la classe fille)
        Class<?> clazz = this.getClass();

        // Parcours des champs déclarés dans la classe (dans l'ordre de déclaration)
        for (Field field : clazz.getDeclaredFields()) {
            // Ignorer le champ "id"
            if (field.getName().equals("id")) {
                continue;
            }
            // Ignorer les champs Transient
            if(!takeTransients && field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            // Ajoute le champ à la liste
            fields.add(field);
        }

        return fields.toArray(new Field[0]);
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public abstract Object[] toTableRow();

//    public void setFields(Object[] fields) {
//        this.fields = fields;
//    }
    
}
