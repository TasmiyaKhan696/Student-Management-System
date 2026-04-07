# Student-Management-System
# Student Management System (Java + JDBC)

## 📌 Project Description

A Java-based Student Management System that demonstrates Object-Oriented Programming concepts and database connectivity using JDBC. The application allows users to manage student records through a simple graphical user interface.

---

## 🚀 Features

* Add Student
* View Students
* Store data in MySQL database
* Retrieve data using JDBC
* Simple GUI using Swing

---

## 🧠 OOP Concepts Used

* **Classes & Objects**
* **Encapsulation**
* **Inheritance** (`Student` extends `Person`)
* **Abstraction** (abstract class and interface)
* **Polymorphism** (method overriding)

---

## 🏗️ Project Structure

model/

* Person.java
* Student.java
* Manageable.java

dao/

* DBConnection.java
* StudentDAO.java

ui/

* MainFrame.java
* StudentForm.java
* StudentTable.java

Main.java
schema.sql

---

## 🗄️ Database

* MySQL database
* Table: `students`
* CRUD operations implemented using JDBC

---

## ⚙️ Technologies Used

* Java
* MySQL
* JDBC
* Swing (GUI)
* Git

---

## ▶️ How to Run

1. Install MySQL
2. Run `schema.sql`
3. Update database credentials in `DBConnection.java`
4. Add MySQL Connector JAR
5. Run `Main.java`

---

## 📌 Conclusion

This project demonstrates how Java OOP principles can be combined with database operations to build a functional and modular application.

---
