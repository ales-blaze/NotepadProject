import javafx.scene.layout.GridPane;

import javax.swing.*;
import javax.swing.border.Border;
//import javax.swing.text.html.Option;
import java.awt.*;
import java.awt.event.*;

public class FindDialog extends JPanel implements ActionListener {
    private final JButton findNextButton;
    JTextArea textArea;
//    public int lastIndex;
    JLabel replaceLabel;

    private TextField findWhat;
    private JTextField replaceWith;
    private JFrame dial;
    private JCheckBox matchCase;
    JRadioButton fromTop , fromBottom;
    Button findButton;
    JButton replaceButton;
    JButton replaceAllButton;
    JButton cancelButton;

    JPanel direction , buttonPanel , findButtonPanel , replaceButtonpanel;
    CardLayout cardLayout;
    private boolean ok;
    JDialog dialog;
    private int lastIndex;

    public FindDialog(JTextArea textArea){
//        dial = new JFrame("Find");
        this.textArea = textArea;
        findWhat = new TextField(20);
        replaceWith = new JTextField(20);
        matchCase = new JCheckBox("Match-Case");
        fromTop = new JRadioButton("From Top to Bottom");
        fromBottom = new JRadioButton("From Bottom to Top");
        fromTop.setSelected(true);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(fromBottom);
        buttonGroup.add(fromTop);

        Border betched = BorderFactory.createEtchedBorder();
        Border titled = BorderFactory.createTitledBorder(betched , "Direction");
        direction = new JPanel(new GridLayout(1,2));
        direction.setBorder(titled);
//        direction.add(buttonGroup);
        direction.add(fromTop);
        direction.add(fromBottom);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1,2));
        southPanel.add(matchCase);
        southPanel.add(direction);

