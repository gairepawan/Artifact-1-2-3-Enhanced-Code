#include <iostream>
#include <fstream>
#include <string>
#include <unordered_map>
#include <list>
#include <sstream>
#include <algorithm>
#include <vector>

// Global data structures
std::unordered_map<std::string, std::string> dataMap;
std::list<std::string> actionHistory;
std::string filename; // Global filename used for all operations

// Load data from a file into the unordered map
void loadDataFromFile() {
    if (filename.empty()) {
        std::cout << "Filename not set. Please enter the filename to load data: ";
        std::cin >> filename;
    }

    std::ifstream file(filename);
    if (!file.is_open()) {
        std::cerr << "Error: Unable to open file " << filename << std::endl;
        return;
    }

    std::string line;
    int loadedCount = 0;
    while (std::getline(file, line)) {
        std::istringstream lineStream(line);
        std::string key, value, combinedValue;

        if (std::getline(lineStream, key, ',')) {
            while (std::getline(lineStream, value, ',')) {
                if (!combinedValue.empty()) {
                    combinedValue += ", ";
                }
                combinedValue += value;
            }
            dataMap[key] = combinedValue;
            ++loadedCount;
        }
    }

    file.close();

    if (loadedCount > 0) {
        std::cout << "Data successfully loaded from " << filename << ". Total records: " << loadedCount << std::endl;
        actionHistory.push_back("Loaded data from " + filename + " (" + std::to_string(loadedCount) + " records)");
    } else {
        std::cout << "File is empty or contains no valid data." << std::endl;
    }
}

// Save data from the unordered map back to the file
void saveDataToFile() {
    if (filename.empty()) {
        std::cerr << "Error: No filename set for saving data. Please load data first." << std::endl;
        return;
    }

    std::ofstream file(filename, std::ios::trunc);
    if (!file.is_open()) {
        std::cerr << "Error: Unable to write to file " << filename << std::endl;
        return;
    }

    for (const auto& entry : dataMap) {
        file << entry.first << "," << entry.second << "\n";
    }

    file.close();
    std::cout << "Data successfully saved to " << filename << std::endl;
}

// Display the data in the unordered map
void displayData() {
    if (dataMap.empty()) {
        std::cout << "No data to display." << std::endl;
        return;
    }

    std::cout << "Loaded Data:" << std::endl;
    for (const auto& entry : dataMap) {
        std::cout << "Key: " << entry.first << ", Value: " << entry.second << std::endl;
    }

    actionHistory.push_back("Displayed all data");
}

// Add a new key-value pair to the map and save to file
void addEntry(const std::string& key, const std::string& value) {
    if (key.empty() || value.empty()) {
        std::cout << "Key or value cannot be empty. Entry not added." << std::endl;
        return;
    }

    if (dataMap.find(key) != dataMap.end()) {
        std::cout << "Key already exists. Value will be updated." << std::endl;
    }

    dataMap[key] = value;
    saveDataToFile();
    actionHistory.push_back("Added entry: Key = " + key + ", Value = " + value);
    std::cout << "Entry added successfully: Key = " << key << ", Value = " << value << std::endl;
}

// Delete an entry by key and save to file
void deleteEntry(const std::string& key) {
    if (dataMap.erase(key)) {
        saveDataToFile();
        actionHistory.push_back("Deleted entry with Key = " + key);
        std::cout << "Entry deleted successfully." << std::endl;
    } else {
        std::cout << "Key not found. No entry deleted." << std::endl;
    }
}

// Search for an entry by key
void searchEntry(const std::string& key) {
    auto it = dataMap.find(key);
    if (it != dataMap.end()) {
        std::cout << "Key: " << key << ", Value: " << it->second << std::endl;
        actionHistory.push_back("Searched for Key = " + key);
    } else {
        std::cout << "Key not found." << std::endl;
    }
}

// Count and display the number of entries
void countEntries() {
    std::cout << "Total entries: " << dataMap.size() << std::endl;
    actionHistory.push_back("Counted total entries: " + std::to_string(dataMap.size()));
}

// Display entries sorted by key
void displaySortedEntries() {
    if (dataMap.empty()) {
        std::cout << "No data to display." << std::endl;
        return;
    }

    std::vector<std::pair<std::string, std::string>> sortedData(dataMap.begin(), dataMap.end());
    std::sort(sortedData.begin(), sortedData.end());

    std::cout << "Sorted Data:" << std::endl;
    for (const auto& entry : sortedData) {
        std::cout << "Key: " << entry.first << ", Value: " << entry.second << std::endl;
    }

    actionHistory.push_back("Displayed sorted entries");
}

// Filter and display entries based on a substring
void filterEntries(const std::string& criteria) {
    bool found = false;
    std::cout << "Entries matching criteria \"" << criteria << "\":" << std::endl;

    for (const auto& entry : dataMap) {
        if (entry.first.find(criteria) != std::string::npos || entry.second.find(criteria) != std::string::npos) {
            std::cout << "Key: " << entry.first << ", Value: " << entry.second << std::endl;
            found = true;
        }
    }

    if (!found) {
        std::cout << "No entries found matching the criteria." << std::endl;
    }

    actionHistory.push_back("Filtered entries with criteria: " + criteria);
}

// Display recent actions from the history
void displayActionHistory() {
    if (actionHistory.empty()) {
        std::cout << "No actions recorded." << std::endl;
        return;
    }

    std::cout << "Recent Actions:" << std::endl;
    for (const auto& action : actionHistory) {
        std::cout << "- " << action << std::endl;
    }
}

// Main menu for user interaction
void displayMenu() {
    std::cout << "\n--- Menu ---\n";
    std::cout << "1. Load Data from File\n";
    std::cout << "2. Display Data\n";
    std::cout << "3. Add Entry\n";
    std::cout << "4. Delete Entry\n";
    std::cout << "5. Search Entry\n";
    std::cout << "6. Display Recent Actions\n";
    std::cout << "7. Count Entries\n";
    std::cout << "8. Display Sorted Entries\n";
    std::cout << "9. Filter Entries\n";
    std::cout << "0. Exit\n";
    std::cout << "Enter your choice: ";
}

int main() {
    int choice;
    std::string key, value, criteria;

    do {
        displayMenu();
        std::cin >> choice;

        switch (choice) {
            case 1:
                loadDataFromFile();
                break;
            case 2:
                displayData();
                break;
            case 3:
                std::cout << "Enter Key: ";
                std::cin >> key;
                std::cin.ignore(); // Ignore newline left by previous input
                std::cout << "Enter Value: ";
                std::getline(std::cin, value);
                addEntry(key, value);
                break;
            case 4:
                std::cout << "Enter Key to delete: ";
                std::cin >> key;
                deleteEntry(key);
                break;
            case 5:
                std::cout << "Enter Key to search: ";
                std::cin >> key;
                searchEntry(key);
                break;
            case 6:
                displayActionHistory();
                break;
            case 7:
                countEntries();
                break;
            case 8:
                displaySortedEntries();
                break;
            case 9:
                std::cout << "Enter filter criteria: ";
                std::cin.ignore(); // Ignore newline left by previous input
                std::getline(std::cin, criteria);
                filterEntries(criteria);
                break;
            case 0:
                std::cout << "Exiting program." << std::endl;
                break;
            default:
                std::cout << "Invalid choice. Please try again." << std::endl;
        }
    } while (choice != 0);

    return 0;
}
