package spoton;

// TR: File sinifi dosya yolundan ses dosyalarini bulmak icin kullanilir.
// EN: The File class is used to find sound files from their paths.
import java.io.File;

// TR: AudioClip kisa oyun seslerini calmak icin uygundur.
// EN: AudioClip is suitable for playing short game sounds.
import javafx.scene.media.AudioClip;

// TR: Bu sinif oyundaki hit, miss ve disappear seslerini yonetir.
// EN: This class manages the hit, miss, and disappear sounds in the game.
public class SoundManager {

    // TR: Noktaya dogru tiklaninca calan ses.
    // EN: Sound played when the player clicks a spot correctly.
    private AudioClip hitSound;

    // TR: Bos yere tiklaninca calan ses.
    // EN: Sound played when the player clicks an empty area.
    private AudioClip missSound;

    // TR: Nokta tiklanmadan kaybolunca calan ses.
    // EN: Sound played when a spot disappears without being clicked.
    private AudioClip disappearSound;

    public SoundManager() {
        // TR: Ses dosyalari proje klasorundeki ExerciseResources icinden yuklenir.
        // EN: Sound files are loaded from ExerciseResources inside the project folder.
        hitSound = loadSound("ExerciseResources/sounds/hit.mp3");
        missSound = loadSound("ExerciseResources/sounds/miss.mp3");
        disappearSound = loadSound("ExerciseResources/sounds/disappear.mp3");
    }

    public void playHit() {
        // TR: Dogru tiklama sesi biraz daha yuksek calinir.
        // EN: The correct click sound is played a little louder.
        play(hitSound, 0.75);
    }

    public void playMiss() {
        // TR: Yanlis tiklama sesi daha dusuk ses seviyesinde calinir.
        // EN: The wrong click sound is played at a lower volume.
        play(missSound, 0.45);
    }

    public void playDisappear() {
        // TR: Kaybolma sesi orta seviyede calinir.
        // EN: The disappear sound is played at a medium volume.
        play(disappearSound, 0.60);
    }

    private AudioClip loadSound(String relativePath) {
        // TR: Once dosyanin var olup olmadigi kontrol edilir.
        // EN: First, the code checks whether the file exists.
        File file = new File(relativePath);
        if (!file.exists()) {
            return null;
        }

        try {
            // TR: Dosya yolu AudioClip formatina cevrilir.
            // EN: The file path is converted into an AudioClip.
            return new AudioClip(file.toURI().toString());
        } catch (Exception exception) {
            // TR: Ses yuklenemezse null doner; oyun yine de calisir.
            // EN: If the sound cannot be loaded, null is returned; the game still works.
            return null;
        }
    }

    private void play(AudioClip sound, double volume) {
        // TR: Ses dosyasi yoksa hicbir sey yapmadan cikilir.
        // EN: If the sound file is missing, the method exits without doing anything.
        if (sound == null) {
            return;
        }

        try {
            // TR: Ses verilen ses seviyesinde calinir.
            // EN: The sound is played with the given volume.
            sound.play(volume);
        } catch (Exception exception) {
            // If sound fails, the game should still continue.
        }
    }
}
