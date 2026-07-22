package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Bomb extends Sprite {

    private int speed = 2;
    private boolean destroyed = true;

    public Bomb(int x, int y) {
        initBomb(x, y);
    }

    private void initBomb(int x, int y) {
        this.x = x;
        this.y = y;

        var ii = new ImageIcon("src/images/bomb.png");
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act() {
        if (destroyed) {
            return;
        }
        y += speed;
        if (y >= BOARD_HEIGHT) {
            destroyed = true;
        }
    }

    public void launch(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.destroyed = false;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
