
package dao;

import database.DBConnection;
import model.Transaction;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class TransactionDAO {

    // ADD TRANSACTION
    public boolean addTransaction(Transaction transaction) {

        String sql = """
                INSERT INTO transactions
                (type, description, category, amount, transaction_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, transaction.getType());
            ps.setString(2, transaction.getDescription());
            ps.setString(3, transaction.getCategory());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setDate(5, Date.valueOf(transaction.getTransactionDate()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET ALL TRANSACTIONS
    public List<Transaction> getAllTransactions() {

        List<Transaction> list = new ArrayList<>();

        String sql = """
                SELECT * FROM transactions
                ORDER BY transaction_date DESC, id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("transaction_date").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // UPDATE TRANSACTION
    public boolean updateTransaction(Transaction transaction) {

        String sql = """
                UPDATE transactions
                SET type = ?, description = ?, category = ?,
                    amount = ?, transaction_date = ?
                WHERE id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, transaction.getType());
            ps.setString(2, transaction.getDescription());
            ps.setString(3, transaction.getCategory());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setDate(5, Date.valueOf(transaction.getTransactionDate()));
            ps.setInt(6, transaction.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE TRANSACTION
    public boolean deleteTransaction(int id) {

        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET TOTAL INCOME OR EXPENSE
    public BigDecimal getTotalByType(String type) {

        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM transactions
                WHERE type = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    // CATEGORY-WISE EXPENSE SUMMARY
    public Map<String, BigDecimal> getCategorySummary() {

        Map<String, BigDecimal> summary = new LinkedHashMap<>();

        String sql = """
                SELECT category, SUM(amount) AS total
                FROM transactions
                WHERE type = 'Expense'
                GROUP BY category
                ORDER BY total DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                summary.put(
                        rs.getString("category"),
                        rs.getBigDecimal("total")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }

    // SEARCH TRANSACTIONS
    public List<Transaction> searchTransactions(String keyword) {

        List<Transaction> list = new ArrayList<>();

        String sql = """
                SELECT * FROM transactions
                WHERE description LIKE ?
                   OR category LIKE ?
                ORDER BY transaction_date DESC, id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(new Transaction(
                            rs.getInt("id"),
                            rs.getString("type"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getBigDecimal("amount"),
                            rs.getDate("transaction_date").toLocalDate()
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
