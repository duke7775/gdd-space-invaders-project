package gdd.sprite;

import static gdd.Global.*;
import java.util.Random;

public class Alien1 extends Enemy {

    private Bomb bomb;
    private int speed = 1;
    private int hp = 1;

    public Alien1(int x, int y) {
        super(x, y);
        bomb = new Bomb(x, y);
    }

    public void act(int direction) {
        super.act(direction);
        y += speed;
    }
    public void damage(){
        hp--;
        if(hp <= 0){
            die();
        }
    }
    public boolean outOfScreen(){
        return y > BOARD_HEIGHT;
    }

    @Override
    public void attack(Player player, Random randomizer) {
        if (bomb == null) {
            return;
        }

        if (randomizer.nextInt(15) == CHANCE && bomb.isDestroyed()) {
            bomb.launch(getX(), getY());
        }
    }

    @Override
    public Bomb getBomb() {
        return bomb;
    }
}
