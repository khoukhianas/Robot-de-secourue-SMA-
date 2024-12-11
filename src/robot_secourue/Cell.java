/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package robot_secourue;

/**
 *
 * @author HP
 */
class Cell {
    private Robot robot;
    private Victim victim;
    private boolean obstacle;
    private int pheromoneTrace = 0;
    private int obstacleId = -1; // ID de l'obstacle (si présent)

    public boolean hasRobot() {
        return robot != null;
    }

    public boolean hasVictim() {
        return victim != null && !victim.isRescued();
    }

    public boolean hasObstacle() {
        return obstacle;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public Robot getRobot() {
        return robot;
    }

    public Victim getVictim() {
        return victim;
    }

    public int getPheromoneTrace() {
        return pheromoneTrace;
    }

    public void increasePheromoneTrace() {
        this.pheromoneTrace++;
    }

    public int getObstacleId() {
        return obstacleId;
    }

    public void setObstacleId(int obstacleId) {
        this.obstacleId = obstacleId;
    }
}
