package model;

public class Fine {
    private final String type;
    private final City city;

    public Fine(String type, City city) {
        this.type = type.trim();
        this.city = city;
    }

    public String getType() { return type; }
    public City getCity() { return city; }

    @Override
    public String toString() {
        return String.format("%s (%s)", type, city.getName());
    }
}