import java.util.*;

public class DictionaryApp {
    private static final Map<String, Set<String>> dictionary = new HashMap<>();
    private static final Map<String, Integer> wordUsage = new TreeMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add a word");
            System.out.println("2. Add a translation");
            System.out.println("3. Remove a word");
            System.out.println("4. Remove a translation");
            System.out.println("5. Show translations of a word");
            System.out.println("6. Top-10 most popular words");
            System.out.println("7. Top-10 least popular words");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }
            
            switch (choice) {
                case 1 -> addWord();
                case 2 -> addTranslation();
                case 3 -> removeWord();
                case 4 -> removeTranslation();
                case 5 -> showTranslations();
                case 6 -> showTopWords(true);
                case 7 -> showTopWords(false);
                case 0 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addWord() {
        System.out.print("Enter an English word: ");
        String word = scanner.nextLine().trim();
        if (dictionary.containsKey(word)) {
            System.out.println("The word already exists in the dictionary!");
        } else {
            dictionary.put(word, new HashSet<>());
            wordUsage.put(word, 0);
            System.out.println("Word added!");
        }
    }

    private static void addTranslation() {
        System.out.print("Enter an English word: ");
        String word = scanner.nextLine().trim();
        if (!dictionary.containsKey(word)) {
            System.out.println("The word is not in the dictionary.");
            return;
        }
        System.out.print("Enter the German translation: ");
        String translation = scanner.nextLine().trim();
        dictionary.get(word).add(translation);
        System.out.println("Translation added!");
    }

    private static void removeWord() {
        System.out.print("Enter the word to remove: ");
        String word = scanner.nextLine().trim();
        if (dictionary.remove(word) != null) {
            wordUsage.remove(word);
            System.out.println("Word removed!");
        } else {
            System.out.println("Word not found.");
        }
    }

    private static void removeTranslation() {
        System.out.print("Enter an English word: ");
        String word = scanner.nextLine().trim();
        if (!dictionary.containsKey(word)) {
            System.out.println("The word is not in the dictionary.");
            return;
        }
        System.out.print("Enter the translation to remove: ");
        String translation = scanner.nextLine().trim();
        if (dictionary.get(word).remove(translation)) {
            System.out.println("Translation removed!");
        } else {
            System.out.println("Translation not found.");
        }
    }

    private static void showTranslations() {
        System.out.print("Enter an English word: ");
        String word = scanner.nextLine().trim();
        if (!dictionary.containsKey(word)) {
            System.out.println("The word is not in the dictionary.");
            return;
        }
        wordUsage.put(word, wordUsage.getOrDefault(word, 0) + 1);
        System.out.println("[" + word + "] - " + dictionary.get(word));
    }

    private static void showTopWords(boolean popular) {
        if (wordUsage.isEmpty()) {
            System.out.println("Not enough data");
            return;
        }
        dictionary.keySet().stream()
                .sorted((w1, w2) -> popular ? wordUsage.get(w2) - wordUsage.get(w1) : wordUsage.get(w1) - wordUsage.get(w2))
                .limit(10)
                .forEach(word -> System.out.println("[" + word + "] - " + wordUsage.get(word) + " lookups"));
    }
}