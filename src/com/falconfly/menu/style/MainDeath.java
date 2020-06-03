package com.falconfly.menu.style;

import com.falconfly.config.MainGlobals;
import com.falconfly.engine.Engine;
import com.falconfly.engine.main.Main;
import com.falconfly.game.GameSetup;
import com.falconfly.game.Gameplay;
import com.falconfly.menu.MenuStorageLoader;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import java.awt.*;

import java.util.List;
import java.util.Stack;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class MainDeath {

        private Stage windowDeath;
        private Scene sceneDeath;
        private Scene sceneSave;

        private Button buttonReplay;
        private Button buttonSave;
        private Button buttonExit;

        private Label labelScore;
        private Label labelMessage;
        private TextField textFieldName;

        private Button buttonOK;
        private Button buttonBack;

        private Engine engine;

        List<Pair<Number, Number>> pathDeath;
        List<Number> enemyPathDeath;

        public MainDeath(List<Pair<Number, Number>> path, List<Number> enemyPath, Engine engine) {
            this.enemyPathDeath = enemyPath;
            this.pathDeath = path;
            this.engine = engine;
        }

        public void display() {

            this.windowDeath = new Stage(); // always in MainMenu
            this.windowDeath.initModality(Modality.APPLICATION_MODAL);

            MenuStorageLoader loader = new MenuStorageLoader();

            BackgroundImage centerBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(3), MainGlobals.WIDTH, MainGlobals.HEIGHT,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            Label mainBackground = new Label();
            mainBackground.setMaxSize(MainGlobals.WIDTH, MainGlobals.HEIGHT);
            mainBackground.setBackground(new Background(centerBackground));

            BackgroundImage exitBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(0),MainGlobals.WIDTH * 0.04,MainGlobals.WIDTH * 0.04,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            this.buttonExit = new Button();
            this.buttonExit.setBackground(new Background(exitBackground));
            this.buttonExit.setOnAction(this::handleExit);
            buttonExit.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonExit.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonExit.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

            buttonExit.setOnMouseEntered(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonExit);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
            });

            buttonExit.setOnMouseExited(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonExit);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            BackgroundImage saveBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(2),MainGlobals.WIDTH * 0.04,MainGlobals.WIDTH * 0.04,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            this.buttonSave = new Button();
            this.buttonSave.setBackground(new Background(saveBackground));
            this.buttonSave.setOnAction(this::handleSave);
            buttonSave.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonSave.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonSave.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

            buttonSave.setOnMouseEntered(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonSave);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
            });

            buttonSave.setOnMouseExited(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonSave);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            BackgroundImage replayBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(1),MainGlobals.WIDTH * 0.04,MainGlobals.WIDTH * 0.04,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            this.buttonReplay = new Button();
            this.buttonReplay.setBackground(new Background(replayBackground));
            this.buttonReplay.setOnAction(this::handleReplay);
            buttonReplay.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonReplay.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonReplay.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

            buttonReplay.setOnMouseEntered(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonReplay);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
            });

            buttonReplay.setOnMouseExited(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonReplay);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            HBox horizontalBoxDeath = new HBox();
            horizontalBoxDeath.setSpacing(10);
            horizontalBoxDeath.setAlignment(Pos.CENTER);
            horizontalBoxDeath.getChildren().addAll(buttonSave, buttonReplay, buttonExit);

            this.labelScore = new Label("Score: 1024");
            this.labelMessage = new Label("What you like to do?");

            VBox verticalBoxDeath = new VBox();
            verticalBoxDeath.setSpacing(30);
            verticalBoxDeath.setAlignment(Pos.CENTER);
            verticalBoxDeath.getChildren().addAll(this.labelScore, this.labelMessage, horizontalBoxDeath);

            BorderPane layout = new BorderPane();
            layout.setCenter(verticalBoxDeath);
            BorderPane.setAlignment(verticalBoxDeath, Pos.BOTTOM_CENTER);

            StackPane root = new StackPane(mainBackground);
            root.getChildren().add(layout);

            this.sceneDeath = new Scene(root);
            this.windowDeath.setScene(this.sceneDeath);
            this.windowDeath.setWidth(MainGlobals.WIDTH);//
            this.windowDeath.setHeight(MainGlobals.HEIGHT);//
            //this.windowDeath.setFullScreen(true);
            //windowDeath.setFullScreenExitHint("");
            this.windowDeath.showAndWait();
        }

        public void handleSave(ActionEvent eventSave) {

            if(eventSave.getSource() == buttonSave) {
                MenuStorageLoader loader = new MenuStorageLoader();

                BackgroundImage centerBackground = new BackgroundImage(
                        new Image(loader.Load("images/MainDeath").get(3), MainGlobals.WIDTH, MainGlobals.HEIGHT,false,true),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT);

                Label mainBackground = new Label();
                mainBackground.setMaxSize(MainGlobals.WIDTH, MainGlobals.HEIGHT);
                mainBackground.setBackground(new Background(centerBackground));

                this.labelScore = new Label("Score: 1024");
                this.textFieldName = new TextField();
                this.textFieldName.setPrefSize(250, 25);
                this.textFieldName.setMaxSize(250, 25);
                this.textFieldName.setMinSize(250, 25);
                this.textFieldName.setPromptText("Enter your name");
                this.labelMessage.setVisible(false);

                BackgroundImage buttonYesBackgroundRedImg = new BackgroundImage(
                        new Image(loader.Load("images/MainDeath").get(5), MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04,false,true),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT);

                this.buttonOK = new Button();
                this.buttonOK.setOnAction(this::handleOK);
                this.buttonOK.setBackground(new Background(buttonYesBackgroundRedImg));
                buttonOK.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
                buttonOK.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
                buttonOK.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

                buttonOK.setOnMouseEntered(event -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonOK);
                    st.setFromX(1);
                    st.setFromY(1);
                    st.setToX(1.2);
                    st.setToY(1.2);
                    st.play();
                });

                buttonOK.setOnMouseExited(event -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonOK);
                    st.setToX(1);
                    st.setToY(1);
                    st.play();
                });

                BackgroundImage buttonNoBackgroundWhiteImg = new BackgroundImage(
                        new Image(loader.Load("images/MainDeath").get(4), MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04,false,true),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT);

                this.buttonBack = new Button();
                this.buttonBack.setOnAction(this::handleBack);
                this.buttonBack.setBackground(new Background(buttonNoBackgroundWhiteImg));
                buttonBack.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
                buttonBack.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
                buttonBack.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

                buttonBack.setOnMouseEntered(event -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonBack);
                    st.setFromX(1);
                    st.setFromY(1);
                    st.setToX(1.2);
                    st.setToY(1.2);
                    st.play();
                });

                buttonBack.setOnMouseExited(event -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonBack);
                    st.setToX(1);
                    st.setToY(1);
                    st.play();
                });

                HBox horizontalBoxDeathSave = new HBox();
                horizontalBoxDeathSave.setSpacing(10);
                horizontalBoxDeathSave.setAlignment(Pos.CENTER);
                horizontalBoxDeathSave.getChildren().addAll(buttonOK, buttonBack);

                VBox verticalBoxDeathSave = new VBox();
                verticalBoxDeathSave.setSpacing(30);
                verticalBoxDeathSave.setAlignment(Pos.CENTER);
                verticalBoxDeathSave.getChildren().addAll(this.labelScore, this.textFieldName, horizontalBoxDeathSave);

                BorderPane layout = new BorderPane();
                layout.setCenter(verticalBoxDeathSave);
                BorderPane.setAlignment(verticalBoxDeathSave, Pos.CENTER);

                StackPane root = new StackPane(mainBackground);
                root.getChildren().add(layout);

                this.sceneSave = new Scene(root);
                this.windowDeath.setScene(this.sceneSave);
            }
        }

        public void handleReplay(ActionEvent eventReply){

            if(eventReply.getSource() == buttonReplay) {
                GameSetup.setIsReplay(true);
                this.windowDeath.close();
            }
        }

        public void handleExit(ActionEvent eventExit) {

            if(eventExit.getSource() == buttonExit) {

                this.windowDeath.close();
            }
        }

    public void handleOK(ActionEvent eventReply) {

        if(eventReply.getSource() == buttonOK) {

            //do something
            this.windowDeath.close();
        }
    }

    public void handleBack(ActionEvent eventReply) {

        if(eventReply.getSource() == buttonBack) {
            MenuStorageLoader loader = new MenuStorageLoader();

            BackgroundImage centerBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(3), MainGlobals.WIDTH, MainGlobals.HEIGHT,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            Label mainBackground = new Label();
            mainBackground.setMaxSize(MainGlobals.WIDTH, MainGlobals.HEIGHT);
            mainBackground.setBackground(new Background(centerBackground));

            BackgroundImage exitBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(0),MainGlobals.WIDTH * 0.04,MainGlobals.WIDTH * 0.04,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            this.buttonExit = new Button();
            this.buttonExit.setBackground(new Background(exitBackground));
            this.buttonExit.setOnAction(this::handleExit);
            buttonExit.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonExit.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonExit.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

            buttonExit.setOnMouseEntered(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonExit);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
            });

            buttonExit.setOnMouseExited(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonExit);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            BackgroundImage saveBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(2),MainGlobals.WIDTH * 0.04,MainGlobals.WIDTH * 0.04,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            this.buttonSave = new Button();
            this.buttonSave.setBackground(new Background(saveBackground));
            this.buttonSave.setOnAction(this::handleSave);
            buttonSave.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonSave.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonSave.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

            buttonSave.setOnMouseEntered(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonSave);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
            });

            buttonSave.setOnMouseExited(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonSave);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            BackgroundImage replayBackground = new BackgroundImage(
                    new Image(loader.Load("images/MainDeath").get(1),MainGlobals.WIDTH * 0.04,MainGlobals.WIDTH * 0.04,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);

            this.buttonReplay = new Button();
            this.buttonReplay.setBackground(new Background(replayBackground));
            this.buttonReplay.setOnAction(this::handleReplay);
            buttonReplay.setPrefSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonReplay.setMaxSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);
            buttonReplay.setMinSize(MainGlobals.WIDTH * 0.04, MainGlobals.WIDTH * 0.04);

            buttonReplay.setOnMouseEntered(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonReplay);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
            });

            buttonReplay.setOnMouseExited(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), buttonReplay);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            HBox horizontalBoxDeath = new HBox();
            horizontalBoxDeath.setSpacing(10);
            horizontalBoxDeath.setAlignment(Pos.CENTER);
            horizontalBoxDeath.getChildren().addAll(buttonSave, buttonReplay, buttonExit);

            this.labelScore = new Label("Score: 1024");
            this.labelMessage = new Label("What you like to do?");

            VBox verticalBoxDeath = new VBox();
            verticalBoxDeath.setSpacing(30);
            verticalBoxDeath.setAlignment(Pos.CENTER);
            verticalBoxDeath.getChildren().addAll(this.labelScore, this.labelMessage, horizontalBoxDeath);

            BorderPane layout = new BorderPane();
            layout.setCenter(verticalBoxDeath);
            BorderPane.setAlignment(verticalBoxDeath, Pos.CENTER);

            StackPane root = new StackPane(mainBackground);
            root.getChildren().add(layout);

            this.sceneDeath = new Scene(root);
            this.windowDeath.setScene(this.sceneDeath);
            this.windowDeath.setWidth(MainGlobals.WIDTH);//
            this.windowDeath.setHeight(MainGlobals.HEIGHT);//
            //this.windowDeath.setFullScreen(true);
            //windowDeath.setFullScreenExitHint("");
            this.windowDeath.showAndWait();
        }
    }
}

