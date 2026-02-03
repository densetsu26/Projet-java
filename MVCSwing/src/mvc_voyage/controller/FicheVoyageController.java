/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_voyage.controller;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;
import mvc_bateau.model.Bateau; // Import du modèle Bateau
import mvc_voyage.dao.VoyageDAO;
import mvc_voyage.model.Voyage;
import mvc_voyage.view.FicheVoyageView;
/**
 *
 * @author USER ONE
 */
public class FicheVoyageController {
    
    private final Voyage model;
    private final FicheVoyageView view;
    private final VoyageDAO dao;
    private final Boolean isNew;
    private final ActionListener onSuccess;
    
    // Le formateur pour les JTextField
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FicheVoyageController(Voyage model, FicheVoyageView view, VoyageDAO dao, Boolean isNew, ActionListener onSuccess) {
        this.model = model;
        this.view = view;
        this.dao = dao;
        this.isNew = isNew;
        this.onSuccess = onSuccess;
    }

    public void initController() {
        // 1. Remplir la liste des bateaux au démarrage (à adapter selon le DAO i think:))
        remplirComboBateaux();

        if (!isNew) {
            // Remplissage des champs pour la modification
            view.getTfLieuDepart().setText(model.getDepart());
            view.getTfDestination().setText(model.getArriver());
            view.getTfDateDepart().setText(model.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            view.getTfHeureDepart().setText(model.getHeureFin().format(DateTimeFormatter.ofPattern("HH:mm")));
            // etc...
        }

        view.getBtEnregVoyage().addActionListener(e -> enregistrer());
    }

    private void remplirComboBateaux() {
        try {
            // On récupère tous les bateaux pour les mettre dans la JComboBox
            // Hypothèse : notre dao possède une méthode pour lister les bateaux ou soit on use un BateauDAO
            // view.getComboBateau().removeAllItems();
            // List<Bateau> bateaux = daoBateau.selectAll(); 
            // for(Bateau b : bateaux) view.getComboBateau().addItem(b);
        } catch (Exception e) {
            System.err.println("Erreur chargement bateaux : " + e.getMessage());
        }
    }

    private void enregistrer() {
        try {
            // 1. Récupération et conversion des dates
            String strDepart = view.getTfDateDepart().getText() + " " + view.getTfHeureDepart().getText();
            String strArrivee = view.getTfDateArrivee().getText() + " " + view.getTfHeureArrivee().getText();
            
            LocalDateTime dateDep = LocalDateTime.parse(strDepart, formatter);
            LocalDateTime dateArr = LocalDateTime.parse(strArrivee, formatter);

            // 2. Validations métier
            if (dateArr.isBefore(dateDep)) {
                JOptionPane.showMessageDialog(view, "La date d'arrivée doit être après le départ.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Logique Voyage (Numéro auto et calculs)
            /*if (isNew) {
                model.setId_Voyage("VOY-" + System.currentTimeMillis() % 10000);
            }*/

            // 4. Mise à jour du modèle
            model.setDepart(view.getTfLieuDepart().getText());
            model.setArriver(view.getTfDestination().getText());
           // model.setDateDebut(dateDep);
            //model.setDatefin(dateArr);
            
            // Récupération du bateau depuis la ComboBox (que l'équipe... bon axelle va changer)
            // Bateau b = (Bateau) view.getComboBateau().getSelectedItem();
            // model.setBateau(b);

            // 5. Sauvegarde
            if (isNew) {
                dao.create(model);
            } else {
                dao.update(model);
            }

            JOptionPane.showMessageDialog(view, "Voyage enregistré avec succès ! ID: " + model.getId_Voyage());
            view.dispose();
            if (onSuccess != null) onSuccess.actionPerformed(null);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Format Date (jj/mm/aaaa) ou Heure (hh:mm) incorrect !", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erreur Base de données : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
