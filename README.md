# Academic-Support-System
The Academic Support System is a GUI application I have built as part of my University topics. This application helps an academic upload their research papers, articles, books or journals to multiple different services at once. It has the following features


-Upload: upload files with associated contacts to a selection of services.

-Save: Save an unfinished upload so as to edit or upload at a later date

-Contacts: Contacts can be saved in an address book with simple details stored. Each contact can be associated with an upload as an author.

-View History: A log of user actions are kept and are able to be viewed by the academic, with optional filters.

The implementation utilised the IntelliJ IDE as the working environment and Java Swing for it's GUI elements. SQLite is used in the database component of the application. From this point on I will discuss some of the notable components that have been added into the application in terms of implementation.

The file structure has been organised into related folders, with panel containing most of the GUI components. The main class is the AssDriver class. This initiates the cardLayout that I utilise to contain and link all the different views of the program in one window. EditPanel, HistoryPanel and ContactPanel all inherit the TwoPanel class. This class provides a basic design language by which all views follow. This design language is analogous to how a book should be read, reading left to right as you go down a page, then moving to the top left of the next page. Another class that is used for consistent design language is the ComProps class. This details the common properties of swing components so that they can be applied readily and easily. These are mostly implemented inside the DocumentListener instances found inside each of the panels. The document listener is designed to allow the components inside the window to change size depending on the size of the window. They scale according to a simple formula and is described in the code. 

Upload panel is the most complex of all the files and it is recommended that this class be analysed last as a lot of the implemented items can be seen in a simpler form inside other classes. One of the reasons for the complex nature of this panel is due to the amount of layout managers and panels that were used. GridBagLayout was used mostly and while this is powerful, it can be very fiddly to get everything perfectly ordered. Each component can have sometimes up to 5 lines of code to just set the layout of it, leading to the bloated nature of some of the code.

I have implemented a simple database utilising JDBC and SQLite, that saves contacts, uploads, templates and logs to file in data/database.db. Each of these items have a dedicated DB class which is in its respective folder. They can all be instantiated, using their namesake class so that instance variables contain the data retrieved from the database. Each of these folders(except for upload) then contains a class detailing a JPanel through which the instance can be displayed. These are used to be easily displayed on the right side of the panels inheriting TwoPanel. A database with some dummy data is provided to show some examples of the features working. In the backup folder, other example databases can be used if desired.

I have utilised a suffix trie which I had personally worked on in another topic (COMP3712). The skeleton code of which was provided by Trent Lewis, but the implementation is that of my own. The suffix trie is used to allow for a user-friendly search bar, that enables searching of contacts inside the ContactPanel or searching uploads inside the EditPanel. The search bar will auto update the list found below with the results of the search. A not found message is shown if the search result was 0.

In the UploadPanel, A file can be dragged into the designated area to be added to the associated upload. This was implemented using the TransferHandler class. The drop is treated as a Transfer Support instance which is then changes to an instance of Transferable. From this, the data can be retrieved using the DataFlavor.javaFileListFlavor. This returns a list allowing for multiple files to added at once via the drag n drop. This also works in tandem with the choose a file button to give users multiple options of adding files. The added fields are displayed in a list and a ListCellRenderer is used to ensure that only the filename is shown. This list cell renderer also implements that any null value inside the list be set as the message notifying the ‘Drag a file to attach’ message. When the list is initialised, a null value is manually passed in to allow for this to occur.

The datePicker component can be found in java.org.jdatepicker. This is an external component that was found on GitHub and was utilised to provide an error free entry that was intuitive for the user to use. It is shown in the UploadPanel view and the HistoryPanel view for date selection. This provided an unforeseen challenge with conversion between java.util.date and java.sql.date which was eventually overcome by converting to an Instant, then to a LocalDate, then to java.sql.date.
