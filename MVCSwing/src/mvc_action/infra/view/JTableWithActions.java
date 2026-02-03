/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_action.infra.view;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author macbook
 */
public class JTableWithActions {
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new JTableWithActions().createAndShowGUI());
//    }
    
    public void createAndShowGUI() {
        JFrame frame = new JFrame("JTable avec actions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Données de base
        String[] columnNames = {"Nom", "Âge", "Actions"};
        Object[][] data = {
            {"Jean", 30, null},
            {"Claire", 25, null},
            {"Ahmed", 40, null}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // seule la colonne "Actions" est éditable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);

        // Ajout du renderer + editor sur la colonne "Actions"
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(table));

        frame.add(new JScrollPane(table));
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
