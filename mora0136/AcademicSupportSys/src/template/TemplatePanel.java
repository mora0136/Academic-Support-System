package template;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TemplatePanel extends JPanel {

    public TemplatePanel() {
        setLayout(new BorderLayout());
        JButton add = new JButton("Add/Edit");
        JButton delete = new JButton("Delete");
        JTextField text = new JTextField();
        DefaultListModel templates = TemplateDB.getTemplates();
//        for (int i = 0; i < templates.getSize() - 1; i++) {
//            edit.addElement(templates.getElementAt(i));
//        }
        JList editTemplate = new JList(templates);

        editTemplate.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (editTemplate.isSelectionEmpty()) {
                    text.setText("");
                } else {
                    text.setText(editTemplate.getSelectedValue().toString());
                }
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(editTemplate.isSelectionEmpty()) {
                    TemplateDB.addTemplate(text.getText());
                }else{
                    Template t = (Template) editTemplate.getSelectedValue();
                    TemplateDB.editTemplate(t.getTemplateID(), text.getText());
                }
                DefaultListModel t = TemplateDB.getTemplates();
                templates.removeAllElements();
                for(int i = 0; i < t.getSize(); i++){
                    templates.addElement(t.getElementAt(i));
                }
                text.setText("");
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TemplateDB.deleteTemplate((Template) editTemplate.getSelectedValue());
                DefaultListModel t = TemplateDB.getTemplates();
                templates.removeAllElements();
                for(int i = 0; i < t.getSize(); i++){
                    templates.addElement(t.getElementAt(i));
                }
                text.setText("");
            }
        });
        JPanel addTextPanel = new JPanel();
        addTextPanel.setLayout(new BorderLayout());
        addTextPanel.add(text, BorderLayout.CENTER);
        addTextPanel.add(add, BorderLayout.EAST);
        add(editTemplate, BorderLayout.CENTER);
        add(delete, BorderLayout.EAST);
        add(addTextPanel, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(500, 500));
    }
}
