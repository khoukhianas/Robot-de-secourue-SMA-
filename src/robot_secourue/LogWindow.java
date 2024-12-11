/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package robot_secourue;

/**
 *
 * @author HP
 */
import javax.swing.*;
import java.awt.*;

public class LogWindow extends JFrame {
    private JTextArea logArea;

    public LogWindow() {
        super("Logs de la Simulation");
        setSize(600, 400);
        setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(240, 240, 240));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public JTextArea getLogArea() {
        return logArea;
    }
}
