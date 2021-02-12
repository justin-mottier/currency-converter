package fr.justinmottier.currencyconverter;

import java.util.Map;

public class CurrencyConverter {
    public static float convert(Rate originalRate, Rate newRate, float price) {
        return EURToOther(otherToEUR(price, originalRate.getValue()), newRate.getValue());
    }

    private static float otherToEUR(float price, float rate) {
        return price / rate;
    }

    private static float EURToOther(float price, float rate) {
        return price * rate;
    }
}
