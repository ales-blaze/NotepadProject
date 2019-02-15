import com.sun.deploy.panel.JreFindDialog;
import javafx.scene.control.Labeled;

import javax.security.auth.kerberos.KerberosTicket;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.logging.FileHandler;

//o to opertion is probelmatic and cancel opption is oproblematic also

public class Notepad implements MenuConstants, ActionListener {

    private String applicationName;
    public JLabel statusBar;
    String fileName;
    JFrame frame;
    JTextArea textArea;
    private JCheckBoxMenuItem wordWrap;
    private JMenuItem cutItem;
    private JMenuItem copyItem;
    private JMenuItem deleteItem;
    private JMenuItem findItem;
    private JMenuItem findNextItem;
    private JMenuItem replaceItem;
    private JMenuItem selectAllItem;
    private JMenuItem gotoItem;
    private JMenuItem pasteItem;
    private FileOperation fileHandler;
    private FindDialog findReplaceDialog;
    private JColorChooser fColorChooser;
    private JColorChooser bcolorChooser;
    private JDialog foregroundDialog;
    private JDialog backroundDialog;
    private FontChooser fontChooser;

    Notepad(){
        //new frame
        frame = new JFrame(fileName + " - " + applicationName);
        //new text area
        textArea = new JTextArea(30 ,60);
        //new status bar
        statusBar = new JLabel("|| Line : 1 , Column : 1" ,JLabel.RIGHT);
        //addin layout to the frame
        //finded error frame.add(new Scr    ollPane(textArea), BorderLayout.CENTER);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        //addin status bar
        frame.add(statusBar,BorderLayout.SOUTH);
        //create dummy node on the east ede and west ede
        frame.add(new JLabel(" "), BorderLayout.EAST);
        frame.add(new JLabel(" "), BorderLayout.WEST);
        createMenuBar(frame);
        //readin of pack function is left , yet to read
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocation(150 , 50);
        frame.setVisible(true);

        fileHandler = new FileOperation(Notepad.this);

        //addin some Listeners to the textArea
        //1. CaretListener
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int position = 0;
                int lineNumber = 0 , column = 0;
                try {
                    position = textArea.getCaretPosition();
                    lineNumber = textArea.getLineOfOffset(position);
                    column = position - textArea.getLineStartOffset(lineNumber);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                if(textArea.getText().length() == 0){
                    lineNumber = 0 ; column = 0;
                }
                statusBar.setText("||    Line :" + (lineNumber + 1) + " , Column :" + (column + 1));
            }
        });

        //2.Document listener , changes the state of the saved var , when any change in the document occurs
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fileHandler.setSaved(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fileHandler.setSaved(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fileHandler.setSaved(false);
            }
        };
        textArea.getDocument().addDocumentListener(docListener);

        WindowListener windowListener = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if(fileHandler.confirmSave()){
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        };
        frame.addWindowListener(windowListener);
    }
    /**
     *
     * @param frame
     */
    void createMenuBar(JFrame frame){
        //create a new MenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenuItem temp;

        //create menu First
        JMenu fileMenu = createMenu(fileText , KeyEvent.VK_F,menuBar);
        JMenu editMenu = createMenu(editText , KeyEvent.VK_E , menuBar);
        JMenu formatMenu = createMenu(formatText , KeyEvent.VK_F , menuBar);
        createMenuItem(fileNew , KeyEvent.VK_N , fileMenu , KeyEvent.VK_N , this);
//        createMenuItem(fileOpen , KeyEvent.VK_O ,fileMenu , KeyEvent.VK_O , this);
        createMenuItem(fileSave , KeyEvent.VK_S ,fileMenu , KeyEvent.VK_S , this);
        createMenuItem(fileOpen , KeyEvent.VK_O ,fileMenu , KeyEvent.VK_O  , this);
        createMenuItem(fileSaveAs , KeyEvent.VK_A , fileMenu , KeyEvent.VK_A ,  this);
        createMenuItem(fileExit , KeyEvent.VK_X , fileMenu , this);

        //create Edit menu with menuitems
        //create undo menu item
        this.createMenuItem(editUndo , KeyEvent.VK_U , editMenu , KeyEvent.VK_Z , this ).setEnabled(false);
        editMenu.addSeparator();
        cutItem = createMenuItem(editCut , KeyEvent.VK_T , editMenu , KeyEvent.VK_X , this);
        copyItem = createMenuItem(editCopy , KeyEvent.VK_C , editMenu , KeyEvent.VK_C , this);
        pasteItem = createMenuItem(editPaste , KeyEvent.VK_P , editMenu , KeyEvent.VK_V , this);

        deleteItem = createMenuItem(editDelete , KeyEvent.VK_L , editMenu ,this);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE , 0));
        editMenu.addSeparator();
        findItem = createMenuItem(editFind , KeyEvent.VK_F , editMenu , KeyEvent.VK_F , this);
        findNextItem = createMenuItem(editFindNext , KeyEvent.VK_N ,editMenu , this);
        findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3 , 0));
        replaceItem = createMenuItem(editReplace , KeyEvent.VK_R , editMenu ,KeyEvent.VK_H , this);
        gotoItem = createMenuItem(editGoTo , KeyEvent.VK_G , editMenu , KeyEvent.VK_G , this);
        editMenu.addSeparator();
        selectAllItem = createMenuItem(editSelectAll , KeyEvent.VK_A , editMenu , KeyEvent.VK_A , this);
        createMenuItem(editDataAndTime , KeyEvent.VK_D , editMenu ,this).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5 , 0));

        //creating format menuitems
        createCheckBoxMenuItem(formatWordWrap , KeyEvent.VK_W , formatMenu , this);
        createMenuItem(formatFont , KeyEvent.VK_F , formatMenu , this );
        formatMenu.addSeparator();
        createMenuItem(formatForeground , KeyEvent.VK_T , formatMenu , this);
        createMenuItem(formatBackground , KeyEvent.VK_P , formatMenu ,this);
