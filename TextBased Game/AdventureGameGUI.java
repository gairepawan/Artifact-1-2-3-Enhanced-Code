import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdventureGameGUI {

    // Singleton class to manage the game's state
    static class GameState {
        private static GameState instance;
        private String playerName;
        private Room currentRoom;
        private int score;
        private DefaultListModel<String> inventory;

        private GameState() {
            this.score = 0;
            this.inventory = new DefaultListModel<>();
        }

        public static GameState getInstance() {
            if (instance == null) {
                instance = new GameState();
            }
            return instance;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public Room getCurrentRoom() {
            return currentRoom;
        }

        public void setCurrentRoom(Room currentRoom) {
            this.currentRoom = currentRoom;
        }

        public int getScore() {
            return score;
        }

        public void increaseScore(int amount) {
            this.score += amount;
        }

        public DefaultListModel<String> getInventory() {
            return inventory;
        }

        public void addItem(String item) {
            if (!inventory.contains(item)) {
                inventory.addElement(item);
            }
        }

        public boolean hasItem(String item) {
            return inventory.contains(item);
        }
    }

    // Room class to represent a location in the game
    static class Room {
        private String name;
        private String description;
        private Map<String, Room> exits;
        private String item;
        private Enemy enemy;
        private String requiredItem;

        public Room(String name, String description) {
            this.name = name;
            this.description = description;
            this.exits = new HashMap<>();
        }

        public void setExit(String direction, Room room) {
            exits.put(direction, room);
        }

        public Room getExit(String direction) {
            return exits.get(direction);
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }

        public Map<String, Room> getExits() {
            return exits;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Enemy getEnemy() {
            return enemy;
        }

        public void setEnemy(Enemy enemy) {
            this.enemy = enemy;
        }

        public String getRequiredItem() {
            return requiredItem;
        }

        public void setRequiredItem(String requiredItem) {
            this.requiredItem = requiredItem;
        }
    }

    // Enemy class to represent adversaries in the game
    static class Enemy {
        private String name;
        private int strength;

        public Enemy(String name, int strength) {
            this.name = name;
            this.strength = strength;
        }

        public String getName() {
            return name;
        }

        public int getStrength() {
            return strength;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdventureGameGUI().startGame());
    }

    private void startGame() {
        JFrame frame = new JFrame("Pawan's Adventure Text Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Prompt player for their name
        String playerName = JOptionPane.showInputDialog(frame, "Enter your name:", "Welcome", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Adventurer";
        }
        GameState.getInstance().setPlayerName(playerName);

        // Create GUI components
        JTextArea descriptionArea = new JTextArea(10, 50);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        JButton northButton = new JButton("North");
        JButton southButton = new JButton("South");
        JButton eastButton = new JButton("East");
        JButton westButton = new JButton("West");

        buttonPanel.add(northButton);
        buttonPanel.add(southButton);
        buttonPanel.add(eastButton);
        buttonPanel.add(westButton);

        DefaultListModel<String> inventoryModel = GameState.getInstance().getInventory();
        JList<String> inventoryList = new JList<>(inventoryModel);
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryList);
        inventoryScrollPane.setBorder(BorderFactory.createTitledBorder("Inventory"));

        JLabel statusBar = new JLabel("Player: " + playerName + " | Score: 0");

        // Create rooms
        Room startRoom = new Room("Start Room", "You are in a small dimly lit room. There's a door to the north.");
        Room hallway = new Room("Hallway", "You are in a narrow hallway. The walls are lined with torches.");
        Room treasureRoom = new Room("Treasure Room", "You have entered a bright room filled with treasure! You win!");
        Room library = new Room("Library", "You are in a dusty library. There are books everywhere.");
        Room armory = new Room("Armory", "You are in an armory with weapons and shields.");
        Room enemyRoom = new Room("Enemy Room", "A hostile goblin blocks your path!");
        Room lockedRoom = new Room("Locked Room", "You stand before the Treasury Room's locked door. Go ahead and use the key in the inventory to unlock the treasury room.");

        // Set exits
        startRoom.setExit("north", hallway);
        hallway.setExit("south", startRoom);
        hallway.setExit("east", library);
        hallway.setExit("west", armory);
        library.setExit("west", hallway);
        armory.setExit("east", hallway);
        hallway.setExit("north", enemyRoom);
        enemyRoom.setExit("north", lockedRoom);
        lockedRoom.setExit("north", treasureRoom);

        // Add items and enemies
        library.setItem("ancient scroll");
        armory.setItem("sword");
        enemyRoom.setEnemy(new Enemy("Goblin", 7));
        lockedRoom.setRequiredItem("key");

        // Initialize game state
        GameState gameState = GameState.getInstance();
        gameState.setCurrentRoom(startRoom);

        // Update GUI with room description
        updateDescription(descriptionArea, gameState.getCurrentRoom(), gameState);

        // Button actions
        northButton.addActionListener(e -> movePlayer("north", descriptionArea, statusBar));
        southButton.addActionListener(e -> movePlayer("south", descriptionArea, statusBar));
        eastButton.addActionListener(e -> movePlayer("east", descriptionArea, statusBar));
        westButton.addActionListener(e -> movePlayer("west", descriptionArea, statusBar));

        // Layout
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(inventoryScrollPane, BorderLayout.EAST);
        frame.add(statusBar, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private void updateDescription(JTextArea descriptionArea, Room currentRoom, GameState gameState) {
        descriptionArea.setText("You are in the " + currentRoom.getName() + ".\n" + currentRoom.getDescription());

        // Check for items in the room
        if (currentRoom.getItem() != null) {
            descriptionArea.append("\nYou found a " + currentRoom.getItem() + "!");
            gameState.addItem(currentRoom.getItem());
            currentRoom.setItem(null); // Remove item after picking up
        }

        // Show available exits
        descriptionArea.append("\nExits: " + currentRoom.getExits().keySet());
    }

    private void movePlayer(String direction, JTextArea descriptionArea, JLabel statusBar) {
        GameState gameState = GameState.getInstance();
        Room currentRoom = gameState.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            descriptionArea.append("\nYou can't go that way!");
        } else {
            gameState.setCurrentRoom(nextRoom);

            // Enemy Room Logic
            if (nextRoom.getEnemy() != null) {
                if (gameState.hasItem("sword")) {
                    descriptionArea.append("\nYou entered the Enemy Room and killed the goblin with the sword.");
                    descriptionArea.append("\nNow you have the key to the Treasury Room!");
                    gameState.addItem("key");
                    gameState.increaseScore(50);
                    nextRoom.setEnemy(null);
                } else {
                    descriptionArea.append("\nYou entered the Enemy Room unarmed! The goblin attacked and you lost.");
                    descriptionArea.append("\nGame over!");
                    JOptionPane.showMessageDialog(null, "Game Over! Final Score: 0");
                    System.exit(0); // End the game
                }
            }
            // Locked Room Logic
            else if (nextRoom.getRequiredItem() != null && !gameState.hasItem(nextRoom.getRequiredItem())) {
                descriptionArea.append("\nUse the key to enter the Treasury Room.");
                gameState.setCurrentRoom(currentRoom); // Prevent entry
            }
            // Treasure Room Logic
            else if (nextRoom.getName().equals("Treasure Room")) {
                descriptionArea.append("\nCongratulations! You unlocked the door and found the treasure!");
                gameState.increaseScore(50);
                JOptionPane.showMessageDialog(null, "You Win! Final Score: " + gameState.getScore());
                System.exit(0); // End the game
            } else {
                updateDescription(descriptionArea, nextRoom, gameState);
            }
        }

        // Update the status bar
        statusBar.setText("Player: " + gameState.getPlayerName() + " | Score: " + gameState.getScore());
    }
}
