/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_voyage.dao;

import mvc_bateau.infra.dao.BaseDBDAO;

import mvc_voyage.model.Voyage;

/**
 *
 * @author user
 */
public class VoyageDAO extends BaseDBDAO<Voyage>{
    
    public VoyageDAO(String pTableName) {
        super("VoyageBD");
    }
    
}
