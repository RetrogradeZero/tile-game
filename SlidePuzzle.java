import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.lang.Math;
import java.util.Random;

public class SlidePuzzle extends Application{
   public static Arc[][][] tileCrop = new Arc[6][6][4];
   public static String[][] tileImg = new String[6][6];
   public static Rectangle[][] tile = new Rectangle[6][6];
   public static int[][] grid = new int[4][4];
   public static Rectangle border = new Rectangle(10, 10, 610, 610);
   
   public static int activeNodeM;
   public static int activeNodeN;
   
   public void start(Stage primaryStage){
      //set up border
      border.setStroke(Color.BLACK); //outline color
      border.setFill(Color.LIGHTGRAY);   //fill color
      border.setArcWidth(20);
      border.setArcHeight(20);
      
      //sets up tiles
      for(int i = 0; i < tileCrop[0].length; i++){
         for(int j = 0; j < tileCrop.length; j++){
            //tile rim : top left - top right - bottom left - bottom right
            tileCrop[i][j][0] = new Arc(35 + 100 * i, 35 + 100 * j, 26, 26, 129.375, 11.25);
            tileCrop[i][j][1] = new Arc(95 + 100 * i, 35 + 100 * j, 26, 26, 39.375, 11.25);
            tileCrop[i][j][2] = new Arc(35 + 100 * i, 95 + 100 * j, 26, 26, 219.375, 11.25);
            tileCrop[i][j][3] = new Arc(95 + 100 * i, 95 + 100 * j, 26, 26, 309.375, 11.25);
            
            for(int k = 0; k < tileCrop[0][0].length; k++){
               tileCrop[i][j][k].setFill(Color.TRANSPARENT);
               tileCrop[i][j][k].setStroke(Color.LIGHTGRAY);
               tileCrop[i][j][k].setStrokeWidth(4);
            }
            
            //tile outline
            tile[i][j] = new Rectangle(15 + 100 * i, 15 + 100 * j, 100, 100);
            tile[i][j].setStroke(Color.BLACK);   //outline color
            tile[i][j].setFill(Color.TRANSPARENT);   //fill color
            tile[i][j].setArcWidth(20);
            tile[i][j].setArcHeight(20);
            
            //tile image - String (init.)
            tileImg[i][j] = "tile" + (6*j+i+1) + ".png";
         }
      }
      
      //Create a pane to hold all the tiles
      Pane pane = new Pane();
      pane.getChildren().add(border);
      for(int i = 0; i < tileImg[0].length; i++)
         for(int j = 0; j < tileImg.length; j++){
            ImageView temp = new ImageView(new Image(tileImg[i][j]));
            temp.relocate(15 + 100 * i, 15 + 100 * j);
            pane.getChildren().add(temp);
         }
      for(int i = 0; i < tileCrop[0].length; i++)
         for(int j = 0; j < tileCrop.length; j++)
            for(int k = 0; k < tileCrop[0][0].length; k++)
               pane.getChildren().add(tileCrop[i][j][k]);
      for(int i = 0; i < tile[0].length; i++)
         for(int j = 0; j < tile.length; j++)
            pane.getChildren().add(tile[i][j]);
      
      
      Scene scene = new Scene(pane, 620, 620);
      primaryStage.setTitle("Slide Puzzle");
      primaryStage.setResizable(false);
      primaryStage.setScene(scene);
      primaryStage.show();
      
      //recursive call
      updateGUI(primaryStage);
   }
   
   public void updateGUI(Stage primaryStage){
      
      //Create a pane to hold all the tiles
      Pane pane = new Pane();
      pane.getChildren().add(border);
      for(int i = 0; i < tileImg[0].length; i++)
         for(int j = 0; j < tileImg.length; j++){
            ImageView temp = new ImageView(new Image(tileImg[i][j]));
            temp.relocate(15 + 100 * i, 15 + 100 * j);
            pane.getChildren().add(temp);
         }
      for(int i = 0; i < tileCrop[0].length; i++)
         for(int j = 0; j < tileCrop.length; j++)
            for(int k = 0; k < tileCrop[0][0].length; k++)
               pane.getChildren().add(tileCrop[i][j][k]);
      for(int i = 0; i < tile[0].length; i++)
         for(int j = 0; j < tile.length; j++)
            pane.getChildren().add(tile[i][j]);
      
      Scene scene = new Scene(pane, 620, 620);
      primaryStage.setScene(scene);
      primaryStage.show();
      
      //event handling
      for(int i = 0; i < tile[0].length; i++){
         for(int j = 0; j < tile.length; j++){
            final int m = i;
            final int n = j;            
            tile[i][j].setOnMouseDragged(new EventHandler<MouseEvent>(){
               @Override public void handle(MouseEvent event){
                  
                  if(m == activeNodeM && n > activeNodeN){
                     downSwipe(m, n);
                     nodeUpdate(m, n);
                  }else if(m == activeNodeM && n < activeNodeN){
                     upSwipe(m, n);
                     nodeUpdate(m, n);
                  }else if(m > activeNodeM && n == activeNodeN){
                     rightSwipe(m, n);
                     nodeUpdate(m, n);
                  }else if(m < activeNodeM && n == activeNodeN){
                     leftSwipe(m, n);
                     nodeUpdate(m, n);
                  }
                  
                  updateGUI(primaryStage);
                  event.consume();
               }
            });
            
            tile[i][j].setOnMousePressed(new EventHandler<MouseEvent>(){
               @Override public void handle(MouseEvent event1){
                  nodeUpdate(m, n);
                  updateGUI(primaryStage);
                  event1.consume();
               }
            });
            
         }
      }// end nested for loop
      
   }
   
   public static void downSwipe(int m, int n){
      String temp = tileImg[m][5];
      for(int j = tileImg[0].length-2; j >= 0; j--)
         tileImg[m][j+1] = tileImg[m][j];
      tileImg[m][0] = temp;
   }
   
   public static void upSwipe(int m, int n){
      String temp = tileImg[m][0];
      for(int j = 0; j < tileImg[0].length-1; j++)
         tileImg[m][j] = tileImg[m][j+1];
      tileImg[m][5] = temp;
   }
   
   public static void rightSwipe(int m, int n){
      String temp = tileImg[5][n];
      for(int i = tileImg[0].length-2; i >= 0; i--)
         tileImg[i+1][n] = tileImg[i][n];
      tileImg[0][n] = temp;
   }
   
   public static void leftSwipe(int m, int n){
      String temp = tileImg[0][n];
      for(int i = 0; i < tileImg[0].length-1; i++)
         tileImg[i][n] = tileImg[i+1][n];
      tileImg[5][n] = temp;
   }
   
   public static void nodeUpdate(int m, int n){
      activeNodeM = m;
      activeNodeN = n;
   }
   
   public static void main(String[] args){
      launch(args);
   }
}
