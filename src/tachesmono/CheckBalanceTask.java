package tachesmono;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBalanceTask implements Task {
    private int account;
    private Callback callback;
    private int retryCount = 0;

    public CheckBalanceTask(int account, Callback cb) {
        this.account = account;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        Double balance = null;
        String login = "ma837383";
        String mdp = "ma837383";
        String url = "jdbc:oracle:thin:@eluard:1521:ENSE2024";
        String sql = "SELECT balance FROM Accounts WHERE id_account = ?";
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, mdp);
            System.out.println("connexion réussie à la bdd");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, account);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    balance = result.getDouble("balance");
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