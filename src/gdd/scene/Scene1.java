package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Enemy;
import gdd.sprite.Bomb;
import gdd.sprite.Boss;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Image;
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

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private boolean stageClear = false;
    //Background
    private Image[] backgrounds;
    private int[] backgroundRepeatCounts;
    private int currentBackgroundIndex = 0;
    private int currentBackgroundRepeat = 0;
    private int backgroundY = 0;
    private static final int BACKGROUND_SCROLL_SPEED = 1;
    // private Shot shot;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;
    private boolean showingStageComplete = false;
    private int stageCompleteTimer = 0;
    private static final int STAGE_COMPLETE_DURATION = 180; //3 s

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;
    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
    }

    public Player getPlayer() {
        return player;
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        spawnMap.clear();
        spawnWaveOne();
        spawnWaveTwo();
        spawnBoss();
    }

    private void spawnWaveOne() {
        // TODO load this configuration from data file
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", 100, 0));
        spawnMap.put(200, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(300, new SpawnDetails("Alien1", 300, 0));
    }

    private void spawnWaveTwo() {
        spawnMap.put(400, new SpawnDetails("Alien1", 400, 0));
        spawnMap.put(401, new SpawnDetails("Alien1", 450, 0));
        spawnMap.put(402, new SpawnDetails("Alien1", 500, 0));
        spawnMap.put(403, new SpawnDetails("Alien1", 550, 0));

        spawnMap.put(500, new SpawnDetails("Alien1", 100, 0));
        spawnMap.put(501, new SpawnDetails("Alien1", 150, 0));
        spawnMap.put(502, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(503, new SpawnDetails("Alien1", 350, 0));
    }

    private void spawnBoss() {
        // Placeholder for boss encounter trigger
    }

    private void initBoard() {

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
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        backgrounds = new Image[] {
            new ImageIcon("src/images/s1.jpeg").getImage(),
            new ImageIcon("src/images/s1_2.jpeg").getImage(),
            new ImageIcon("src/images/s1_3.jpeg").getImage(),
        };
        backgroundRepeatCounts = new int[] { 1, 4, Integer.MAX_VALUE };
        // shot = new Shot();
        bossDefeated = false;
        stageClear = false;
    }

    private void drawMap(Graphics g) {
    Image currentBackground = backgrounds[currentBackgroundIndex];
    int nextIndex = Math.min(currentBackgroundIndex + 1, backgrounds.length - 1);
    Image nextBackground = backgrounds[nextIndex];
    g.drawImage(currentBackground,
            0,
            backgroundY,
            BOARD_WIDTH,
            BOARD_HEIGHT,
            this);

    g.drawImage(nextBackground,
            0,
            backgroundY - BOARD_HEIGHT,
            BOARD_WIDTH,
            BOARD_HEIGHT,
            this);
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
            if (enemy instanceof Boss boss && boss.isVisible()) {
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

            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawBossHealthBar(g);
            drawBombing(g);
            drawPlayer(g);
            drawShot(g);

            if (showingStageComplete) {
            drawStageComplete(g);
        }
        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }
    // ...existing code...

private void drawStageComplete(Graphics g) {
    
    // 深色半透明背景
    g.setColor(new Color(0, 0, 0, 180));
    g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
    
    // ===== 修正: 主面板背景居中 =====
    int panelWidth = 600;
    int panelHeight = 280;
    int panelX = (BOARD_WIDTH - panelWidth) / 2;  // 水平居中
    int panelY = (BOARD_HEIGHT - panelHeight) / 2;  // 垂直居中
    
    g.setColor(new Color(20, 30, 50, 230));
    g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
    
    // 金色边框
    g.setColor(new Color(255, 215, 0));
    g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
    
    // ===== 标题 "MISSION COMPLETE" =====
    g.setColor(new Color(255, 220, 0));
    g.setFont(new Font("Arial", Font.BOLD, 48));
    String title = "MISSION COMPLETE";
    int titleWidth = g.getFontMetrics().stringWidth(title);
    g.drawString(title, (BOARD_WIDTH - titleWidth) / 2, panelY + 70);
    
    // 标题下方横线
    g.setColor(new Color(255, 215, 0));
    int lineWidth = 360;
    g.fillRect((BOARD_WIDTH - lineWidth) / 2, panelY + 90, lineWidth, 2);
    
    // ===== "Stage 1 Cleared" =====
    g.setColor(new Color(100, 255, 100));
    g.setFont(new Font("Arial", Font.BOLD, 30));
    String cleared = "Stage 1 Cleared";
    int clearedWidth = g.getFontMetrics().stringWidth(cleared);
    g.drawString(cleared, (BOARD_WIDTH - clearedWidth) / 2, panelY + 140);
    
    // ===== "Next Mission" =====
    g.setColor(new Color(200, 200, 255));
    g.setFont(new Font("Arial", Font.PLAIN, 22));
    String next = "Next Mission";
    int nextWidth = g.getFontMetrics().stringWidth(next);
    g.drawString(next, (BOARD_WIDTH - nextWidth) / 2, panelY + 185);
    
    // ===== "Alien Planet" =====
    g.setColor(new Color(0, 255, 255));
    g.setFont(new Font("Arial", Font.BOLD, 32));
    String planet = "Alien Planet";
    int planetWidth = g.getFontMetrics().stringWidth(planet);
    g.drawString(planet, (BOARD_WIDTH - planetWidth) / 2, panelY + 225);
    
    // ===== 底部进度条 =====
    int barWidth = 400;
    int barHeight = 6;
    int barX = (BOARD_WIDTH - barWidth) / 2;
    int barY = panelY + 255;
    
    // 进度条背景
    g.setColor(new Color(60, 60, 80));
    g.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);
    
    // 进度
    float progress = Math.min(1f, (float)stageCompleteTimer / STAGE_COMPLETE_DURATION);
    int currentWidth = (int)(barWidth * progress);
    
    g.setColor(new Color(0, 200, 255));
    g.fillRoundRect(barX, barY, currentWidth, barHeight, 3, 3);
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
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                // Add more cases for different enemy types if needed
                case "Alien2":
                    // Enemy enemy2 = new Alien2(sd.x, sd.y);
                    // enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        // player
        // Background scroll
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

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
                if(powerup.getY() > BOARD_HEIGHT) {
                    powerup.die();
            }
        }
    }
        powerups.removeIf(powerup -> !powerup.isVisible());
    

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
            }
        }

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();
                    
                    int enemyWidth = enemy.getImage().getWidth(null);
                    int enemyHeight = enemy.getImage().getHeight(null);

                    if (enemy.isVisible() && shot.isVisible()
                        && shotX >= enemyX
                        && shotX <= enemyX + enemyWidth
                        && shotY >= enemyY
                        && shotY <= enemyY + enemyHeight) {
                        

                    if (enemy instanceof Boss boss) {
                        boss.damage(); 
                        if(boss.isDying()) {
                            bossDefeated = true;
                            spawnBossPowerUps(boss);
                        }

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

                int y = shot.getY();
                // y -= 4;
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);


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
            if (enemy.isVisible()) {
                int y = enemy.getY();
                if (y > BOARD_HEIGHT - ALIEN_HEIGHT) {
                    inGame = false;
                    message = "Invasion!";
                }
                enemy.act(direction);
            }
        }
        //boos  Spawn
        if (!bossSpawned
                && enemies.isEmpty()
                && frame > 500) {

            Boss boss = new Boss(250, 50);
            enemies.add(boss);
            bossSpawned = true;
        }

        // bombs - enemy attack logic
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

        checkStageClearAndAdvance();
    }

    //After boss, spawn power ups
    private void spawnBossPowerUps(Boss boss) {
        int baseX = boss.getX();
        int baseY = boss.getY();
        powerups.add(new SpeedUp(baseX - 40, baseY));
        powerups.add(new SpeedUp(baseX, baseY));
        powerups.add(new SpeedUp(baseX + 40, baseY));
    }

    //check if the scene is clear
    private void checkStageClearAndAdvance() {
    
        if (bossDefeated && !stageClear) {
        
        // check all power-ups collected
            boolean allPowerUpsCollected = true;
            for (PowerUp powerup : powerups) {
                if (powerup.isVisible()) {
                    allPowerUpsCollected = false;
                    break;
            }
        }
        
        // all power-ups collected
        if (allPowerUpsCollected) {
            stageClear = true;
            showingStageComplete = true;
            stageCompleteTimer = 0;
        }
    }

        if (showingStageComplete) {
            stageCompleteTimer++;
        
    
            if (stageCompleteTimer >= STAGE_COMPLETE_DURATION) {
                transitionToScene2();
            }
        }
    }

    // Removed duplicate checkStageClearAndAdvance method

    private void transitionToScene2() {
        stop();
        game.loadScene2(player);
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
                if (shots.size() < 4) {
                    // Create a new shot and add it to the list
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }

        }
    }
}
