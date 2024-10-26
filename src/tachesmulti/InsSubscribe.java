package tachesmulti;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class InsSubscribe implements Task {
    private int account;
    private int ins;
    private Callback callback;
    private int retryCount = 0;

    public InsSubscribe(int account, int ins, Callback cb) {
        this.account = account;
        this.ins = ins;
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

        String sqlinsertData = "INSERT INTO Subscriptions (id_subscription, id_insurance, id_client, subscription_date) VALUES (subscription_seq.NEXTVAL, ?, ?, ?)";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url2, login, mdp);
            System.out.println("connexion réussie à la bdd2");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlinsertData);
            
            LocalDate currentDate = LocalDate.now();
            preparedStatement.setInt(1, ins);
            preparedStatement.setInt(2, id_cl);
            preparedStatement.setString(3, currentDate.toString());
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
    public Callback getCallback(){
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