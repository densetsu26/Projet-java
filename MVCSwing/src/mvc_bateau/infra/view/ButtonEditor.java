/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author macbook
 */
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel;
    private final JTable table;
    private ActionListener editActionListener;
    private ActionListener deleteActionListener;

    public ButtonEditor(JTable table) {
        this.table = table;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton[] btns = ButtonRenderer.actionBtns();

        btns[0].addActionListener(e -> {
            if(Objects.nonNull(editActionListener)) {
                editActionListener.actionPerformed(e);
            }
            fireEditingStopped();
        });

        btns[1].addActionListener(e -> {
            if(Objects.nonNull(deleteActionListener)) {
                deleteActionListener.actionPerformed(e);
            }
            fireEditingStopped();
        });

        //panel.add(btnView);
        panel.add(btns[0]);
        panel.add(btns[1]);
    }

    public void setEditActionListener(ActionListener editActionListener) {
        this.editActionListener = editActionListener;
    }

    public void setDeleteActionListener(ActionListener deleteActionListener) {
        this.deleteActionListener = deleteActionListener;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
