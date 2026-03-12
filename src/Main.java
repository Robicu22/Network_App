import javax.swing.*;
import java.awt.event.*;
import java.io.*;


//lsof -i :PORT

public class Main {

    public void SendMessage(String ipClient){

    }

    public Process serverProcess;
    private BufferedWriter serverWriter;

    public Process clientProcess;

    JTextArea log = new JTextArea();

    public void main(String[] args) {

        JFrame frame = new JFrame("Network Control");
        frame.setSize(500,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        JButton serverBtn = new JButton("Start Server");
        JButton clientBtn = new JButton("Start Client");
       // JButton stopBtn = new JButton("Stop Server/Client");
        JTextField ipField = new JTextField(15);


        JScrollPane scroll = new JScrollPane(log);

        serverBtn.addActionListener(e -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("./Native/server");
                log.append("Server started\n");
                serverProcess = pb.start();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
                serverWriter = new BufferedWriter(
                        new OutputStreamWriter(serverProcess.getOutputStream())
                );

                new Thread(() -> {
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String currentLine = line;


                            SwingUtilities.invokeLater(() -> {
                                log.append(currentLine + "\n");
                            });

                            if (currentLine.contains("Chose a message to send:")) {
                                String message = JOptionPane.showInputDialog("Enter message to send:");
                                if (message != null) {
                                    serverWriter.write(message);
                                    serverWriter.newLine();
                                    serverWriter.flush();
                                }
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> log.append("Connection lost: " + ex.getMessage() + "\n"));
                    }
                }).start();

            } catch(Exception ex){
                ex.printStackTrace();
                log.append(ex.getMessage() + "\n");
            };
        });

        clientBtn.addActionListener(e -> {
            try {
                if(ipField.getText().equals("")) {log.append("IP is empty\n"); return;}
                ProcessBuilder pb =
                        new ProcessBuilder("./Native/client",ipField.getText());
                clientProcess = pb.start();
                log.append("Client started\n");

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(clientProcess.getInputStream()));

                new Thread(() -> {
                    try {

                        String line;
                        while((line = reader.readLine()) != null) {
                            String finalLine = line;

                            SwingUtilities.invokeLater(() -> {
                                log.append(finalLine + "\n");
                            });
                        }
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                }).start();
            } catch(Exception ex){
                ex.printStackTrace();
                log.append(ex.getMessage() + "\n");
            }
        });


        JPanel panel = new JPanel();
        panel.add(serverBtn);
        panel.add(clientBtn);
        panel.add(ipField);

        frame.add(panel,"North");
        frame.add(scroll,"Center");

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if(serverProcess != null){
                    serverProcess.destroy();
                }

                if(clientProcess != null){
                    clientProcess.destroy();
                }

            }
        });
    }
}
