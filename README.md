# SpotOn JavaFX 8 Game

This project implements the SpotOn assignment as a plain JavaFX 8 source project.

## Run

If you use the JavaFX Toolkit from the Desktop, double-click these files from this project folder:

```text
compile_spoton.bat
run_spoton.bat
```

Open the project folder in an IDE that supports JDK 8 / JavaFX 8, then run:

```text
spoton.Main
```

If you compile from the command line with a JDK 8 installation, run these commands from the project root:

```powershell
javac -d out src\spoton\*.java
java -cp out spoton.Main
```

Keep the working directory as the project root so the game can load:

- `ExerciseResources/images/red_spot.png`
- `ExerciseResources/images/green_spot.png`
- `ExerciseResources/images/life.png`
- `ExerciseResources/sounds/hit.mp3`
- `ExerciseResources/sounds/miss.mp3`
- `ExerciseResources/sounds/disappear.mp3`

## Game Notes

- Click the moving spots before they disappear.
- Empty clicks reduce the score.
- When the game ends, choose `Yeniden Oyna` to restart.
- Sound effects use `AudioClip`, which is better for short game sounds.
