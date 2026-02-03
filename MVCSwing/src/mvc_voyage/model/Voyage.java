/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_voyage.model;

import java.time.Duration;
import java.time.LocalDate;
import mvc_bateau.infra.model.BaseModel;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;
import mvc_ticket.model.Ticket;
import mvc_bateau.model.Bateau;


/**
 *
 * @author user
 */
public class Voyage extends BaseModel{
    private long id_Voyage;
    private String depart;
    private String arriver;
    private LocalDateTime dateDebut;
    private LocalDateTime datefin;
    private Bateau bateau;
    public Duration calculerDurée(){
        Duration diff;
        diff = Duration.between(dateDebut,datefin);
        
        return diff;
    }
    public Voyage(int id,String depart,String arriver,LocalDateTime dateDebut, LocalDateTime datefin,Bateau bateau){
        this.id_Voyage=id;
        this.depart=depart;
        this.arriver=arriver;
        this.dateDebut= dateDebut;
        this.datefin= datefin;
        this.bateau=bateau;
        
        
    }
    @Override
    public Object[] toTableRow() {
        return new Object[]{getId(), getDepart(), getArriver(), getDateDebut(), getDatefin(), getBateau()};
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id_Voyage;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id_Voyage = id;
    }

    /**
     * @return the depart
     */
    public String getDepart() {
        return depart;
    }

    /**
     * @param depart the depart to set
     */
    public void setDepart(String depart) {
        this.depart = depart;
    }

    /**
     * @return the arriver
     */
    public String getArriver() {
        return arriver;
    }

    /**
     * @param arriver the arriver to set
     */
    public void setArriver(String arriver) {
        this.arriver = arriver;
    }

    /**
     * @return the dateDebut
     */
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    /**
     * @param dateDebut the dateDebut to set
     */
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * @return the datefin
     */
    public LocalDateTime getDatefin() {
        return datefin;
    }

    /**
     * @param datefin the datefin to set
     */
    public void setDatefin(LocalDateTime datefin) {
        this.datefin = datefin;
    }

    /**
     * @return the bateau
     */
    public Bateau getBateau() {
        return bateau;
    }

    /**
     * @param bateau the bateau to set
     */
    public void setBateau(Bateau bateau) {
        this.bateau = bateau;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.arriver);
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
        final Voyage other = (Voyage) obj;
        return Objects.equals(this.arriver, other.arriver);
    }
    /*public Ticket editerTicket(){
        return new Ticket();
    }*/
    
}
