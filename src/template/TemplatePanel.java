package template;

import panel.ComProps;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class TemplatePanel extends JPanel {

    public TemplatePanel() {
        setLayout(new BorderLayout());
        JButton add = new JButton("Add/Edit");
        Image addImg = null, deleteImg = null;
        try {
            addImg = ImageIO.read(new File("resources/add.png"));
            deleteImg = ImageIO.read(new File("resources/delete.png"));
        }catch(IOException e){
            System.out.println(e);
        }

        JButton delete = new JButton("Delete");
        delete.setEnabled(false);
        JTextField text = new JTextField();
        DefaultListModel templates = TemplateDB.getTemplates();
        JList editTemplate = new JList(templates);

        ComProps.listProperties(editTemplate, 20);
        ComProps.buttonProperties(add, addImg, 30, 30, 18, true);
        ComProps.buttonProperties(delete, deleteImg, 50, 50, 24, false);
        ComProps.textFieldProperties(text, 18);

        editTemplate.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (editTemplate.isSelectionEmpty()) {
                    text.setText("");
                } else {
                    text.setText(editTemplate.getSelectedValue().toString());
                    delete.setEnabled(true);
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
