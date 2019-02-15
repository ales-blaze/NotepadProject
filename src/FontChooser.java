import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FontChooser extends JPanel {
    private final JList<String> jfontLists;
    private final String[] fontType;
    private final JList<String> jFontStyle;
    private final JList<String> jFontSize;
    private final JTextArea textArea;
    private Font font;
    private boolean ok;
    private JDialog dialog;
    private final JButton okButton;
    private final JButton cancelButton;

    //create a new constructor which take font as parameter
    public FontChooser(Font font){
        this.font = font;
        // 'et all the available fonts in the local system as array of strin'
        String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        //creatin' a list of fonts in the 'ui
        jfontLists = new JList<String>(fontList);
        jfontLists.setSelectedIndex(0);

        jfontLists.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                textArea.setFont(createFont());
            }
        });
        fontType = new String[]{"Regular", "Bold", "Italics", "Bold Italics"};

        jFontStyle = new JList<String>(fontType);
        jFontStyle.setSelectedIndex(0);

        jFontStyle.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
               textArea.setFont(createFont());
            }
        });

        String[] fontSize = new String[30];
        for(int index = 0 ; index < fontSize.length ;index++){
            fontSize = new String[10 + index * 2];
        }
        jFontSize = new JList<String>(fontSize);
        jFontSize.setSelectedIndex(0);

        jFontSize.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                textArea.setFont(createFont());
            }
        });

        // Create a panel that will contian labels for lists
        JPanel jLabelPanel = new JPanel();
        jLabelPanel.setLayout(new GridLayout(1 , 3));
        jLabelPanel.add(new JLabel("Font") , JLabel.CENTER);
        jLabelPanel.add(new JLabel("Font Style" , JLabel.CENTER));
        jLabelPanel.add(new JLabel("Font Size" , JLabel.CENTER));

        JPanel jListPanel = new JPanel();
        jListPanel.setLayout(new GridLayout(1 ,3));
        jListPanel.add(new JScrollPane(jfontLists));
        jListPanel.add(new JScrollPane(jFontStyle));
        jListPanel.add(new JScrollPane(jFontSize));

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        //add action listeners
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok = true;
                FontChooser.this.font = FontChooser.this.createFont();
                dialog.setVisible(false);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        //added label and lists in  the dialo'
        //addin' buttons to panel and panel to dialo'
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(new JLabel("    "));
        buttonPanel.add(cancelButton);

        textArea = new JTextArea(5 ,30);

        //create a panel to contain text Area
        JPanel jTextAreaPanel = new JPanel();
        jTextAreaPanel.add(new JScrollPane(textArea));

        JPanel centralPanel = new JPanel(new GridLayout(3, 1));
        // added by me
        centralPanel.add(jLabelPanel);
        //-------
        centralPanel.add(jListPanel);
        centralPanel.add(jTextAreaPanel);

        this.setLayout(new BorderLayout());

        this.add(centralPanel , BorderLayout.CENTER);
        this.add(buttonPanel , BorderLayout.SOUTH);
        this.add(new JLabel(" ") , BorderLayout.EAST);
        this.add(new JLabel(" ") , BorderLayout.WEST);
        textArea.setFont(font);
    }

    public Font createFont() {
        Font font = this.font;
        int fontStyle = Font.PLAIN;
        int selectedStyle = jFontStyle.getSelectedIndex();

        switch (selectedStyle){
            case 0:
                fontStyle = Font.PLAIN;
                break;
            case 1:
                fontStyle = Font.BOLD;
                break;
            case 2:
                fontStyle = Font.ITALIC;
                break;
            case 3:
                fontStyle = Font.BOLD + Font.ITALIC;
                break;
                default:
                    fontStyle = Font.PLAIN;
        }
        int fontSize =  Integer.parseInt(jFontSize.getSelectedValue());

        String fontName = jfontLists.getSelectedValue();

        font = new Font(fontName , fontStyle , fontSize);
        return font;
    }
    public boolean showDialog(Component parent , String title){
        ok = false;
        Frame owner = null;

        if(parent instanceof Frame){
            owner = (Frame) parent;
        }
        else
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class , parent );
        if(dialog == null || dialog.getOwner() != owner){
            dialog = new JDialog(owner , true);
            dialog.add(this);
            dialog.getRootPane().setDefaultButton(okButton);
            dialog.setSize(400 , 325);
        }
        dialog.setTitle(title);
        dialog.setVisible(true);
        ok = true;
        return ok;
    }
}