package tachesmono;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetInsurance implements Task {
    private Callback callback;
    private int retryCount = 0;

    public GetInsurance(Callback cb) {
        this.callback = cb;
    }

    @Override 
    public Object execute() {
        List<String> insurance = new ArrayList<>();
        String login = "ma837383";
        String mdp = "ma837383";
        String url = "jdbc:oracle:thin:@butor:1521:ENSB2024";
        String sql = "SELECT id_insurance, price, type FROM Insurances";
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, mdp);
            System.out.println("connexion réussie à la bdd2");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    int id_ins = result.getInt("id_insurance");
                    double price_ins =  result.getDouble("price");
                    String type_ins = result.getString("type");
                    insurance.add(id_ins + "-> assurance de " + type_ins + " | abonnement annuel : " + price_ins);
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

        return insurance;
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