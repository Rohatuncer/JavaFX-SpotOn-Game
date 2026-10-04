package spoton;

// TR: Bu importlar spotun hareket ve kuculme animasyonlari icin kullanilir.
// EN: These imports are used for the spot movement and shrinking animations.
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

// TR: Spot sinifi Circle sinifindan kalitim alir ve oyundaki tiklanabilir noktayi temsil eder.
// EN: The Spot class extends Circle and represents the clickable spot in the game.
public class Spot extends Circle {

    // TR: Spotun ayni anda hareket edip kuculmesini saglayan animasyon.
    // EN: Animation that lets the spot move and shrink at the same time.
    private ParallelTransition animation;

    public Spot(double radius, Image image) {
        // TR: Circle sinifinin constructor'i cagrilir ve spot yaricapi ayarlanir.
        // EN: The Circle constructor is called and the spot radius is set.
        super(radius);

        // TR: Merkez degerleri sifir verilir; konumu PathTransition belirler.
        // EN: Center values are set to zero; PathTransition controls the position.
        setCenterX(0);
        setCenterY(0);

        // TR: Fare spotun ustune gelince el isareti gosterilir.
        // EN: A hand cursor is shown when the mouse is over the spot.
        setCursor(Cursor.HAND);

        // TR: Spotun etrafina beyaz kenarlik eklenir.
        // EN: A white border is added around the spot.
        setStroke(Color.WHITE);
        setStrokeWidth(2);

        // TR: Resim yuklenemezse basit kirmizi renk kullanilir.
        // EN: If the image cannot be loaded, a simple red color is used.
        if (image == null || image.isError()) {
            setFill(Color.CRIMSON);
        } else {
            // TR: Resim basariliysa spotun icine desen olarak yerlestirilir.
            // EN: If the image is loaded, it is used as the fill pattern of the spot.
            setFill(new ImagePattern(image, 0, 0, 1, 1, true));
        }
    }

    public void play(Point2D start, Point2D end, Duration duration, EventHandler<ActionEvent> onFinished) {
        // TR: Path, spotun baslangic noktasindan bitis noktasina gidecegi yolu tutar.
        // EN: Path stores the route from the start point to the end point.
        Path path = new Path();
        path.getElements().add(new MoveTo(start.getX(), start.getY()));
        path.getElements().add(new LineTo(end.getX(), end.getY()));

        // TR: PathTransition spotu belirlenen yol uzerinde hareket ettirir.
        // EN: PathTransition moves the spot on the selected path.
        PathTransition move = new PathTransition(duration, path, this);
        move.setCycleCount(1);

        // TR: ScaleTransition spotu animasyon boyunca %25 boyuta kadar kucultur.
        // EN: ScaleTransition shrinks the spot down to 25% during the animation.
        ScaleTransition shrink = new ScaleTransition(duration, this);
        shrink.setFromX(1.0);
        shrink.setFromY(1.0);
        shrink.setToX(0.25);
        shrink.setToY(0.25);
        shrink.setCycleCount(1);

        // TR: ParallelTransition hareket ve kuculme animasyonlarini ayni anda calistirir.
        // EN: ParallelTransition runs the movement and shrinking animations at the same time.
        animation = new ParallelTransition(move, shrink);
        animation.setCycleCount(1);

        // TR: Animasyon bitince GameController'a haber verilir.
        // EN: When the animation ends, GameController is notified.
        animation.setOnFinished(onFinished);
        animation.play();
    }

    public void stopAnimation() {
        // TR: Spot tiklanirsa veya oyun biterse animasyon durdurulur.
        // EN: If the spot is clicked or the game ends, the animation is stopped.
        if (animation != null) {
            animation.stop();
        }
    }
}