//        findButton = new Button("Find");
        findNextButton = new JButton("Find Next");
        replaceButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");
        cancelButton = new JButton("Cancel");


        replaceButtonpanel = new JPanel(new GridLayout(4,1));
        replaceButtonpanel.add(findNextButton);
        replaceButtonpanel.add(replaceButton);
        replaceButtonpanel.add(replaceAllButton);
        replaceButtonpanel.add(cancelButton);

        JPanel textPanel = new JPanel(new GridLayout(3,2));
        textPanel.add(new JLabel("Find"));
        textPanel.add(findWhat);
        textPanel.add(replaceLabel = new JLabel("Replace"));
        textPanel.add(replaceWith);

        this.setLayout(new BorderLayout());
        this.setSize(200,200);

        this.add(new JLabel("    "),BorderLayout.NORTH);
        this.add(textPanel ,BorderLayout.CENTER);
        this.add(replaceButtonpanel, BorderLayout.EAST);
        this.add(southPanel , BorderLayout.SOUTH);

        this.setVisible(true);
        //add listeners to the button
        findNextButton.addActionListener(this);
        replaceButton.addActionListener(this);
        replaceAllButton.addActionListener(this);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        findWhat.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                enableDisableButtons();
            }
        });
        findWhat.addTextListener(new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                enableDisableButtons();
            }
        });
    }

    private void enableDisableButtons() {
        if(findWhat.getText().length() == 0){
            findNextButton.setEnabled(false);
            replaceButton.setEnabled(false);
            replaceAllButton.setEnabled(false);
        }
        else{
            findNextButton.setEnabled(true);
            replaceButton.setEnabled(true);
            replaceAllButton.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() == findNextButton){
            findNextWithSelection();
        }
        else if(event.getSource() == replaceButton){
            replaceNext();
        }
        else if(event.getSource() == replaceAllButton){
            JOptionPane.showMessageDialog(this , "Total replacemenst made : " + replaceAllNext());
        }
    }

    private int replaceAllNext() {
        if(fromBottom.isSelected()) {
            textArea.setCaretPosition(textArea.getText().length() - 1);
        }
        else if(fromTop.isSelected()){
            textArea.setCaretPosition(0);
        }

        int index = 0;
        int counter = 0;
        do{
           index = findNext();
           if(index == -1) break;
           counter++;
           textArea.replaceRange(replaceWith.getText() , index , index + findWhat.getText().length());
        }while(index != -1);
        return counter;
    }

    public void replaceNext() {
        if(textArea.getSelectionStart() == textArea.getSelectionEnd()){
            findNextWithSelection();
            return;
        }

        String searchText = findWhat.getText();
        String tempTextArea = textArea.getSelectedText();

        if(matchCase.isSelected() && tempTextArea.equals(searchText) || (!matchCase.isSelected() && tempTextArea.equalsIgnoreCase(searchText))){
        textArea.replaceSelection(replaceWith.getText());
        findNextWithSelection();
        }
    }

    public void findNextWithSelection() {
        int index = findNext();

        if(index != -1){
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + findWhat.getText().length());
        }
        else{
            if(fromTop.isSelected()){
                //should lastIndex be altered?
                textArea.setCaretPosition(0);
            }
            else if(fromBottom.isSelected()){
                lastIndex = textArea.getText().length() -1;
                textArea.setCaretPosition(textArea.getText().length() - 1);
            }
        }
    }

    public int findNext(){
        String first  = textArea.getText();
        String second = findWhat.getText();
        int selectionStartIndex = textArea.getSelectionStart();
        int selectionEndIndex = textArea.getSelectionEnd();
        int caretPosition = textArea.getCaretPosition();
        if(fromTop.isSelected()){
            if(selectionStartIndex != selectionEndIndex){
                lastIndex = selectionStartIndex + 1;
            }
            if(!matchCase.isSelected()){
                lastIndex = first.toUpperCase().indexOf(second.toUpperCase() , lastIndex);
            }
            else{
                lastIndex = first.indexOf(second , lastIndex);
            }
        }
        else if (fromBottom.isSelected()){
            if(selectionStartIndex != selectionEndIndex){
                lastIndex = selectionEndIndex - second.length() - 1;
            }
            if(!matchCase.isSelected()){
                lastIndex = first.toUpperCase().lastIndexOf(second.toUpperCase() , lastIndex);
            }
            else{
                lastIndex = first.lastIndexOf(second , lastIndex);
            }
        }
        return lastIndex;
    }
   /* public int findNext() {
        String first = textArea.getText();
        String second = findWhat.getText();
//        textArea.setCaretPosition(0);
        lastIndex = 0;

        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();

        if(fromBottom.isSelected()){
            if(selectionStart != selectionEnd){
                lastIndex = selectionEnd - second.length() - 1;
            }
            if(!matchCase.isSelected()){
                //need to think of it
                lastIndex = first.toUpperCase().lastIndexOf(second.toUpperCase(),lastIndex);
            }
            else{
                lastIndex = first.lastIndexOf(second , lastIndex);
            }
        }
        else if(fromTop.isSelected()){
            if(selectionEnd != selectionStart){
                lastIndex = selectionStart + 1;
            }
            if(!matchCase.isSelected()){
                lastIndex = first.toUpperCase().indexOf(second.toUpperCase() , lastIndex);
            }
            else{
                lastIndex = first.indexOf(second , lastIndex);
            }
        }
        return lastIndex;
    }*/

    public boolean showDialog(Component parent , boolean isFind){
        Frame owner = null;
        if(owner instanceof Frame)
            owner = (Frame) parent;
        else{
            //don't know about this code line below
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class , parent);
        }
        if(dialog == null || dialog.getOwner() != owner){
            dialog = new JDialog(owner , false);
            dialog.add(this);
            dialog.getRootPane().setDefaultButton(findNextButton);
        }
        if(findWhat.getText().length() == 0){
            findNextButton.setEnabled(false);
        }
        else{
            findNextButton.setEnabled(true);
        }

        replaceAllButton.setVisible(false);
        replaceButton.setVisible(false);
        replaceWith.setVisible(false);
        replaceLabel.setVisible(false);

        if(isFind){
            dialog.setSize(400 , 180);
            dialog.setTitle("Find");
        }
        else{
            replaceButton.setVisible(true);
            replaceAllButton.setVisible(true);
            replaceWith.setVisible(true);
            replaceLabel.setVisible(true);

            dialog.setSize(400 , 200);
            dialog.setTitle("Replace");
        }

        dialog.setVisible(true);
        return ok;
    }
}
