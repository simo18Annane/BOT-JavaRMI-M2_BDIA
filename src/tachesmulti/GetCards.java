package tachesmulti;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class GetCards implements Task {
    private int account;
    private Callback callback;
    private int retryCount = 0;

    public GetCards(int account, Callback cb) {
        this.account = account;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        List<Integer> cardsID = new ArrayList<>();

        String login = "ma837383";
        String mdp = "ma837383";
        String url1 = "jdbc:oracle:thin:@eluard:1521:ENSE2024";
        String url2 = "jdbc:oracle:thin:@butor:1521:ENSB2024";
        int id_cl = 0;
        Connection connection = null;

        String sqlgetData = "SELECT id_client FROM Accounts WHERE id_account = ?";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url1, login, mdp);
            System.out.println("connexion réussie à la bdd1");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlgetData);
            preparedStatement.setInt(1, account);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    id_cl = result.getInt("id_client");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        String sqlgetCard = "SELECT id_card FROM Cards WHERE id_client = ?";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url2, login, mdp);
            System.out.println("connexion réussie à la bdd2");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlgetCard);
            preparedStatement.setInt(1, id_cl);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    cardsID.add(result.getInt("id_card"));
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return cardsID;
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