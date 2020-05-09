package panel;

import javax.swing.*;
import java.awt.*;

//ComponentProperties
public class ComProps {

    public static void headingProperties(JLabel label, int fontSize){
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
    }

    public static void textFieldProperties(JTextField field, int fontSize){
        field.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

    public static void textAreaProperties(JTextArea area, int fontSize){
        area.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }


    public static void listProperties(JList list, int fontSize){
        list.setFont(new Font("Arial", Font.PLAIN, fontSize));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    /**
     * Details the standard design layout of a button inside this display
     * @param btn The button to be edited
     * @param img The associated image with the button
     * @param width The desired width of the image(Image aspect ratio is retained so smallest of width and height is used)
     * @param height The desired height of the image(Image aspect ratio is retained so smallest of width and height is used)
     * @param fontSize The font size desired on the button(0 disabled the text from being viewable)
     * @param iconLeft Is true if the desired position of the text is to the right of the image
     */
    public static void buttonProperties(JButton btn, Image img, int width, int height, int fontSize, boolean iconLeft) {
        if(img != null) {
            img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
            btn.setIcon(new ImageIcon(img));
        }

        if(!iconLeft){
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
        }else{
            btn.setVerticalTextPosition(SwingConstants.CENTER);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setFocusPainted(false);
    }

    public static void checkBoxProperties(JCheckBox box, int fontSize){
        box.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }
}
