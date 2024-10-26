package tachesmulti;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientInsurances implements Task {
    private int account;
    private Callback callback;
    private int retryCount = 0;

    public ClientInsurances (int account, Callback cb) {
        this.account = account;
        this.callback = cb;
    }

    @Override 
    public Object execute() {
        List<String> insuranceCl = new ArrayList<>();
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

        String sqlgetIns = "SELECT ins.type, sub.subscription_date FROM Insurances ins, Subscriptions sub WHERE sub.id_client = ? AND sub.id_insurance = ins.id_insurance";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url2, login, mdp);
            System.out.println("connexion réussie à la bdd2");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlgetIns);
            preparedStatement.setInt(1, id_cl);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    String type = result.getString("type");
                    String date = result.getString("subscription_date");
                    insuranceCl.add("Assurance " + type + " ; date de souscription : " + date);
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

        return insuranceCl;
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