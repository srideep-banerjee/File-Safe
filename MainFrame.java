package test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
/**
 * Main class of the project that launches the initial GUI and handles the face
 * recognition, user profile creation, user authentication and their related 
 * file handling.
 * @author Srideep Banerjee
 */
public class MainFrame extends CanvasFrame {
    static volatile int tsk=0;
    OpenCVFrameGrabber og;
    CascadeClassifier cc;
    LBPHFaceRecognizer fr;
    static ArrayList<String> names=new ArrayList();
    /**
     * Constructor that initializes the GUI components
     */
    public MainFrame(String s) {
        super(s);
        setVisible(false);
        var v=getContentPane().getSize();
        cc=new CascadeClassifier();
        fr=LBPHFaceRecognizer.create();
        initComponents();
        lb.setSize(v);
        setCanvasSize(v.width,v.height-jp.getSize().height);
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jp = new javax.swing.JPanel();
        lb = new java.awt.Label();
        vb = new javax.swing.JButton();
        ad = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(0, 0));
        setPreferredSize(new java.awt.Dimension(700, 500));

        jp.setLayout(new java.awt.GridBagLayout());

        lb.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lb.setMinimumSize(new java.awt.Dimension(100, 18));
        lb.setText("Starting Camera...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.6;
        jp.add(lb, gridBagConstraints);

        vb.setText("Verify");
        vb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verify(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        jp.add(vb, gridBagConstraints);

        ad.setText("Add");
        ad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        jp.add(ad, gridBagConstraints);

        getContentPane().add(jp, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void verify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verify
        tsk=2;
    }//GEN-LAST:event_verify

    private void add(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add
        tsk=1;
    }//GEN-LAST:event_add
    /**
     * This function resizes the image to 200 by 200 
     * and converts the resized image to gray scale for training the recognizer.
     * @param m The Mat object representing the input image to be converted
     */
    public void processFaceImage(Mat m){
        Size s=new Size(200,200);
        opencv_imgproc.resize(m, m, s);
        opencv_imgproc.cvtColor(m, m,opencv_imgproc.COLOR_BGR2GRAY);
    }
    /**
     * This function adds a new user.
     * It takes 10 valid images of the user 
     * and uses them to train the recognizer
     */
    public void addFace(){
        lb.setText("Adding face...");
        String name=JOptionPane.showInputDialog(this,"Enter your name: ");
        if(name==null)return;
        else if(name.length()==0||name.indexOf(':')>-1||name.isBlank()){JOptionPane.showMessageDialog(this,"Invalid Name");return;}
        if(names.indexOf(name.toUpperCase())>-1){JOptionPane.showMessageDialog(this,"Name already exists!");return;}
        ToMat ftm=new ToMat();
        Frame f=null;
        MatVector mv=new MatVector(10);
        Mat mm=new Mat(10,1,CV_32SC1);
        IntBuffer ib=mm.createBuffer();
        lb.setText("Click Add to capture image.");
        vb.setEnabled(false);
        for(int i=1;i<=10;i++){
            while(tsk!=1){
                try{f=og.grab();this.showImage(f);}catch(IOException e){}
            }
            tsk=0;
            Mat m=ftm.convert(f);
            RectVector r=new RectVector();
            cc.detectMultiScale(m, r);
            Rect[] rr=r.get();
            if(rr.length==0){JOptionPane.showMessageDialog(this,"No face detected");i--;continue;}
            else if(rr.length>1){JOptionPane.showMessageDialog(this,"Multiple face addittion not allowed.");i--;continue;}
            m=new Mat(m,rr[0]);
            processFaceImage(m);
            ib.put(i-1,names.size());
            mv.put(i-1, m);
            lb.setText(i+" out of 10 images captured.");
        }
        lb.setText("Saving face data...");
        try(FileWriter fw=new FileWriter("File Safe\\names.txt",true);
            BufferedWriter buff=new BufferedWriter(fw);){
            buff.append(name.toUpperCase()+"\n");
        }catch(IOException e){JOptionPane.showMessageDialog(this,"Failed to write data");return;}
        fr.update(mv, mm);
        fr.write("File Safe\\recognizer.xml");
        names.add(name.toUpperCase());
        JOptionPane.showMessageDialog(this,"Face added successfully");
        File dir=new File("File Safe\\User Data\\"+name.toUpperCase());
        dir.mkdir();
        dir=new File("File Safe\\User Data\\"+name.toUpperCase()+"\\names.txt");
        try{dir.createNewFile();}catch(IOException e){System.out.println(e.getMessage());}
        lb.setText("Saving face data...Done");
        vb.setEnabled(true);
    }
    /**
     * This function first detects a face.
     * If detected, then recognizes the detected face.
     * If recognized then launches the recognized user's profile
     * @return returns 1 if face is verified 0 otherwise.
     */
    public int verifyFace(){
        ToMat tm=new ToMat();
        Frame f=null;
        if(names.size()==0){JOptionPane.showMessageDialog(this,"No user added so far.");return 0;}
        int counter=0;
        IntBuffer ib=null;int out=1;
        while(counter++<61){
                try{f=og.grab();this.showImage(f);}catch(IOException e){}
            tsk=0;
            Mat m=tm.convert(f);
            RectVector r=new RectVector();
            cc.detectMultiScale(m, r);
            Rect[] rr=r.get();
            if(rr.length==0){out=2;continue;}
            else if(rr.length>1){out=3;continue;}
            m=new Mat(m,rr[0]);
            processFaceImage(m);
            ib=IntBuffer.allocate(1);
            DoubleBuffer db=DoubleBuffer.allocate(1);
            fr.predict(m, ib, db);
            if(db.get(0)<=56){out=0;break;}
        }
        switch(out) {
            case 1:
                JOptionPane.showMessageDialog(this,"Face not recognized");
                return 0;
            case 2:
                JOptionPane.showMessageDialog(this,"No face Detected");
                return 0;
            case 3:
                JOptionPane.showMessageDialog(this,"Multiple face Detected");
                return 0;
            default:
                new UserPage(names.get(ib.get(0))).setVisible(true);
                return 1;
        }
    }
    /**
     * This function is called at start by main function. It either creates the 
     * necessary files and folders or reads them.
     */
    public void load(){
        File dir=new File("File Safe");
        if(!dir.exists())if(!dir.mkdir()){System.out.println("cant make dir");return;}
        dir=new File("File Safe\\names.txt");
        try{if(!dir.exists())dir.createNewFile();}catch(IOException e){System.out.println(e.getMessage());return;}
        try(BufferedReader buff=new BufferedReader(new FileReader(dir))){
            String s;
            while((s=buff.readLine())!=null)names.add(s);
        }catch(IOException e){}
        dir=new File("File Safe\\recognizer.xml");
        if(!dir.exists())fr.write("File Safe\\recognizer.xml");
        dir=new File("File Safe\\User Data");
        if(!dir.exists())dir.mkdir();
        cc.load("D:\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
        fr.read("File Safe\\recognizer.xml");
    }
    /**
     * The main function that makes the GUI components visible, turns on camera
     * and contains the event loop for the login page.
     */
    public static void main(String args[]){
        MainFrame mf=new MainFrame("Title");
        mf.load();
        mf.setVisible(true);
        mf.vb.setEnabled(false);
        mf.ad.setEnabled(false);
        OpenCVFrameGrabber ocg=new OpenCVFrameGrabber(0);
        mf.og=ocg;
        Frame f;
        try{
        ocg.start();}catch(IOException e){System.out.println("Couldn\'t start camera!");}
        mf.repaint();
        mf.vb.setEnabled(true);
        mf.ad.setEnabled(true);
        mf.lb.setText("Starting camera...Done");
        int x;
        while(true){
            x=tsk;
            int b=0;
            switch(x){
                case 1:
                tsk=0;
                mf.addFace();
                break;
                case 2:
                tsk=0;
                b=mf.verifyFace();
                break;
                default:
                try{
                    f=ocg.grab();
                    mf.showImage(f);
                }catch(IOException e){System.out.println("Can\'t capture frame");}  
            }
            if(b==1)break;
        }
        
        mf.setVisible(false);
        mf.dispose();
        try{mf.og.stop();mf.og.release();}catch(IOException e){System.out.println(e.getMessage());}
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ad;
    private javax.swing.JPanel jp;
    private java.awt.Label lb;
    private javax.swing.JButton vb;
    // End of variables declaration//GEN-END:variables
}