//        createCheckBoxMenuItem(viewStatusBar , KeyEvent.VK_S , viewMenu , this).setSelected(true);
        //adding menuListener
        MenuListener menuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(Notepad.this.textArea.getText().length() == 0){
                    findItem.setEnabled(false);
                    findNextItem.setEnabled(false);
                    replaceItem.setEnabled(false);
                    selectAllItem.setEnabled(false);
                    gotoItem.setEnabled(false);
                }
                else{
                    findItem.setEnabled(true);
                    findNextItem.setEnabled(true);
                    replaceItem.setEnabled(true);
                    selectAllItem.setEnabled(true);
                    gotoItem.setEnabled(true);
                }
                if(Notepad.this.textArea.getSelectionStart() == textArea.getSelectionEnd()){
                    cutItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    //pasteItem.setEnabled(false);
                }
                else{
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    pasteItem.setEnabled(true);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        };
        editMenu.addMenuListener(menuListener);
        frame.setJMenuBar(menuBar);
    }

    /**
     *
     * @param name
     * @param key
     * @param toMenuBar
     * @return
     */
    JMenu createMenu(String name ,int key ,JMenuBar toMenuBar){
        JMenu menu = new JMenu(name);
        menu.setMnemonic(key);
        toMenuBar.add(menu);
        return menu;
    }

    /**
     *
     * @param name
     * @param key
     * @param toMenu
     * @param actionListener
     * @return
     */
    JCheckBoxMenuItem createCheckBoxMenuItem(String name , int key , JMenu toMenu , ActionListener actionListener ){
        JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(name);
        checkBox.setMnemonic(key);
        checkBox.addActionListener(actionListener);
        checkBox.setSelected(false);
        toMenu.add(checkBox);
        return checkBox;
    }
    /**
     *
     * @param name Name of the menu Item
     * @param key  shortcut key for the menuItem
     * @param actionListener  action listener for detecting user clicks
     * @param toMenu    Menu to which menuItem mst be added
     * @return returns the desired menuItem
     */
    JMenuItem createMenuItem(String name , int key , JMenu toMenu , ActionListener actionListener){
        //create a menuItem
        JMenuItem newItem = new JMenuItem(name , key);
        //added listener to it
        newItem.addActionListener(actionListener);
        //added menuitem to the Menu
        toMenu.add(newItem);
        return newItem;
    }

    /**
     *
     * @param name
     * @param key
     * @param toMenu
     * @param aclKey
     * @param actionListener
     * @return
     */
    JMenuItem createMenuItem(String name , int key , JMenu toMenu ,int aclKey , ActionListener actionListener){
        JMenuItem menuItem = new JMenuItem(name , key);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(aclKey , ActionEvent.CTRL_MASK));
        menuItem.addActionListener(actionListener);
        toMenu.add(menuItem);
        return menuItem;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if(command.equals(fileNew)){
            fileHandler.newFile();
        }
        else if(command.equals(fileOpen)){
            fileHandler.openFile();
        }
        else if(command.equals(fileSave)){
            fileHandler.saveFile();
        }
        else if(command.equals(fileSaveAs)){
            fileHandler.saveFile();
        }
        else if(command.equals(fileExit)){
            if(fileHandler.confirmSave()){
                System.exit(0);
            }
        }
        else if(command.equals(editCopy)){
            textArea.copy();
        }
        else if(command.equals(editCut)){
            textArea.cut();
        }
        else if(command.equals(editPaste)){
            textArea.paste();
        }
        else if(command.equals(editDelete)){
            textArea.replaceSelection("");
        }
        else if(command.equals(editFind)){
            if(textArea.getText().length() == 0)
                return;
            if(findReplaceDialog == null){
                findReplaceDialog = new FindDialog(Notepad.this.textArea);
            }
            findReplaceDialog.showDialog(this.frame, true);
        }

        else if(command.equals(editFindNext)){
            if(textArea.getText().length() == 0)
                return;
            if(findReplaceDialog == null)
                statusBar.setText("nothing to Find , First use Find Option!");
            else findReplaceDialog.findNextWithSelection();
        }
        else if(command.equals(editReplace)){
            //yet to implement
            if(textArea.getText().length() == 0) return;
            if(findReplaceDialog == null) findReplaceDialog = new FindDialog(textArea);
            findReplaceDialog.showDialog(this.frame , false);
        }
        else if(command.equals(editGoTo)){
            if(Notepad.this.textArea.getText() == "") return;
            goTo();
        }
        else if(command.equals(editSelectAll)){
            this.textArea.selectAll();
        }

        else if(command.equals(editDataAndTime)){
            textArea.insert(new Date().toString() , textArea.getSelectionStart());
        }

        else if(command.equals(formatWordWrap)){
            JCheckBoxMenuItem wordWrap = (JCheckBoxMenuItem)event.getSource();
            textArea.setLineWrap(wordWrap.isSelected());
        }

        else if(command.equals(formatFont)){
            if (fontChooser == null)
            fontChooser = new FontChooser(textArea.getFont());

            if( fontChooser.showDialog(this.frame, "Choose Font")){
                this.textArea.setFont(fontChooser.createFont());
            }
        }
        else if(command.equals(formatForeground)){
            showForegroundColourDialog();
        }
        else if (command.equals(formatBackground)){
            showBackgroundColourDialog();
        }
        else{
            statusBar.setText("Command not Avaliable!");
        }
    }

    private void showBackgroundColourDialog() {
        if(bcolorChooser == null) bcolorChooser = new JColorChooser();
        if(backroundDialog == null) backroundDialog = JColorChooser.createDialog(this.frame, formatBackground, false, bcolorChooser, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Notepad.this.textArea.setBackground(bcolorChooser.getColor());
            }
        },null );
        backroundDialog.setVisible(true);
    }

    private void showForegroundColourDialog() {
        if(fColorChooser == null) fColorChooser = new JColorChooser();
        if(foregroundDialog == null) {
            foregroundDialog = JColorChooser.createDialog(this.frame, formatForeground, false, fColorChooser, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Notepad.this.textArea.setForeground(fColorChooser.getColor());
                }
            } , null);
        }

        foregroundDialog.setVisible(true);
    }

    private void goTo() {
        int lineNumber = 0;
        try {
            lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
            String tempStr = JOptionPane.showInputDialog(this.frame , "Enter Line Number :" , "" + lineNumber );
            if(tempStr == null) return;
            lineNumber = Integer.parseInt(tempStr);
            textArea.setCaretPosition(textArea.getLineStartOffset(lineNumber - 1));
//            textArea.setCaretPosition(textArea.getLineStartOffset(lineNumber - 1));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Notepad();
    }
}



interface MenuConstants{

    //Menu Entries
    final String fileText = "File";
    final String editText = "Edit";
    final String viewText = "View";
    final String formatText = "format";

    //file menu items
    final String fileNew = "New";
    final String fileOpen = "Open";
    final String fileSave = "Save";
    final  String fileSaveAs = "Save As..";
    final String filePageSetup = "Page Setup...";
    final String filePrint = "Print";
    final String fileExit = "Exit";

    //Edit menu items
    final String editUndo = "Undo";
    final String editCut = "Cut";
    final String editCopy = "Copy";
    final String editPaste = "Paste";
    final String editDelete = "Delete";
    final String editFind = "Find...";
    final String editFindNext = "Find Next";
    final String editReplace = "Replace";
    final String editGoTo = "Goto";
    final String editSelectAll = "Select All";
    final String editDataAndTime = "Date and Time";


    //Format menu items
    final String formatWordWrap = "WordWrap";
    final String formatFont = "Font...";
    final String formatForeground = "Set Text Colour...";
    final String formatBackground = "Set Pad Colour...";

    final String viewStatusBar = "Status Bar";
}

