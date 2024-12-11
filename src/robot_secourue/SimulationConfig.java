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

public class SimulationConfig {

    public void showConfigWindow() {
        JFrame frame = new JFrame("Simulation de Sauvetage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        // Création de la fenêtre de logs
        LogWindow logWindow = new LogWindow();
        logWindow.setVisible(true);

        JTextArea logArea = logWindow.getLogArea(); // On récupère la zone de log depuis la logWindow

        JLabel gridSizeLabel = new JLabel("Taille de la grille :");
        JSpinner gridSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 5, 50, 1));

        JLabel robotsLabel = new JLabel("Nombre de robots :");
        JSpinner robotsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 500, 1));

        JLabel victimsLabel = new JLabel("Nombre de victimes :");
        JSpinner victimsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 500, 1));

        JLabel obstaclesLabel = new JLabel("Nombre d'obstacles :");
        JSpinner obstaclesSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 500, 1));

        JLabel minEnergyLabel = new JLabel("Énergie minimale des robots :");
        JSpinner minEnergySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));

        JLabel maxEnergyLabel = new JLabel("Énergie maximale des robots :");
        JSpinner maxEnergySpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));

        JLabel victimMinEnergyLabel = new JLabel("Énergie minimale des victimes :");
        JSpinner victimMinEnergySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

        JLabel victimMaxEnergyLabel = new JLabel("Énergie maximale des victimes :");
        JSpinner victimMaxEnergySpinner = new JSpinner(new SpinnerNumberModel(20, 5, 50, 1));

        JButton startButton = new JButton("Démarrer");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Reprendre");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Paramètres de la Simulation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int gridy = 0;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(gridSizeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(gridSizeSpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(robotsLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(robotsSpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(victimsLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(victimsSpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(obstaclesLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(obstaclesSpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(minEnergyLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(minEnergySpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(maxEnergyLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(maxEnergySpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(victimMinEnergyLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(victimMinEnergySpinner, gbc);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        controlPanel.add(victimMaxEnergyLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(victimMaxEnergySpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);

        gridy++;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        controlPanel.add(buttonPanel, gbc);

        frame.setLayout(new BorderLayout());
        frame.add(controlPanel, BorderLayout.WEST);

        SimulationPanel[] simulationPanel = new SimulationPanel[1];
        startButton.addActionListener(e -> {
            logArea.setText("");

            int gridSize = (int) gridSizeSpinner.getValue();
            int numRobots = (int) robotsSpinner.getValue();
            int numVictims = (int) victimsSpinner.getValue();
            int numObstacles = (int) obstaclesSpinner.getValue();
            int minEnergy = (int) minEnergySpinner.getValue();
            int maxEnergy = (int) maxEnergySpinner.getValue();
            int victimMinEnergy = (int) victimMinEnergySpinner.getValue();
            int victimMaxEnergy = (int) victimMaxEnergySpinner.getValue();

            // Vérification cohérence énergie robots
            if (minEnergy > maxEnergy) {
                logArea.append("Erreur : l'énergie minimale des robots est supérieure à l'énergie maximale.\n");
                return;
            }

            // Vérification cohérence énergie victimes
            if (victimMinEnergy > victimMaxEnergy) {
                logArea.append("Erreur : l'énergie minimale des victimes est supérieure à l'énergie maximale.\n");
                return;
            }

            int totalCells = gridSize * gridSize;
            int totalRequired = numRobots + numVictims + numObstacles;

            // Vérifier si on ne dépasse pas le nombre total de cellules
            if (totalRequired > totalCells) {
                logArea.append("Erreur : Trop d'agents (robots/victimes/obstacles) par rapport à la taille de la grille.\n");
                return;
            }

            if (simulationPanel[0] != null) {
                frame.remove(simulationPanel[0]);
            }

            simulationPanel[0] = new SimulationPanel(gridSize, numRobots, numVictims, minEnergy, maxEnergy, numObstacles, victimMinEnergy, victimMaxEnergy, logArea);

            JScrollPane simulationScrollPane = new JScrollPane(simulationPanel[0]);
            simulationScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            simulationScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            frame.add(simulationScrollPane, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();

            new Thread(simulationPanel[0]::startSimulation).start();
        });

        pauseButton.addActionListener(e -> {
            if (simulationPanel[0] != null) simulationPanel[0].pauseSimulation();
        });

        resumeButton.addActionListener(e -> {
            if (simulationPanel[0] != null) simulationPanel[0].resumeSimulation();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}