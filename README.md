# 💰 Personal Budget Tracker

A **Java-based Personal Budget Tracker** that helps users manage their income and expenses, monitor their balance, set monthly spending limits, analyze spending by category, and save/load financial data using a CSV-style text file.

The application is completely **console-based** and uses Java's standard libraries without requiring any external dependencies.

---

## ✨ Features

- 💵 Add income transactions
- 💸 Add expense transactions
- 📋 View all transactions
- 🔍 Filter transactions by:
  - Income
  - Expenses
  - Month
- 📊 View financial summary
- 💰 Calculate total income
- 💸 Calculate total expenses
- 🏦 Calculate current balance
- 📅 Calculate monthly balance
- 📈 Calculate monthly savings rate
- 🗂️ View spending by category
- 🎯 Set monthly budget limits
- ⚠️ Receive budget warnings
- 🗑️ Delete transactions
- 💾 Save financial data to a file
- 📂 Load saved financial data automatically
- 🇮🇳 Indian Rupee (₹) currency formatting
- ✅ Input validation
- 🔢 Accurate money calculations using `BigDecimal`

The core tracker maintains transactions, monthly category limits, balances, monthly summaries, savings rates, and category-based expense totals.

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| **Java** | Core programming language |
| **BigDecimal** | Accurate financial calculations |
| **Java Records** | Transaction data model |
| **Collections Framework** | Transaction and budget management |
| **Java Time API** | Date and month handling |
| **File I/O** | Save/load financial data |
| **Scanner** | Console input |
| **CSV-style text file** | Data persistence |

---

## 📂 Project Structure

```text
Personal-Budget-Tracker/
│
├── Main.java
├── BudgetTracker.java
├── Transaction.java
├── budget_data.csv
└── README.md
```

### File Description

#### `Main.java`

Contains the **console user interface**, menu system, input validation, and application flow. The menu provides options for adding transactions, viewing transactions, summaries, category spending, budget limits, deletion, saving, and exiting.

#### `BudgetTracker.java`

Contains the **main business logic** of the application, including transaction management, calculations, budget limits, warnings, and data persistence.

#### `Transaction.java`

Defines the transaction data model using a Java `record`. It supports income/expense types and categories such as Salary, Food, Transport, Rent, Bills, Shopping, Health, Entertainment, Education, and Other.

---

# 🚀 Getting Started

## Prerequisites

You need:

- Java **17 or newer**
- JDK installed and configured
- Command Prompt / Terminal

Check your Java installation:

```bash
java --version
```

Check the Java compiler:

```bash
javac --version
```

---

## 📥 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/personal-budget-tracker.git
```

### 2. Navigate to the Project

```bash
cd personal-budget-tracker
```

### 3. Compile the Java Files

```bash
javac Main.java BudgetTracker.java Transaction.java
```

### 4. Run the Application

```bash
java Main
```

---

# 🖥️ Application Menu

When the application starts, the following menu is displayed:

```text
---------------- MENU ----------------
 1. Add income        5. Spending by category
 2. Add expense       6. Set monthly budget limit
 3. View transactions 7. Delete a transaction
 4. Summary           8. Save now
 0. Save & exit
--------------------------------------
```

The menu is implemented directly in `Main.java`.

---

# 💵 Add Income

Select:

```text
1. Add income
```

You will enter:

- Amount
- Category
- Description
- Date

Example:

```text
Amount (₹): 50000
Category: Salary
Description: Monthly salary
Date: 2026-10-01
```

The application automatically updates the current balance after adding the transaction.

---

# 💸 Add Expense

Select:

```text
2. Add expense
```

Example:

```text
Amount (₹): 2500
Category: Food
Description: Monthly groceries
Date: 2026-10-02
```

If a category is approaching or exceeding its monthly budget, the application displays a warning.

---

# 📋 View Transactions

Select:

```text
3. View transactions
```

You can view:

- All transactions
- Income only
- Expenses only

You can also filter transactions by month using:

```text
yyyy-MM
```

Transactions are displayed with:

```text
ID
Date
Type
Category
Amount
Description
```



---

# 📊 Financial Summary

Select:

```text
4. Summary
```

The application displays **all-time** information:

```text
Total income
Total expenses
Balance
```

And the current month's:

```text
Income
Expenses
Net savings
Savings rate
```

The application also warns the user if the overall balance becomes negative.

---

# 🗂️ Spending by Category

Select:

```text
5. Spending by category
```

The application calculates expenses for each category and displays:

- Category
- Amount spent
- Percentage of total spending
- Visual spending bar
- Budget warning

Example:

```text
Food             ₹8,500.00    42.5%  ########
Transport        ₹3,000.00    15.0%  ###
Shopping         ₹2,500.00    12.5%  ##
```

Category expenses are sorted from **highest to lowest spending**.

---

# 🎯 Monthly Budget Limits

Select:

```text
6. Set monthly budget limit
```

You can assign a monthly spending limit to categories such as:

```text
Food
Transport
Rent
Shopping
Entertainment
Education
```

Example:

```text
Food monthly limit: ₹10,000
```

The application provides a warning when spending reaches **80% of the configured budget** and another warning when the budget is exceeded.

### Remove a Budget

Enter:

```text
0
```

when asked for the monthly limit.

---

# 🗑️ Delete Transaction

Select:

```text
7. Delete a transaction
```

Enter the transaction ID:

```text
Transaction ID to delete: 5
```

The application asks for confirmation before permanently removing the transaction.

---

# 💾 Data Storage

The application automatically stores data in:

```text
budget_data.csv
```

When the application starts, it attempts to load existing data.

When the application exits, it automatically saves the current data.

---

# 📄 Data Format

The saved file uses a CSV-style format.

### Transaction

```text
T,id,TYPE,CATEGORY,amount,date,description
```

Example:

```text
T,1,INCOME,SALARY,50000.00,2026-10-01,Monthly salary
```

### Budget Limit

```text
L,CATEGORY,limit
```

Example:

```text
L,FOOD,10000.00
```

The tracker supports saving and loading both transactions and monthly category limits.

---

# 💰 Accurate Money Calculations

The project uses Java's `BigDecimal` instead of `double` for financial calculations.

This helps prevent common floating-point precision problems when handling money.

Transactions are also normalized to **two decimal places**.

---

# 🏷️ Transaction Categories

The application currently supports:

```text
Salary
Food
Transport
Rent
Bills
Shopping
Health
Entertainment
Education
Other
```

These categories are defined in `Transaction.java`.

---

# 📈 Savings Rate

The monthly savings rate is calculated as:

```text
Savings Rate =
(Monthly Income - Monthly Expenses)
----------------------------------- × 100
       Monthly Income
