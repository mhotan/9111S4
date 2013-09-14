package com.hotan.ninetripleone.supply;

import com.hotan.ninetripleone.supply.util.ImageLoader;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class that begins the application.
 * <br>Sets the stage to present the application
 * <br>
 * 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        // Set the icon for the Application
        primaryStage.getIcons().add(ImageLoader.loadImage("MegaForceIcon.jpg"));
        
        // Set the attributes of the stage
        // Screen dimensions and such.
        // Set up the 9111 Megaforce application icon.  Not a priority
        
        // TODO pass off to supporting class to set the stage.
        
    }

    /**
     * Used to start app from eclipse development enviroment
     * @param args not used.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
