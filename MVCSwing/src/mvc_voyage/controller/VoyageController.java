/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mvc_voyage.controller;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import mvc_voyage.infra.dao.IVoyageDAO;
import mvc_voyage.infra.view.ButtonEditor;
import mvc_voyage.infra.view.ButtonRenderer;
import mvc_voyage.model.Voyage;
import mvc_voyage.view.VoyageView;
import mvc_voyage.view.FicheVoyageView;

/**
 *
 * @author USER ONE
 */
public class VoyageController {

    /**
     * @param args the command line arguments
     */
    private final List<Voyage> model;
    private final VoyageView view;
    private final IVoyageDAO dao;
    
    public VoyageController(List<Voyage> m, VoyageView v, IVoyageDAO d){
       model = m;
       view = v;
       dao = d;
       initView();
    }
   
    public void initView(){
        // Chargement initial des données dans le tableau
        refreshTable();
    }
   
    public void refreshTable() {
        // On recharge la liste depuis la base pour être à jour
        try {
            List<Voyage> updatedList = dao.selectAll();
            refreshTable(updatedList);
        } catch (Exception e) {
            System.err.println("Erreur refresh : " + e.getMessage());
        }
    }
   
    private void refreshTable(List<Voyage> model) {
        SwingUtilities.invokeLater(() -> {
            // Remplace "getTbListeVoyages" par le nom du getter de la table dans VoyageView
            DefaultTableModel tableModel = (DefaultTableModel) view.getTbListeVoyages().getModel();
            tableModel.setRowCount(0);
            for(Voyage v : model) {
                // IMPORTANT : La méthode toTableRow() doit exister dans la classe Voyage
                tableModel.addRow(v.toTableRow());
            }
        });
    }
   
    public void initController(){
        // Action pour le bouton Modifier
        ActionListener editActionListener = (e) -> {
            int selectedRow = view.getTbListeVoyages().getSelectedRow();
            if (selectedRow != -1) modifier(selectedRow);
        };

        // Action pour le bouton Supprimer
        ActionListener deleteActionListener = (e) -> {
            int selectedRow = view.getTbListeVoyages().getSelectedRow();
            if(selectedRow != -1 && JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(view, "Supprimer ce voyage ?")) {
                supprimer(selectedRow);
            }
        };

        // Configuration des boutons dans la colonne "Actions" du tableau
        try {
            ButtonRenderer btnRend = (ButtonRenderer) view.getTbListeVoyages().getColumn("Actions").getCellRenderer();
            btnRend.setEditActionListener(editActionListener);
            btnRend.setDeleteActionListener(deleteActionListener);
            
            ButtonEditor btnEditor = (ButtonEditor) view.getTbListeVoyages().getColumn("Actions").getCellEditor();
            btnEditor.setEditActionListener(editActionListener);
            btnEditor.setDeleteActionListener(deleteActionListener);
        } catch (Exception ex) {
            System.err.println("Note: Colonne Actions non configurée ou absente.");
        }

       // Bouton "Nouveau Voyage"
       view.getBtNouveau().addActionListener(e -> nouveau());
    }
   
    private void modifier(int tableRow) {
        // On récupère l'ID (souvent en colonne 0)
        Long id = (Long) view.getTbListeVoyages().getValueAt(tableRow, 0);
        
        FicheVoyageView editView = new FicheVoyageView();
        FicheVoyageController controller = new FicheVoyageController(
                dao.selectById(id), 
                editView, 
                dao, 
                Boolean.FALSE, // Mode Mise à jour
                (e)-> refreshTable() // Si succès, on rafraîchit
        );
        controller.initController();
        editView.setVisible(true);
    }
   
    private void supprimer(int tableRow) {
        Long id = (Long) view.getTbListeVoyages().getValueAt(tableRow, 0);
        try {
            dao.delete(id);
            JOptionPane.showMessageDialog(view, "Voyage supprimé");
            refreshTable();
        } catch (SQLException ex) {
            Logger.getLogger(VoyageController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(view, "Erreur suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
   
    private void nouveau() {
        FicheVoyageView newView = new FicheVoyageView();
        FicheVoyageController controller = new FicheVoyageController(
                new Voyage(), 
                newView, 
                dao, 
                Boolean.TRUE, // Mode Création
                (e)-> refreshTable() // Si succès, on rafraîchit
        );
        controller.initController();
        newView.setVisible(true);
    }
}
    
