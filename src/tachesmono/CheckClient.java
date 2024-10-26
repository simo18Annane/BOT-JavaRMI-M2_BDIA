package tachesmono;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class CheckClient implements Task {
    private String clientName;
    private Callback callback;
    private int retryCount = 0;

    public CheckClient(String clName, Callback cb) {
        this.clientName = clName;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        List<Integer> accountIds = new ArrayList<>();
        boolean clExists = false;
        String login = "ma837383";
        String mdp = "ma837383";
        String url = "jdbc:oracle:thin:@eluard:1521:ENSE2024";
        String sql_1 = "SELECT COUNT(*) FROM Clients WHERE name = ?";
        String sql_2 = "SELECT id_account FROM Accounts ac, Clients cl WHERE ac.id_client = cl.id_client AND cl.name = ?";
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, mdp);
            System.out.println("connexion réussie à la bdd");
            PreparedStatement preparedStatement = connection.prepareStatement(sql_1);

            preparedStatement.setString(1, clientName);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if(result.next()) {
                    clExists = result.getInt(1) > 0;
                }
            }
            if(clExists) {
                preparedStatement = connection.prepareStatement(sql_2);
                preparedStatement.setString(1, clientName);
                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        accountIds.add(result.getInt("id_account"));
                    }
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
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }

        return accountIds;
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