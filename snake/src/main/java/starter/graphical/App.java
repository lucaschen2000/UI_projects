package starter.graphical;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.LinkedList;
import java.util.Queue;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class App extends Application {
    int x_resolution = 1280;
    int y_resolution = 800;

    enum SCENES {SPLASH, LEVEL1, LEVEL2, LEVEL3, PAUSE, END}
    Scene splash, levelScene, endScene, pauseScene;
    Group splashRoot;
    Group endRoot;
    Group pauseRoot;
    Pane levelRoot;
    GraphicsContext gc;
    int score = 0;
    int highscore = 0;
    int fruit_eaten = 0;
    final double speed1 = 0.05;
    final double speed2 = 0.1;
    final double speed3 = 0.2;
    double speed = speed1;
    int gridLength = 30;
    int width = gridLength;
    int height = gridLength;
    int cellSize = y_resolution/gridLength;
    final int time_limit = 30;
    int time_left = time_limit;

    boolean game_over = false;
    boolean initial_spawn = true;
    Queue<Integer> levelSelect = new LinkedList<>();

    List<PointDir> snake = new ArrayList<>();
    List<Point> fruits = new ArrayList<>();
    int fruit_x = 0;
    int fruit_y = 0;

    Random random = new Random();


    public enum Direction{
        up, down, left, right
    }

    public Direction leftTurn(Direction dir){
        if(dir == Direction.up){
            return Direction.left;
        } else if(dir == Direction.down){
            return Direction.right;
        } else if(dir == Direction.left){
            return Direction.down;
        } else if(dir == Direction.right){
            return Direction.up;
        }
        return Direction.up;
    }

    public Direction rightTurn(Direction dir){
        if(dir == Direction.up){
            return Direction.right;
        } else if(dir == Direction.down){
            return Direction.left;
        } else if(dir == Direction.left){
            return Direction.up;
        } else if(dir == Direction.right){
            return Direction.down;
        }
        return Direction.up;
    }

    public class Point{
        int x;
        int y;
        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public class PointDir{
        double x;
        double y;
        Direction dir;
        Queue<Direction> movement = new LinkedList<>();
        int buffer = 0;
        public PointDir(double x, double y, Direction dir){
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal rounded = BigDecimal.valueOf(value);
        rounded = rounded.setScale(places, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }

    @Override
    public void start(Stage stage) throws Exception {

        splashRoot = new Group();
        splash = new Scene(splashRoot, x_resolution, y_resolution, Color.BLACK);

        levelRoot = new Pane();
        levelScene = new Scene(levelRoot, x_resolution, y_resolution);

        endRoot = new Group();
        endScene = new Scene(endRoot, x_resolution, y_resolution, Color.BLACK);

        pauseRoot = new Group();
        pauseScene = new Scene(pauseRoot, x_resolution, y_resolution, Color.TAN);

        splashScreen(stage);

        splash.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.DIGIT1) {
                setScene(stage, SCENES.LEVEL1);

                levelScreen(stage, SCENES.LEVEL1);
                time_left = time_limit;
            }
            if(event.getCode() == KeyCode.Q) {
                setScene(stage, SCENES.END);
                endScreen(stage);
            }
        });
    }

    void setScene(Stage stage, SCENES scene) {
        switch(scene) {
            case SPLASH:
                stage.setTitle("L443chen Lucas' Snake");
                stage.setScene(splash);
                stage.show();
                break;
            case LEVEL1:
                stage.setTitle("Level 1");
                stage.setScene(levelScene);
                stage.show();
                break;
            case LEVEL2:
                stage.setTitle("Level 2");
                stage.setScene(levelScene);
                stage.show();
                break;
            case LEVEL3:
                stage.setTitle("Level 3");
                stage.setScene(levelScene);
                stage.show();
                break;
            case PAUSE:
                stage.setTitle("PAUSED");
                stage.setScene(pauseScene);
                stage.show();
                break;
            case END:
                stage.setTitle("Game over");
                stage.setScene(endScene);
                stage.show();
                break;
        }
    }

    void levelScreen(Stage stage, SCENES scene){
        fruit_eaten = 0;
        if(scene == SCENES.LEVEL1){
            spawnFruitLevel1();
            speed = speed1;
        } else if(scene == SCENES.LEVEL2){
            spawnFruitLevel2();
            speed = speed2;
        } else if(scene == SCENES.LEVEL3){
            spawnFruitLevel3();
            speed = speed3;
        }



        if(scene == SCENES.LEVEL1 && initial_spawn == true){
            Canvas canvas = new Canvas(x_resolution, y_resolution);
            gc = canvas.getGraphicsContext2D();
            levelRoot.getChildren().add(canvas);
            snake.add(new PointDir(width/2, height/2, Direction.up));
            snake.add(new PointDir(width/2, height/2 + 1, Direction.up));
            initial_spawn = false;
        }

        AnimationTimer timer = new AnimationTimer() {
            long prevFrame = 0;
            @Override
            public void handle(long now) {
                if(snake.isEmpty() || game_over){
                    this.stop();
                    setScene(stage, SCENES.END);
                    endScreen(stage);
                    return;
                }
                double x_coord = round(snake.get(0).x, 5);
                double y_coord = round(snake.get(0).y, 5);

                if(!levelSelect.isEmpty() && (int)x_coord == x_coord && (int)y_coord == y_coord){

                    this.stop();
                    int level = levelSelect.remove();
                    if(level == 1){
                        setScene(stage, SCENES.LEVEL1);
                        levelScreen(stage, SCENES.LEVEL1);
                    } else if(level == 2){
                        setScene(stage, SCENES.LEVEL2);
                        levelScreen(stage, SCENES.LEVEL2);
                    } else if(level == 3){
                        setScene(stage, SCENES.LEVEL3);
                        levelScreen(stage, SCENES.LEVEL3);
                    }
                    return;
                }
                if(prevFrame == 0){
                    prevFrame = now;
                    tick(gc, stage, scene, this, now - prevFrame);
                    return;
                }

                tick(gc, stage, scene, this, now - prevFrame);
                if(now - prevFrame > 1000000000){
                    prevFrame = now;
                }
            }
        };
        timer.start();

        levelScene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT) {
//                snake_dir = leftTurn(snake_dir);
//                System.out.println("LEFT");
//                snake.get(0).dir = Direction.left;
                snake.get(0).movement.add(leftTurn(snake.get(0).dir));
            }
            if(event.getCode() == KeyCode.RIGHT) {
//                snake_dir = rightTurn(snake_dir);
//                System.out.println("RIGHT");
//                snake.get(0).dir = Direction.right;
                snake.get(0).movement.add(rightTurn(snake.get(0).dir));

            }
            if(event.getCode() == KeyCode.P) {
                timer.stop();
                setScene(stage, SCENES.PAUSE);
                pauseScreen(stage, scene);
                return;
            }
            if(event.getCode() == KeyCode.R) {
                timer.stop();
                setScene(stage, SCENES.SPLASH);
                return;
            }
            if(event.getCode() == KeyCode.DIGIT1) {
                levelSelect.add(1);
                time_left = time_limit;
                return;
            }
            if(event.getCode() == KeyCode.DIGIT2) {
                levelSelect.add(2);
                time_left = time_limit;
                return;
            }
            if(event.getCode() == KeyCode.DIGIT3) {
                levelSelect.add(3);
                time_left = time_limit;
                return;
            }
            if(event.getCode() == KeyCode.Q) {
                timer.stop();
                setScene(stage, SCENES.END);
                endScreen(stage);
                return;
            }
        });
    }

    public void tick(GraphicsContext gc, Stage stage, SCENES scene, AnimationTimer timer, long second){

//        System.out.println(now);
//        double x_coord = round(snake.get(0).x, 5);
//        double y_coord = round(snake.get(0).y, 5);
//        if(movement.isEmpty()){
////            System.out.println("EMPTY");
//        } else {
////            System.out.println("NOT EMPTY");
//
////            System.out.print((int)x_coord);System.out.print(" ");
////            System.out.print(x_coord);System.out.print(" ");
////            System.out.print((int)y_coord);System.out.print(" ");
////            System.out.print(y_coord);System.out.print(" ");
////            System.out.print((int)x_coord == x_coord);
////            System.out.println((int)y_coord == y_coord);
//            if((int)x_coord == x_coord && (int)y_coord == y_coord){
////                System.out.println("SNAPPED TO GRID");
//                snake.get(1).dir = movement.peek();
//                snake.get(0).dir = movement.remove();
//
//            }
//        }
//        System.out.print(round(snake.get(0).x, 5));System.out.print(" ");
//        System.out.println(round(snake.get(0).y, 5));
        for (int i = snake.size() - 1; i >= 0; i--){
//            System.out.print("snake size: " + snake.size());
//            System.out.println("index: " + i);
            double x_coord = round(snake.get(i).x, 5);
            double y_coord = round(snake.get(i).y, 5);
            if(snake.get(i).movement.isEmpty()){
            } else {
                if((int)x_coord == x_coord && (int)y_coord == y_coord){
                    if(snake.get(i).buffer == 0){
                        Direction newdir = snake.get(i).movement.remove();
                        snake.get(i).dir = newdir;
//                        if( i == 0 ){
//                            System.out.println("HEAD TURNING");
//                        }
                        if(i != snake.size() - 1){
//                            System.out.println("TURN and apply");
                            snake.get(i+1).movement.add(newdir);
                            snake.get(i+1).buffer += 1/speed - 1;
                        }
                    }
                }
                if(snake.get(i).buffer > 0){
                    snake.get(i).buffer--;
                }
            }
        }


        for (int i = 0; i <= snake.size() - 1; i++){
            if(snake.get(i).dir == Direction.up){
                snake.get(i).y -= speed;
            } else if (snake.get(i).dir == Direction.down){
                snake.get(i).y += speed;
            } else if (snake.get(i).dir == Direction.left){
                snake.get(i).x -= speed;
            } else if (snake.get(i).dir == Direction.right){
                snake.get(i).x += speed;
            }
        }


        if(snake.get(0).y <= -1){
            game_over = true;
        }
        if(snake.get(0).y >= height){
            game_over = true;
        }
        if(snake.get(0).x <= -1){
            game_over = true;
        }
        if(snake.get(0).x >= width){
            game_over = true;
        }

        for(int i = 1; i < snake.size(); i++){
            if(round(snake.get(0).x, 2) == round(snake.get(i).x,2) && round(snake.get(0).y,2) == round(snake.get(i).y, 2)){
                game_over = true;
                return;
            }
        }

        for(int i = 0; i < fruits.size(); i++){
            Point f = fruits.get(i);
            double x_coord = round(snake.get(0).x, 5);
            double y_coord = round(snake.get(0).y, 5);
            if((int)x_coord == x_coord && (int)y_coord == y_coord) {
                if (f.x == round(snake.get(0).x, 5) && f.y == round(snake.get(0).y, 5)) { //eaten

                    Direction snake_dir = snake.get(snake.size() - 1).dir;
                    if (snake_dir == Direction.up) {
                        snake.add(new PointDir(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y + 1, Direction.up));
                    } else if (snake_dir == Direction.down) {
                        snake.add(new PointDir(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y - 1, Direction.down));
                    } else if (snake_dir == Direction.left) {
                        snake.add(new PointDir(snake.get(snake.size() - 1).x + 1, snake.get(snake.size() - 1).y, Direction.left));
                    } else if (snake_dir == Direction.right) {
                        snake.add(new PointDir(snake.get(snake.size() - 1).x - 1, snake.get(snake.size() - 1).y, Direction.right));
                    }

                    fruits.remove(i);
                    spawnFruit();
                    fruit_eaten++;
                    if (scene == SCENES.LEVEL1) {
                        score = score + 1;
                    } else if (scene == SCENES.LEVEL2) {
                        score = score + 2;
                    } else if (scene == SCENES.LEVEL3) {
                        score = score + 3;
                    }
                    String sound = getClass().getClassLoader().getResource("click.mp3").toString();
                    AudioClip eat = new AudioClip(sound);
                    eat.play();
                }
            }
        }
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width*cellSize,height*cellSize);


        for(int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if ((i + j) % 2 == 1) {
                    gc.setFill(Color.TAN);
                } else {
                    gc.setFill(Color.BURLYWOOD);
                }
                gc.fillRect(cellSize * j,cellSize * i,cellSize,cellSize);
            }
        }

        gc.setFill(Color.MEDIUMPURPLE);
        for(Point f: fruits){
            Image image = new Image("apple.png");

            gc.drawImage(image, f.x * cellSize, f.y * cellSize, cellSize, cellSize);
        }
        Image head;
        if(snake.get(0).dir == Direction.down){
            head = new Image("snakeDown.png");
        } else if(snake.get(0).dir == Direction.up){
            head = new Image("snakeUp.png");
        } else if(snake.get(0).dir == Direction.left){
            head = new Image("snakeLeft.png");
        } else {
            head = new Image("snakeRight.png");
        }
        gc.drawImage(head, snake.get(0).x * cellSize, snake.get(0).y * cellSize, cellSize, cellSize);

        for(int i = 1; i < snake.size(); i++){
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(snake.get(i).x*cellSize, snake.get(i).y*cellSize, cellSize, cellSize);
            gc.setFill(Color.LIMEGREEN);
            gc.fillRect(snake.get(i).x*cellSize, snake.get(i).y*cellSize, cellSize-1, cellSize-1);
        }

        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(width*cellSize,0, x_resolution - width*cellSize,height*cellSize);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("",60));
        if(scene == SCENES.LEVEL1){
            gc.fillText("Level 1", width*cellSize + 10, 50);
        } else if(scene == SCENES.LEVEL2){
            gc.fillText("Level 2", width*cellSize + 10, 50);
        } else if(scene == SCENES.LEVEL3){
            gc.fillText("Level 3", width*cellSize + 10, 50);
        }

        if(scene != SCENES.LEVEL3){
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("",60));
            gc.fillText("Time left: " + time_left, width*cellSize + 10, 600);
            if(second > 1000000000){
                time_left--;
            }
            if(time_left <= 0){
                time_left = time_limit;
                if(scene == SCENES.LEVEL1){
                    levelSelect.add(2);
                }
                if(scene == SCENES.LEVEL2){
                    levelSelect.add(3);
                }

            }
        }



        gc.setFill(Color.BLACK);
        gc.setFont(new Font("",40));
        gc.fillText("Points: " + score, width*cellSize + 10, 200);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("",40));
        gc.fillText("Fruit eaten this level: " + fruit_eaten, width*cellSize + 10, 400);

        gc.setFill(Color.SLATEGREY);
        gc.fillRect(0,cellSize * height,x_resolution, y_resolution - cellSize * height);
    }

    void splashScreen(Stage stage){
        Text text_name = new Text(100, 150, "Lucas Chen's Snake");
        text_name.setFont(Font.font("calibri", FontWeight.BOLD, FontPosture.REGULAR, 100));
        text_name.setFill(Color.RED);
        Text text_userID = new Text(100, 250, "UserID: L443chen");
        text_userID.setFont(Font.font("calibri", FontWeight.NORMAL, FontPosture.REGULAR, 50));
        text_userID.setFill(Color.LIGHTBLUE);

        Text text_instructions = new Text(100, 325, "Instructions:\n-You control a snake, use left and right keys\n\tto turn "+
                "relative to your current direction\n-Collect fruit and grow, if you run into a wall or \n\tyourself, you lose!\n" +
                "-Collect as much fruit as possible and beat your friends!\n" + "-Score multiplier: Level 1:*1 " +
                "Level 2:*2 Level 3:*3");
        text_instructions.setFont(Font.font("calibri", FontWeight.NORMAL, FontPosture.REGULAR, 40));
        text_instructions.setFill(Color.LIGHTCORAL);

        Text text_start = new Text(100, 750, "Press 1 to start");
        text_start.setFont(Font.font("calibri", FontWeight.BOLD, FontPosture.REGULAR, 80));
        text_start.setFill(Color.LIGHTGREEN);

        splashRoot.getChildren().add(text_name);
        splashRoot.getChildren().add(text_userID);
        splashRoot.getChildren().add(text_instructions);
        splashRoot.getChildren().add(text_start);

        stage.setTitle("L443chen Lucas' Snake");
        stage.setScene(splash); // set multiple scenes: splash, level 1, 2, 3, switchScenes
        stage.setMinWidth(x_resolution);
        stage.setMinWidth(x_resolution);
        stage.setResizable(false);
        stage.show();

        splash.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.DIGIT1) {
                setScene(stage, SCENES.LEVEL1);
                levelScreen(stage, SCENES.LEVEL1);
                time_left = time_limit;
                return;
            }
            if(event.getCode() == KeyCode.DIGIT2) {
                setScene(stage, SCENES.LEVEL2);
                levelScreen(stage, SCENES.LEVEL2);
                time_left = time_limit;
                return;
            }
            if(event.getCode() == KeyCode.DIGIT3) {
                setScene(stage, SCENES.LEVEL3);
                levelScreen(stage, SCENES.LEVEL3);
                time_left = time_limit;
                return;
            }
            if(event.getCode() == KeyCode.Q) {
                setScene(stage, SCENES.END);
                endScreen(stage);
                return;
            }
        });
    }

    void pauseScreen(Stage stage, SCENES scene){
        Text text_name = new Text(100, 150, "PAUSED");
        text_name.setFont(Font.font("calibri", FontWeight.BOLD, FontPosture.REGULAR, 100));
        text_name.setFill(Color.BLACK);

        pauseRoot.getChildren().add(text_name);


        pauseScene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.P) {
                if(scene == SCENES.LEVEL1){
                    setScene(stage, SCENES.LEVEL1);
                    levelScreen(stage, SCENES.LEVEL1);
                } else if(scene == SCENES.LEVEL2){
                    setScene(stage, SCENES.LEVEL2);
                    levelScreen(stage, SCENES.LEVEL2);
                } else if(scene == SCENES.LEVEL3){
                    setScene(stage, SCENES.LEVEL3);
                    levelScreen(stage, SCENES.LEVEL3);
                }
            }
        });
    }

    void endScreen(Stage stage){

        String sound = getClass().getClassLoader().getResource("endsound.mp3").toString();
        AudioClip endsoundbyte = new AudioClip(sound);
        endsoundbyte.play();

        Text text_score = new Text(100, 150, "Your Score: " + score);
        text_score.setFont(Font.font("calibri", FontWeight.BOLD, FontPosture.REGULAR, 80));
        text_score.setFill(Color.RED);

        if(score > highscore){
            highscore = score;
        }

        Text text_highscore = new Text(100, 300, "High score this session: " + highscore);
        text_highscore.setFont(Font.font("calibri", FontWeight.BOLD, FontPosture.REGULAR, 80));
        text_highscore.setFill(Color.RED);

        Text text_end = new Text(100, 500, "GAME OVER, press 1 to restart at level 1 or R to go to title screen");
        text_end.setFont(Font.font("calibri", FontWeight.BOLD, FontPosture.REGULAR, 30));
        text_end.setFill(Color.RED);

        Rectangle background = new Rectangle();
        background.setFill(Color.BLACK);
        background.setHeight(y_resolution);
        background.setWidth(x_resolution);

        endRoot.getChildren().add(background);
        endRoot.getChildren().add(text_score);
        endRoot.getChildren().add(text_highscore);
        endRoot.getChildren().add(text_end);


        score = 0;
        fruit_eaten = 0;
        time_left = time_limit;
        fruits.clear();
        snake.clear();
        initial_spawn = true;
        game_over = false;
        endScene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.R) {
                setScene(stage, SCENES.SPLASH);
                return;
            }
            if(event.getCode() == KeyCode.DIGIT1) {
                time_left = time_limit;
                setScene(stage, SCENES.LEVEL1);
                levelScreen(stage, SCENES.LEVEL1);
                return;
            }
            if(event.getCode() == KeyCode.Q) {
                setScene(stage, SCENES.END);
                endScreen(stage);
                return;
            }
        });
    }

    public void spawnFruit(){
        start: while(true){
            fruit_x = random.nextInt(width);
            fruit_y = random.nextInt(height);
            if(fruitOverlap(fruit_x, fruit_y)) continue start;
            fruits.add(new Point(fruit_x, fruit_y));
            break;
        }
    }

    public boolean fruitOverlap(int x, int y) {
        for(PointDir s:snake){
            if(Math.round(s.x) == fruit_x && Math.round(s.y) == fruit_y){
                return true;
            }
        }
        for(Point f:fruits){
            if(f.x == fruit_x && f.y == fruit_y){
                return true;
            }
        }
        return false;
    }

    public void spawnFruitLevel1(){
        fruits.clear();
        spawnLevelFruit(5, 6);
        spawnLevelFruit(15, 12);
        spawnLevelFruit(5, 19);
        spawnLevelFruit(26, 22);
        spawnLevelFruit(0, 0);
    }

    public void spawnFruitLevel2(){
        fruits.clear();
        spawnLevelFruit(10, 16);
        spawnLevelFruit(2, 27);
        spawnLevelFruit(15, 9);
        spawnLevelFruit(30, 2);
        spawnLevelFruit(7, 17);

        spawnLevelFruit(14, 9);
        spawnLevelFruit(23, 10);
        spawnLevelFruit(5, 4);
        spawnLevelFruit(18, 28);
        spawnLevelFruit(3, 13);
    }

    public void spawnFruitLevel3(){
        fruits.clear();
        spawnLevelFruit(18, 17);
        spawnLevelFruit(23, 21);
        spawnLevelFruit(13, 18);
        spawnLevelFruit(4, 23);
        spawnLevelFruit(5, 26);

        spawnLevelFruit(10, 6);
        spawnLevelFruit(1, 12);
        spawnLevelFruit(4, 9);
        spawnLevelFruit(1, 7);
        spawnLevelFruit(24, 17);

        spawnLevelFruit(11, 12);
        spawnLevelFruit(20, 4);
        spawnLevelFruit(14, 20);
        spawnLevelFruit(24, 9);
        spawnLevelFruit(11, 7);
    }

    public void spawnLevelFruit(int x, int y){
        if(fruitOverlap(x, y)) spawnFruit();
        else fruits.add(new Point(x, y));
    }

}

