/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Utilisé pour spécifier la colonne de la base de données correspondante au champ
 */
@Inherited
@Target({ElementType.FIELD, ElementType.TYPE}) // ElementType.TYPE c'est pour la surchage de l'annotation par une autre
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * Le champ de l'entité correspondant
     * @return
     */
    String value() default "";
}
