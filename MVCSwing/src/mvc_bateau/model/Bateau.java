/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.model;

import java.util.Objects;
import mvc_bateau.infra.model.BaseModel;
import mvc_bateau.infra.dao.Column;

/**
 *
 * @author macbook
 */
public class Bateau extends BaseModel {
    
    //public final static String[] FIELDS_NAMES = new String[] { "nom", "description", "etiquette_Siege", "capacite" };
    //public final static Class<?>[] FIELDS_CLASSES = new Class[] { String.class, String.class, String.class, Integer.class};
    
    private String numero;
    private String nom;
    private String description;
    @Column("etiquette_Siege")
    private String numerotationSiege;
    private Integer capacite;

    public Bateau() {
        this.capacite = 0;
    }

    public Bateau(String nom, String description, String numerotationSiege, Integer capacite) {
        this.nom = nom;
        this.description = description;
        this.numerotationSiege = numerotationSiege;
        this.capacite = capacite;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[] {id, nom, description, numerotationSiege, capacite};
    }
    
    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getNumerotationSiege() {
        return numerotationSiege;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumerotationSiege(String numerotationSiege) {
        this.numerotationSiege = numerotationSiege;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.nom);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bateau other = (Bateau) obj;
        return Objects.equals(this.nom, other.nom);
    }

    @Override
    public String toString() {
        return "Bateau{" + "nom=" + nom + ", description=" + description + ", numerotationSiege=" + numerotationSiege + ", capacite=" + capacite + '}';
    }
}
