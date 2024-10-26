package tachesmono;

import partage.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class WithdrawTask implements Task {
    private int account;
    private double amount;
    private Callback callback;
    private int retryCount = 0;

    public WithdrawTask(int account, double amount, Callback cb) {
        this.account = account;
        this.amount = amount;
        this.callback = cb;
    }

    @Override
    public Object execute() {
        boolean opSuccess = false;
        String login = "ma837383";
        String mdp = "ma837383";
        String url = "jdbc:oracle:thin:@eluard:1521:ENSE2024";
        String sqlUpdate = "UPDATE Accounts SET balance = balance - ? WHERE id_account = ?";
        String sqlInsertOp = "INSERT INTO Transactions (id_transaction, id_account, transaction_date, type) VALUES (transaction_seq.NEXTVAL, ?, ?, ?)";
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, mdp);
            System.out.println("connexion réussie à la bdd");
            PreparedStatement preparedStatementUpdate = connection.prepareStatement(sqlUpdate);
            PreparedStatement preparedStatementInsert = connection.prepareStatement(sqlInsertOp);

            connection.setAutoCommit(false);

            preparedStatementUpdate.setDouble(1, amount);
            preparedStatementUpdate.setInt(2, account);
            int rowsUpdated = preparedStatementUpdate.executeUpdate();
            if(rowsUpdated > 0) {
                LocalDate currentDate = LocalDate.now();
                preparedStatementInsert.setInt(1, account);
                preparedStatementInsert.setString(2, currentDate.toString());
                preparedStatementInsert.setString(3, "Retrait de " + amount);
                preparedStatementInsert.executeUpdate();

                connection.commit();
                opSuccess = true;
            } else {
                connection.rollback();
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