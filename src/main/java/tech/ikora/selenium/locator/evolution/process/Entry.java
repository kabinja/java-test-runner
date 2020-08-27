package tech.ikora.selenium.locator.evolution.process;

public class Entry {
    private final String key;
    private final String value;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String format(String prefix, String separator){
        return format(prefix, separator, "");
    }

    public String format(String separator){
        return format("", separator, "");
    }

    public String format(String prefix, String separator, String suffix){
        return String.format("%s%s%s%s%s", prefix, key, separator, value, suffix);
    }
}
