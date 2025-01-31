import java.util.*;
import java.util.function.Consumer;
import model.*;

public class TaxFinesDatabase {
    private static final Map<String, Person> database = new HashMap<>();
    private static final Map<String, City> cityPool = new HashMap<>();
    private static int cityIdCounter = 1;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Print full database");
            System.out.println("2. Print data by ID");
            System.out.println("3. Print data by fine type");
            System.out.println("4. Print data by city");
            System.out.println("5. Add a new person");
            System.out.println("6. Add fines to an existing record");
            System.out.println("7. Remove a fine");
            System.out.println("8. Update person information");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }
            System.out.println();

            switch (choice) {
                case 1 -> printDatabase();
                case 2 -> printDataByID();
                case 3 -> printDataByFineType();
                case 4 -> printDataByCity();
                case 5 -> addPerson();
                case 6 -> addFines();
                case 7 -> removeFine();
                case 8 -> updatePerson();
                case 0 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private static void printDatabase() {
        if (database.isEmpty()) {
            System.out.println("Database is empty.");
            return;
        }
        database.values().forEach(System.out::println);
    }

    private static void printDataByID() {
        String id = inputId();
        if (id == null) return;

        Person person = database.get(id);
        if (person != null) {
            System.out.println(person);
        } else {
            System.out.println("Person not found.");
        }
    }

    private static void printDataByFineType() {
        System.out.print("Enter fine type: ");
        String type = scanner.nextLine().trim();
        boolean found = database.values().stream()
                .filter(p -> p.hasFineType(type))
                .peek(System.out::println)
                .count() > 0;
        if (!found) System.out.println("No data found.");
    }

    private static void printDataByCity() {
        City city = inputCity();
        boolean found = database.values().stream()
                .filter(p -> p.getCity().equals(city))
                .peek(System.out::println)
                .count() > 0;
        if (!found) System.out.println("No data found.");
    }

    private static void addPerson() {
        String id = inputId();
        if (id == null || database.containsKey(id)) {
            System.out.println("Invalid or existing ID");
            return;
        }

        String firstName = inputName("first");
        String lastName = inputName("last");
        String birthDate = inputBirthDate();
        City city = inputCity();

        database.put(id, new Person(id, firstName, lastName, birthDate, city));
        System.out.println("Person added successfully!");
    }

    private static void addFines() {
        String id = inputId();
        if (id == null) return;

        Person person = database.get(id);
        if (person == null) {
            System.out.println("Person not found.");
            return;
        }

        System.out.print("Enter fine type: ");
        String type = scanner.nextLine().trim();
        City city = inputCity();
        person.addFine(new Fine(type, city));
        System.out.println("Fine added successfully!");
    }

    private static void removeFine() {
        String id = inputId();
        if (id == null) return;

        Person person = database.get(id);
        if (person == null) {
            System.out.println("Person not found.");
            return;
        }

        System.out.print("Enter fine type to remove: ");
        String type = scanner.nextLine().trim();
        City city = inputCity();
        if (person.removeFines(type, city)) {
            System.out.println("Fine(s) removed successfully!");
        } else {
            System.out.println("Fine not found.");
        }
    }

    private static void updatePerson() {
        String id = inputId();
        if (id == null) return;

        Person person = database.get(id);
        if (person == null) {
            System.out.println("Person not found.");
            return;
        }

        updateField("first name", person::setFirstName);
        updateField("last name", person::setLastName);
        updateBirthDate(person);
        updateCity(person);

        System.out.print("Replace all fines? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            person.getFines().clear();
            addNewFines(person);
        }
        System.out.println("Person updated successfully!");
    }

    private static String inputId() {
        System.out.print("Enter ID (10 digits): ");
        String input = scanner.nextLine().trim();
        
        if (!input.matches("\\d{10}")) {
            System.out.println("Invalid ID format. Must be exactly 10 digits.");
            return null;
        }
        return input;
    }

    private static City inputCity() {
        while (true) {
            System.out.print("Enter city name: ");
            String cityName = scanner.nextLine().trim();
            if (cityName.matches("[a-zA-Z-]+")) return cityPool.computeIfAbsent(cityName.toLowerCase(), k -> 
                new City(cityIdCounter++, cityName));
            System.out.println("Invalid city name. Use letters and hyphens only.");
        }
    }

    private static String inputName(String type) {
        while (true) {
            System.out.printf("Enter %s name: ", type);
            String name = scanner.nextLine().trim();
            if (name.matches("[a-zA-Z-]+")) return name;
            System.out.println("Invalid name. Use letters and hyphens only.");
        }
    }

    private static String inputBirthDate() {
        while (true) {
            System.out.print("Enter birth date (dd.mm.yyyy): ");
            String date = scanner.nextLine().trim();
            if (date.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) return date;
            System.out.println("Invalid date format. Use dd.mm.yyyy.");
        }
    }

    private static void updateField(String fieldName, Consumer<String> setter) {
        System.out.printf("Enter new %s (leave empty to keep current): ", fieldName);
        String value = scanner.nextLine().trim();
        if (!value.isEmpty()) setter.accept(value);
    }

    private static void updateBirthDate(Person person) {
        System.out.print("Enter new birth date (dd.mm.yyyy) or empty: ");
        String date = scanner.nextLine().trim();
        if (!date.isEmpty() && date.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            person.setBirthDate(date);
        }
    }

    private static void updateCity(Person person) {
        System.out.print("Enter new city (leave empty to keep current): ");
        String cityName = scanner.nextLine().trim();
        if (!cityName.isEmpty()) {
            person.setCity(cityPool.computeIfAbsent(cityName.toLowerCase(), k -> 
                new City(cityIdCounter++, cityName)
            ));
        }
    }

    private static void addNewFines(Person person) {
        System.out.print("Enter number of fines: ");
        try {
            int count = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < count; i++) {
                System.out.printf("Fine #%d:%n", i + 1);
                System.out.print("Type: ");
                String type = scanner.nextLine().trim();
                City city = inputCity();
                person.addFine(new Fine(type, city));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }
}