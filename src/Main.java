import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Network Control");
        frame.setSize(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton serverBtn = new JButton("Start Server");
        JButton clientBtn = new JButton("Start Client");

        JTextArea log = new JTextArea();
        JScrollPane scroll = new JScrollPane(log);

        serverBtn.addActionListener(e -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("./server");
                Process p = pb.start();
                log.append("Server started\n");
            } catch(Exception ex){
                log.append("Server error\n");
            }
        });

        clientBtn.addActionListener(e -> {
            try {
                ProcessBuilder pb =
                        new ProcessBuilder("./client","192.168.1.42");
                Process p = pb.start();
                log.append("Client started\n");
            } catch(Exception ex){
                log.append("Client error\n");
            }
        });

        JPanel panel = new JPanel();
        panel.add(serverBtn);
        panel.add(clientBtn);

        frame.add(panel,"North");
        frame.add(scroll,"Center");

        frame.setVisible(true);
    }
}