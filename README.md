# 💰 Budget Tracker

A simple **Java-based Budget Tracker** application that helps users manage their personal finances by recording income and expenses and calculating the current balance.

---

## 📖 Overview

The Budget Tracker is a console-based Java application that allows users to:

- Add income
- Add expenses
- Track current balance
- View all income records
- View all expense records

This project demonstrates the use of Java Collections (`ArrayList`), Object-Oriented Programming (OOP), and basic financial calculations.

---

## ✨ Features

- ➕ Add income
- ➖ Add expenses
- 💵 Automatically update balance
- 📋 Display all income records
- 🧾 Display all expense records
- 🏦 View current account balance
- 🧑‍💻 Simple and beginner-friendly code

---

## 🛠 Technologies Used

- Java
- ArrayList
- Object-Oriented Programming (OOP)
- Console Application

---

## 📂 Project Structure

```
Budget-Tracker/
│
├── BudgetTracker.java
├── Main.java          (Optional)
└── README.md
```

---

## ⚙️ How It Works

1. Create a `BudgetTracker` object.
2. Add income using `addIncome()`.
3. Add expenses using `addExpense()`.
4. Check the current balance using `getBalance()`.
5. Print all income and expense records.

---

## 📌 Methods

| Method | Description |
|---------|-------------|
| `addIncome(double income)` | Adds income and updates balance |
| `addExpense(double expense)` | Adds an expense and deducts it from balance |
| `getBalance()` | Returns the current balance |
| `printIncomes()` | Displays all recorded incomes |
| `printExpenses()` | Displays all recorded expenses |

---

## 🚀 Example Usage

```java
public class Main {
    public static void main(String[] args) {
        BudgetTracker tracker = new BudgetTracker();

        tracker.addIncome(5000);
        tracker.addIncome(2000);

        tracker.addExpense(1500);
        tracker.addExpense(800);

        tracker.printIncomes();
        tracker.printExpenses();

        System.out.println("Current Balance: ₹" + tracker.getBalance());
    }
}
```

### Sample Output

```
Incomes:
5000.0
2000.0

Expenses:
1500.0
800.0

Current Balance: ₹4700.0
```

---

## 📈 Future Enhancements

- Save data to a file or database
- Expense categories
- Monthly budget limits
- Income and expense history
- Edit/Delete transactions
- GUI using Java Swing or JavaFX
- Charts and reports
- Export to CSV or PDF

---

## 🤝 Contributing

Contributions are welcome!

1. Fork this repository.
2. Create a new branch.

```bash
git checkout -b feature-name
```

3. Commit your changes.

```bash
git commit -m "Add new feature"
```

4. Push to GitHub.

```bash
git push origin feature-name
```

5. Open a Pull Request.

---

## 📄 License
--siddharth gajbhare--

---

## 👨‍💻 Author

**Siddharth Gajbhare**

GitHub: https://github.com/siddharthgajbhare

---

⭐ If you found this project helpful, please give it a **Star** on GitHub!
