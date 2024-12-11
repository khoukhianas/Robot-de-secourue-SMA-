/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package robot_secourue;

/**
 *
 * @author HP
 */
import java.util.List;
import java.util.Random;
import javax.swing.JTextArea;
import java.util.ArrayList;

public class Robot {
    private int x, y;
    private int energy;
    private List<Robot> robots;
    private JTextArea logArea;
    private int id;

    public Robot(int x, int y, int energy, List<Robot> robots, JTextArea logArea, int id) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.robots = robots;
        this.logArea = logArea;
        this.id = id;
    }

    public boolean hasEnergy() {
        return energy > 0;
    }

    public void act(Cell[][] grid, int gridSize, List<Victim> victims) {
        if (!hasEnergy()) return; // Arrête si le robot n'a plus d'énergie

        moveWithPheromones(grid, gridSize);

        // Vérifie si une victime est sur la case du robot
        Cell currentCell = grid[x][y];
        if (currentCell.hasVictim()) {
            Victim victim = currentCell.getVictim();
            // Si la victime n'a pas encore été secourue
            if (!victim.isRescued()) {
                if (energy >= victim.getEnergy()) {
                    // Suffisamment d'énergie pour secourir
                    energy -= victim.getEnergy();
                    victim.rescue();
                    logArea.append("Victime " + victim.getId() + " secourue à (" + x + "," + y + ") !\n");
                    logArea.append("Robot " + id + " a secouru la victime " + victim.getId() + "\n");
                } else {
                    // Pas assez d'énergie, demande d'aide
                    logArea.append("Robot " + id + " ne peut pas secourir la victime " + victim.getId() + " faute d'énergie. Demande d'aide...\n");
                    boolean success = requestHelp(grid, gridSize, victim);
                    // Si la victime a été secourue pendant la demande d'aide
                    if (victim.isRescued()) {
                        logArea.append("Robot " + id + " n'a plus besoin d'agir car la victime " + victim.getId() + " a été secourue.\n");
                    } else if (success && energy >= victim.getEnergy()) {
                        energy -= victim.getEnergy();
                        victim.rescue();
                        logArea.append("Victime " + victim.getId() + " secourue à (" + x + "," + y + ") !\n");
                        logArea.append("Robot " + id + " a secouru la victime " + victim.getId() + "\n");
                    } else {
                        logArea.append("Robot " + id + " n'a toujours pas assez d'énergie après demande d'aide.\n");
                    }
                }
            }
        }
    }


    /**
     * Déplacement basé sur les phéromones :
     * Désormais, seulement haut, bas, gauche, droite.
     */
    private void moveWithPheromones(Cell[][] grid, int gridSize) {
        if (energy <= 0) return;
        energy--;

        Random random = new Random();
        List<int[]> possibleMoves = new ArrayList<>();
        int currentX = x;
        int currentY = y;

        // Seulement 4 directions
        int[][] directions = {
            {0, -1},  // Haut
            {0, 1},   // Bas
            {-1, 0},  // Gauche
            {1, 0}    // Droite
        };

        for (int[] dir : directions) {
            int newX = currentX + dir[0];
            int newY = currentY + dir[1];
            if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize 
                && !grid[newX][newY].hasObstacle()) {
                possibleMoves.add(new int[]{newX, newY});
            }
        }

        if (possibleMoves.isEmpty()) {
            // Pas de mouvement possible
            return;
        }

        // Filtrer par plus faible niveau de phéromone
        int minPheromone = Integer.MAX_VALUE;
        for (int[] move : possibleMoves) {
            int ph = grid[move[0]][move[1]].getPheromoneTrace();
            if (ph < minPheromone) {
                minPheromone = ph;
            }
        }

        List<int[]> bestMoves = new ArrayList<>();
        for (int[] move : possibleMoves) {
            if (grid[move[0]][move[1]].getPheromoneTrace() == minPheromone) {
                bestMoves.add(move);
            }
        }

        // Choisir au hasard parmi les meilleures options
        int[] chosenMove = bestMoves.get(random.nextInt(bestMoves.size()));

        // Déplacer le robot
        synchronized (grid) {
            grid[x][y].setRobot(null);
            x = chosenMove[0];
            y = chosenMove[1];
            grid[x][y].setRobot(this);
            // Augmenter la trace de phéromone sur la cellule visitée
            grid[x][y].increasePheromoneTrace();
        }
    }

    private boolean requestHelp(Cell[][] grid, int gridSize, Victim victim) {
        List<Robot> nearbyRobots = getRobotsInRange(grid, gridSize, 2);
        boolean helpReceived = false;

        for (Robot helper : nearbyRobots) {
            if (helper != this && helper.energy > 0) {
                // Transfert d'énergie uniquement si la victime n'est pas secourue
                int energyNeeded = victim.getEnergy() - this.energy;
                int transfer = Math.min(helper.energy, energyNeeded);

                if (transfer > 0) {
                    helper.energy -= transfer;
                    this.energy += transfer;
                    logArea.append("Robot " + helper.id + " a aidé Robot " + id + " à secourir la victime " + victim.getId() + "\n");

                    // Si le robot principal peut secourir la victime après l'aide
                    if (this.energy >= victim.getEnergy()) {
                        this.energy -= victim.getEnergy();
                        victim.rescue();
                        logArea.append("Victime " + victim.getId() + " secourue à (" + x + "," + y + ") !\n");
                        logArea.append("Robot " + id + " a secouru la victime " + victim.getId() + "\n");
                        helpReceived = true;
                        break;
                    }
                }
            }
        }

        if (!helpReceived) {
            logArea.append("Aucune aide n'a pu être apportée à Robot " + id + " pour la victime " + victim.getId() + "\n");
        }
        return helpReceived;
    }


    private List<Robot> getRobotsInRange(Cell[][] grid, int gridSize, int range) {
        List<Robot> res = new ArrayList<>();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize) {
                    Robot r = grid[nx][ny].getRobot();
                    if (r != null) {
                        res.add(r);
                    }
                }
            }
        }
        return res;
    }

    public static void shareEnergyBetweenRobots(Cell[][] grid, int gridSize) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Robot r = grid[i][j].getRobot();
                if (r != null) {
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                         if (dx == 0 && dy == 0) continue;//Évite de vérifier la cellule actuelle du robot
                            int nx = i + dx;
                            int ny = j + dy;
                            if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize && grid[nx][ny].getRobot() != null) {
                                Robot r2 = grid[nx][ny].getRobot();
                                int diff = r.energy - r2.energy;
                                if (Math.abs(diff) > 5) {
                                    int transfer = diff / 2;
                                    if (diff > 0 && r.energy > 0) {
                                        int actualTransfer = Math.min(transfer, r.energy);
                                        r.energy -= actualTransfer;
                                        r2.energy += actualTransfer;
                                        r.logArea.append("Robot " + r.id + " partage " + actualTransfer + " unités d'énergie avec Robot " + r2.id + "\n");
                                    } else if (diff < 0 && r2.energy > 0) {
                                        int actualTransfer = Math.min(-transfer, r2.energy);
                                        r2.energy -= actualTransfer;
                                        r.energy += actualTransfer;
                                        r.logArea.append("Robot " + r2.id + " partage " + actualTransfer + " unités d'énergie avec Robot " + r.id + "\n");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int getEnergy() {
        return energy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }
}
