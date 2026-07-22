package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private int width;
    private int height;
    private int currentSpeed = 2;
    private int dy = 0;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor
        int scaledWidth = ii.getIconWidth() * SCALE_FACTOR;
        int scaledHeight = ii.getIconHeight() * SCALE_FACTOR;
        var scaledImage = ii.getImage().getScaledInstance(scaledWidth,
                scaledHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        width = scaledWidth;
        height = scaledHeight;

        setX((BOARD_WIDTH - width) / 2);
        setY(BOARD_HEIGHT - height - 20);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public void act() {
        x += dx;
        y += dy;

        if (x <= 0) {
            x = 0;
        }

        if (x >= BOARD_WIDTH - width) {
            x = BOARD_WIDTH - width;
        }

        if (y <= 0) {
            y = 0;
        }

        int bottomBound = BOARD_HEIGHT - height - 10;
        if (y >= bottomBound) {
            y = bottomBound;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -currentSpeed;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
