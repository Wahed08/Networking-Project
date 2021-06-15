import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class Server {

    ServerSocket serverSocket = null;
    Socket socket = null;
    boolean isStart = true;
    String zipString = "";
    ArrayList<dataFile>myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new Server();
    }


    Server() throws IOException {
        JFrame jFrame = new JFrame("Server GUI");
        jFrame.setBounds(300, 100, 600, 600);
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);

        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));


        JLabel guiText = new JLabel("Server GUI");
        guiText.setPreferredSize(new Dimension(100,50));
        guiText.setFont(new Font("Arial", Font.BOLD, 27));
        guiText.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel j1 = new JPanel();
        j1.setPreferredSize(new Dimension(600,10));
        j1.setBackground(Color.pink);
        j1.setBorder(BorderFactory.createSoftBevelBorder(1));

        JLabel jLabel1 = new JLabel("Server Port : 1234");
        jLabel1.setFont(new Font("Arial", Font.BOLD, 25));
        j1.add(jLabel1);


        JPanel j2 = new JPanel();
        j2.setPreferredSize(new Dimension(100,300));

        j2.setBackground(Color.LIGHT_GRAY);
        j2.setBorder(BorderFactory.createSoftBevelBorder(0));

        JScrollPane jScrollPane = new JScrollPane(j2);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel j3 = new JPanel();
        j3.setPreferredSize(new Dimension(600,20));
        j3.setBackground(Color.cyan);
        j3.setBorder(BorderFactory.createSoftBevelBorder(0));

        JButton addBtn = new JButton("Add File");
        addBtn.setPreferredSize(new Dimension(100,40));
        addBtn.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        j3.add(addBtn);


        jFrame.add(guiText);
        jFrame.add(j1);
        jFrame.add(jScrollPane);
        jFrame.add(j3);
        jFrame.setVisible(true);





        addBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isStart){
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Choose a File to send");

                    if(jFileChooser.showOpenDialog(null) == jFileChooser.APPROVE_OPTION){

                        File fileToUpload = jFileChooser.getSelectedFile();
                        String fileName = (String)fileToUpload.getName();

                        try {
                            FileInputStream fileInputStream = new FileInputStream(fileToUpload.getAbsolutePath());
                            byte[] fileBytes = new byte[(int)fileToUpload.length()];
                            fileInputStream.read(fileBytes);
                            myFiles.add(new dataFile(fileName,fileBytes));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        zipString += fileName;
                        zipString += '|';

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                        jpFileRow.setPreferredSize(new Dimension(200,50));

                        JLabel jlFileName = new JLabel(fileName);
                        //jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10,0, 10,0));
                        //jlFileName.

                        jpFileRow.setName(String.valueOf(fileName));
                        jpFileRow.add(jlFileName);
                        //jpFileRow.addMouseListener(getMyMouseListener(jFrame));
                        j2.add(jpFileRow);
                        jFrame.validate();
                    }
                }
            }
        });


        serverSocket = new ServerSocket(1234);
        socket = serverSocket.accept();
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        byte[] fileNameBytes = zipString.getBytes();
        dout.writeInt(fileNameBytes.length);
        dout.write(fileNameBytes);



        while(true){

            DataInputStream din = new DataInputStream(socket.getInputStream());
            int fileNameLength = din.readInt();
            byte[] fileNameBytes1 = new byte[fileNameLength];
            din.readFully(fileNameBytes1, 0, fileNameBytes1.length);
            String fileName1 = new String(fileNameBytes1);



            for(dataFile my : myFiles){
                if(my.getName().equals(fileName1)){
                    byte[] fileBytes = my.getData();
                    dout = new DataOutputStream(socket.getOutputStream());
                    dout.writeInt(fileBytes.length);
                    dout.write(fileBytes);
                }
            }

        }

    }
}