```

If there is no income during the selected month, the application displays:

```text
n/a (no income this month)
```

---

# ⚠️ Budget Warning System

The application uses an **80% warning threshold**.

### Example

If:

```text
Food Budget = ₹10,000
```

and spending reaches:

```text
₹8,000
```

the application warns:

```text
Food budget is 80% used
```

If spending exceeds the limit:

```text
Over Food budget by ₹1,500
```

---

# ✅ Input Validation

The application validates:

- Positive transaction amounts
- Valid transaction categories
- Valid dates
- Valid months
- Valid transaction IDs
- Valid budget limits

Transaction amounts must be greater than zero. Invalid transaction data is rejected when the `Transaction` object is created.

---

# 🔄 Application Workflow

```text
             ┌──────────────────┐
             │    Start App     │
             └────────┬─────────┘
                      ↓
             ┌──────────────────┐
             │ Load Saved Data  │
             └────────┬─────────┘
                      ↓
             ┌──────────────────┐
             │   Main Menu      │
             └────────┬─────────┘
                      ↓
       ┌──────────────┼──────────────┐
       ↓              ↓              ↓
   Add Income    Add Expense    View Summary
       │              │              │
       └──────────────┼──────────────┘
                      ↓
             ┌──────────────────┐
             │ Budget Analysis  │
             └────────┬─────────┘
                      ↓
             ┌──────────────────┐
             │ Save Data        │
             └────────┬─────────┘
                      ↓
             ┌──────────────────┐
             │      Exit        │
             └──────────────────┘
```

---

# 🏗️ Architecture

The project is divided into three main components:

```text
Main
 │
 │ Console UI & Input
 ↓
BudgetTracker
 │
 │ Business Logic
 ↓
Transaction
 │
 │ Data Model
 ↓
budget_data.csv
```

### Main

Handles:

- User interaction
- Menu
- Input validation
- Display

### BudgetTracker

Handles:

- Transactions
- Balance
- Monthly calculations
- Budget limits
- Warnings
- Save/load

### Transaction

Handles:

- Transaction data
- Income/expense type
- Categories
- Validation

---

# 🔮 Future Enhancements

Possible improvements include:

- 🖥️ Java Swing GUI
- 🌐 Web version using Spring Boot
- 📱 Android application
- 📊 Graphical charts
- 📈 Expense analytics dashboard
- 🔐 User authentication
- ☁️ Cloud database
- 🗄️ MySQL/PostgreSQL support
- 📄 PDF financial reports
- 📧 Email reports
- 📅 Recurring transactions
- 🔔 Budget notifications
- 📤 Excel export
- 🔎 Advanced transaction search
- 🌙 Dark mode

---

# 🤝 Contributing

Contributions are welcome!

### 1. Fork the repository

### 2. Create a feature branch

```bash
git checkout -b feature/new-feature
```

### 3. Make your changes

### 4. Commit

```bash
git add .
git commit -m "Add new feature"
```

### 5. Push

```bash
git push origin feature/new-feature
```

### 6. Create a Pull Request

---

# 👨‍💻 Author

**Siddharth Gajbhare**

GitHub:

```text
https://github.com/siddharthgajbhare
```

---

# 📄 License

This project is developed for **educational and personal learning purposes**.

---

## ⭐ Support

If you found this project useful, please consider giving the repository a ⭐ on GitHub.

**Made with Java ☕ and good financial habits 💰**
