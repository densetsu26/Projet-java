/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.controller;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import mvc_bateau.dao.BateauDAO;
import mvc_bateau.infra.view.ButtonEditor;
import mvc_bateau.infra.view.ButtonRenderer;
import mvc_bateau.model.Bateau;
import mvc_bateau.view.BateauView;
import mvc_bateau.view.FicheBateauView;

public class BateauController {
    private final List<Bateau> model;
    private final BateauView view;
    private final BateauDAO dao;
   
    public BateauController(List<Bateau> m, BateauView v, BateauDAO d){
       model = m;
       view = v;
       dao = d;
       initView();
    }
   
    public void initView(){
        // Données de vue depuis le model
        refreshTable();
    }
   
    /**
     * Actualiser une table
     */
    public void refreshTable() {
        refreshTable(model);
    }
    
    /**
     * Actualiser une table à partir d'un objet model
     */
    private void refreshTable(List<Bateau> model) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel tableModel = (DefaultTableModel) view.getTbListeBateaux().getModel();
            tableModel.setRowCount(0);
            for(Bateau b : model) {
                tableModel.addRow(b.toTableRow());
            }
        });
    }
   
    public void initController(){
        // Events Boutons "Actions"
        ActionListener editActionListener = (e) -> {
            int selectedRow = view.getTbListeBateaux().getSelectedRow();
            modifier(selectedRow);
        };
        ActionListener deleteActionListener = (e) -> {
            int selectedRow = view.getTbListeBateaux().getSelectedRow();
            if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(view, "Voulez-vous vraiment supprimer ce bateau ?")) {
                supprimer(selectedRow);
            }
        };
        // Boutons "Actions"
        ButtonRenderer btnRend = (ButtonRenderer) view.getTbListeBateaux().getColumn("Actions").getCellRenderer();
        btnRend.setEditActionListener(editActionListener);
        btnRend.setDeleteActionListener(deleteActionListener);
        ButtonEditor btnEditor = (ButtonEditor) view.getTbListeBateaux().getColumn("Actions").getCellEditor();
        btnEditor.setEditActionListener(editActionListener);
        btnEditor.setDeleteActionListener(deleteActionListener);
       // Bouton Nouveau
       view.getBtNouveau().addActionListener(e -> nouveau());
    }
    
    /**
     * Modifier une ligne de la table
     */
    private void modifier(int tableRow) {
        // Récupération de l'ID
        Long id = (Long) view.getTbListeBateaux().getValueAt(tableRow, 0);
        // Afficher la vue en modale
        FicheBateauView editView = new FicheBateauView();
        FicheBateauController controller = new FicheBateauController(
                dao.selectById(id), 
                editView, 
                dao, 
                Boolean.FALSE, // MAJ
                (e)-> {
                    // Au succès de la modification -> actualiser la ligne
                    SwingUtilities.invokeLater(() -> {
                        DefaultTableModel tableModel = (DefaultTableModel) view.getTbListeBateaux().getModel();
                        int i = 0;
                        for(Object obj : ((FicheBateauController) e.getSource()).getModel().toTableRow()) {
                            tableModel.setValueAt(obj, tableRow, i++);
                        }
                    });
                });
        controller.initController();
        editView.setVisible(true);
    }
    
    /**
     * Supprimer une ligne de la table
     */
    private void supprimer(int tableRow) {
        // Récupération de l'ID
        Long id = (Long) view.getTbListeBateaux().getValueAt(tableRow, 0);
        String modelTitle = (String) view.getTbListeBateaux().getValueAt(tableRow, 1);
        try {
            // Suppression DAO
            dao.delete(id);
            // Messages
            JOptionPane.showMessageDialog(
                    view, 
                    "Suppression du bateau "+ modelTitle + " effectuée avec succès");
            // Retirer la ligne de la table
            SwingUtilities.invokeLater(() -> {
               ((DefaultTableModel) view.getTbListeBateaux().getModel()).removeRow(tableRow); 
            });
        } catch (SQLException ex) {
            Logger.getLogger(BateauController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    view, 
                    "Erreur lors de la suppression du bateau : "+modelTitle, 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
   
    /**
     * Nouvel enregistrement
     */
    private void nouveau() {
        FicheBateauView newView = new FicheBateauView();
        FicheBateauController controller = new FicheBateauController(
                new Bateau(), 
                newView, 
                dao, 
                Boolean.TRUE, // Création
                (e)-> {
                    // Au succès de l'enregistrement -> ajout à la table
                    SwingUtilities.invokeLater(() -> {
                        DefaultTableModel tableModel = (DefaultTableModel) view.getTbListeBateaux().getModel();
                        tableModel.addRow(((FicheBateauController) e.getSource()).getModel().toTableRow());
                    });
                });
        controller.initController();
        newView.setVisible(true);
    }
    
}

