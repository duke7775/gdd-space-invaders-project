package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Bomb;
import gdd.sprite.Boss2;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {

    private static final int MAX_PLAYER_SHOTS = 4;

    private final Game game;
    private Timer timer;
    private AudioPlayer audioPlayer;

    private int frame = 0;
    private int direction = -1;
    private int deaths = 0;
    private boolean inGame = true;
    private boolean bossSpawned = false;
    private String message = "Game Over";

    private final Player player;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;

    private Image[] backgrounds;
    private int[] backgroundRepeatCounts;
    private int currentBackgroundIndex = 0;
    private int currentBackgroundRepeat = 0;
    private int backgroundY = 0;
    private static final int BACKGROUND_SCROLL_SPEED = 2;

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();
    private final HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();

    public Scene2(Game game, Player player) {
        this.game = game;
        this.player = player;
        loadSpawnDetails();
    }


    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene2.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        if (player != null) {
            player.setDying(false);
            if (player.getImage() != null) {
                int width = player.getImage().getWidth(null);
                int height = player.getImage().getHeight(null);
                player.setX((BOARD_WIDTH - width) / 2);
                player.setY(BOARD_HEIGHT - height - 20);
            }
        }

        backgrounds = new Image[] {
            new ImageIcon("src/images/s1.jpeg").getImage(),
            new ImageIcon("src/images/s1_2.jpeg").getImage(),
            new ImageIcon("src/images/s1_3.jpeg").getImage(),
        };
        backgroundRepeatCounts = new int[] { 1, 4, Integer.MAX_VALUE };
    }

    private void drawBackground(Graphics g) {
        if (backgrounds == null || backgrounds.length == 0) {
            return;
        }
        Image current = backgrounds[currentBackgroundIndex];
        int nextIndex = Math.min(currentBackgroundIndex + 1, backgrounds.length - 1);
        Image next = backgrounds[nextIndex];

        g.drawImage(current, 0, backgroundY, BOARD_WIDTH, BOARD_HEIGHT, this);
        g.drawImage(next, 0, backgroundY - BOARD_HEIGHT, BOARD_WIDTH, BOARD_HEIGHT, this);
    }

    private void loadSpawnDetails() {
        spawnMap.clear();
        spawnWaveOne();
        spawnWaveTwo();
        spawnWaveThree();
        spawnBossSeed();
    }

    private void spawnWaveOne() {
        spawnMap.put(80, new SpawnDetails("Alien1", 150, 0));
        spawnMap.put(120, new SpawnDetails("Alien1", 450, 0));
        spawnMap.put(160, new SpawnDetails("PowerUp-SpeedUp", 280, 0));
    }

    private void spawnWaveTwo() {
        for (int i = 0; i < 6; i++) {
            spawnMap.put(260 + i * 20, new SpawnDetails("Alien1", 80 + i * 90, 0));
        }
    }

    private void spawnWaveThree() {
        for (int i = 0; i < 10; i++) {
            spawnMap.put(420 + i * 15, new SpawnDetails("Alien1", 40 + i * 70, 0));
        }
        spawnMap.put(580, new SpawnDetails("PowerUp-SpeedUp", 360, 0));
    }
