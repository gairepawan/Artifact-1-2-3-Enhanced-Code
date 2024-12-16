import tkinter as tk
from tkinter import messagebox, simpledialog


class GameState:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(GameState, cls).__new__(cls)
            cls._instance.player_name = "Adventurer"
            cls._instance.current_room = None
            cls._instance.score = 0
            cls._instance.inventory = set()
        return cls._instance

    def add_item(self, item):
        self.inventory.add(item)

    def has_item(self, item):
        return item in self.inventory


class Room:
    def __init__(self, name, description):
        self.name = name
        self.description = description
        self.exits = {}
        self.item = None
        self.enemy = None
        self.required_item = None

    def set_exit(self, direction, room):
        self.exits[direction] = room


class Enemy:
    def __init__(self, name, strength):
        self.name = name
        self.strength = strength


class AdventureGameGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Adventure Game")

        # Game State
        self.game_state = GameState()
        self.prompt_player_name()

        # GUI Elements
        self.description_area = tk.Text(root, height=10, wrap=tk.WORD)
        self.description_area.pack(fill=tk.BOTH, expand=True)
        self.description_area.config(state=tk.DISABLED)

        self.button_frame = tk.Frame(root)
        self.button_frame.pack()

        self.north_button = tk.Button(self.button_frame, text="North", command=lambda: self.move_player("north"))
        self.north_button.grid(row=0, column=1)

        self.south_button = tk.Button(self.button_frame, text="South", command=lambda: self.move_player("south"))
        self.south_button.grid(row=2, column=1)

        self.east_button = tk.Button(self.button_frame, text="East", command=lambda: self.move_player("east"))
        self.east_button.grid(row=1, column=2)

        self.west_button = tk.Button(self.button_frame, text="West", command=lambda: self.move_player("west"))
        self.west_button.grid(row=1, column=0)

        self.status_label = tk.Label(root, text=f"Player: {self.game_state.player_name} | Score: 0")
        self.status_label.pack()

        self.create_rooms()
        self.update_description()

    def prompt_player_name(self):
        player_name = simpledialog.askstring("Player Name", "Enter your name:")
        if player_name:
            self.game_state.player_name = player_name.strip()
        else:
            self.game_state.player_name = "Adventurer"

    def create_rooms(self):
        # Create rooms
        start_room = Room("Start Room", "You are in a small dimly lit room. There's a door to the north.")
        hallway = Room("Hallway", "You are in a narrow hallway. The walls are lined with torches.")
        library = Room("Library", "You are in a dusty library. There are books everywhere.")
        armory = Room("Armory", "You are in an armory with weapons and shields.")
        enemy_room = Room("Enemy Room", "A hostile goblin blocks your path!")
        treasure_room = Room("Treasure Room", "You have entered a bright room filled with treasure! You win!")

        # Set exits
        start_room.set_exit("north", hallway)
        hallway.set_exit("south", start_room)
        hallway.set_exit("east", library)
        hallway.set_exit("west", armory)
        library.set_exit("west", hallway)
        armory.set_exit("east", hallway)
        hallway.set_exit("north", enemy_room)
        enemy_room.set_exit("north", treasure_room)

        # Add items and enemies
        library.item = "ancient scroll"
        armory.item = "sword"
        enemy_room.enemy = Enemy("Goblin", 7)

        # Set initial state
        self.rooms = {
            "start_room": start_room,
            "hallway": hallway,
            "library": library,
            "armory": armory,
            "enemy_room": enemy_room,
            "treasure_room": treasure_room,
        }
        self.game_state.current_room = start_room

    def update_description(self):
        room = self.game_state.current_room
        self.description_area.config(state=tk.NORMAL)
        self.description_area.delete(1.0, tk.END)
        self.description_area.insert(tk.END, f"You are in the {room.name}.\n{room.description}\n")
        if room.item:
            self.description_area.insert(tk.END, f"You found a {room.item}!\n")
            self.game_state.add_item(room.item)
            room.item = None
        self.description_area.insert(tk.END, f"Exits: {', '.join(room.exits.keys())}")
        self.description_area.config(state=tk.DISABLED)

    def move_player(self, direction):
        current_room = self.game_state.current_room
        if direction not in current_room.exits:
            self.description_area.config(state=tk.NORMAL)
            self.description_area.insert(tk.END, "\nYou can't go that way!")
            self.description_area.config(state=tk.DISABLED)
            return

        next_room = current_room.exits[direction]
        self.game_state.current_room = next_room
        if next_room.enemy:
            if self.game_state.has_item("sword"):
                messagebox.showinfo("Battle", f"You defeated the {next_room.enemy.name}!")
                next_room.enemy = None
                self.game_state.add_item("key")
                self.game_state.score += 50
            else:
                messagebox.showinfo("Game Over", "You were defeated by the enemy!")
                self.root.quit()
        elif next_room.name == "Treasure Room":
            self.game_state.score += 100
            messagebox.showinfo("Victory", f"You win! Final Score: {self.game_state.score}")
            self.root.quit()
        self.update_status()
        self.update_description()

    def update_status(self):
        self.status_label.config(text=f"Player: {self.game_state.player_name} | Score: {self.game_state.score}")


if __name__ == "__main__":
    root = tk.Tk()
    AdventureGameGUI(root)
    root.mainloop()
