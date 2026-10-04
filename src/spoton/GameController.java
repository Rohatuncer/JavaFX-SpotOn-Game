package spoton;

// TR: Bu importlar dosya okuma, liste tutma ve rastgele sayi uretme icin kullanilir.
// EN: These imports are used for file reading, storing lists, and generating random numbers.
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// TR: Bu importlar JavaFX animasyonlari ve zamanlayici icin kullanilir.
// EN: These imports are used for JavaFX animations and the timer.
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// TR: Bu importlar ekrandaki konum, bosluk ve hizalama ayarlari icindir.
// EN: These imports are for position, spacing, and alignment settings on the screen.
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;

// TR: Bu importlar buton, yazi, resim ve oyun alani gibi arayuz parcalari icindir.
// EN: These imports are for interface parts such as buttons, labels, images, and the game area.
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

// TR: GameController oyunun ana kontrol sinifidir.
// EN: GameController is the main controller class of the game.
public class GameController {

    // TR: Oyunun baslangic cani.
    // EN: Starting lives of the game.
    private static final int INITIAL_LIVES = 3;

    // TR: Oyuncunun sahip olabilecegi en fazla can.
    // EN: Maximum number of lives the player can have.
    private static final int MAX_LIVES = 7;

    // TR: Her 10 basarili tiklamada seviye artar.
    // EN: The level increases after every 10 successful clicks.
    private static final int SPOTS_PER_LEVEL = 10;

    // TR: Spotun ekrandaki temel yaricapi.
    // EN: The basic radius of the spot on the screen.
    private static final double SPOT_RADIUS = 34.0;

    // TR: Yeni spotlar 0.5 saniyede bir uretilir.
    // EN: New spots are created every 0.5 seconds.
    private static final double GENERATION_INTERVAL_SECONDS = 0.5;

    // TR: Ilk seviyede spotun animasyon suresi.
    // EN: The animation duration of a spot at the first level.
    private static final double BASE_ANIMATION_SECONDS = 3.4;

    // TR: Oyun cok hizlansa bile animasyon bu sureden daha kisa olmaz.
    // EN: Even if the game gets faster, the animation will not be shorter than this.
    private static final double MIN_ANIMATION_SECONDS = 1.1;

    // TR: Ana ekran duzenini tutar.
    // EN: Holds the main screen layout.
    private final BorderPane root;

    // TR: Spotlarin gorundugu ve tiklandigi oyun alani.
    // EN: The game area where spots appear and are clicked.
    private final Pane playPane;

    // TR: Skor, seviye ve mesaj yazilarini gosteren etiketler.
    // EN: Labels that show score, level, and messages.
    private final Label scoreLabel;
    private final Label levelLabel;
    private final Label messageLabel;

    // TR: Can resimlerini yan yana gosteren kutu.
    // EN: Box that displays life images side by side.
    private final HBox livesBox;

    // TR: Yeni spot uretmek icin calisan zamanlayici.
    // EN: Timer that creates new spots.
    private final Timeline spotGenerator;

    // TR: Oyun seslerini yoneten yardimci sinif.
    // EN: Helper class that manages game sounds.
    private final SoundManager soundManager;

    // TR: Rastgele konum ve rastgele spot resmi secmek icin kullanilir.
    // EN: Used to choose random positions and random spot images.
    private final Random random;

    // TR: Ekranda halen aktif olan spotlari tutar.
    // EN: Stores the spots that are still active on the screen.
    private final List<Spot> activeSpots;

    // TR: Spot ve can gorselleri bu degiskenlerde tutulur.
    // EN: Spot and life images are stored in these variables.
    private final Image redSpotImage;
    private final Image greenSpotImage;
    private final Image lifeImage;

    // TR: Oyunun degisen durum bilgileri burada tutulur.
    // EN: The changing game state values are stored here.
    private int score;
    private int level;
    private int lives;
    private int clickedSpotsInLevel;
    private boolean gameOver;