//boss2 spawn
    private void spawnBossSeed() {
    spawnMap.put(900, new SpawnDetails("Boss2", BOARD_WIDTH / 2 - 90, 60));
}

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }
        //Remove enemy bomb 
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (!enemy.isVisible() || enemy.isDying()) {
                Bomb bomb = enemy.getBomb();
                if (bomb != null) {
                    bomb.setDestroyed(true);
                }
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);
    }
    //Draw boss health 
    private void drawBossHealthBar(Graphics g) {
        final int barWidth = 320;
        final int barHeight = 18;
        final int barX = (BOARD_WIDTH - barWidth) / 2;
        final int barY = 18;

        for (Enemy enemy : enemies) {
            if (enemy instanceof Boss2 boss && boss.isVisible()) {
                float ratio = boss.getMaxHp() == 0 ? 0 : (float) boss.getHp() / boss.getMaxHp();
                int currentWidth = (int) (barWidth * Math.max(0, Math.min(1f, ratio)));

                g.setColor(new Color(0, 0, 0, 150));
                g.fillRoundRect(barX - 10, barY - 8, barWidth + 20, barHeight + 24, 12, 12);

                g.setColor(Color.DARK_GRAY);
                g.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);

                g.setColor(new Color(220, 0, 0));
                g.fillRoundRect(barX + 2, barY + 2, Math.max(0, currentWidth - 4), barHeight - 4, 8, 8);

                g.setColor(Color.WHITE);
                g.drawRoundRect(barX, barY, barWidth, barHeight, 10, 10);
                break;
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {

        for (Enemy e : enemies) {
            Bomb bomb = e.getBomb();
            if (bomb != null && !bomb.isDestroyed()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);

        g.setColor(Color.green);

        if (inGame) {

            drawBackground(g);
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawBossHealthBar(g);
            drawBombing(g);
            drawPlayer(g);
            drawShot(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {


        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails spawn = spawnMap.get(frame);
        if (spawn != null) {
            switch (spawn.type) {
                case "Alien1":
                    enemies.add(new Alien1(spawn.x, spawn.y));
                    break;
                case "PowerUp-SpeedUp":
                    powerups.add(new SpeedUp(spawn.x, spawn.y));
                    break;
                case "Boss2":
                    enemies.add(new Boss2(spawn.x, spawn.y));
                    bossSpawned = true;
                    break;
                default:
                    System.out.println("Unknown spawn type: " + spawn.type);
                    break;
            }
        }

        if (!bossSpawned && deaths >= NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            if (timer != null) {
                timer.stop();
            }
            message = "Mission Complete!";
        }

        backgroundY += BACKGROUND_SCROLL_SPEED;
        if (backgroundY >= BOARD_HEIGHT) {
            backgroundY = 0;
            currentBackgroundRepeat++;
            if (currentBackgroundRepeat >= backgroundRepeatCounts[currentBackgroundIndex]) {
                if (currentBackgroundIndex < backgrounds.length - 1) {
                    currentBackgroundIndex++;
                    currentBackgroundRepeat = 0;
                }
            }
        }

        player.act();

        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        handleShots();
        handleEnemyMovement();
        handleEnemyBombs();
    }

    private void handleShots() {
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (!shot.isVisible()) {
                shotsToRemove.add(shot);
                continue;
            }

            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Enemy enemy : enemies) {
                if (!enemy.isVisible()) {
                    continue;
                }

                int enemyX = enemy.getX();
                int enemyY = enemy.getY();
                int enemyWidth = enemy.getImage().getWidth(null);
                int enemyHeight = enemy.getImage().getHeight(null);

                if (shotX >= enemyX && shotX <= enemyX + enemyWidth
                        && shotY >= enemyY && shotY <= enemyY + enemyHeight) {

                    if (enemy instanceof Boss2 boss) {
                        boss.damage();
                    } else {
                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                    }

                    shot.die();
                    shotsToRemove.add(shot);
                    break;
                }
            }

            int y = shot.getY() - 20;
            if (y < 0) {
                shot.die();
                shotsToRemove.add(shot);
            } else {
                shot.setY(y);
            }
        }
        shots.removeAll(shotsToRemove);
    }

    private void handleEnemyMovement() {
        for (Enemy enemy : enemies) {
            int x = enemy.getX();
            if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                direction = -1;
                for (Enemy e2 : enemies) {
                    e2.setY(e2.getY() + GO_DOWN);
                }
            }
            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;
                for (Enemy e2 : enemies) {
                    e2.setY(e2.getY() + GO_DOWN);
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isVisible()) {
                continue;
            }
            int y = enemy.getY();
            if (y > BOARD_HEIGHT - ALIEN_HEIGHT) {
                inGame = false;
                message = "Invasion!";
            }
            enemy.act(direction);
        }
    }

    private void handleEnemyBombs() {
        for (Enemy enemy : enemies) {
            enemy.attack(player, randomizer);
            Bomb bomb = enemy.getBomb();
            if (bomb == null) {
                continue;
            }

            if (!bomb.isDestroyed()) {
                bomb.act();

                if (player.isVisible()
                        && bomb.getX() >= player.getX()
                        && bomb.getX() <= player.getX() + PLAYER_WIDTH
                        && bomb.getY() >= player.getY()
                        && bomb.getY() <= player.getY() + PLAYER_HEIGHT) {

                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }

                if (bomb.getY() >= BOARD_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < MAX_PLAYER_SHOTS) {
                    // Create a new shot and add it to the list
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }

        }
    }
}
