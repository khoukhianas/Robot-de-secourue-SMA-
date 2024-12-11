/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package robot_secourue;

import javax.swing.JTextArea;

/**
 *
 * @author HP
 */

public class Victim {
    private int x, y;
//    private int urgency;
    private int energy;
    private boolean rescued;
    private JTextArea logArea;
    private int id;

    public Victim(int x, int y, int energy, JTextArea logArea, int id) {
        this.x = x;
        this.y = y;
//        this.urgency = urgency;
        this.energy = energy;
        this.rescued = false;
        this.logArea = logArea;
        this.id = id;
    }

    public void rescue() {
        rescued = true;
//        logArea.append("Victime " + id + " secourue à (" + x + "," + y + ") !\n");
    }

    public boolean isRescued() {
        return rescued;
    }

//    public int getUrgency() {
//        return urgency;
//    }

    public int getEnergy() {
        return energy;
    }

    public void decreaseEnergy() {
        if (energy > 0) {
            energy--;
            if (energy == 0 && !rescued) {
                logArea.append("Victime " + id + " à (" + x + "," + y + ") a épuisé son énergie.\n");
            }
        }
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
