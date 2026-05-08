# 🚀 Elevator System Design using Multithreading in Java

## 📝 Project Overview
This project implements an **Elevator System** using **multithreading** and other advanced Java features to simulate the functionality of multiple elevators operating in a building. It handles concurrent requests, efficient resource management, and realistic elevator behavior.

---

## 🌟 Features
- **Multi-threaded Processing:** Concurrent handling of elevator requests using Java threads.
- **Optimized Scheduling Algorithms:** Implements algorithms like **FCFS (First Come First Serve)** and **Look Scheduling** for efficient operation.
- **Realistic Simulation:** Simulates multiple elevators with configurable speed, capacity, and floors.
- **Thread-Safe Design:** Ensures proper synchronization using locks and thread-safe collections.
- **Customizable Parameters:** Easily configure the number of elevators, building floors, request frequency, and more.
- **Advanced Java Concepts:**
  - **Thread Pools** for managing threads efficiently.
  - **Synchronization** and **Locks** for managing shared resources.
  - **Executor Framework** for handling tasks.
  - **Java 8 Streams** for clean and efficient data processing.

---

## 🏗️ System Architecture
The system consists of the following components:

1. **Elevator Class**  
   Represents an individual elevator, tracking its current floor, status, and assigned requests.

2. **Request Handler**  
   Manages and queues incoming requests, distributing them among elevators.

3. **Scheduler**  
   Implements scheduling algorithms to decide how requests are assigned to elevators.

4. **Simulation Engine**  
   Orchestrates the entire simulation, generating requests and monitoring elevator operations.

---

## 🔧 Technology Stack
- **Language:** Java 17+
- **Libraries:**  
  - `java.util.concurrent` for multithreading and synchronization.
  - Java 8 features like Streams and Lambda expressions for cleaner code.
- **Build Tool:** Maven or Gradle (choose as per your preference).

---

## ⚙️ Setup & Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/VanshAngaria/Elevator-System-Design.git
