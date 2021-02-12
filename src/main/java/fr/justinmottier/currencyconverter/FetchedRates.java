package fr.justinmottier.currencyconverter;

import java.util.Map;

public class FetchedRates {
    private Map<String, Float> rates;
    private String date;
    private String base;

    public Map<String, Float> getRates() {
        return rates;
    }

    public boolean supportedCurrency(String currency) {
        return this.rates.containsKey(currency);
    }

    public String toString() {
        StringBuilder line = new StringBuilder();
        StringBuilder currencies_name = new StringBuilder();
        StringBuilder currencies_rate = new StringBuilder();

        line.append("+");
        currencies_name.append("|");
        currencies_rate.append("|");

        for (Map.Entry<String, Float> entry: this.rates.entrySet()) {
            int cell_length = String.valueOf(entry.getValue()).length() + 2;
            line.append("-".repeat(cell_length)).append("+");
            currencies_rate.append(" ").append(entry.getValue()).append(" |");
            String cell_space = " ".repeat((cell_length - entry.getKey().length()) / 2);
            currencies_name.append(cell_space).append(entry.getKey()).append(cell_space);
            if (currencies_name.length() + 1 < line.length()) {
                currencies_name.append(" |");
            } else {
                currencies_name.append("|");
            }
        }

        return line + System.lineSeparator() +
                currencies_name + System.lineSeparator() +
                line + System.lineSeparator() +
                currencies_rate + System.lineSeparator() +
                line;
    }
}
