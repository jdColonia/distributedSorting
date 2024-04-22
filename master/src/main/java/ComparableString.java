public class ComparableString implements Comparable<ComparableString> {

    private String value;

    public ComparableString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(ComparableString other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }

}