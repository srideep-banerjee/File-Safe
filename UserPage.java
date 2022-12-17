package test;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
/**
 * This class handles all the interactions with user after verification is done.
 * @author Srideep Banerjee
 */
class UserPage extends java.awt.Frame {
    String name;
    volatile boolean rb=false;
    Cryptor c;
    DefaultListModel dlm;
    /**
     * Constructor that :<br>
     * 1. Initializes the GUI components<br>
     * 2. Decrypts the list of stored files<br>
     * 3. Loads the list of stored files to GUI
     */
    public UserPage(String nam){
        name=nam;
        dlm=new DefaultListModel();
        c=new Cryptor(nam+(nam.length()*2));
        initComponents();
        welcus.setText("Welcome, "+nam.toUpperCase());
        File dir=new File("File Safe\\User Data\\"+name.toUpperCase()+"\\names.txt");
        byte b[]=new byte[0];
        try(FileInputStream fis=new FileInputStream(dir)){
            b=fis.readAllBytes();
        }catch(IOException e){System.out.println("File can't be open.");}
        b=c.decrypt(b);
        String s="";
        int len=b.length;
        for(int i=0;i<len;i++){
            msgbox.setText("Loading...("+((i+1)/len*100)+"%)");
            if((char)b[i]=='\n'){dlm.addElement(s);s="";}
            else s=s+(char)b[i];
        }
        msgbox.setText("Loading...Done");
        if(dlm.size()==0)msgbox.setText("No files added yet.");
        fl.setModel(dlm);
        fl.addListSelectionListener((ListSelectionEvent e) -> {
            int x = fl.getSelectedIndex();
            if (x != -1) {
                clicked(x);
                fl.clearSelection();
            }
        });
    }
    /**
     * This function maps a click on the list of files to open a file or
     * remove a file from file safe based on the flag rb.
     */
    public void clicked(int ind){
        if(rb){rb=false;removeFile(ind);}
        else openFile(ind);
    }
    /**
     * This function :<br> 
     * 1.Reads and decrypts file name<br>
     * 2.Reads and decrypts the encrypted file contents<br>
     * 3.Writes the decrypted file and sets it to delete on exit.<br>
     * 4.Executes that file
     */
    void openFile(int ind){
        File f = new File("File Safe\\User Data\\"+name.toUpperCase()+"\\"+ind+".en");
        byte b[]=new byte[0];
        try(FileInputStream fis=new FileInputStream(f)){
            b=fis.readAllBytes();
        }catch(IOException e){System.out.println("File can't be open.");}
        b=c.decrypt(b);
        f=new File("File Safe\\User Data\\"+name.toUpperCase()+"\\temp");
        if(f.mkdir())f.deleteOnExit();
        f=new File(f.getPath()+"\\"+((String)dlm.get(ind)));
        try{if(!f.exists())f.createNewFile();f.deleteOnExit();}catch(IOException e){}
        try(FileOutputStream fos=new FileOutputStream(f.getPath())){
            fos.write(b);
        }catch(IOException e){}
        try{
        Desktop.getDesktop().open(f);
        this.setState(javax.swing.JFrame.ICONIFIED);
        }catch(IOException e){System.out.println("Couldn't execute file.");}
    }
    /**
     * This function :<br> 
     * 1.Selects a directory from the user using JFileChooser<br>
     * 2.Reads and decrypts the encrypted file contents<br>
     * 3.Writes the decrypted file to the selected folder<br>
     * 4.Deletes the encrypted file<br>
     * 5.Calls exitForm() to refresh file names list
     */
    void removeFile(int ind){
        File f=new File("File Safe\\User Data\\"+name.toUpperCase()+"\\"+ind+".en");
        adder.setMultiSelectionEnabled(false);
        adder.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        adder.showDialog(this,"Restore");
        File dir=adder.getSelectedFile();
        if(dir==null)return;
        int ans=JOptionPane.showConfirmDialog(this,"Restore selected file to "+dir.getName()+" ?","",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(ans!=JOptionPane.OK_OPTION)return;
        byte b[]=new byte[0];
        try(FileInputStream fis=new FileInputStream(f)){
            b=fis.readAllBytes();
        }catch(IOException e){System.out.println("File can't be open.");}
        b=c.decrypt(b);
        dir=new File(dir.getAbsolutePath()+"\\"+(String)dlm.getElementAt(ind));
        try(FileOutputStream fos=new FileOutputStream(dir)){
            fos.write(b);
        }catch(IOException e){}
        dlm.remove(ind);
        this.exitForm(null);
        f.delete();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        adder = new javax.swing.JFileChooser();
        headerPanel = new javax.swing.JPanel();
        welcus = new javax.swing.JLabel();
        fileScroller = new javax.swing.JScrollPane();
        fl = new javax.swing.JList<>();
        functionalPanel = new javax.swing.JPanel();
        rem = new javax.swing.JButton();
        add = new javax.swing.JButton();
        msgbox = new javax.swing.JLabel();

        adder.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
        adder.setApproveButtonText("Add");

        setPreferredSize(new java.awt.Dimension(700, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        headerPanel.setLayout(new java.awt.BorderLayout());

        welcus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        welcus.setText("Welcome, SRIDEEP");
        headerPanel.add(welcus, java.awt.BorderLayout.WEST);

        add(headerPanel, java.awt.BorderLayout.NORTH);

        fileScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fileScroller.setOpaque(false);

        fl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileScroller.setViewportView(fl);

        add(fileScroller, java.awt.BorderLayout.CENTER);

        functionalPanel.setLayout(new java.awt.BorderLayout());

        rem.setText("Remove");
        rem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remover(evt);
            }
        });
        functionalPanel.add(rem, java.awt.BorderLayout.EAST);

        add.setText("   Add   ");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adr(evt);
            }
        });
        functionalPanel.add(add, java.awt.BorderLayout.WEST);

        msgbox.setText("Loading...");
        functionalPanel.add(msgbox, java.awt.BorderLayout.CENTER);

        add(functionalPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * This function is called at application close or file removal to
     * encrypt and rewrite the new list of files.
     * @param evt if set to null then does nothing, 
     * otherwise exits the program.
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        String s="";
        for(int i=0;i<dlm.size();i++)s=s+dlm.get(i)+'\n';
        try(FileOutputStream fos=new FileOutputStream("File Safe\\User Data\\"+name.toUpperCase()+"\\names.txt")){
            fos.write(c.encrypt(s.getBytes()));
        }catch(IOException e){}
        if(evt!=null)System.exit(0);
    }//GEN-LAST:event_exitForm
    /**
     * This function is called when "Add" button is clicked, it:-<br>
     * 1.Selects file(s) from the user using JFileChooser<br>
     * 2.Reads those files<br>
     * 3.Encrypts those files<br>
     * 4.Write those encrypted files<br>
     * 5.Deletes the original files.
     * @param evt An unused parameter
     */
    private void adr(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adr
        adder.setMultiSelectionEnabled(true);
        adder.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        adder.showDialog(this,"Add");
        File fa[]=adder.getSelectedFiles();
        if(fa.length==0)return;
        int ans=JOptionPane.showConfirmDialog(this,"Move "+fa.length+"files(s) to File Safe ?","",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(ans!=JOptionPane.OK_OPTION)return;
        for(File f:fa){
            byte b[]=new byte[0];
            try(FileInputStream fis=new FileInputStream(f)){
                b=fis.readAllBytes();
            }catch(IOException e){System.out.println("File can't be open.");}
            b=c.encrypt(b);
            try(FileOutputStream fos=new FileOutputStream("File Safe\\User Data\\"+name.toUpperCase()+"\\"+dlm.size()+".en")){
                fos.write(b);
            }catch(IOException e){}
            dlm.addElement(f.getName());
            f.delete();
            this.exitForm(null);
        }
    }//GEN-LAST:event_adr
    /**
     * This function is called when "Remove" button is pressed.
     * It sets the flag rb to true for removing a file.
     * @param evt 
     */
    private void remover(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remover
        msgbox.setText("click on a file to restore");
        rb=true;
    }//GEN-LAST:event_remover

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JFileChooser adder;
    private javax.swing.JScrollPane fileScroller;
    private javax.swing.JList<String> fl;
    private javax.swing.JPanel functionalPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel msgbox;
    private javax.swing.JButton rem;
    private javax.swing.JLabel welcus;
    // End of variables declaration//GEN-END:variables
}
