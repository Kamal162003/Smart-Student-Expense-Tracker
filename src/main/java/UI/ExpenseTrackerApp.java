
package UI;

import dao.TransactionDAO;
import model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class ExpenseTrackerApp extends JFrame {

    private final TransactionDAO dao = new TransactionDAO();

    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private static final Color NAVY = new Color(25, 38, 65);
    private static final Color BACKGROUND = new Color(245, 247, 251);
    private static final Color GREEN = new Color(28, 160, 105);
    private static final Color RED = new Color(220, 75, 75);
    private static final Color BLUE = new Color(65, 115, 210);
    private static final Color TEXT = new Color(40, 50, 70);
    private static final Color LIGHT_BORDER = new Color(230, 233, 240);

    private static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    private static final Font BOLD_FONT =
            new Font("Segoe UI", Font.BOLD, 16);

    private static final Color[] CHART_COLORS = {
            new Color(65, 115, 210),
            new Color(28, 160, 105),
            new Color(230, 145, 55),
            new Color(155, 100, 205),
            new Color(220, 75, 75),
            new Color(45, 170, 180),
            new Color(180, 150, 65),
            new Color(110, 125, 150)
    };

    // CONSTRUCTOR

    public ExpenseTrackerApp() {

        setTitle("Smart Student Expense Tracker");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        createSidebar();
        createMainContent();

        refreshAll();
    }

    // SIDEBAR

    private void createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(225, 0));

        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 15, 20, 15)
        );

        JLabel logo = new JLabel("  STUDENT");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        logo.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("  EXPENSE TRACKER");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(180, 195, 220));

        sidebar.add(logo);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(55));

        JLabel menuTitle = new JLabel("  MAIN MENU");
        menuTitle.setForeground(new Color(155, 175, 205));
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));

        sidebar.add(menuTitle);
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(createSidebarButton(
                "Dashboard", e -> refreshAll()
        ));

        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createSidebarButton(
                "Add Transaction", e -> addTransaction()
        ));

        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createSidebarButton(
                "Category Summary", e -> showCategorySummary()
        ));

        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createSidebarButton(
                "Refresh Data", e -> refreshAll()
        ));

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("Java + MySQL Project");
        footer.setForeground(new Color(155, 175, 205));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        sidebar.add(footer);

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createSidebarButton(
            String text,
            java.awt.event.ActionListener action
    ) {

        JButton button = new JButton(text);

        button.setFont(NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(NAVY);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(195, 45));
        button.setPreferredSize(new Dimension(195, 45));

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(action);

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(48, 65, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(NAVY);
            }
        });

        return button;
    }

    // MAIN CONTENT

    private void createMainContent() {

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 15, 30)
        );

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 27));
        heading.setForeground(TEXT);

        JLabel description = new JLabel(
                "Manage and monitor your student finances"
        );

        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);

        header.add(heading);
        header.add(Box.createVerticalStrut(5));
        header.add(description);

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 20));
        centerPanel.setOpaque(false);

        centerPanel.add(createDashboard(), BorderLayout.NORTH);
        centerPanel.add(createTransactionSection(), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Smart Student Expense Tracker | Java + MySQL"
        );

        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(Color.GRAY);
        footer.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    // DASHBOARD CARDS

    private JPanel createDashboard() {

        JPanel dashboard = new JPanel(new GridLayout(1, 3, 18, 0));
        dashboard.setOpaque(false);

        incomeLabel = new JLabel("₹0.00");
        expenseLabel = new JLabel("₹0.00");
        balanceLabel = new JLabel("₹0.00");

        dashboard.add(createCard("TOTAL INCOME", incomeLabel, GREEN));
        dashboard.add(createCard("TOTAL EXPENSES", expenseLabel, RED));
        dashboard.add(createCard("AVAILABLE BALANCE", balanceLabel, BLUE));

        return dashboard;
    }

    private JPanel createCard(
            String title,
            JLabel amountLabel,
            Color accent
    ) {

        JPanel card = new JPanel(new BorderLayout(5, 15));
        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LIGHT_BORDER),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.GRAY);

        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        amountLabel.setForeground(accent);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.CENTER);

        return card;
    }

    // TRANSACTION SECTION

    private JPanel createTransactionSection() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LIGHT_BORDER),
                        BorderFactory.createEmptyBorder(20, 20, 15, 20)
                )
        );

        JLabel sectionTitle = new JLabel("Recent Transactions");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        sectionTitle.setForeground(TEXT);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(NORMAL_FONT);

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Search by description or category..."
        );

        JButton searchButton = createButton("Search", BLUE);
        JButton resetButton = createButton(
                "Reset", new Color(110, 120, 140)
        );

        searchButton.addActionListener(e -> searchTransactions());
        searchField.addActionListener(e -> searchTransactions());

        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadTransactions(dao.getAllTransactions());
        });

        JPanel searchButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        searchButtons.setOpaque(false);
        searchButtons.add(searchButton);
        searchButtons.add(resetButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.setOpaque(false);

        topPanel.add(sectionTitle, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Type", "Description",
                "Category", "Amount (₹)", "Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);

        transactionTable.setFont(NORMAL_FONT);
        transactionTable.setRowHeight(34);

        transactionTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        transactionTable.setAutoCreateRowSorter(true);
        transactionTable.setShowVerticalLines(false);
        transactionTable.setGridColor(new Color(235, 238, 243));

        transactionTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        transactionTable.getTableHeader().setBackground(
                new Color(240, 243, 248)
        );

        transactionTable.getTableHeader().setForeground(TEXT);
        transactionTable.getTableHeader().setReorderingAllowed(false);

        transactionTable.setSelectionBackground(
                new Color(220, 232, 250)
        );

        transactionTable.setSelectionForeground(TEXT);

        transactionTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {

                        Component component =
                                super.getTableCellRendererComponent(
                                        table, value, isSelected,
                                        hasFocus, row, column
                                );

                        if (!isSelected) {

                            int modelRow =
                                    table.convertRowIndexToModel(row);

                            String type = table.getModel()
                                    .getValueAt(modelRow, 1)
                                    .toString();

                            component.setBackground(
                                    row % 2 == 0
                                            ? Color.WHITE
                                            : new Color(247, 249, 252)
                            );

                            component.setForeground(TEXT);
                            component.setFont(NORMAL_FONT);

                            if (column == 1) {

                                component.setFont(
                                        new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                13
                                        )
                                );

                                if (type.equalsIgnoreCase("Income")) {
                                    component.setForeground(GREEN);
                                } else if (
                                        type.equalsIgnoreCase("Expense")
                                ) {
                                    component.setForeground(RED);
                                }
                            }
                        }

                        return component;
                    }
                }
        );

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(LIGHT_BORDER)
        );

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5)
        );

        buttonPanel.setOpaque(false);

        JButton addButton = createButton("Add Transaction", GREEN);
        JButton editButton = createButton("Edit Selected", BLUE);
        JButton deleteButton = createButton("Delete Selected", RED);
        JButton categoryButton = createButton("Category Summary", NAVY);
        JButton exportButton = createButton("Export CSV", new Color(25, 135, 95));

        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editSelectedTransaction());
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        categoryButton.addActionListener(e -> showCategorySummary());
        exportButton.addActionListener(e -> exportTransactionsToCSV());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(categoryButton);
        buttonPanel.add(exportButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // BUTTON DESIGN

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        return button;
    }

    // ADD TRANSACTION

    private void addTransaction() {

        JTextField descriptionField = new JTextField(15);
        JTextField categoryField = new JTextField(15);
        JTextField amountField = new JTextField(15);

        JTextField dateField = new JTextField(
                LocalDate.now().toString(), 15
        );

        JComboBox<String> typeBox = new JComboBox<>(
                new String[]{"Expense", "Income"}
        );

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        form.add(new JLabel("Type:"));
        form.add(typeBox);

        form.add(new JLabel("Description:"));
        form.add(descriptionField);

        form.add(new JLabel("Category:"));
        form.add(categoryField);

        form.add(new JLabel("Amount:"));
        form.add(amountField);

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Add New Transaction",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String description = descriptionField.getText().trim();
            String category = categoryField.getText().trim();

            if (description.isEmpty() || category.isEmpty()) {
                showError("Description and category cannot be empty.");
                return;
            }

            BigDecimal amount = new BigDecimal(
                    amountField.getText().trim()
            );

            LocalDate date = LocalDate.parse(
                    dateField.getText().trim()
            );

            String type = (String) typeBox.getSelectedItem();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }

            Transaction transaction = new Transaction(
                    type, description, category, amount, date
            );

            if (dao.addTransaction(transaction)) {
                showMessage("Transaction added successfully!");
                refreshAll();
            } else {
                showError("Unable to add transaction.");
            }

        } catch (NumberFormatException e) {
            showError("Enter a valid amount.");
        } catch (DateTimeParseException e) {
            showError("Enter the date in YYYY-MM-DD format.");
        }
    }

    // EDIT TRANSACTION

    private void editSelectedTransaction() {

        int row = transactionTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a transaction to edit.");
            return;
        }

        int modelRow = transactionTable.convertRowIndexToModel(row);

        int id = (int) tableModel.getValueAt(modelRow, 0);

        String oldType = tableModel.getValueAt(modelRow, 1).toString();
        String oldDescription = tableModel.getValueAt(modelRow, 2).toString();
        String oldCategory = tableModel.getValueAt(modelRow, 3).toString();
        String oldAmount = tableModel.getValueAt(modelRow, 4).toString();
        String oldDate = tableModel.getValueAt(modelRow, 5).toString();

        JComboBox<String> typeBox = new JComboBox<>(
                new String[]{"Expense", "Income"}
        );

        typeBox.setSelectedItem(oldType);

        JTextField descriptionField = new JTextField(oldDescription);
        JTextField categoryField = new JTextField(oldCategory);
        JTextField amountField = new JTextField(oldAmount);
        JTextField dateField = new JTextField(oldDate);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        form.add(new JLabel("Type:"));
        form.add(typeBox);

        form.add(new JLabel("Description:"));
        form.add(descriptionField);

        form.add(new JLabel("Category:"));
        form.add(categoryField);

        form.add(new JLabel("Amount:"));
        form.add(amountField);

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Transaction ID: " + id,
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String description = descriptionField.getText().trim();
            String category = categoryField.getText().trim();

            if (description.isEmpty() || category.isEmpty()) {
                showError("Description and category cannot be empty.");
                return;
            }

            BigDecimal amount = new BigDecimal(
                    amountField.getText().trim()
            );

            LocalDate date = LocalDate.parse(
                    dateField.getText().trim()
            );

            String type = (String) typeBox.getSelectedItem();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }

            Transaction transaction = new Transaction(
                    id, type, description, category, amount, date
            );

            if (dao.updateTransaction(transaction)) {
                showMessage("Transaction updated successfully!");
                refreshAll();
            } else {
                showError("Unable to update transaction.");
            }

        } catch (NumberFormatException e) {
            showError("Enter a valid amount.");
        } catch (DateTimeParseException e) {
            showError("Enter the date in YYYY-MM-DD format.");
        }
    }

    // DELETE TRANSACTION

    private void deleteSelectedTransaction() {

        int row = transactionTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a transaction to delete.");
            return;
        }

        int modelRow = transactionTable.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete transaction ID " + id + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {

            if (dao.deleteTransaction(id)) {
                showMessage("Transaction deleted successfully!");
                refreshAll();
            } else {
                showError("Unable to delete transaction.");
            }
        }
    }

    // SEARCH

    private void searchTransactions() {

        String keyword = searchField.getText().trim();

        loadTransactions(dao.searchTransactions(keyword));
    }

    // LOAD TABLE

    private void loadTransactions(List<Transaction> transactions) {

        tableModel.setRowCount(0);

        for (Transaction transaction : transactions) {

            Object[] row = {
                    transaction.getId(),
                    transaction.getType(),
                    transaction.getDescription(),
                    transaction.getCategory(),
                    transaction.getAmount(),
                    transaction.getTransactionDate()
            };

            tableModel.addRow(row);
        }
    }

    // CSV EXPORT

    private void exportTransactionsToCSV() {

        List<Transaction> transactions = dao.getAllTransactions();

        if (transactions.isEmpty()) {
            showMessage("There are no transactions to export.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Export Transactions to CSV");
        fileChooser.setSelectedFile(
                new java.io.File("Student_Expenses.csv")
        );

        int result = fileChooser.showSaveDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File file = fileChooser.getSelectedFile();

        String filePath = file.getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(".csv")) {
            file = new java.io.File(filePath + ".csv");
        }

        if (file.exists()) {

            int overwrite = JOptionPane.showConfirmDialog(
                    this,
                    "This file already exists. Do you want to replace it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
            );

            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                file.toPath(), StandardCharsets.UTF_8
        )) {

            // CSV HEADER

            writer.write(
                    "ID,Type,Description,Category,Amount,Date"
            );

            writer.newLine();

            // CSV DATA

            for (Transaction transaction : transactions) {

                writer.write(
                        transaction.getId() + ","
                                + csvEscape(transaction.getType()) + ","
                                + csvEscape(transaction.getDescription()) + ","
                                + csvEscape(transaction.getCategory()) + ","
                                + transaction.getAmount().toPlainString() + ","
                                + csvEscape(
                                transaction.getTransactionDate().toString()
                        )
                );

                writer.newLine();
            }

            showMessage(
                    "Transactions exported successfully!\n\nSaved at:\n"
                            + file.getAbsolutePath()
            );

        } catch (IOException e) {

            showError(
                    "Unable to export CSV file.\n" + e.getMessage()
            );
        }
    }

    // HANDLE COMMAS AND QUOTES IN CSV TEXT

    private String csvEscape(String value) {

        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // DASHBOARD CALCULATIONS

    private void refreshDashboard() {

        BigDecimal income = dao.getTotalByType("Income");
        BigDecimal expenses = dao.getTotalByType("Expense");

        BigDecimal balance = income.subtract(expenses);

        incomeLabel.setText("₹" + income.toPlainString());
        expenseLabel.setText("₹" + expenses.toPlainString());
        balanceLabel.setText("₹" + balance.toPlainString());
    }

    // CATEGORY SUMMARY POPUP

    private void showCategorySummary() {

        Map<String, BigDecimal> summary = dao.getCategorySummary();

        if (summary.isEmpty()) {
            showMessage("No expense records found.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal amount : summary.values()) {
            total = total.add(amount);
        }

        JDialog dialog = new JDialog(
                this,
                "Category-wise Expense Analysis",
                true
        );

        dialog.setSize(850, 550);
        dialog.setMinimumSize(new Dimension(750, 500));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);

        header.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel title = new JLabel("Expense Breakdown");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "Understand where your money is being spent"
        );

        subtitle.setFont(NORMAL_FONT);
        subtitle.setForeground(new Color(195, 205, 225));

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setOpaque(false);

        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(subtitle);

        header.add(headingPanel, BorderLayout.WEST);

        dialog.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(BACKGROUND);

        content.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JPanel chartCard = new JPanel(new BorderLayout(10, 10));
        chartCard.setBackground(Color.WHITE);

        chartCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LIGHT_BORDER),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel chartTitle = new JLabel("Spending Distribution");
        chartTitle.setFont(BOLD_FONT);
        chartTitle.setForeground(TEXT);

        CategoryPieChart pieChart = new CategoryPieChart(summary);

        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(pieChart, BorderLayout.CENTER);

        JPanel detailsCard = new JPanel(new BorderLayout(10, 15));
        detailsCard.setBackground(Color.WHITE);

        detailsCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LIGHT_BORDER),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        );

        JLabel detailsTitle = new JLabel("Category Details");
        detailsTitle.setFont(BOLD_FONT);
        detailsTitle.setForeground(TEXT);

        JPanel categoryList = new JPanel();
        categoryList.setLayout(
                new BoxLayout(categoryList, BoxLayout.Y_AXIS)
        );

        categoryList.setBackground(Color.WHITE);

        int index = 0;

        for (Map.Entry<String, BigDecimal> entry : summary.entrySet()) {

            Color categoryColor =
                    CHART_COLORS[index % CHART_COLORS.length];

            BigDecimal amount = entry.getValue();

            double percentage = amount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            JPanel item = new JPanel(new BorderLayout(10, 5));
            item.setBackground(Color.WHITE);

            item.setBorder(
                    BorderFactory.createEmptyBorder(10, 0, 10, 0)
            );

            JPanel left = new JPanel(new BorderLayout(8, 0));
            left.setOpaque(false);

            JPanel colorBox = new JPanel();
            colorBox.setBackground(categoryColor);
            colorBox.setPreferredSize(new Dimension(12, 12));

            JLabel categoryName = new JLabel(entry.getKey());
            categoryName.setFont(NORMAL_FONT);
            categoryName.setForeground(TEXT);

            left.add(colorBox, BorderLayout.WEST);
            left.add(categoryName, BorderLayout.CENTER);

            JPanel right = new JPanel();
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setOpaque(false);

            JLabel amountLabel = new JLabel(
                    "₹" + amount.toPlainString()
            );

            amountLabel.setFont(
                    new Font("Segoe UI", Font.BOLD, 13)
            );

            amountLabel.setForeground(TEXT);
            amountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JLabel percentLabel = new JLabel(
                    String.format("%.1f%%", percentage)
            );

            percentLabel.setFont(
                    new Font("Segoe UI", Font.PLAIN, 11)
            );

            percentLabel.setForeground(Color.GRAY);
            percentLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            right.add(amountLabel);
            right.add(percentLabel);

            item.add(left, BorderLayout.WEST);
            item.add(right, BorderLayout.EAST);

            categoryList.add(item);
            categoryList.add(new JSeparator());

            index++;
        }

        JScrollPane detailsScroll = new JScrollPane(categoryList);
        detailsScroll.setBorder(null);
        detailsScroll.getViewport().setBackground(Color.WHITE);

        detailsCard.add(detailsTitle, BorderLayout.NORTH);
        detailsCard.add(detailsScroll, BorderLayout.CENTER);

        content.add(chartCard);
        content.add(detailsCard);

        dialog.add(content, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);

        footerPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        );

        JLabel totalLabel = new JLabel(
                "TOTAL EXPENSES: ₹" + total.toPlainString()
        );

        totalLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );

        totalLabel.setForeground(NAVY);

        JButton closeButton = createButton("Close", NAVY);
        closeButton.addActionListener(e -> dialog.dispose());

        footerPanel.add(totalLabel, BorderLayout.WEST);
        footerPanel.add(closeButton, BorderLayout.EAST);

        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // CUSTOM PIE CHART

    private class CategoryPieChart extends JPanel {

        private final Map<String, BigDecimal> data;
        private final BigDecimal total;

        public CategoryPieChart(Map<String, BigDecimal> data) {

            this.data = data;

            BigDecimal sum = BigDecimal.ZERO;

            for (BigDecimal amount : data.values()) {
                sum = sum.add(amount);
            }

            this.total = sum;

            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(320, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int size = Math.min(getWidth(), getHeight()) - 50;

            if (size <= 0) {
                g2.dispose();
                return;
            }

            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            int startAngle = 90;
            int colorIndex = 0;

            int remainingAngle = 360;
            int itemCount = data.size();

            for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {

                int angle;

                if (colorIndex == itemCount - 1) {

                    angle = remainingAngle;

                } else {

                    angle = entry.getValue()
                            .multiply(BigDecimal.valueOf(360))
                            .divide(
                                    total,
                                    0,
                                    RoundingMode.HALF_UP
                            )
                            .intValue();

                    angle = Math.max(
                            0,
                            Math.min(angle, remainingAngle)
                    );
                }

                g2.setColor(
                        CHART_COLORS[colorIndex % CHART_COLORS.length]
                );

                g2.fillArc(
                        x, y, size, size,
                        startAngle, -angle
                );

                startAngle -= angle;
                remainingAngle -= angle;
                colorIndex++;
            }

            g2.dispose();
        }
    }

    // REFRESH EVERYTHING

    private void refreshAll() {

        refreshDashboard();
        loadTransactions(dao.getAllTransactions());
    }

    // MESSAGE HELPERS

    private void showMessage(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
