package tachesmulti;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateCard implements Task {
    private int account;
    private int cardType;
    private Callback callback;
    private int retryCount = 0;

    public CreateCard(int account, int cardType, Callback cb) {
        this.account = account;
        this.cardType = cardType;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        boolean opSuccess = false;
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

       
        String sqlinsertData = "INSERT INTO Cards (id_card, id_client, type, credit) VALUES (card_seq.NEXTVAL, ?, ?, ?)";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url2, login, mdp);
            System.out.println("connexion réussie à la bdd2");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlinsertData);
            String ct = "";
            if(cardType == 1) {
                ct = "Visa";
            } else if (cardType == 2) {
                ct = "MasterCard";
            }
            preparedStatement.setInt(1, id_cl);
            preparedStatement.setString(2, ct);
            preparedStatement.setDouble(3, 0.0);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            opSuccess = true;
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

        return opSuccess;
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