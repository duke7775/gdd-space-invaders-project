package gdd.sprite;

import static gdd.Global.*;
import java.util.Random;
import javax.swing.ImageIcon;

public class Boss2 extends Enemy {
    private Bomb bomb;
    private int hp = 100;
    private int maxHp = 100;

    private int speed = 2;
    private int moveDirection = 1;
    private int attackCooldown = 0;

    public Boss2(int x, int y) {
        super(x, y);

        bomb = new Bomb(x, y);

        var ii = new ImageIcon("src/images/boss2.png");
        var scaledImage = ii.getImage().getScaledInstance(
        180,
        120,
        java.awt.Image.SCALE_SMOOTH);

        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        x += speed * moveDirection;

        if (x <= 0) {
            moveDirection = 1;
        }

        if (image != null) {
            int maxX = BOARD_WIDTH - image.getWidth(null);
            if (x >= maxX) {
                moveDirection = -1;
            }
        }
    }

    @Override
    public void attack(Player player, Random random) {

        if (bomb == null) {
            return;
        }

        attackCooldown++;

        if (attackCooldown >= 60 && bomb.isDestroyed()) {

        bomb.launch(
            getX() + image.getWidth(null) / 2,
            getY() + image.getHeight(null)
        );

        attackCooldown = 0;
        }
    }

    @Override
    public Bomb getBomb() {
        return bomb;
    }

    public void damage() {
        hp--;
        System.out.println("Boss2 HP = " + hp);
        if (hp <= 0) {
            setDying(true);
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }
}
