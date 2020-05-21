package panel;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

public class HomePanel extends JPanel implements ActionListener {
    JPanel cardPane, buttonGroupTop, buttonGroupBottom;
    CardLayout cardLayout;
    //Instance variables of image and button allow for resizing to change dimensions as per window size
    Image uploadImg, editImg, historyImg, settingImg, contactsImg;
    JButton upload, edit, history, setting, contacts;
    HomePanel(JPanel pane){
        buttonGroupTop = new JPanel();
        buttonGroupBottom = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        buttonGroupTop.setLayout(gridBag);
        buttonGroupBottom.setLayout(gridBag);
        setLayout(gridBag);

        try {
            uploadImg = ImageIO.read(new File("resources/upload.png"));
            editImg = ImageIO.read(new File("resources/edit.png"));
            historyImg = ImageIO.read(new File("resources/history.png"));
            contactsImg = ImageIO.read(new File("resources/contacts.png"));
            settingImg = ImageIO.read(new File("resources/setting.png"));
        }catch(IOException io){
            System.out.println(io);
        }

        buttonGroupTop.add(upload = new JButton("Upload"));
        buttonGroupTop.add(edit = new JButton("Edit"));
        buttonGroupTop.add(history = new JButton("History"));
        buttonGroupBottom.add(contacts = new JButton("Contacts"));
        buttonGroupBottom.add(setting = new JButton("Settings"));

        upload.addActionListener(this);
        edit.addActionListener(this);
        history.addActionListener(this);
        contacts.addActionListener(this);
        setting.addActionListener(this);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupTop, c);
        add(buttonGroupTop);
        c.gridwidth= GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupBottom,c);
        add(buttonGroupBottom);

        //TO-DO declare get height and get width as variables.
        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
//                System.out.println("x="+getWidth()+", y="+getHeight()); // easily observe the current view port size
                GridBagConstraints c2 = new GridBagConstraints();
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int font = 32;
                int width = 200;
                int height = 200;
                c2.insets = new Insets(20, 50, 20, 50);

                if(getWidth()<1200 || getHeight()<600){
                    //Scaling factor is decided by restricting factor/element height/width
                    //e.g. 200/1200 = 0.16. (button width)/(1200 as per condition in if statement)
                    width = (windowWidth) / (1200/200);
                    height = (windowHeight) / (600/200);
                    width = (int)(getWidth()*0.16);
                    font = (int)(Double.min(windowWidth /(1300/font), windowHeight/(600/font)));
                    c2.insets = new Insets((int)(getHeight()*0.0333), (int)(getWidth()*0.0416), (int)(getHeight()*0.0333), (int)(getWidth()*0.0416));

                }
                    buttonProperties(upload, uploadImg, width, height, font, gridBag, c2);
                    buttonProperties(edit, editImg, width, height, font, gridBag, c2);
                    buttonProperties(history, historyImg, width, height, font, gridBag, c2);
                    buttonProperties(contacts, contactsImg, width, height, font, gridBag, c2);
                    buttonProperties(setting, settingImg, width, height, font, gridBag, c2);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case "Upload":
                cardLayout.show(cardPane, "Upload");
                break;
            case "Edit":
                cardLayout.show(cardPane, "Edit");
                break;
            case "History":
                cardLayout.show(cardPane, "History");
                break;
            case "Contacts":
                cardLayout.show(cardPane, "Contacts");
                break;
            case "Settings":
                cardLayout.show(cardPane, "Settings");
                break;
            default:
                System.out.print("error action not recognised");
                break;
        }
    }

    private void buttonProperties(JButton btn, Image img, int width, int height, int font, GridBagLayout gridBag, GridBagConstraints c){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, font));
        btn.setFocusPainted(false);
        gridBag.setConstraints(btn,c);
    }
}