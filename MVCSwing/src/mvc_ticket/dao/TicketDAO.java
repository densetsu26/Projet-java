/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_ticket.dao;

import mvc_ticket.model.Ticket;
import mvc_bateau.infra.dao.BaseDBDAO;

/**
 *
 * @author user
 */
public class TicketDAO extends BaseDBDAO<Ticket> {
    
    public TicketDAO(String pTableName) {
        super("TicketBD");
    }
    
}
