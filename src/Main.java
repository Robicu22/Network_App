import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class Main {
    public Process serverProcess;
    public Process clientProcess;
    public void main(String[] args) {

        JFrame frame = new JFrame("Network Control");
        frame.setSize(500,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        JButton serverBtn = new JButton("Start Server");
        JButton clientBtn = new JButton("Start Client");
        JButton stopBtn = new JButton("Stop Server/Client");
        JTextField ipField = new JTextField(15);

        JTextArea log = new JTextArea();
        JScrollPane scroll = new JScrollPane(log);

        serverBtn.addActionListener(e -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("./native/server");

                serverProcess = pb.start();
                log.append("Server started\n");

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));

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

        clientBtn.addActionListener(e -> {
            try {
                if(ipField.getText().equals("")) {log.append("IP is empty\n"); return;}
                ProcessBuilder pb =
                        new ProcessBuilder("./native/client",ipField.getText());
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
