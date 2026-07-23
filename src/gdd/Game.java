package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import gdd.sprite.Player;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JFrame  {

    private TitleScene titleScene;
    private Scene1 scene1;
    private Scene2 scene2;
    private JPanel currentScene;

    public Game() {
        initUI();
        loadTitle();
    }

    private void initUI() {

        setTitle("Nova Storm");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

    }

        public void loadTitle() {
        stopCurrentScene();
        titleScene = new TitleScene(this);
        showScene(titleScene);
        titleScene.start();
    }

    public void loadScene1() {
        stopCurrentScene();
        scene1 = new Scene1(this);
        showScene(scene1);
        scene1.start();
    }

    public void loadScene2(Player player) {
        stopCurrentScene();
        scene2 = new Scene2(this, player);
        showScene(scene2);
        scene2.start();
    }

    private void stopCurrentScene() {
        if (currentScene == null) {
            return;
        }

        if (currentScene instanceof TitleScene title) {
            title.stop();
        } else if (currentScene instanceof Scene1 s1) {
            s1.stop();
        } else if (currentScene instanceof Scene2 s2) {
            s2.stop();
        }

        remove(currentScene);
        currentScene = null;
    }

    private void showScene(JPanel scene) {
        currentScene = scene;
        add(scene);
        scene.requestFocusInWindow();
        revalidate();
        repaint();
    }
}