    public GameController(BorderPane root) {
        // TR: Main sinifindan gelen ana ekran kaydedilir.
        // EN: The main screen coming from the Main class is saved.
        this.root = root;

        // TR: Arayuz elemanlari ve yardimci nesneler olusturulur.
        // EN: Interface elements and helper objects are created.
        playPane = new Pane();
        scoreLabel = createHudLabel();
        levelLabel = createHudLabel();
        messageLabel = createHudLabel();
        livesBox = new HBox(5);
        soundManager = new SoundManager();
        random = new Random();
        activeSpots = new ArrayList<Spot>();

        // TR: Resimler proje klasorundeki ExerciseResources icinden yuklenir.
        // EN: Images are loaded from ExerciseResources inside the project folder.
        redSpotImage = loadImage("ExerciseResources/images/red_spot.png");
        greenSpotImage = loadImage("ExerciseResources/images/green_spot.png");
        lifeImage = loadImage("ExerciseResources/images/life.png");

        // TR: Timeline her 0.5 saniyede createSpot metodunu calistirir.
        // EN: Timeline runs the createSpot method every 0.5 seconds.
        spotGenerator = new Timeline(new KeyFrame(Duration.seconds(GENERATION_INTERVAL_SECONDS),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        createSpot();
                    }
                }));
        spotGenerator.setCycleCount(Animation.INDEFINITE);

        // TR: Ekran kurulur ve ilk oyun degerleri hazirlanir.
        // EN: The screen is built and the first game values are prepared.
        buildLayout();
        resetState();
        updateHud();
    }

    public void startGame() {
        // TR: Yeni oyun baslarken butun degerler sifirlanir.
        // EN: All values are reset when a new game starts.
        resetState();
        updateHud();
        gameOver = false;

        // TR: Spot uretici zamanlayici bastan baslatilir.
        // EN: The spot generator timer is started from the beginning.
        spotGenerator.playFromStart();
    }

    public void restartGame() {
        // TR: Yeniden baslamadan once zamanlayici ve eski spotlar temizlenir.
        // EN: Before restarting, the timer and old spots are cleared.
        spotGenerator.stop();
        removeAllSpots();
        startGame();
    }

    private void buildLayout() {
        // TR: Ust kisimda skor, seviye, can ve yeniden oyna butonu bulunur.
        // EN: The top area contains score, level, lives, and the restart button.
        HBox hud = new HBox(28);
        hud.setPadding(new Insets(12, 18, 12, 18));
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setStyle("-fx-background-color: #1f2933;");

        // TR: Oyunun basligi icin etiket olusturulur.
        // EN: A label is created for the game title.
        Label titleLabel = createHudLabel();
        titleLabel.setText("SpotOn");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // TR: Can resimlerinin yaninda gorunen Lives yazisi.
        // EN: The Lives text shown next to the life images.
        Label livesLabel = createHudLabel();
        livesLabel.setText("Lives:");

        // TR: Bu buton oyunu istenilen anda yeniden baslatir.
        // EN: This button restarts the game at any time.
        Button restartButton = new Button("Yeniden Oyna");
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                restartGame();
            }
        });

        messageLabel.setText("");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        // TR: Ust bardaki butun elemanlar sirayla eklenir.
        // EN: All elements in the top bar are added in order.
        hud.getChildren().addAll(titleLabel, scoreLabel, levelLabel, livesLabel, livesBox, restartButton, messageLabel);

        // TR: Oyun alaninin boyutu ve arka plan rengi ayarlanir.
        // EN: The game area's size and background color are set.
        playPane.setPrefSize(900, 580);
        playPane.setMinSize(400, 320);
        playPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f6f8fb, #dfe7ef);");

        // TR: Oyuncu bos alana tiklarsa miss olarak sayilir.
        // EN: If the player clicks an empty area, it counts as a miss.
        playPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!gameOver && event.getTarget() == playPane) {
                    registerMiss();
                }
            }
        });

        // TR: Ust bar ve oyun alani ana ekrana yerlestirilir.
        // EN: The top bar and game area are placed on the main screen.
        root.setTop(hud);
        root.setCenter(playPane);
    }

    private Label createHudLabel() {
        // TR: Ust bardaki yazilar icin ortak gorunum olusturulur.
        // EN: A common style is created for the labels in the top bar.
        Label label = new Label();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setStyle("-fx-text-fill: white;");
        return label;
    }

    private void resetState() {
        // TR: Oyunun baslangic degerleri tekrar atanir.
        // EN: The starting game values are assigned again.
        score = 0;
        level = 1;
        lives = INITIAL_LIVES;
        clickedSpotsInLevel = 0;
        gameOver = false;
        messageLabel.setText("");
    }

    private void createSpot() {
        // TR: Oyun bittiyse yeni spot uretilmez.
        // EN: If the game is over, no new spot is created.
        if (gameOver) {
            return;
        }

        // TR: Oyun alaninin guncel genislik ve yukseklik degerleri alinir.
        // EN: The current width and height of the game area are read.
        double width = Math.max(playPane.getWidth(), playPane.getPrefWidth());
        double height = Math.max(playPane.getHeight(), playPane.getPrefHeight());

        // TR: Alan cok kucukse spot uretmek guvenli degildir.
        // EN: If the area is too small, creating a spot is not safe.
        if (width <= SPOT_RADIUS * 2 || height <= SPOT_RADIUS * 2) {
            return;
        }

        // TR: Yeni spot rastgele kirmizi veya yesil resimle olusturulur.
        // EN: A new spot is created with a random red or green image.
        final Spot spot = new Spot(SPOT_RADIUS, random.nextBoolean() ? redSpotImage : greenSpotImage);

        // TR: Spot icin rastgele baslangic ve bitis noktalari secilir.
        // EN: Random start and end points are selected for the spot.
        final Point2D start = randomPoint(width, height);
        final Point2D end = randomPoint(width, height);

        // TR: Spot tiklaninca puan kazanilir ve tiklama bos alana gitmez.
        // EN: When the spot is clicked, points are earned and the click does not go to the empty area.
        spot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
                handleSpotClicked(spot);
            }
        });

        // TR: Spot aktif listeye ve oyun ekranina eklenir.
        // EN: The spot is added to the active list and to the game screen.
        activeSpots.add(spot);
        playPane.getChildren().add(spot);

        // TR: Spot hareket etmeye ve kuculmeye baslar.
        // EN: The spot starts moving and shrinking.
        spot.play(start, end, Duration.seconds(getAnimationSeconds()), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleSpotDisappeared(spot);
            }
        });
    }

    private Point2D randomPoint(double width, double height) {
        // TR: Spotun tamamen ekranda kalmasi icin kenarlardan yaricap kadar uzak nokta secilir.
        // EN: A point away from the edges by the radius is chosen so the spot stays fully visible.
        double x = SPOT_RADIUS + random.nextDouble() * (width - SPOT_RADIUS * 2);
        double y = SPOT_RADIUS + random.nextDouble() * (height - SPOT_RADIUS * 2);
        return new Point2D(x, y);
    }

    private double getAnimationSeconds() {
        // TR: Level arttikca spot daha hizli hareket eder.
        // EN: As the level increases, the spot moves faster.
        double seconds = BASE_ANIMATION_SECONDS - (level - 1) * 0.28;
        return Math.max(MIN_ANIMATION_SECONDS, seconds);
    }

    private void handleSpotClicked(Spot spot) {
        // TR: Oyun bittiyse veya spot zaten silindiyse islem yapilmaz.
        // EN: If the game is over or the spot was already removed, nothing happens.
        if (gameOver || !activeSpots.remove(spot)) {
            return;
        }

        // TR: Tiklanan spotun animasyonu durdurulur ve ekrandan kaldirilir.
        // EN: The clicked spot's animation is stopped and it is removed from the screen.
        spot.stopAnimation();
        playPane.getChildren().remove(spot);

        // TR: Dogru tiklama skor kazandirir.
        // EN: A correct click increases the score.
        score += 10 * level;
        clickedSpotsInLevel++;
        soundManager.playHit();
        messageLabel.setText("+ " + (10 * level) + " points");

        // TR: 10 dogru tiklama olunca level artar.
        // EN: After 10 correct clicks, the level increases.
        if (clickedSpotsInLevel >= SPOTS_PER_LEVEL) {
            advanceLevel();
        }

        updateHud();
    }

    private void registerMiss() {
        // TR: Bos alana tiklamak skor kaybettirir.
        // EN: Clicking an empty area decreases the score.
        score -= 15 * level;
        soundManager.playMiss();
        messageLabel.setText("- " + (15 * level) + " points");
        updateHud();
    }

    private void handleSpotDisappeared(Spot spot) {
        // TR: Spot daha once tiklandiysa veya oyun bittiyse islem yapilmaz.
        // EN: If the spot was already clicked or the game is over, nothing happens.
        if (gameOver || !activeSpots.remove(spot)) {
            return;
        }

        // TR: Spot tiklanmadan kaybolduysa can azalir.
        // EN: If the spot disappears without being clicked, one life is lost.
        playPane.getChildren().remove(spot);
        lives = Math.max(0, lives - 1);
        soundManager.playDisappear();
        messageLabel.setText("A spot disappeared!");
        updateHud();

        if (lives == 0) {
            endGame();
        }
    }

    private void advanceLevel() {
        // TR: Level bir artar ve level icindeki tiklama sayisi sifirlanir.
        // EN: The level increases by one and the click count for the level resets.
        level++;
        clickedSpotsInLevel = 0;

        // TR: Level atlayinca can en fazla 7 olacak sekilde artar.
        // EN: When leveling up, lives increase up to a maximum of 7.
        if (lives < MAX_LIVES) {
            lives++;
        }

        // TR: Level atlama mesaji ve sesi verilir.
        // EN: A level-up message and sound are given.
        soundManager.playHit();
        messageLabel.setText("Level " + level + "!");
    }

    private void updateHud() {
        // TR: Skor ve level yazilari guncellenir.
        // EN: Score and level labels are updated.
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);

        // TR: Can kutusu temizlenip mevcut can sayisi kadar resim eklenir.
        // EN: The lives box is cleared and images are added for the current number of lives.
        livesBox.getChildren().clear();
        for (int i = 0; i < lives; i++) {
            livesBox.getChildren().add(createLifeNode());
        }
    }

    private Node createLifeNode() {
        // TR: Can resmi yuklenemezse basit bir O harfi gosterilir.
        // EN: If the life image cannot be loaded, a simple O letter is shown.
        if (lifeImage == null || lifeImage.isError()) {
            Label fallback = new Label("O");
            fallback.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            fallback.setStyle("-fx-text-fill: #ff6b6b;");
            return fallback;
        }

        // TR: Can resmi kucuk bir ImageView icinde gosterilir.
        // EN: The life image is shown inside a small ImageView.
        ImageView imageView = new ImageView(lifeImage);
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void endGame() {
        // TR: Oyun bitti olarak isaretlenir ve spot uretimi durdurulur.
        // EN: The game is marked as over and spot generation is stopped.
        gameOver = true;
        spotGenerator.stop();
        removeAllSpots();
        messageLabel.setText("Game Over");

        // TR: Game Over penceresindeki iki buton olusturulur.
        // EN: Two buttons are created for the Game Over dialog.
        ButtonType playAgainButton = new ButtonType("Yeniden Oyna");
        ButtonType closeButton = new ButtonType("Kapat", ButtonBar.ButtonData.CANCEL_CLOSE);

        // TR: Oyuncuya final skor ve level bilgisi gosterilir.
        // EN: The final score and level are shown to the player.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over");
        alert.setContentText("Final score: " + score + "\nReached level: " + level);
        alert.getButtonTypes().setAll(playAgainButton, closeButton);

        Optional<ButtonType> answer = alert.showAndWait();

        // TR: Oyuncu Yeniden Oyna derse oyun bastan baslar.
        // EN: If the player chooses Play Again, the game starts again.
        if (answer.isPresent() && answer.get() == playAgainButton) {
            restartGame();
        }
    }

    private void removeAllSpots() {
        // TR: Aktif spotlarin kopyasi alinir; boylece listeyi silerken hata olmaz.
        // EN: A copy of active spots is created to avoid errors while removing them.
        List<Spot> spotsToRemove = new ArrayList<Spot>(activeSpots);
        for (Spot spot : spotsToRemove) {
            // TR: Her spotun animasyonu durdurulur ve ekrandan kaldirilir.
            // EN: Each spot's animation is stopped and it is removed from the screen.
            spot.stopAnimation();
            playPane.getChildren().remove(spot);
        }

        // TR: Aktif spot listesi tamamen temizlenir.
        // EN: The active spot list is fully cleared.
        activeSpots.clear();
    }

    private Image loadImage(String relativePath) {
        // TR: Once resim dosyasinin var olup olmadigi kontrol edilir.
        // EN: First, the code checks whether the image file exists.
        File file = new File(relativePath);
        if (!file.exists()) {
            return null;
        }

        try {
            // TR: Dosya yolu Image nesnesine cevrilir.
            // EN: The file path is converted into an Image object.
            return new Image(file.toURI().toString());
        } catch (RuntimeException exception) {
            // TR: Resim yuklenemezse null doner ve oyun yedek gorunum kullanir.
            // EN: If the image cannot be loaded, null is returned and the game uses a fallback view.
            return null;
        }
    }
}
