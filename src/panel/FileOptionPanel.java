package panel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Date;

public class FileOptionPanel extends JPanel{
    public FileOptionPanel(File file) {
        JLabel name, path, size, lastMod, readable;
        JLabel nameField, pathField, sizeField, lastModField, readableField;
        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        name = new JLabel("File Name:");
        path = new JLabel("File Path:");
        size = new JLabel("File Size:");
        lastMod = new JLabel("Last Modified:");
        readable = new JLabel("isReadable: ");

        nameField = new JLabel(file.getName());
        pathField = new JLabel(file.getPath());
        sizeField = new JLabel(String.valueOf((file.length()))+"B");
        lastModField = new JLabel(String.valueOf(new Date(file.lastModified())));
        readableField = new JLabel(String.valueOf(file.canRead()));

        //Adjust constraints for the label so it's at (5,5).
        add(name);
        add(nameField);
        add(path);
        add(pathField);
        add(size);
        add(sizeField);
        add(lastMod);
        add(lastModField);
        add(readable);
        add(readableField);
        for(Component c : this.getComponents()){
            c.setFont(new Font("Arial", Font.PLAIN,16));
        }

        SpringUtilities.makeCompactGrid(this,
                5, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
    }
}
