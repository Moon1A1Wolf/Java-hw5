import java.util.*;

public class DockSimulation {
    // Константи для налаштування моделі
    private static final int DAY_HOURS = 24; // Кількість годин у добі

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Введення параметрів
        double avgPassengerInterval = getValidDoubleInput(scanner, "Введіть середній час між появою пасажирів (в хвилинах): ");
        double avgBoatInterval = getValidDoubleInput(scanner, "Введіть середній час між прибуттям катерів (в хвилинах): ");
        int maxPassengersOnDock = getValidIntInput(scanner, "Введіть максимальну кількість пасажирів на причалі: ");
        boolean isTerminalStop = getValidBooleanInput(scanner, "Катер зупиняється на кінцевій зупинці? (1 - так, 2 - ні): ");

        // Ініціалізація черги пасажирів і списку для очікування
        Queue<Passenger> passengerQueue = new ArrayDeque<>();
        List<Double> waitingTimes = new ArrayList<>();
        Random random = new Random();

        // Змінна для відстеження максимального числа пасажирів
        int maxPassengersAtAnyTime = 0;

        // Моделювання для кожної години доби
        for (int hour = 0; hour < DAY_HOURS; hour++) {
            double passengerArrivalTime = 0;
            double boatArrivalTime = 0;

            while (passengerArrivalTime < 60 || boatArrivalTime < 60) {
                // Додавання пасажирів до черги
                passengerArrivalTime += getRandomTime(avgPassengerInterval, random);
                if (passengerArrivalTime < 60) {
                    passengerQueue.add(new Passenger(roundTime(passengerArrivalTime)));
                    maxPassengersAtAnyTime = Math.max(maxPassengersAtAnyTime, passengerQueue.size());
                }

                // Прибуття катера
                boatArrivalTime += getRandomTime(avgBoatInterval, random);
                if (boatArrivalTime < 60) {
                    int availableSeats = random.nextInt(10) + 1; // Випадкова кількість місць (1-10)
                    int boardedPassengers = 0;

                    // Посадка пасажирів
                    while (!passengerQueue.isEmpty() && boardedPassengers < availableSeats) {
                        Passenger passenger = passengerQueue.poll();
                        boardedPassengers++;
                        double waitingTime = roundTime(Math.max(0, boatArrivalTime - passenger.getArrivalTime()));
                        waitingTimes.add(waitingTime);
                    }
                }
            }
        }

        // Розрахунок середнього часу перебування пасажирів
        if (!waitingTimes.isEmpty()) {
            double totalWaitingTime = waitingTimes.stream().mapToDouble(Double::doubleValue).sum();
            int averageWaitingTime = (int) Math.round(totalWaitingTime / waitingTimes.size());
            System.out.println("Середній час перебування пасажирів на причалі: " + averageWaitingTime + " хвилин.");
        } else {
            System.out.println("Не було пасажирів для розрахунку середнього часу перебування.");
        }

        // Визначення достатнього інтервалу між катерами
        double sufficientBoatInterval = findSufficientBoatInterval(avgPassengerInterval, maxPassengersOnDock, random);
        System.out.println("Достатній інтервал між прибуттям катерів: " + (int) Math.round(sufficientBoatInterval) + " хвилин.");

        scanner.close();
    }

    // Генерація випадкового часу на основі середнього інтервалу
    private static double getRandomTime(double avgInterval, Random random) {
        return -Math.log(1 - random.nextDouble()) * avgInterval;
    }

    // Округлення часу до двох знаків після коми
    private static double roundTime(double time) {
        return Math.round(time * 100.0) / 100.0;
    }

    // Перевірка введення дійсного числа
    private static double getValidDoubleInput(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                if (value > 0) {
                    break;
                } else {
                    System.out.println("Будь ласка, введіть додатнє число.");
                }
            } else {
                System.out.println("Некоректне введення. Спробуйте ще раз.");
                scanner.next();
            }
        }
        return value;
    }

    // Перевірка введення цілого числа
    private static int getValidIntInput(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value > 0) {
                    break;
                } else {
                    System.out.println("Будь ласка, введіть додатнє число.");
                }
            } else {
                System.out.println("Некоректне введення. Спробуйте ще раз.");
                scanner.next();
            }
        }
        return value;
    }

    // Перевірка введення для вибору кінцевої зупинки
    private static boolean getValidBooleanInput(Scanner scanner, String prompt) {
        int choice;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice == 1) {
                    return true;
                } else if (choice == 2) {
                    return false;
                } else {
                    System.out.println("Будь ласка, введіть 1 або 2.");
                }
            } else {
                System.out.println("Некоректне введення. Спробуйте ще раз.");
                scanner.next();
            }
        }
    }

    // Метод для знаходження достатнього інтервалу між катерами
    private static double findSufficientBoatInterval(double avgPassengerInterval, int maxPassengers, Random random) {
        double boatInterval = avgPassengerInterval; // Початкове значення
        while (true) {
            Queue<Passenger> queue = new ArrayDeque<>();
            double passengerTime = 0, boatTime = 0;
            int maxQueueSize = 0;

            for (int hour = 0; hour < DAY_HOURS; hour++) {
                passengerTime = 0;
                boatTime = 0;

                while (passengerTime < 60 || boatTime < 60) {
                    passengerTime += getRandomTime(avgPassengerInterval, random);
                    if (passengerTime < 60) {
                        queue.add(new Passenger(roundTime(passengerTime)));
                        maxQueueSize = Math.max(maxQueueSize, queue.size());
                    }

                    boatTime += boatInterval;
                    if (boatTime < 60) {
                        int availableSeats = random.nextInt(10) + 1; // Количество мест в катере
                        while (!queue.isEmpty() && availableSeats-- > 0) {
                            queue.poll(); // Пассажир садится
                        }
                    }
                }
            }

            // Якщо кількість пасажирів на причалі більше, ніж максимально допустимий розмір
            // черги, зменшуємо інтервал між катерами
            if (maxQueueSize <= maxPassengers) {
                break; // Якщо все в порядку, виходимо з циклу
            }
            boatInterval -= 0.1; // Зменшуємо інтервал
        }
        return boatInterval;
    }

    // Клас для моделювання пасажира
    static class Passenger {
        private final double arrivalTime;

        public Passenger(double arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public double getArrivalTime() {
            return arrivalTime;
        }
    }
}
