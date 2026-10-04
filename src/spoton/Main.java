package spoton;

// TR: JavaFX uygulamasini baslatmak icin gerekli temel siniflar.
// EN: Basic classes needed to start the JavaFX application.
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// TR: Main sinifi oyunun baslangic noktasi olarak calisir.
// EN: The Main class works as the starting point of the game.
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // TR: BorderPane, ekrani ust kisim ve oyun alani olarak ayirmamizi saglar.
        // EN: BorderPane lets us split the screen into a top bar and a game area.
        BorderPane root = new BorderPane();

        // TR: GameController butun oyun kurallarini ve ekran elemanlarini yonetir.
        // EN: GameController manages all game rules and screen elements.
        GameController controller = new GameController(root);

        // TR: Scene, JavaFX penceresinin icindeki ana ekrandir.
        // EN: Scene is the main screen inside the JavaFX window.
        Scene scene = new Scene(root, 900, 650);

        // TR: Pencere basligi, minimum boyutu ve sahnesi ayarlanir.
        // EN: The window title, minimum size, and scene are set here.
        primaryStage.setTitle("SpotOn Game");
        primaryStage.setMinWidth(760);
        primaryStage.setMinHeight(560);
        primaryStage.setScene(scene);
        primaryStage.show();

        // TR: Pencere acildiktan sonra oyun baslatilir.
        // EN: The game starts after the window is shown.
        controller.startGame();
    }

    public static void main(String[] args) {
        // TR: JavaFX uygulamasini calistiran komuttur.
        // EN: This command launches the JavaFX application.
        launch(args);
    }
}
