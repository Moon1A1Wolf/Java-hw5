package model;

import java.util.*;
import java.util.stream.Collectors;

public class Person {
    private final String id;
    private String firstName;
    private String lastName;
    private String birthDate;
    private City city;
    private final List<Fine> fines = new ArrayList<>();

    public Person(String id, String firstName, String lastName, String birthDate, City city) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.city = city;
    }

    public boolean hasFineType(String type) {
        return fines.stream().anyMatch(f -> f.getType().equalsIgnoreCase(type));
    }

    public boolean removeFines(String type, City city) {
        return fines.removeIf(f -> f.getType().equalsIgnoreCase(type) && f.getCity().equals(city));
    }

    public void addFine(Fine fine) {
        fines.add(fine);
    }

    public List<Fine> getFines() {
        return Collections.unmodifiableList(fines);
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    @Override
    public String toString() {
        String finesStr = fines.isEmpty() ? "No fines" : fines.stream()
                .map(Fine::toString)
                .collect(Collectors.joining("; "));
        return String.format("ID: %s, Name: %s %s, Born: %s, City: %s, Fines: [%s]",
                id, firstName, lastName, birthDate, city.getName(), finesStr);
    }
}