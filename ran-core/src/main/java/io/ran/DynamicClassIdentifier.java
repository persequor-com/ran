package io.ran;

public class DynamicClassIdentifier {
    private final String identifier;

    private DynamicClassIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String get() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DynamicClassIdentifier other = (DynamicClassIdentifier) o;
        return identifier.equals(other.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public static DynamicClassIdentifier create(String identifier) {
        if (identifier == null || identifier.matches("[^0-9a-zA-Z_]")) {
            throw new RuntimeException("Invalid dynamic class identifier. Only alphanumeric and _ is allowed in identifier");
        }
        return new DynamicClassIdentifier(identifier);
    }
}