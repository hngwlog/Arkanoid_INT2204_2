# Arkanoid Game

A modern JavaFX implementation of the classic Arkanoid brick-breaking game with enhanced features including power-ups, boss battles, multiplayer mode, and customizable skins.

---

## Table of Contents

- [Features](#features)
- [Dependencies](#dependencies)
- [How to Build](#how-to-build)
- [How to Run](#how-to-run)
- [Gameplay](#gameplay)
- [Project Structure](#project-structure)
- [Authors](#authors)

---

## Features

### Core Gameplay
- **Classic Brick-Breaking Mechanics**: Control a paddle to bounce a ball and destroy bricks
- **Multiple Brick Types**:
  - Normal Bricks: Break with one hit
  - Strong Bricks: Indestructible obstacles
  - Invisible Bricks: Become visible after first hit
  - Explosive Bricks: Destroy adjacent bricks when hit
- **Lives System**: Start with 3 lives, lose one when ball falls off screen
- **Score Tracking**: Earn points for each brick destroyed
- **High Score System**: Persistent leaderboard for each level (top 10 scores)

### Power-Ups
- **Add Ball**: Doubles the number of balls on screen (up to 8 max)
- **Extend Paddle**: Increases paddle width by 1.5x for 5 seconds
- **Immortal**: Prevents ball from being lost for 10 seconds
- **Slow**: Reduces ball speed by 20% for 5 seconds

### Game Modes
- **Single Player**: Progress through 10 pre-designed levels
- **Multiplayer**: Compete against another player on randomly generated maps

### Customization
- **Paddle Skins**: 5 different paddle designs
- **Ball Colors**: 5 color options
- **Audio Settings**: Adjustable volume control
- **Key Bindings**: Customizable movement keys for both players

### Visual & Audio
- **Sprite-based Animations**: Smooth animations for power-ups, explosions, and effects
- **Sound Effects**: Brick hits, paddle bounces, explosions, and game over
- **Background Music**: Menu and gameplay background music
- **Visual Effects**: Explosion animations, brick hit effects

### Technical Features
- **A* Pathfinding**: Boss enemies use intelligent pathfinding to chase the player
- **Collision Detection**: Precise axis-aligned bounding box (AABB) collision system
- **Physics System**: Realistic ball reflection angles based on paddle contact point
- **State Management**: Clean game state transitions (Ready → Running → Paused → Game Over)
- **Persistent Storage**: JSON-based save system for settings, high scores, and skins

---

## Dependencies

### Software
- **Java Development Kit (JDK)**: Oracle JDK 22
- **Apache Maven**: 3.8.0

### Libraries
The project uses Maven for dependency management. Key dependencies include:

- **JavaFX**: 21.0.1 (core, controls, media, graphics)
- **Jackson**: 2.18.1 (JSON processing)
- **JUnit Jupiter**: 5.11.3 (testing)

All dependencies are automatically managed through the `pom.xml` file.

---

## How to Build

### Prerequisites
1. Ensure JDK 21+ is installed:
   ```bash
   java -version
   ```

2. Ensure Maven is installed:
   ```bash
   mvn -version
   ```

### Build Steps

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Arkanoid
   ```

2. Build the project using Maven:
   ```bash
   mvn clean package
   ```

3. The compiled JAR file will be generated in the `target/` directory

### Running Tests
To run the unit tests:
```bash
mvn test
```

---

## How to Run

### Option 1: Using Pre-built Release (Recommended for Users)

1. Download the latest release from the [Releases](../../releases) page
2. Ensure Java 21+ is installed on your system
3. Run `start-jar.cmd`.

### Option 2: Using Maven (For Developers)

If you've cloned and built the project:

```bash
mvn javafx:run
```

This will compile and run the application directly.

---

## Gameplay

### Controls

#### Single Player Mode
- **Move Left**: A (default, customizable)
- **Move Right**: D (default, customizable)
- **Launch Ball**: SPACE

#### Multiplayer Mode
- **Player 1 Left**: A (default, customizable)
- **Player 1 Right**: D (default, customizable)
- **Player 2 Left**: LEFT ARROW (default, customizable)
- **Player 2 Right**: RIGHT ARROW (default, customizable)
- **Launch Ball**: SPACE

### Gameplay Video
![single](https://github.com/user-attachments/assets/72399495-c20d-42e1-b501-758e16f6331c)
![multi](https://github.com/user-attachments/assets/099f31c4-842e-4f6f-a042-e8a4b7c9f954)

---

## Project Structure

### Architecture Overview

The project follows object-oriented design principles with clear separation of concerns:

```
src/com/raumania/
├── core/                 # Core systems and utilities
│   ├── AStarInstructor   # Pathfinding algorithm
│   ├── AudioManager      # Audio system (singleton)
│   ├── HighScore         # Score persistence
│   ├── MapLoader         # Level loading system
│   └── SpriteSheet       # Animation system
├── gameplay/             # Game logic
│   ├── manager/          # Game state management
│   │   ├── GameManager   # Main game update and state
│   │   ├── InputHandler  # Player input processing
│   │   └── EffectCountDown # Power-up timer tracking
│   └── objects/          # Game entities
│       ├── core/         # Base classes
│       ├── brick/        # Brick types (Factory pattern)
│       ├── powerup/      # Power-up types (Factory pattern)
│       ├── boss/         # Boss enemies
│       └── visualeffect/ # Visual effects
├── gui/                  # User interface
│   ├── manager/          # Scene management
│   └── screen/           # Screen implementations
├── math/                 # Math utilities (Vec2f)
├── utils/                # Helper classes
└── main/                 # Application entry point
```

### Design Patterns Used
- **Singleton**: AudioManager, HighScore, MapLoader
- **Factory**: BrickFactory, PowerUpFactory
- **State**: GameManager.GameState
- **Observer**: JavaFX Properties for reactive UI
- **Strategy**: Different brick behaviors through inheritance

### Entity Relationship Diagram (ERD)
![l5fHRziu4dxlhp2s3wUWJP7TlUxXYbvmZgqaGct7i9oalQ8866bYyqAH0aavyHRzxoTIiZmsgIWDS9k7sE9y70vdXZF3aV-jD96wo5YlB-y95pJjdP4CogTaIJKakf3mwjNVUWCXeJSboJrPmAky-kpzaeZidIH5HZWbxv8Iyall-wjU-PSmebIl7vVC8wwqB18jfALLVs--AqqqJNxqxY](https://github.com/user-attachments/assets/32e92f26-3572-421d-a05d-16b965b6331e)

---

## Authors

### Team Members

| Name | Student ID |
|------|------------|
| *Nguyễn Lê Hoàng Long* | *24020209* |
| *Nguyễn Ngọc Khôi Nguyên* | *24020254* |
| *Lê Đình Tuấn* | *24020344* |
| *Tạ Xuân Kiên* | *24020191* |

### Course Information
- **Course**: Object-Oriented Programming (I2526 INT2204 2)
- **Institution**: *University of Engineering and Technology - Vietnam National University*

---

**Enjoy playing Arkanoid!** 🎮🧱
