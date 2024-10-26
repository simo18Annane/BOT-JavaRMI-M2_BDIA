package tachesmono;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CardBalance implements Task {
    private int idCard;
    private Callback callback;
    private int retryCount = 0;

    public CardBalance(int idCard, Callback cb) {
        this.idCard = idCard;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        Double balance = null;
        String login = "ma837383";
        String mdp = "ma837383";
        String url = "jdbc:oracle:thin:@butor:1521:ENSB2024";
        String sql = "SELECT credit FROM Cards WHERE id_card = ?";
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, mdp);
            System.out.println("connexion réussie à la bdd2");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, idCard);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    balance = result.getDouble("credit");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }

        return balance;
    }

    @Override
    public Callback getCallback() {
        return callback;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public void incrementRetryCount() {
        retryCount++;
    }
}