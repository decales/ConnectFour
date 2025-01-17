package com.example.a3_1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {

        // int gridDimension = 16;
        //
        // double displayHeight = Screen.getPrimary().getBounds().getHeight();
        // double displayRatio = 0.8;
        // double rootPaddingRatio = 0.05;        
        // double gridSpacingRatio = 0.05;
        //
        // double rootPadding = (rootPaddingRatio * displayHeight * displayRatio) / 2;
        // double tileSpacing = (gridSpacingRatio * displayHeight * displayRatio) / gridDimension;
        // double gridHeight = (displayHeight * displayRatio) - rootPadding * 2;
        //
        // Model model = new Model(gridHeight);
        // Controller controller = new Controller(model);
        // 
        // // Initialize UI components
        HBox root = new HBox();
        // root.paddingProperty().set(new Insets(rootPadding));
        // root.spacingProperty().set(rootPadding);
        // root.setStyle("-fx-background-color: lightgrey");
        //
        // // Grid
        // GridView gridView = new GridView(controller, tileSpacing, gridDimension);
        // 
        // // Side menu
        // AnchorPane sideMenu = new AnchorPane();
        // TileMenu gridMenu = new TileMenu(controller);
        // ControlMenu controlMenu = new ControlMenu(controller);
        // sideMenu.setTopAnchor(gridMenu, 0.0);
        // sideMenu.setBottomAnchor(controlMenu, 0.0);
        // sideMenu.getChildren().addAll(gridMenu, controlMenu);
        //
        // root.getChildren().addAll(gridView, sideMenu);
        // model.addSubscribers(gridView, gridMenu, controlMenu);

        Scene scene = new Scene(root);
        stage.setTitle("(A*)nt Simulator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
