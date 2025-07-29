# 🚆 CLI-Based Railway Ticket Booking System

A Java-based Command-Line Application to book train tickets with full support for user registration, login, seat selection, and ticket booking — all with file-based JSON persistence.

## ✨ Features

- 🔐 User Registration & Login (passwords hashed for security)
- 🚄 Train Search & Seat Availability
- 🪑 Seat Selection and Booking
- 🧾 Ticket Booking History per User
- 📂 JSON-based file storage (no database needed)
- 📅 Travel Date & Route Handling
- 🧪 Robust CLI input handling and validation

## 📁 Technologies Used

- Java (OOP)
- Jackson Library (for JSON parsing)
- File I/O for persistence
- CLI (Command-Line Interface)

## 🧰 Project Structure

```
ticket-booking-system/
├── src/
│   ├── ticket.booking.entities/    # Models: User, Ticket, Train
│   ├── ticket.booking.service/     # Core logic (e.g., TrainService)
│   └── ticket.booking/             # Entry point & CLI menus
├── trains.json                     # Stores train data
├── users.json                      # Stores user data
└── README.md
```

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/cli-ticket-booking.git
   ```

2. **Compile and Run the App**
   ```bash
   cd cli-ticket-booking
   javac -cp ".;lib/*" src/ticket/booking/Main.java
   java -cp ".;lib/*;src" ticket.booking.Main
   ```

> Make sure to add Jackson JARs to the `lib/` folder or use Maven to manage dependencies.

## 🔐 Security

- Passwords are hashed using **BCrypt**
- Sensitive fields (like raw password) are ignored during JSON serialization

## 📌 Future Improvements

- ✅ Add database (e.g., MySQL/PostgreSQL) support
- 🌐 Convert to a Web-based UI using Spring Boot or React
- 📅 Add booking cancellation and modification features

---

