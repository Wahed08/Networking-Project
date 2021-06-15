import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class Client {

    Socket socket;
    DataInputStream din;
    boolean startLoop = false;
    String downloadFile = "";
    public static void main(String[] args) throws IOException {
        new Client();
    }

    Client() throws IOException {
        JFrame jFrame = new JFrame("Client GUI");
        jFrame.setBounds(300, 100, 600, 600);
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);

        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel gui = new JLabel("Client GUI");
        gui.setPreferredSize(new Dimension(100,50));
        gui.setFont(new Font("Arial", Font.BOLD, 27));
        gui.setAlignmentX(Component.CENTER_ALIGNMENT);



        JPanel j1 = new JPanel();
        j1.setPreferredSize(new Dimension(600,20));
        j1.setBackground(Color.pink);
        j1.setBorder(BorderFactory.createSoftBevelBorder(1));

        JLabel jLabel0 = new JLabel("IP : ");
        jLabel0.setFont(new Font("Arial", Font.BOLD, 18));
        //j1.add(jLabel0);

        JTextField ipTxt = new JTextField();
        ipTxt.setFont(new Font("Arial", Font.BOLD,18));
        ipTxt.setColumns(8);


        JLabel jLabel1 = new JLabel("Port : ");
        //jLabel1.setBounds(100, 100, 100, 200);
        jLabel1.setFont(new Font("Arial", Font.BOLD, 18));




        JTextField portTxt = new JTextField();
        portTxt.setFont(new Font("Arial", Font.BOLD, 18));
        portTxt.setColumns(8);


        jFrame.add(gui);
        j1.add(jLabel0);
        j1.add(ipTxt);
        j1.add(jLabel1);
        j1.add(portTxt);


        JButton jCntBtn = new JButton("Connect");
        jCntBtn.setPreferredSize(new Dimension(130,30));
        jCntBtn.setFont(new Font("Arial", Font.BOLD, 20));
        jCntBtn.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        j1.add(jCntBtn);


        JPanel j2 = new JPanel();
        j2.setBounds(0, 100, 600, 200);
        j2.setPreferredSize(new Dimension(100,400));
        j2.setBackground(Color.LIGHT_GRAY);
        j2.setBorder(BorderFactory.createSoftBevelBorder(0));

        JScrollPane jScrollPane = new JScrollPane(j2);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        JLabel txtChoose = new JLabel("   Choose File You Want to Download");
        txtChoose.setPreferredSize(new Dimension(380,50));
        txtChoose.setFont(new Font("Arial", Font.BOLD, 20));
        txtChoose.setBorder(BorderFactory.createSoftBevelBorder(0));
        txtChoose.setAlignmentX(Component.CENTER_ALIGNMENT);
        j2.add(txtChoose);



        jFrame.add(j1);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);



        jCntBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                String ip = ipTxt.getText();
                int PORT = Integer.parseInt(portTxt.getText().trim());
                //String ip = "localhost";
                //int PORT = 1234;
                if(jCntBtn.getText() == "Connect"){
                    jCntBtn.setText("Disconnect");

                    try {

                        socket = new Socket(ip, PORT);

                        DataInputStream din = new DataInputStream(socket.getInputStream());
                        int fileNameLength = din.readInt();
                        byte[] fileNameBytes = new byte[fileNameLength];
                        din.readFully(fileNameBytes, 0, fileNameBytes.length);
                        String fileName = new String(fileNameBytes);

                        String temp = "";
                        for(int i = 0; i<fileName.length(); i++){
                            char c = fileName.charAt(i);
                            if(c == '|'){

                                JPanel jpFileRow = new JPanel();
                                jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                                jpFileRow.setPreferredSize(new Dimension(200,50));

                                JPopupMenu popMenu = new JPopupMenu();

                                JMenuItem item1 = new JMenuItem("Download");
                                popMenu.add(item1);
                                item1.addActionListener(getAction());
                                item1.setActionCommand("Download");


                                JMenuItem item2 = new JMenuItem("Option-1");
                                popMenu.add(item2);
                                //item2.addActionListener(this);
                                //item2.setActionCommand("Option-1");


                                JMenuItem item3 = new JMenuItem("Option-2");
                                popMenu.add(item3);
                                //item3.addActionListener(this);
                                //item3.setActionCommand("Option-2");

                                JLabel jlFileName = new JLabel(temp);
                                jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                                jlFileName.setBorder(new EmptyBorder(10,0, 10,0));

                                jpFileRow.setName(temp);
                                jpFileRow.add(jlFileName);
                                //jpFileRow.addMouseListener(getMyMouseListener(jFrame));
                                j2.add(jpFileRow);
                                jFrame.validate();

                                jpFileRow.addMouseListener(new MouseListener(){
                                    @Override
                                    public void mouseClicked(MouseEvent me) {
                                        //JPanel jPanel1 = (JPanel)me.getSource();
                                        //fileNameToDownload = (String)jPanel1.getName();

                                        popMenu.show(jpFileRow, me.getX(), me.getY());
                                        JPanel jPanel = (JPanel)me.getSource();
                                        downloadFile = jPanel.getName();
                                    }
                                    @Override
                                    public void mousePressed(MouseEvent me) {            }
                                    @Override
                                    public void mouseReleased(MouseEvent me) {            }
                                    @Override
                                    public void mouseEntered(MouseEvent me) {            }
                                    @Override
                                    public void mouseExited(MouseEvent me) {            }
                                });

                                temp = "";
                            }else{
                                temp += c;
                            }
                        }


                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    try {
                        socket.close();
                        jCntBtn.setText("Connect");
                        startLoop = false;
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });


    }

    public ActionListener getAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                if(command.equals("Download")){
                    try {
                        DataInputStream din = new DataInputStream(socket.getInputStream());
                        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

                        byte[] fileNameBytes = downloadFile.getBytes();
                        dout.writeInt(fileNameBytes.length);
                        dout.write(fileNameBytes);

                        int fileContentLength = din.readInt();
                        byte[] fileContentBytes = new byte[fileContentLength];
                        din.readFully(fileContentBytes, 0, fileContentBytes.length);

                        File fileDownload = new File(downloadFile);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                        fileOutputStream.write(fileContentBytes);
                        fileOutputStream.close();

                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }
}
