package fr.justinmottier.currencyconverter;

public class Price {
    private float value;
    private String currency;

    public Price(float value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public float getValue() {
        return this.value;
    }

    public String getCurrency() {
        return currency;
    }
}
