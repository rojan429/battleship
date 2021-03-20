package com.mini.battleship;
import com.mini.battleship.Board.Cell;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.Random;

public class BattleshipMain extends Application {

    private boolean running = false;
    private Board enemyBoard, playerBoard;

    private int shipsToPlace = 5;

    private boolean enemyTurn = false;

    private Random random = new Random();

    Alert alert = new Alert(AlertType.NONE);

    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(800, 1000);

        //text for board label
        Text cmpText = new Text("                                           COMPUTER BOARD");
        cmpText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
        cmpText.setTextAlignment(TextAlignment.CENTER);
        root.setTop(cmpText);
        Text plText = new Text("                                              PLAYER BOARD");
        plText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
        plText.setTextAlignment(TextAlignment.CENTER);
        root.setBottom(plText);

        //send out an alert
        alert.setAlertType(AlertType.INFORMATION);
        alert.setTitle("Game launching...");
        alert.setHeaderText("Placing your battleships.");
        alert.setContentText("You need to fill your board first with 5 battleships, each varying in size, and cannot be placed beside other ships. Use left mouse button to place your ship vertically, right mouse button to place your ship horizontally.");
        alert.showAndWait();


        enemyBoard = new Board(true, event -> {
            if (!running)
                return;
            System.out.println("Player move");
            Cell cell = (Cell) event.getSource();

            if (cell.wasShot){
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText(null);
                alert.setContentText("You already fired in that location!");
                alert.showAndWait();
                return;}

            enemyTurn = !cell.shoot();

            //if enemy ships reach 0
            if (enemyBoard.ships == 0) {
                System.out.println("YOU WIN");
                //send out an alert
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle("Game finished!");
                alert.setHeaderText(null);
                alert.setContentText("You won the game!");
                alert.showAndWait();
                System.exit(0);
            }

            if (enemyTurn)
                enemyMove();
        });

        playerBoard = new Board(false, event -> {
            if (running)
                return;

            Cell cell = (Cell) event.getSource();

            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                    System.out.println("Starting the game!");
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setTitle("Game starting...");
                    alert.setHeaderText(null);
                    alert.setContentText("Starting the game! Click on enemy board to fire. Whoever hits their enemy will get an extra turn.");
                    alert.showAndWait();
                }
            }
        });

        VBox vbox = new VBox(50, enemyBoard, playerBoard);
        vbox.setAlignment(Pos.CENTER);

        root.setCenter(vbox);

        return root;
    }

    private void enemyMove() {

        while (enemyTurn) {

            int x = random.nextInt(10);
            int y = random.nextInt(10);

            Cell cell = playerBoard.getCell(x, y);
            if (cell.wasShot)
                continue;

            System.out.println("Enemy move");

            enemyTurn = cell.shoot();

            if (playerBoard.ships == 0) {
                System.out.println("YOU LOSE");

                //send out an alert
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle("Game finished!");
                alert.setHeaderText(null);
                alert.setContentText("You lost the game!");
                alert.showAndWait();
                System.exit(0);
            }
        }
    }

    private void startGame() {
        // place enemy ships
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        running = true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
