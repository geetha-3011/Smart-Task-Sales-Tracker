# Smart Task Sales Tracker

A **Java-based desktop application** that automates sales and task tracking, provides real-time summaries, and improves business efficiency through smart data management.

---

## 📌 Overview

The **Smart Task Sales Tracker** is designed to simplify and automate sales management processes. It allows businesses to record, monitor, and analyze sales and task performance efficiently — reducing manual effort and enabling better decision-making.

This project integrates **Java Swing for the user interface** and **JDBC with MySQL** for backend data handling. It demonstrates the use of **object-oriented programming principles**, **data persistence**, and **modular design** to create a professional-level desktop application.

---

## 🎯 Objectives

- Automate daily sales and task management  
- Provide summarized insights for decision-making  
- Reduce manual errors and improve productivity  
- Enable managers to easily track team performance  

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-------------|----------|
| **Java (JDK 17+)** | Core programming language used for logic and GUI |
| **Java Swing** | For building a responsive and interactive desktop interface |
| **JDBC (Java Database Connectivity)** | For connecting and interacting with the MySQL database |
| **MySQL** | Backend database for storing user, task, and sales data |
| **MySQL Connector JAR** | JDBC driver for database connectivity |
| **VS Code / IntelliJ IDEA** | IDE for development and debugging |

---

## 📂 Project Structure

## 📂 Project Structure

```
SMART-TASK-SALES-TRACKER/
│
├── lib/
│   └── mysql-connector-j-9.4.0.jar        # JDBC driver for MySQL connection
│
├── out/                                   # Compiled .class files
│   ├── JDBCUtils.class
│   ├── Sale.class
│   ├── SaleDAO.class
│   ├── SmartTaskSalesJDBC.class
│   ├── Task.class
│   ├── TaskDAO.class
│   ├── User.class
│   └── UserDAO.class
│
├── src/                                   # Source code files
│   └── SmartTaskSalesJDBC.java
│
└── README.md                              # Project documentation
```
---

## ⚙️ Features

- User management (add and view users)  
- Task management (assign, update, and track tasks)  
- Sales management (record and analyze sales data)  
- Automated summary generation  
- MySQL database connectivity via JDBC  
- Simple, intuitive GUI built using Swing  

---

## 🚀 How It Works

1. The user interacts with the **Java Swing GUI** to input or view data.  
2. Data is sent through **JDBC** to the MySQL database for storage.  
3. The system processes and retrieves sales and task records dynamically.  
4. Real-time summaries are displayed for performance analysis and reporting.  

---

## 🔒 Object-Oriented Concepts Used

- **Encapsulation:** Private fields with getters and setters in classes like `Sale`, `Task`, and `User`.  
- **Abstraction:** DAO (Data Access Object) pattern separates logic and database operations.  
- **Polymorphism:** Overloaded methods for flexible data handling.  
- **Modularity:** Organized code structure for easier maintenance and scalability.  

---

## 💡 Future Enhancements

- Add authentication and role-based access  
- Integrate graphical reports and dashboards  
- Enable data export (Excel/PDF)  
- Add email/SMS notifications for task deadlines  

---

## 📈 Business Value

This project can help small and medium businesses:

- Streamline their sales tracking  
- Improve accuracy in data handling  
- Save time through automation  
- Enhance managerial decision-making  

---

## 👩‍💻 Author

**GeethaLakshmi. T**  
*Final Year B.Tech (Information Technology)*  

📧 **Email:** geethalakshmi0399@gmail.com  
🔗 **LinkedIn:** [linkedin.com/in/geethalakshmi3011](https://www.linkedin.com/in/geethalakshmi3011)  
💼 **GitHub:** [github.com/geetha-3011](https://github.com/geetha-3011)  

---

## 📜 License
This project is licensed under the MIT License. You are free to use and modify it with proper attribution.
