/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package mvc_ticket.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
// Imports iText (on doit avoir le .jar iText 5.x ou similaire)
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import mvc_voyage.infra.dao.ITicketDAO; // À créer (hérite de IBaseDBDAO)
import mvc_voyage.model.Ticket;
import mvc_voyage.model.Voyage;
import mvc_voyage.view.TicketView; // La vue de vente de ticket
/**
 *
 * @author USER ONE
 */

public class TicketController {

    private final TicketView view;
    private final Voyage voyageConcerne; // Le voyage sélectionné
    private final ITicketDAO ticketDao;
    
    // Constructeur
    public TicketController(Voyage voyage, TicketView view, ITicketDAO dao) {
        this.voyageConcerne = voyage;
        this.view = view;
        this.ticketDao = dao;
    }

    public void initController() {
        // Afficher les infos du voyage dans la vue (si des labels existent)
        view.getLblInfoVoyage().setText("Voyage " + voyageConcerne.getNumeroVoyage() + " vers " + voyageConcerne.getLieuArrivee());
        
        // Action bouton "Imprimer / Vendre"
        view.getBtImprimer().addActionListener(e -> traiterVente());
        
        // Action bouton Annuler
        view.getBtAnnuler().addActionListener(e -> view.dispose());
    }

    private void traiterVente() {
        try {
            // 1. Récupérer les données de saisie
            String nomPassager = view.getTfNomPassager().getText();
            int nbPlacesDemandees = (int) view.getSpNbPlaces().getValue(); // JSpinner

            if (nomPassager.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Le nom du passager est obligatoire.");
                return;
            }

            // 2. VÉRIFICATION DE LA CAPACITÉ (CRITIQUE)
            // On compte combien de tickets ont déjà été vendus pour ce voyage
            long placesVendues = ticketDao.countPlacesVendues(voyageConcerne.getId());
            int capaciteBateau = voyageConcerne.getBateau().getCapacite();
            int placesRestantes = capaciteBateau - (int)placesVendues;

            if (nbPlacesDemandees > placesRestantes) {
                JOptionPane.showMessageDialog(view, 
                    "Attention ! Il ne reste que " + placesRestantes + " places disponibles.", 
                    "Capacité insuffisante", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Création de l'objet Ticket
            Ticket nouveauTicket = new Ticket();
            nouveauTicket.setNomPassager(nomPassager);
            nouveauTicket.setVoyage(voyageConcerne);
            nouveauTicket.setDateAchat(LocalDateTime.now());
            // nouveauTicket.setPrix(...); // Si on gere le prix

            // 4. Sauvegarde en Base
            ticketDao.create(nouveauTicket);

            // 5. Génération du PDF
            genererPDF(nouveauTicket);

            JOptionPane.showMessageDialog(view, "Ticket enregistré et imprimé avec succès !");
            view.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erreur Base de données : " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur technique : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Génère un fichier PDF simple avec iText
     */
    private void genererPDF(Ticket ticket) {
        Document document = new Document();
        try {
            String fileName = "Ticket_" + ticket.getId() + "_" + ticket.getNomPassager() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            // Contenu du PDF
            document.add(new Paragraph("=== TICKET DE VOYAGE ==="));
            document.add(new Paragraph("Passager : " + ticket.getNomPassager()));
            document.add(new Paragraph("Voyage N° : " + voyageConcerne.getNumeroVoyage()));
            document.add(new Paragraph("Départ : " + voyageConcerne.getLieuDepart() + " le " + 
                    voyageConcerne.getDateHeureDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Arrivée : " + voyageConcerne.getLieuArrivee()));
            document.add(new Paragraph("Bateau : " + voyageConcerne.getBateau().getNom()));
            document.add(new Paragraph("--------------------------"));
            document.add(new Paragraph("Date d'émission : " + LocalDateTime.now().toString()));
            
            document.close();

            // Ouvrir automatiquement le PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(fileName));
            }

        } catch (Exception e) {
            System.err.println("Erreur création PDF: " + e.getMessage());
        }
    }
}
