/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mvc_bateau.controller.BateauController;
import mvc_bateau.dao.BateauDAO;
import mvc_bateau.infra.exception.DBException;
import mvc_bateau.model.Bateau;
import mvc_bateau.view.BateauView;
import mvc_bateau.view.FicheBateauView;

/**
 *
 * @author macbook
 */
public class Main {
    
    public static void main(String [] args){
        /*
        *
        * MVC Swing
        *
        */
        gui();
        /*
        *
        * JDBC
        *
        */
        //jdbc();
        
        /*
        *
        * DAO
        *
        */
        //dao();
    }
    
    private static void gui() {
        theme();
        BateauDAO dao = new BateauDAO();
        BateauView view = new BateauView();
        BateauController controller = new BateauController(dao.selectAll(), view, new BateauDAO());
        controller.initController();
        view.setVisible(true);
    }
    
    private static void jdbc() {
        final String nomHote = "localhost";
        final String port = "5432";
        final String nomBD = "maritime";
        final String userName = "adm_maritime"; 
        final String password = "maritime@2025";
        String connectionUrl = String.format(
                "jdbc:postgresql://%s:%s/%s", 
                nomHote, 
                port, 
                nomBD);
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
            // READ
            List<Bateau> list = new ArrayList<>();
            String sql = "SELECT * FROM Bateau"; // Requête SQL
            PreparedStatement ps = conn.prepareStatement(sql); // Requête préparée évitant les injections SQL
            ResultSet rs = ps.executeQuery(); // ResultSet encapsulant les résultats d'une requête
            while (rs.next()) {
                Bateau b = new Bateau();
                b.setId(rs.getLong("id"));
                b.setNumero(rs.getString("numero"));
                b.setNom(rs.getString("nom"));
                b.setDescription(rs.getString("description"));
                b.setNumerotationSiege(rs.getString("etiquette_Siege"));
                b.setCapacite(rs.getInt("capacite"));
                list.add(b);
            }
            System.out.println("Liste des bateaux via JDBC = "+list);
            
            ps.close(); // Libération dess ressources
            rs.close(); // Libération dess ressources
            
            // UPDATE
            Bateau b = new Bateau("MADONA", "MADONA", "MDN", 1_000_000);
            sql = "UPDATE Bateau SET numero = ?, nom = ?, description = ?, etiquette_Siege = ?, capacite = ? WHERE id = ?"; // Requête SQL
            ps = conn.prepareStatement(sql); // Requête préparée évitant les injections SQL
            ps.setString(1, b.getNumero());
            ps.setString(2, b.getNom());
            ps.setString(3, b.getDescription());
            ps.setString(4, b.getNumerotationSiege());
            ps.setInt(5, b.getCapacite());
            ps.setLong(6, 1L);
            ps.executeUpdate();
            
            ps.close(); // Libération dess ressources
            
            // DELETE
            sql = "DELETE FROM Bateau WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, 3);
            ps.executeUpdate();
            
            ps.close(); // Libération dess ressources
            
            // CREATE
            b = new Bateau("DOLCE", "DOLCE", "DLC", 3_000_000);
            sql = "INSERT INTO Bateau (nom, description, etiquette_Siege, capacite) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, b.getNom());
            ps.setString(2, b.getDescription());
            ps.setString(3, b.getNumerotationSiege());
            ps.setInt(4, b.getCapacite());
            ps.executeUpdate();
            
            ps.close(); // Libération dess ressources
            
            // Close connexion
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw new DBException("Echec de connexion à la base de données");
        }
        
    }
    
    public static void dao() {
        // READ
        BateauDAO dao = new BateauDAO();
        List<Bateau> list = dao.selectAll();
        System.out.println("Liste = "+ list);
        // UPDATE
        Bateau model = list.get(0);
        System.out.println("Model MAJ = "+ model);
        model.setDescription("update description");
        try {
            dao.update(model);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // CREATE
        model = new Bateau("TEST", "TEST", "TST", 1_000_000);
        try {
            model.setId(dao.create(model));
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            // DELETE
            dao.delete(model.getId());
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // RESELECT
        list = dao.selectAll();
        System.out.println("Liste = "+ list);
    }
    
    private static void theme() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FicheBateauView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}
