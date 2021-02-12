package fr.justinmottier.currencyconverter;

public class Rate {
    private final String currency;
    private float value;

    public Rate(String currency, float value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return this.currency;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.value).append(" ").append(this.currency).append(" equals 1 EUR");
        return s.toString();
    }
}
