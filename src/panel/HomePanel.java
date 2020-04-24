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
    //Instance variables of image and button allow for resizing to change dimenions as per window size
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
        buttonGroupBottom.add(setting = new JButton("Setting"));

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupTop, c);
        add(buttonGroupTop);
        c.gridwidth= GridBagConstraints.REMAINDER;
        gridBag.setConstraints(buttonGroupBottom,c);
        add(buttonGroupBottom);

        //TO-DO declare get height and get width as variables.
        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                System.out.println("x="+getWidth()+", y="+getHeight());
                GridBagConstraints c2 = new GridBagConstraints();
                int font = 32;
                if(getWidth() <= 300 || getHeight() <= 300){
                    font = 8;
                }else if(getWidth() <= 600 || getHeight() <= 600){
                    font = 16;
                }
                if(getWidth()<1200 && getHeight()<600){
                    //Scaling factor is decided by restricinting factor/element height/width
                    //e.g. 200/1200 = 0.16. (button width)/(1200 as per condition in if statement)
                    int height = (int)(getHeight()*0.333);
                    int width = (int)(getWidth()*0.16);
                    c2.insets = new Insets((int)(getHeight()*0.0333), (int)(getWidth()*0.0416), (int)(getHeight()*0.0333), (int)(getWidth()*0.0416));
                    buttonProperties(upload, uploadImg, width, height, font, gridBag, c2);
                    buttonProperties(edit, editImg, width, height, font, gridBag, c2);
                    buttonProperties(history, historyImg, width, height, font, gridBag, c2);
                    buttonProperties(contacts, contactsImg, width, height, font, gridBag, c2);
                    buttonProperties(setting, settingImg, width, height, font, gridBag, c2);

                }else if(getWidth() < 1200){
                    int width = (int)(getWidth()*0.16);
                    c2.insets = new Insets(20, (int)(getWidth()*0.0416), 20, (int)(getWidth()*0.0416));
                    buttonProperties(upload, uploadImg, width, width, font, gridBag, c2);
                    buttonProperties(edit, editImg, width, width, font, gridBag, c2);
                    buttonProperties(history, historyImg, width, width, font, gridBag, c2);
                    buttonProperties(contacts, contactsImg, width, width, font, gridBag, c2);
                    buttonProperties(setting, settingImg, width, width, font, gridBag, c2);
                }else if(getHeight() < 600){
                    int height = (int)(getHeight()*0.333);
//                    int font = 16;//(int)(16/(600/getHeight()));
                    c2.insets = new Insets((int)(getHeight()*0.0333), 50, (int)(getHeight()*0.0333), 50);
                    buttonProperties(upload, uploadImg, height, height, font, gridBag, c2);
                    buttonProperties(edit, editImg, height, height, font, gridBag, c2);
                    buttonProperties(history, historyImg, height, height, font, gridBag, c2);
                    buttonProperties(contacts, contactsImg, height, height, font, gridBag, c2);
                    buttonProperties(setting, settingImg, height, height, font, gridBag, c2);
                }else{
                    c2.insets = new Insets(20, 50, 20, 50);
                    buttonProperties(upload, uploadImg, 200, 200, font, gridBag, c2);
                    buttonProperties(edit, editImg, 200, 200, font, gridBag, c2);
                    buttonProperties(history, historyImg, 200, 200, font, gridBag, c2);
                    buttonProperties(contacts, contactsImg, 200, 200, font, gridBag, c2);
                    buttonProperties(setting, settingImg, 200, 200, font, gridBag, c2);
                }
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
            case "Setting":
                cardLayout.show(cardPane, "Setting");
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
        btn.addActionListener(this); //adds a duplicate action listener everytime screen is resized, needs to be changed.
        gridBag.setConstraints(btn,c);
    }
}