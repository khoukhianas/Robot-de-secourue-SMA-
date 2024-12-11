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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationPanel extends JPanel {
    private int gridSize;
    private int cellSize;
    private Cell[][] grid;
    private List<Robot> robots;
    private List<Victim> victims;
    private boolean running;
    private boolean paused;
    private JTextArea logArea;

    private int victimMinEnergy;
    private int victimMaxEnergy;
    private int obstacleCount = 0; 

    class Zone {
        int startX;
        int endX;
        int startY;
        int endY;
        Zone(int startX, int endX, int startY, int endY) {
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
        }
    }

    public SimulationPanel(int gridSize, int numRobots, int numVictims, int minEnergy, int maxEnergy,
                           int numObstacles, int victimMinEnergy, int victimMaxEnergy, JTextArea logArea) {
        this.gridSize = gridSize;
        this.cellSize = Math.max(10, 600 / gridSize);
        this.grid = new Cell[gridSize][gridSize];
        this.robots = new ArrayList<>();
        this.victims = new ArrayList<>();
        this.running = true;
        this.paused = false;
        this.logArea = logArea;
        this.victimMinEnergy = victimMinEnergy;
        this.victimMaxEnergy = victimMaxEnergy;

        initializeGrid();
        placeObstacles(numObstacles);
        List<Zone> zones = createZones(numRobots);
        placeRobots(numRobots, minEnergy, maxEnergy, zones);
        placeVictims(numVictims, victimMinEnergy, victimMaxEnergy);

        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        setBackground(Color.WHITE);
    }

    private void initializeGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    private List<Zone> createZones(int numRobots) {
        int zonesCols = (int) Math.ceil(Math.sqrt(numRobots));
        int zonesRows = (int) Math.ceil((double) numRobots / zonesCols);

        int zoneWidth = gridSize / zonesCols;
        int zoneHeight = gridSize / zonesRows;

        int remainderX = gridSize % zonesCols;
        int remainderY = gridSize % zonesRows;

        List<Zone> zones = new ArrayList<>();

        int startY = 0;
        for (int row = 0; row < zonesRows; row++) {
            int currentZoneHeight = zoneHeight + (row < remainderY ? 1 : 0);
            int endY = startY + currentZoneHeight - 1;

            int startX = 0;
            for (int col = 0; col < zonesCols; col++) {
                int currentZoneWidth = zoneWidth + (col < remainderX ? 1 : 0);
                int endX = startX + currentZoneWidth - 1;

                zones.add(new Zone(startX, endX, startY, endY));
                startX = endX + 1;
            }

            startY = endY + 1;
        }

        logArea.append("Zones créées : \n");
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            logArea.append("Zone " + (i+1) + " : (" + z.startX + "," + z.startY + ") à (" + z.endX + "," + z.endY + ")\n");
        }

        return zones;
    }

    private void placeObstacles(int numObstacles) {
        Random random = new Random();
        int attempts = 0;
        int maxAttempts = gridSize * gridSize * 10;

        for (int i = 0; i < numObstacles; i++) {
            boolean placed = false;
            while (!placed) {
                attempts++;
                if (attempts > maxAttempts) {
                    logArea.append("Impossible de placer tous les obstacles.\n");
                    return;
                }
                int x = random.nextInt(gridSize);
                int y = random.nextInt(gridSize);

                if (!grid[x][y].hasObstacle() && !grid[x][y].hasRobot() && !grid[x][y].hasVictim()) {
                    grid[x][y].setObstacle(true);
                    obstacleCount++;
                    grid[x][y].setObstacleId(obstacleCount);
                    placed = true;
                    logArea.append("Obstacle " + obstacleCount + " placé à (" + x + "," + y + ")\n");
                }
            }
        }
    }

    private void placeRobots(int numRobots, int minEnergy, int maxEnergy, List<Zone> zones) {
        Random random = new Random();
        int attempts = 0;
        int maxAttempts = gridSize * gridSize * 10;

        int robotCount = 0;

        // Etape 1 : Au moins un robot par zone
        for (Zone zone : zones) {
            if (robotCount >= numRobots) break;
            boolean placed = false;
            while (!placed) {
                attempts++;
                if (attempts > maxAttempts) {
                    logArea.append("Impossible de placer tous les robots.\n");
                    return;
                }

                int x = random.nextInt(zone.endX - zone.startX + 1) + zone.startX;
                int y = random.nextInt(zone.endY - zone.startY + 1) + zone.startY;

                if (!grid[x][y].hasRobot() && !grid[x][y].hasObstacle() && !grid[x][y].hasVictim()) {
                    int robotEnergy = random.nextInt(maxEnergy - minEnergy + 1) + minEnergy;
                    Robot robot = new Robot(x, y, robotEnergy, robots, logArea, robotCount + 1);
                    robots.add(robot);
                    grid[x][y].setRobot(robot);
                    logArea.append("Robot " + (robotCount + 1) + " placé à (" + x + "," + y + ") avec "
                                   + robotEnergy + " unités d'énergie dans zone ["
                                   + zone.startX + "-" + zone.endX + "," + zone.startY + "-" + zone.endY + "]\n");
                    placed = true;
                    robotCount++;
                }
            }
        }

        // Etape 2 : Les robots restants
        while (robotCount < numRobots) {
            attempts++;
            if (attempts > maxAttempts) {
                logArea.append("Impossible de placer tous les robots.\n");
                return;
            }

            Zone zone = zones.get(random.nextInt(zones.size()));
            int x = random.nextInt(zone.endX - zone.startX + 1) + zone.startX;
            int y = random.nextInt(zone.endY - zone.startY + 1) + zone.startY;

            if (!grid[x][y].hasRobot() && !grid[x][y].hasObstacle() && !grid[x][y].hasVictim()) {
                int robotEnergy = random.nextInt(maxEnergy - minEnergy + 1) + minEnergy;
                Robot robot = new Robot(x, y, robotEnergy, robots, logArea, robotCount + 1);
                robots.add(robot);
                grid[x][y].setRobot(robot);
                logArea.append("Robot " + (robotCount + 1) + " placé à (" + x + "," + y + ") avec "
                               + robotEnergy + " unités d'énergie dans zone ["
                               + zone.startX + "-" + zone.endX + "," + zone.startY + "-" + zone.endY + "]\n");
                robotCount++;
            }
        }
    }

    private void placeVictims(int numVictims, int victimMinEnergy, int victimMaxEnergy) {
        Random random = new Random();
        int attempts = 0;
        int maxAttempts = gridSize * gridSize * 10;

        for (int i = 0; i < numVictims; i++) {
            boolean placed = false;
            while (!placed) {
                attempts++;
                if (attempts > maxAttempts) {
                    logArea.append("Impossible de placer toutes les victimes.\n");
                    return;
                }

                int x = random.nextInt(gridSize);
                int y = random.nextInt(gridSize);

                if (!grid[x][y].hasVictim() && !grid[x][y].hasObstacle() && !grid[x][y].hasRobot()) {
                    int energy = random.nextInt(victimMaxEnergy - victimMinEnergy + 1) + victimMinEnergy;
                    Victim victim = new Victim(x, y, energy, logArea, i+1);
                    victims.add(victim);
                    grid[x][y].setVictim(victim);

                    logArea.append("Victime " + (i+1) + " placée à (" + x + "," + y + ") avec " + energy + " énergie.\n");
                    placed = true;
                }
            }
        }
    }

    public void startSimulation() {
        running = true;
        while (running) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (victims.stream().anyMatch(v -> !v.isRescued() && v.getEnergy() <= 0)) {
                logArea.append("Une ou plusieurs victimes ont épuisé leur énergie. Simulation terminée.\n");
                stopSimulation();
                break;
            }

            if (robots.stream().noneMatch(Robot::hasEnergy)) {
                logArea.append("Tous les robots sont à court d'énergie. Simulation terminée.\n");
                stopSimulation();
                break;
            }

            if (victims.stream().allMatch(Victim::isRescued)) {
                logArea.append("Toutes les victimes ont été secourues. Simulation terminée.\n");
                stopSimulation();
                break;
            }

            ArrayList<Thread> threads = new ArrayList<>();
            for (Robot robot : robots) {
                if (robot.hasEnergy()) {
                    Thread robotThread = new Thread(() -> robot.act(grid, gridSize, victims));
                    threads.add(robotThread);
                    robotThread.start();
                }
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Robot.shareEnergyBetweenRobots(grid, gridSize);

            for (Victim victim : victims) {
                if (!victim.isRescued() && victim.getEnergy() > 0) {
                    victim.decreaseEnergy();
                }
            }

            updateLogs();
            repaint();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pauseSimulation() {
        paused = true;
    }

    public synchronized void resumeSimulation() {
        paused = false;
        notifyAll();
    }

    public void stopSimulation() {
        running = false;
    }

    private void updateLogs() {
        logArea.append("Robots actifs (énergie > 0) : " + robots.stream().filter(Robot::hasEnergy).count() + "\n");
        logArea.append("Victimes sauvées : " + victims.stream().filter(Victim::isRescued).count() + "\n");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int gridWidth = gridSize * cellSize;
        int gridHeight = gridSize * cellSize;
        int startX = (panelWidth - gridWidth) / 2;
        int startY = (panelHeight - gridHeight) / 2;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int px = startX + j * cellSize;
                int py = startY + i * cellSize;

                int phLevel = grid[i][j].getPheromoneTrace();

                int grayValue = 255 - (phLevel * 5);
                if (grayValue < 0) grayValue = 0;
                Color cellColor = new Color(grayValue, grayValue, grayValue);
                g.setColor(cellColor);
                g.fillRect(px, py, cellSize, cellSize);

                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(px, py, cellSize, cellSize);

                // Obstacle
                if (grid[i][j].hasObstacle()) {
                    g.setColor(Color.GRAY);
                    g.fillRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
                }

                // Victime
                if (grid[i][j].getVictim() != null) {
                    Victim victim = grid[i][j].getVictim();
                    Color victimColor;

                    if (victim.isRescued()) {
                        victimColor = new Color(150, 150, 150);
                    } else {
                        victimColor = victim.getEnergy() < 5 ? new Color(255, 0, 0) : new Color(0, 200, 0);
                        if (victim.getEnergy() < 10) {
                            victimColor = victimColor.brighter().brighter();
                        }
                    }
                    g.setColor(victimColor);
                    g.fillOval(px + 5, py + 5, cellSize - 10, cellSize - 10);

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    String idText = "V" + victim.getId();
                    g.drawString(idText, px + cellSize / 2 - g.getFontMetrics().stringWidth(idText) / 2, py + cellSize / 2 - 10);
                    g.drawString(String.valueOf(victim.getEnergy()), px + cellSize / 2 - 6, py + cellSize / 2 + 10);
                }

                // Robot
                if (grid[i][j].hasRobot()) {
                    Robot robot = grid[i][j].getRobot();
                    Color robotColor = new Color(0, 0, 200);

                    if (robot.getEnergy() < 5) {
                        robotColor = robotColor.brighter().brighter();
                    }

                    g.setColor(robotColor);
                    g.fillRect(px + 5, py + 5, cellSize - 10, cellSize - 10);

                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    String idText = "R" + robot.getId();
                    g.drawString(idText, px + cellSize / 2 - g.getFontMetrics().stringWidth(idText) / 2, py + cellSize / 2 - 10);
                    g.drawString(String.valueOf(robot.getEnergy()), px + cellSize / 2 - 6, py + cellSize / 2 + 10);
                }

                if (phLevel > 0) {
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    String phText = String.valueOf(phLevel);
                    g.drawString(phText, px + cellSize - g.getFontMetrics().stringWidth(phText) - 2, py + cellSize - 2);
                }
            }
        }
    }
}