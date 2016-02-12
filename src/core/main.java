package core;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import core.entities.Player;
import core.entities.World;
import gui.GUI;

public class main {
  public static String player1Name;
  public static String player2Name;
  public static Player player1;
  public static Player player2;
  public static Player neut1;
  public static Player neut2;
  public static Player neut3;
  public static Player neut4;
  
  public static boolean didPress = false;
  public static World world;
  
  /*
   * main method, set up board and then create frame
   */

  public static void main(String[] args) throws IOException {
    
    GUI window=new GUI();
    //window.createGUI(); //create frame
    
    while(!didPress){
      window.setLog("Welcome! What is player 1's name?");
    }
    didPress = false;
    player1Name = window.getInput();
    window.resetInput();
    while(!didPress){
      window.setLog(player1Name + " will be blue. What is player two's name?");
    }
    didPress = false;
    String player2Name = window.getInput();
    window.setLog(player2Name + " will be red. Lets begin!"); 
    window.resetInput();
    
    // create players
    player1 = new Player(player1Name, Color.blue);
    player2 = new Player(player2Name, Color.red);
    neut1 = new Player("neutral 1", Color.BLACK);
    neut2 = new Player("neutral 2", Color.green);
    neut3 = new Player("neutral 3", Color.darkGray);
    neut4 = new Player("neutral 4", Color.ORANGE);
    
    world = WorldBuilder.Build();
    createGame();
    
    //world.invalidate();
    
    world.reset_invalidate();
    
    
    
  }
  
  public static void createGame(){
    int statesOwned = 0;
    for (int i = 0; i < world.getStates().size(); i++){
      //give each player 9, and each neutral 6
      if(i < statesOwned + Constants.INIT_COUNTRIES_PLAYER){
        statesOwned += Constants.INIT_COUNTRIES_PLAYER;
        world.getState(i).setOwner(player1);        
      }
      else if(i < statesOwned + Constants.INIT_COUNTRIES_PLAYER){
        statesOwned += Constants.INIT_COUNTRIES_PLAYER;
        world.getState(i).setOwner(player2);        
      }
      else if(i < statesOwned + Constants.INIT_COUNTRIES_NEUTRAL){
        statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
        world.getState(i).setOwner(neut1);        
      }
      else if(i < statesOwned + Constants.INIT_COUNTRIES_NEUTRAL){
        statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
        world.getState(i).setOwner(neut2);        
      }
      else if(i < statesOwned + Constants.INIT_COUNTRIES_NEUTRAL){
        statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
        world.getState(i).setOwner(neut3);        
      }
      else if(i < statesOwned + Constants.INIT_COUNTRIES_NEUTRAL){
        statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
        world.getState(i).setOwner(neut4);        
      }
      world.getState(i).setArmy(1);
    }
  }
}