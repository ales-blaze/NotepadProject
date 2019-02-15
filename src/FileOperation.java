import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class FileOperation {
    Notepad notepad;
    File fileRefernece;
    JFileChooser fileChooser;
    private String fileName ;
    String applicationName = "NotePad";

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    private boolean saved , newFileFlag;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileOperation(Notepad notepad){
        this.notepad = notepad;
        newFileFlag = true;
        saved = true;
        fileName = "Untitled";
        fileRefernece = new File(fileName);
        this.notepad.frame.setTitle(applicationName + " - " + fileName);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
    }

    public boolean saveFile(){
        if(!newFileFlag){
           return save(fileRefernece);
        }
        return saveAs();
    }

    private boolean saveAs() {
        File file = null;
        fileChooser.setDialogTitle("Save File...");
        fileChooser.setApproveButtonText("Save");
        fileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);
        fileChooser.setApproveButtonToolTipText("Click me to save file!");
        do{
            if(fileChooser.showSaveDialog(this.notepad.frame) != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            file = fileChooser.getSelectedFile();
            //if file doesn't exist , create new file and then save it
            if(!file.exists()) break;
            if(JOptionPane.showConfirmDialog(this.notepad.frame ,"<html>" + /*added .getName() instead of .getPath()*/file.getName() + "file already exists <br> Do you want to replace it? <html>","Save As", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                break;
        }while(true);

//        updateStatus(file , true);
        return save(file);
    }

    public boolean save(File file){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            if(file != null){
                bufferedWriter.write(this.notepad.textArea.getText());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        updateStatus(file, true);
        return true;
    }

    //open file operation
    public boolean openFile(File file){
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line = null;
            while((line = bufferedReader.readLine()) != null){
                this.notepad.textArea.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        updateStatus(file , true);
        this.notepad.textArea.setCaretPosition(0);
        return true;
    }

    private void updateStatus(File file, boolean saved) {
        if(saved){
            this.saved = saved;
            fileName = file.getName();
            if(!file.canWrite()){
                fileName += " (Read-Only)";
                newFileFlag = true;
            }
            this.fileRefernece = file;
            this.notepad.frame.setTitle(fileName + "-" + applicationName);
            this.notepad.statusBar.setText("File:" + file.getPath() + "saved/opened successfully.");
            newFileFlag = false;
        }
        else{
            this.notepad.statusBar.setText("Failed to open/save: " + file.getPath());
        }
    }

    void openFile(){
        File file = null;
        if(!confirmSave()) return;
        fileChooser.setDialogTitle("Open FIle");
        fileChooser.setApproveButtonText("Open");
        fileChooser.setApproveButtonMnemonic(KeyEvent.VK_O);
        fileChooser.setApproveButtonToolTipText("Click me to open selected file");

        do {
            if (fileChooser.showOpenDialog(this.notepad.frame) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = fileChooser.getSelectedFile();

            if (file.exists()) {
                break;
            }
            //file doesn't exist
            JOptionPane.showConfirmDialog(this.notepad.frame , "<html>" + file.getName() + "<br> File Not Found <br> Please verify correct file name was given.<html>" , "Open" , JOptionPane.INFORMATION_MESSAGE);
            this.notepad.textArea.setText("");
        }while(true);

        if(!openFile(file)){
            fileName = "Untitled" ;
            saved = true;
            this.notepad.frame.setTitle( applicationName + '-' + fileName);
        }
        if(!file.canWrite()){
            newFileFlag = true;
        }
    }

    boolean confirmSave(){
        String message = "<html> Text in the file" + this.fileRefernece.getPath() + " has been changed !<br> Do you want to save changes to the file?<html>";
        if(!saved){
            int isSave = JOptionPane.showConfirmDialog(this.notepad.frame , message , applicationName , JOptionPane.YES_NO_CANCEL_OPTION);

            if(isSave == JOptionPane.CANCEL_OPTION) return false;
            if(isSave == JOptionPane.YES_OPTION && !saveFile()) return false;
        }
        return true;
    }

    void newFile(){
        if(confirmSave()) {
            this.notepad.textArea.setText("");
            fileName = "Untitled";
            fileRefernece = new File(fileName);
            saved = true;
            newFileFlag = true;
            this.notepad.frame.setTitle(fileName + "-" + applicationName);
        }
        else return;
    }
}

