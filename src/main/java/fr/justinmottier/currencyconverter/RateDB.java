package fr.justinmottier.currencyconverter;

import org.hsqldb.jdbc.JDBCDataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RateDB {
    private static RateDB instance = null;

    private final String dbSocket = "jdbc:hsqldb:mem:currencyConverter";
    private final String dbUsername = "sa";
    private final String dbPassword = "";
    private Connection conn;

    public static RateDB getInstance() {
        return RateDB.instance == null ? new RateDB() : RateDB.instance;
    }

    private RateDB() {
        try {
            JDBCDataSource dataSource = new JDBCDataSource();
            dataSource.setURL(this.dbSocket);
            this.conn = dataSource.getConnection(dbUsername, dbPassword);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.initDB();
    }

    public void initDB() {
        String createTableRequest = "CREATE TABLE rate (name CHAR(3), value FLOAT)";
        try {
            Statement s = this.conn.createStatement();
            s.executeUpdate(createTableRequest);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

//        // On insère ensuite quelques utilisateurs
//        String requeteUser1 = "INSERT INTO users(login,password) VALUES('straumat', 'straumat16')";
//        String requeteUser2 = "INSERT INTO users(login,password) VALUES('jgoncalves', 'jgoncalves16')";
//        String requeteUser3 = "INSERT INTO users(login,password) VALUES('sgoumard', 'sgoumard16')";
//
//        // Puis on l'execute
//        executerUpdate(connexion,requeteUser1);
//        executerUpdate(connexion,requeteUser2);
//        executerUpdate(connexion,requeteUser3);
    }

    public void addRate(Map<String, Float> rates) {
        rates.forEach(this::addRate);
    }

    public void addRate(String currency, float rate) {
        String insertRateRequest = "INSERT INTO rate(name, value) VALUES(?, ?)";
        try {
            PreparedStatement insertRateStatement = this.conn.prepareStatement(insertRateRequest);
            insertRateStatement.setString(1, currency);
            insertRateStatement.setFloat(2, rate);
            insertRateStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Rate fetchRate(String currency) {
        ArrayList<Rate> rates = this.fetchRate(Collections.singletonList(currency));
        return rates == null || rates.isEmpty() ? null : rates.get(0);
    }

    public ArrayList<Rate> fetchRate(List<String> currencies) {
        ArrayList<Rate> rates = new ArrayList<Rate>();
        String getRateRequest = "SELECT name, value FROM rate WHERE name IN (";
        String[] questionMarks = new String[currencies.size()];
        Arrays.fill(questionMarks, "?");
        getRateRequest += String.join(", ", questionMarks) + ')';
        try {
            PreparedStatement getRateStatement = this.conn.prepareStatement(getRateRequest);
            for (int i = 0; i < currencies.size(); i++) {
                getRateStatement.setString(i + 1, currencies.get(i));
            }
            ResultSet results = getRateStatement.executeQuery();
            while (results.next()) {
                rates.add(new Rate(results.getString("name"), results.getFloat("value")));
            }
            return rates;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<Rate> fetchAllRates() {
        ArrayList<Rate> rates = new ArrayList<Rate>();
        String getRateRequest = "SELECT name, value FROM rate";
        try {
            Statement getRateStatement = this.conn.createStatement();
            ResultSet results = getRateStatement.executeQuery(getRateRequest);
            while (results.next()) {
                rates.add(new Rate(results.getString("name"), results.getFloat("value")));
            }
            return rates;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> fetchSupportedCurrencies() {
        ArrayList<String> currencies = new ArrayList<String>();
        String getRateRequest = "SELECT name FROM rate";
        try {
            Statement getCurrenciesStatement = this.conn.createStatement();
            ResultSet results = getCurrenciesStatement.executeQuery(getRateRequest);
            while (results.next()) {
                currencies.add(results.getString("name"));
            }
            return currencies;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
