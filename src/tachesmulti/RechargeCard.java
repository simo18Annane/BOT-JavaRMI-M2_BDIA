package tachesmulti;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RechargeCard implements Task {
    private int account;
    private int idCard;
    private double amount;
    private Callback callback;
    private int retryCount = 0;

    public RechargeCard(int account, int idCard, double amount, Callback cb) {
        this.account = account;
        this.idCard = idCard;
        this.amount = amount;
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
        double balance = 0;
        Connection connection = null;

        String sqlgetData = "SELECT id_client, balance FROM Accounts WHERE id_account = ?";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url1, login, mdp);
            System.out.println("connexion réussie à la bdd1");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlgetData);
            preparedStatement.setInt(1, account);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    id_cl = result.getInt("id_client");
                    balance = result.getDouble("balance");
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

        if (amount <= balance) {
            String sqlUpdateCards = "UPDATE Cards SET credit = credit + ? WHERE id_card = ?";
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(url2, login, mdp);
                System.out.println("connexion réussie à la bdd2");
                PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateCards);
                connection.setAutoCommit(false);

                preparedStatement.setDouble(1, amount);
                preparedStatement.setInt(2, idCard);
                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0) {
                    connection.commit();
                    opSuccess = true;
                } else {
                    connection.rollback();
                }
            }
            catch (ClassNotFoundException e) {
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

            String sqlUpdateAccount = "UPDATE Accounts SET balance = balance - ? WHERE id_account = ?";
            String sqlInsertOp = "INSERT INTO Transactions (id_transaction, id_account, transaction_date, type) VALUES (transaction_seq.NEXTVAL, ?, ?, ?)";
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(url1, login, mdp);
                System.out.println("connexion réussie à la bdd1");
                PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateAccount);
                PreparedStatement preparedStatementInsert = connection.prepareStatement(sqlInsertOp);
                connection.setAutoCommit(false);

                preparedStatement.setDouble(1, amount);
                preparedStatement.setInt(2, account);
                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0) {
                    LocalDate currentDate = LocalDate.now();
                    preparedStatementInsert.setInt(1, account);
                    preparedStatementInsert.setString(2, currentDate.toString());
                    preparedStatementInsert.setString(3, "rechargement de la carte id :" + idCard + " avec le solde : " + amount);
                    preparedStatementInsert.executeUpdate();
                    connection.commit();
                } else {
                    connection.rollback();
                }
            }
            catch (ClassNotFoundException e) {
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