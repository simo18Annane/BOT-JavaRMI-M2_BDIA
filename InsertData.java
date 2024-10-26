import java.sql.*;
import javax.sql.rowset.*;
import com.sun.rowset.*;
import java.util.Random;
import java.time.LocalDate;

public class InsertData {
    final private static String LOGIN = "ma837383";
    final private static String MDP = "ma837383";
    final private static String URL = "jdbc:oracle:thin:@eluard:1521:ENSE2024";

    public static void main(String[] args) {
        Connection connexion = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connexion = DriverManager.getConnection(URL, LOGIN, MDP);
            System.out.println("Connexion réussie à la bdd");

            createTables(connexion);

            insertClients(connexion, 100);
            System.out.println("Insertion de 100 clients terminée.");

            createAccount(connexion, 150);
            System.out.println("Création de 150 comptes a été avec succès");

            firstTransaction(connexion, 150);
            System.out.println("Transactions d'ouverture ajoutées avec succès.");

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

    //Création des tables Clients, Accounts et Transactions
    private static void createTables(Connection connexion) throws SQLException {
        String sqlClients = "CREATE TABLE Clients (id_client INTEGER PRIMARY KEY, name VARCHAR2(100) NOT NULL, city  VARCHAR2(100))";
        String sqlAccounts = "CREATE TABLE Accounts (id_account INTEGER PRIMARY KEY, id_client INTEGER, balance NUMBER NOT NULL, FOREIGN KEY (id_client) REFERENCES Clients(id_client))";
        String sqlTransactions = "CREATE TABLE Transactions (id_transaction INTEGER PRIMARY KEY, id_account INTEGER, transaction_date VARCHAR2(20) NOT NULL, type VARCHAR2(100) NOT NULL, CONSTRAINT fk_account FOREIGN KEY (id_account) REFERENCES Accounts(id_account))";

        PreparedStatement prstClients = connexion.prepareStatement(sqlClients);
        PreparedStatement prstAccounts = connexion.prepareStatement(sqlAccounts);
        PreparedStatement prstTransactions = connexion.prepareStatement(sqlTransactions);

        prstClients.executeUpdate();
        prstAccounts.executeUpdate();
        prstTransactions.executeUpdate();

        prstClients.close();
        prstAccounts.close();
        prstTransactions.close();

    }

    //insertion des données dans la table Clients
    private static void insertClients(Connection connexion, int nbOfClients) throws SQLException {
        String[] cities = {"Dijon", "Paris", "Lyon", "Nice", "Nîmes", "Strasbourg", "Marseille"};

        String sqlQuery = "INSERT INTO Clients (id_client, name, city) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connexion.prepareStatement(sqlQuery);

        for (int i=1; i<=nbOfClients; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, "client_" + i);
            preparedStatement.setString(3, cities[(int) (Math.random()*7)]);
            preparedStatement.addBatch(); //Ajouter à la liste de batch
        }

        preparedStatement.executeBatch();//Executer toutes les insertions en une seule operation
        preparedStatement.close();
    }

    //insertion des données dans la table Accounts
    private static void createAccount(Connection connexion, int nbOfAccount) throws SQLException {

        String sqlQuery = "INSERT INTO Accounts (id_account, id_client, balance) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connexion.prepareStatement(sqlQuery);

        for (int i=1; i<=nbOfAccount; i++) {
                double val = 100 + (Math.random() * (1000000 - 100));
                val = Math.round(val*100.0)/100.0;
            if(i <= 100){
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, i);
                preparedStatement.setDouble(3, val);
                preparedStatement.addBatch();
            }
            else {
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, (int) (Math.random()*100) + 1);
                preparedStatement.setDouble(3, val);
                preparedStatement.addBatch();
            }
        }

        preparedStatement.executeBatch();
        preparedStatement.close();
    }

    //insertion des données dans la table Transactions
    private static void firstTransaction(Connection connexion, int nbOfTransaction) throws SQLException {

        String sqlQuery = "INSERT INTO Transactions (id_transaction, id_account, transaction_date, type) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connexion.prepareStatement(sqlQuery);

        for (int i=1; i<=nbOfTransaction; i++) {
            LocalDate currentDate = LocalDate.now();
            preparedStatement.setInt(1, i);
            preparedStatement.setInt(2, i);
            preparedStatement.setString(3, currentDate.toString());
            preparedStatement.setString(4, "Ouverture");
            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
        preparedStatement.close();
    }
}