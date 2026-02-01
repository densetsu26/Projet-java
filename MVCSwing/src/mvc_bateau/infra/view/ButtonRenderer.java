/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author macbook
 */
public class ButtonRenderer extends JPanel implements TableCellRenderer {
    
    private ActionListener editActionListener;
    private ActionListener deleteActionListener;
    
    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton[] btns = actionBtns();
        btns[0].addActionListener(e -> {
            if(Objects.nonNull(editActionListener)) {
                editActionListener.actionPerformed(e);
            }
        });

        btns[1].addActionListener(e -> {
            if(Objects.nonNull(deleteActionListener)) {
                deleteActionListener.actionPerformed(e);
            }
        });
        //add(new JButton("Voir"));
        add(btns[0]);
        add(btns[1]);
    }
    
    public static JButton[] actionBtns() {
        JButton btnEdit = new JButton();
        btnEdit.setToolTipText("Modifier");
        JButton btnDelete = new JButton();
        btnDelete.setToolTipText("Supprimer");
        btnEdit.setIcon(new javax.swing.ImageIcon(ButtonRenderer.class.getResource("/mvc_bateau/icons/edit-icon.png"))); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(ButtonRenderer.class.getResource("/mvc_bateau/icons/data-delete-icon.png"))); // NOI18N
        return new JButton[]{btnEdit, btnDelete};
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
    
    public void setEditActionListener(ActionListener editActionListener) {
        this.editActionListener = editActionListener;
    }

    public void setDeleteActionListener(ActionListener deleteActionListener) {
        this.deleteActionListener = deleteActionListener;
    }
}
