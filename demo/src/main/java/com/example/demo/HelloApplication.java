package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, 1280, 720, Color.BLACK);

        Rectangle redBlock = new Rectangle(50, 50, Color.RED);
        redBlock.setX(300);
        redBlock.setY(200);

        Keyb keyboard = new Keyb();


        Rectangle[] blocks = {
                new Rectangle(0, 300, 500, 50),
                new Rectangle(400, 200, 100, 100),
                new Rectangle(150, 100, 100, 50),
        };

        for (int i = 0; i < blocks.length; i++){
            blocks[i].setFill(Color.WHITE);
            root.getChildren().add(blocks[i]);
        }
        
        Rectangle temp;
        for(int i=0; i < blocks.length; i++){
            for(int j=1; j < (blocks.length-i); j++){ //Sort the elements depending on their Y position
                if(blocks[j-1].getY() > blocks[j].getY()){
                    temp = blocks[j-1];
                    blocks[j-1] = blocks[j];
                    blocks[j] = temp;
                }
            }
        }

        AnimationTimer frames = new AnimationTimer() {

            double hsp = 0;
            double vsp = 0;
            double grv = 0.4;
            double walkspeed = 4.0;
            boolean onGround = false;

            @Override
            public void handle(long l) {

                vsp += grv;
                hsp = walkspeed * ((keyboard.goEast ? 1 : 0) - (keyboard.goWest? 1 : 0));

                if (onGround && keyboard.goNorth){
                    vsp = -10;
                    onGround = false;
                }

                for (int i = 0; i < blocks.length; i++){
                    collision(blocks[i]);
                }

                redBlock.setX(redBlock.getX() + hsp);
                redBlock.setY(redBlock.getY() + vsp);

            }

            public void collision(Rectangle box){

                //Check horizontally
                if ((redBlock.getY() + redBlock.getHeight()) > box.getY() && redBlock.getY() < (box.getY() + box.getHeight())){
                    if ((redBlock.getX() + redBlock.getWidth()) + hsp > box.getX() && redBlock.getX() + hsp < (box.getX() + box.getWidth())){
                        hsp = 0;
                    }

                    /*If youre hroizontally aligned with a block, that means youre not on it,
                      therefore onGround should be false, (This needs the elements in the array to be sorted vertically
                      from highest to lowest, and its done before the vertical collision to make onGround true again
                      if the conditinos are met. */
                    onGround = false;
                }

                //Check vertically
                if ((redBlock.getX() + redBlock.getWidth()) > box.getX() && redBlock.getX() < (box.getX() + box.getWidth())){
                    if ((redBlock.getY() + redBlock.getHeight()) + vsp > box.getY() && redBlock.getY() + vsp < (box.getY() + box.getHeight())){

                        //Save vsp
                        double temp = 0;
                        if (vsp != 0){
                            temp = vsp;
                        }

                        //Stop the player
                        vsp = 0;

                        //If youre on a block, make onGround true
                        if (redBlock.getY() + redBlock.getWidth() < box.getY() + 10){
                            onGround = true;
                        }

                        //Corner collision
                        while
                        ( //Conditino
                                   redBlock.getY() + redBlock.getHeight() > box.getY()
                                && redBlock.getY()                        < box.getY()
                                || redBlock.getY()                        < box.getY() + box.getHeight()
                                && redBlock.getY() + redBlock.getHeight() > box.getY() + box.getHeight()
                        )
                        { //Instruction
                            redBlock.setY(redBlock.getY() - Math.signum(temp) * 2);
                        }
                    }
                }
            }
        };

        root.getChildren().add(redBlock);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    keyboard.goNorth = true; break;
                    case LEFT:  keyboard.goWest  = true; break;
                    case RIGHT: keyboard.goEast  = true; break;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    keyboard.goNorth = false; break;
                    case LEFT:  keyboard.goWest  = false; break;
                    case RIGHT: keyboard.goEast  = false; break;
                }
            }
        });

        stage.setTitle("funny red");
        stage.setScene(scene);
        stage.show();

        frames.start();
    }

    public static void main(String[] args) {
        launch();
    }
}