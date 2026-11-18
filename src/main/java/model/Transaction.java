
package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private int id;
    private String type;
    private String description;
    private String category;
    private BigDecimal amount;
    private LocalDate transactionDate;

    public Transaction(
            String type,
            String description,
            String category,
            BigDecimal amount,
            LocalDate transactionDate) {

        this.type = type;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Transaction(
            int id,
            String type,
            String description,
            String category,
            BigDecimal amount,
            LocalDate transactionDate) {

        this.id = id;
        this.type = type;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
