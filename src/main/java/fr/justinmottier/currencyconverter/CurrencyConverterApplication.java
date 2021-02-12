package fr.justinmottier.currencyconverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@SpringBootApplication
public class CurrencyConverterApplication implements CommandLineRunner {

    @Autowired
    private ConfigurableApplicationContext context;

    private final RateDB db = RateDB.getInstance();
    private ArrayList<String> supportedCurrencies;
    private final Random rand = new Random();


    public void run(String... args) {
        this.fillDB();
        this.supportedCurrencies = db.fetchSupportedCurrencies();
        this.printDBContent();
        this.printExamples(5);
        this.loop();
        SpringApplication.exit(this.context, () -> 0);
    }

    public void fillDB() {
        FetchedRates fetchedRates = WebClient.create("https://api.exchangeratesapi.io/latest").get().retrieve().bodyToMono(FetchedRates.class).timeout(Duration.ofSeconds(10)).block();
        db.addRate(fetchedRates.getRates());
        db.addRate("EUR", 1);
    }

    private void printDBContent() {
        List<Rate> rates = this.db.fetchAllRates();
        List<List<Rate>> slicedRates = new ArrayList<>();
        slicedRates.add(rates.subList(0, rates.size() / 2));
        slicedRates.add(rates.subList(rates.size() / 2, rates.size()));
        for (List<Rate> slice: slicedRates) {
            StringBuilder line = new StringBuilder();
            StringBuilder currencies_name = new StringBuilder();
            StringBuilder currencies_rate = new StringBuilder();

            line.append("+");
            currencies_name.append("|");
            currencies_rate.append("|");
            for (Rate aRate : slice) {
                int cell_length = String.valueOf(aRate.getValue()).length() + 2;
                line.append("-".repeat(cell_length)).append("+");
                currencies_rate.append(" ").append(aRate.getValue()).append(" |");
                String cell_space = " ".repeat((cell_length - aRate.getCurrency().length()) / 2);
                currencies_name.append(cell_space).append(aRate.getCurrency()).append(cell_space);
                if (currencies_name.length() + 1 < line.length()) {
                    currencies_name.append(" |");
                } else {
                    currencies_name.append("|");
                }
            }

            System.out.println(
                    line + System.lineSeparator() +
                    currencies_name + System.lineSeparator() +
                    line + System.lineSeparator() +
                    currencies_rate + System.lineSeparator() +
                    line
            );
        }
    }

    public void printExamples(int nbExamples) {
        System.out.println("Here are some conversions examples:");
        for (int i = 0; i < nbExamples; i++) {
            ArrayList<Rate> rates = db.fetchRate(Arrays.asList(randomCurrency(), randomCurrency()));
            if (rates.size() != 2) {
                i--;
                continue;
            }
            float price = (float) (rand.nextFloat() * Math.pow(10, rand.nextInt(5) + 1));
            float convertedPrice = CurrencyConverter.convert(rates.get(0), rates.get(1), price);

            System.out.println(price + " " + rates.get(0).getCurrency() + " is equal to " + convertedPrice + " " + rates.get(1).getCurrency());
        }
    }

    public String randomCurrency() {
        return supportedCurrencies.get(rand.nextInt(supportedCurrencies.size()));
    }

    public void loop() {
        boolean exit = false;
        Scanner input = new Scanner(System.in);

        while (!exit) {
            System.out.println("Do you want to convert a currency or exit the app ? [C / E]: ");
            String answer = input.next();
            switch (answer) {
                case "C":
                    this.convertInterface();
                    break;
                case "E":
                    System.out.println("Exiting...");
                    exit = true;
                    break;
                default:
                    System.out.println(answer + " isn't a correct choice");
                    continue;
            }
        }
    }

    public void convertInterface() {
        Scanner input = new Scanner(System.in);
        String currency = null;

        while (!this.supportedCurrencies.contains(currency)) {
            System.out.println("Please type the currency you want to convert:");
            currency = input.next();
        }
        Rate startRate = db.fetchRate(currency);

        currency = null;
        while (!this.supportedCurrencies.contains(currency)) {
            System.out.println("Please type the currency you want your value to be converted to:");
            currency = input.next();
        }
        Rate endRate = db.fetchRate(currency);

        float price = 0;
        boolean priceIsFloat = false;

        while (!priceIsFloat) {
            try {
                System.out.println("Please type the value to be converted:");
                price = Float.parseFloat(input.next());
                priceIsFloat = true;
            } catch (NumberFormatException ignored) {
                System.out.println("Your input isn't a valid number");
            }
        }

        float newPrice = CurrencyConverter.convert(startRate, endRate, price);

        System.out.println(price + " " + startRate.getCurrency() + " is equal to " + newPrice + " " + endRate.getCurrency());
    }

    public static void main(String[] args) {
        SpringApplication.run(CurrencyConverterApplication.class, args);
    }
}
