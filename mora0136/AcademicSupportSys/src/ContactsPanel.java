import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class ContactsPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane, left, right, leftTop, rightTop, rightBottom, panelMiddle;
    JScrollPane scrollMiddle;
    Image backImg, searchImg, addNewImg, editImg, deleteImg, saveImg;

    ContactsPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        left = new JPanel();
        right = new JPanel();
        leftTop = new JPanel();
        panelMiddle = new JPanel();
        rightTop = new JPanel();
        rightBottom = new JPanel();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Set the frame to 1x2 grid(left and right Panels)
        setLayout(new GridLayout(1, 2));

        JButton back = new JButton("Back");
        backImg = ImageIO.read(new File("resources/back.png"));

        JTextField searchField = new JTextField("Search...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 32));
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusListener() {
            //Note that whatever was in the box previously is erased
            public void focusGained(FocusEvent e) {
                searchField.setText("");
                searchField.setForeground(Color.BLACK);
            }
            public void focusLost(FocusEvent e) {
                if(searchField.getText().isEmpty()){
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton search = new JButton("Search");
        searchImg = ImageIO.read(new File("resources/search.png"));
        //Action Event needed, either make exclusive to search or could auto do it as entering text

        JButton addNew = new JButton("Add New");
        addNewImg = ImageIO.read(new File("resources/add.png"));

        JButton edit = new JButton("Edit");
        editImg = ImageIO.read(new File("resources/edit_contact.png"));
        saveImg = ImageIO.read(new File("resources/save.png"));

        JButton delete = new JButton("Delete");
        deleteImg = ImageIO.read(new File("resources/delete.png"));

        back.addActionListener(this::actionPerformed);

        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        leftTop.setLayout(gridBag);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        gridBag.setConstraints(back, c);
        leftTop.add(back);
        c.insets = new Insets(10, 10, 10, 0);
        c.weightx = 25;
        gridBag.setConstraints(searchField, c);
        leftTop.add(searchField);
        c.insets = new Insets(10, 0, 10, 10);
        c.weightx = 2;
        gridBag.setConstraints(search, c);
        leftTop.add(search);
        leftTop.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        //Add contacts to leftMiddle scroll view
        ContactList content = new ContactList();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        scrollMiddle = new JScrollPane(content);

        //Output area
        rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.Y_AXIS));
        JLabel givenName = new JLabel("Name");
        JTextField givenNameField = new JTextField();
        JLabel lastName = new JLabel("Surname");
        JTextField lastNameField = new JTextField();
        JLabel email = new JLabel("Email");
        JTextField emailField = new JTextField();
        JLabel phone = new JLabel("Phone");
        JTextField phoneField = new JTextField();
        content.setOutputPanel(givenNameField, lastNameField, emailField, phoneField);
        rightTop.add(givenName);
        rightTop.add(givenNameField);
        rightTop.add(lastName);
        rightTop.add(lastNameField);
        rightTop.add(email);
        rightTop.add(emailField);
        rightTop.add(phone);
        rightTop.add(phoneField);


        rightBottom.setLayout(new GridLayout(1, 2));
        rightBottom.add(edit);
        rightBottom.add(delete);

        left.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        left.add(leftTop, BorderLayout.NORTH);
        left.add(scrollMiddle, BorderLayout.CENTER);
        left.add(addNew, BorderLayout.SOUTH);

        right.add(rightTop, BorderLayout.NORTH);
        right.add(rightBottom, BorderLayout.SOUTH);

        add(left);
        add(right);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                GridBagConstraints c2 = new GridBagConstraints();
                int font = 16;

                if(windowWidth <= 600){
                    font = 0;
                }

                if(windowWidth < 800){
                    int width = (int)(windowWidth*0.0625);
                    buttonProperties(back, backImg, width, width, font, false);
                    buttonProperties(search, searchImg, width, width, font, false);
                    if(windowWidth < 500) {
                        buttonProperties(addNew, addNewImg, width, width, 0, true);
                        buttonProperties(edit, editImg, width, width, 0, true);
                        buttonProperties(delete, deleteImg, width, width, 0, true);
                    }else{
                        buttonProperties(addNew, addNewImg, width, width, 16, true);
                        buttonProperties(edit, editImg, width, width, 16, true);
                        buttonProperties(delete, deleteImg, width, width, 16, true);
                    }
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                }else{
                    buttonProperties(back, backImg, 50, 50, font, false);
                    buttonProperties(search, searchImg, 50, 50, font, false);
                    buttonProperties(addNew, addNewImg, 50, 50, font, true);
                    buttonProperties(edit, editImg, 50, 50, font, true);
                    buttonProperties(delete, deleteImg, 50, 50, font, true);
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                }
            }
        });
    }
    private void buttonProperties(JButton btn, Image img, int width, int height, int font, boolean IconLeft){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));
        if(!IconLeft){
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
        }
        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, font));
        btn.setFocusPainted(false);
//        btn.addActionListener(this);
//        gridBag.setConstraints(btn,c);
    }


    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }

}

//Maybe put into contact file?? wait until working

class ContactList extends JPanel{
    ArrayList listOfContacts;
    JTextField givenNameField;
    JTextField lastNameField;
    JTextField emailField;
    JTextField phoneField;
    ContactList(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        listOfContacts = new ArrayList<Contact>();
        try {
            File contacts = new File("data/contacts.txt");
            Scanner scan = new Scanner(contacts);
            int i = 0;
            while(scan.hasNext()){
                String[] contactInfo = scan.next().split(",");
                System.out.println(contactInfo);
                Contact c = new Contact(contactInfo[0], contactInfo[1], contactInfo[2], contactInfo[3]);
                listOfContacts.add(c);
                add(contactButton(c, i));
                i++;
            }
        }catch(Exception e1){
            System.out.println(e1);
        };
    }
    public void setOutputPanel(JTextField givenNameField, JTextField lastNameField, JTextField emailField, JTextField phoneField){
        this.givenNameField = givenNameField;
        this.lastNameField = lastNameField;
        this.emailField = emailField;
        this.phoneField = phoneField;
    }
    //Use to implement searching
    public ContactList searchForContact(String search){
        return new ContactList();
    }

    public JButton contactButton(Contact c, int index){
        JButton btn = new JButton(c.getName()+" "+c.getSurname());
        btn.setMaximumSize(new Dimension(1000, 500));
        btn.setMinimumSize(new Dimension(80, 400));
        btn.setMargin(new Insets(25, 0, 25, 0));
        btn.setFont(new Font("Arial", Font.PLAIN, 32));
        btn.addActionListener(this::actionPerformed);
        btn.setActionCommand(String.valueOf(index));
        return btn;
    }
    public void actionPerformed(ActionEvent e){
        System.out.println(e);
        Contact c = (Contact) listOfContacts.get(Integer.parseInt(e.getActionCommand()));
        givenNameField.setText(c.getName());
        lastNameField.setText(c.getSurname());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
    }

}

class Contact{

    String givenName;
    String surname;
    String email;
    String phone;

    public Contact(String givenName, String surname, String email, String phone) {
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }
    public String getName() {
        return givenName;
    }

    public void setName(String name) {
        this.givenName = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}