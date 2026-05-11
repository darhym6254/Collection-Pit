# Project Name: Collection Pit

## Project Description
Collection Pit is a Java application designed to help Magic: The Gathering players track and manage their personal bulk card collections. The application allows users to add cards to their inventory, view their full collection, update card quantities, remove cards, and search for specific cards by name. Launched from Visual Studio Code, all interaction takes place through the terminal. Card data is stored in a local SQLite database so the collection is saved between sessions.

## Purpose
Magic: The Gathering players who collect cards in bulk often struggle to remember exactly which cards they own, leading to duplicate purchases, missed trade opportunities, and wasted money when building new decks. Collection Pit solves this by letting a player query their collection in seconds instead of physically searching through hundreds of cards. When planning a deck, a player can look up every card they already own before making any purchases.

## Project Tasks

- **Task 1: Project Proposal**
  - Defined the problem and proposed a solution
  - Outlined how the application would meet all course requirements

- **Task 2: Application Design**
  - Designed all classes, properties, and methods
  - Defined the SQLite database schema
  - Mapped every course requirement to a specific design decision

- **Task 3: Class Implementation**
  - Implemented the abstract `Card` base class and six card subclasses
  - Built the `ManageCollection` interface and `Collection` class
  - Implemented the terminal menu in `Main`

- **Task 4: Database Implementation**
  - Implemented `DatabaseManager` with full CRUD support using SQLite JDBC
  - Connected the in-memory collection to the database on startup and after every change
  - Used the `type` column to reconstruct the correct card subclass when loading saved data

- **Task 5: Final Testing and Submission**
  - Tested all menu options end to end
  - Verified data persists correctly across sessions
  - Finalized GitHub repository and documentation

## Project Skills Learned
- Object-oriented programming in Java (inheritance, interfaces, polymorphism, composition)
- Abstract classes and access specifiers
- SQLite database integration using JDBC
- Terminal-based user input and output
- Version control with Git and GitHub
- Project planning from proposal through implementation

## Language Used
- **Java**: Core application language, run through Visual Studio Code
- **SQLite**: Local database for persistent card storage via JDBC driver

## Development Process Used
- **Incremental Development**: The project was built in phases — proposal, design, class implementation, and database implementation — with each phase building directly on the last.

## How to Run
1. Open the `Collection_Pit` folder in Visual Studio Code
2. Make sure the **Extension Pack for Java** is installed in VS Code
3. Open `src/Main.java` and click **Run** above the `main` method
4. The terminal will open and the menu will appear
5. `collection.db` is created automatically in the project folder on the first run — no setup needed

> **Dependency:** The SQLite JDBC driver (`sqlite-jdbc-3.46.0.0.jar`) is included in the `lib/` folder and is referenced automatically by VS Code.

## Folder Structure
```
Collection_Pit/
├── src/                  # All Java source files
│   ├── Main.java
│   ├── Card.java
│   ├── ManageCollection.java
│   ├── Collection.java
│   ├── DatabaseManager.java
│   ├── CreatureCard.java
│   ├── InstantCard.java
│   ├── SorceryCard.java
│   ├── LandCard.java
│   ├── ArtifactCard.java
│   └── EnchantmentCard.java
├── lib/                  # SQLite JDBC driver
│   └── sqlite-jdbc-3.46.0.0.jar
├── bin/                  # Compiled class files (auto-generated)
└── .vscode/              # VS Code project settings
```

## Demo Video
[Collection Pit - Project Demo](https://www.youtube.com/watch?v=PLACEHOLDER)

## Link to Project
[Collection Pit Repository](https://github.com/darhym6254/Collection-Pit)
