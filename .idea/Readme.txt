# Smart Student Expense Tracker

A Java Swing desktop application designed to help
students manage their income and expenses efficiently.

## Features

- Income and expense management
- Add, edit and delete transactions
- Dashboard with income, expenses and balance
- Category-wise expense summary
- Pie chart visualization
- Search and filter transactions
- Export transactions to CSV
- MySQL database integration

## Tech Stack

- Java 25
- Java Swing
- FlatLaf
- MySQL 8.0
- JDBC
- Maven
- IntelliJ IDEA

## Project Architecture

- Main: Application entry point
- Database: Database connectivity
- Model: Transaction representation
- DAO: Database operations
- UI: Graphical interface

## Database Setup

1. Install MySQL Server.
2. Create a database named student_expense.
3. Create the transactions table using the provided schema.
4. Configure database connection credentials locally.
5. Run the application using Main.java.

## How to Run

1. Install JDK 25.
2. Install IntelliJ IDEA.
3. Open the project as a Maven project.
4. Allow Maven to download dependencies.
5. Configure MySQL.
6. Run Main.java.