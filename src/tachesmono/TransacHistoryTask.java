package tachesmono;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransacHistoryTask implements Task {
    private int account;
    private Callback callback;
    private int retryCount = 0;

    public TransacHistoryTask(int account, Callback cb) {
        this.account = account;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        List<String> history = new ArrayList<>();
        String login = "ma837383";
        String mdp = "ma837383";
        String url = "jdbc:oracle:thin:@eluard:1521:ENSE2024";
        String sql = "SELECT transaction_date, type FROM Transactions WHERE id_account = ? ORDER BY transaction_date DESC";
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, mdp);
            System.out.println("connexion réussie à la bdd");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, account);
            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    String date = result.getString("transaction_date");
                    String type = result.getString("type");
                    history.add(date + ": " + type);
                }
            }
        } catch (ClassNotFoundException e){
            System.out.println("Erreur de chargement du driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return history;
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