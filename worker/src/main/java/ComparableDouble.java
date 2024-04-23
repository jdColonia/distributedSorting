public class ComparableDouble implements Comparable<ComparableDouble> {

    private Double value;

    public ComparableDouble(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public int compareTo(ComparableDouble other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}