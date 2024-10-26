import java.sql.*;
import javax.sql.rowset.*;
import com.sun.rowset.*;

public class InsertDatabdd2 {
    final private static String LOGIN = "ma837383";
    final private static String MDP = "ma837383";
    final private static String URL = "jdbc:oracle:thin:@butor:1521:ENSB2024";

    public static void main(String[] args) {
        Connection connexion = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connexion = DriverManager.getConnection(URL, LOGIN, MDP);
            System.out.println("Connexion réussie à la bdd");

            createTables(connexion);
            insertInsurances(connexion);
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du driver JDBC: " + e.getMessage());
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connexion != null) {
                    connexion.close();
                }
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }

    }

    //insertion des tables Cards, Insurances, Subscriptions
    private static void createTables(Connection connexion) throws SQLException {
        String sqlCards = "CREATE TABLE Cards (id_card INTEGER PRIMARY KEY, id_client INTEGER, type VARCHAR2(100) NOT NULL, credit NUMBER NOT NULL)";
        String sqlInsurances = "CREATE TABLE Insurances (id_insurance INTEGER PRIMARY KEY, price NUMBER NOT NULL, type VARCHAR2(100) NOT NULL)";
        String sqlSubscriptions = "CREATE TABLE Subscriptions (id_subscription INTEGER PRIMARY KEY, id_insurance INTEGER, id_client INTEGER, subscription_date VARCHAR2(20) NOT NULL, FOREIGN KEY (id_insurance) REFERENCES Insurances(id_insurance))";

        PreparedStatement prstCards = connexion.prepareStatement(sqlCards);
        PreparedStatement prstInsurances = connexion.prepareStatement(sqlInsurances);
        PreparedStatement prstSubscriptions = connexion.prepareStatement(sqlSubscriptions);

        prstCards.executeUpdate();
        prstInsurances.executeUpdate();
        prstSubscriptions.executeUpdate();

        prstCards.close();
        prstInsurances.close();
        prstSubscriptions.close();

    }

    //insertion des données dans la table Insurances
    private static void insertInsurances(Connection connexion) throws SQLException {
        String[] types = {"vie", "auto", "habitation", "voyage", "biens", "sante", "scolaire", "animaux", "juridique", "cyber risques"};
        Double[] price = {85.25, 120.75, 72.85, 45.36, 68.69, 145.25, 32.25, 53.69, 67.25, 75.8};

        String sqlQuery = "INSERT INTO Insurances (id_insurance, price, type) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connexion.prepareStatement(sqlQuery);

        for (int i=1; i<=10; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setDouble(2, price[i-1]);
            preparedStatement.setString(3, types[i-1]);
            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
        preparedStatement.close();
    }
}
