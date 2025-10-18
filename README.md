
# ♟️Java Chess Application

A real-time multiplayer Chess game built in Java, featuring a complete Swing-based GUI, a custom socket-driven network framework, and a multithreaded, pull-based communication model
# 🧩 Project Overview

This project is a client-server Chess system that allows two players to play over a network. It uses a custom Java networking framework (nframework) to handle multiple clients concurrently, ensuring efficient communication and proper synchronization between game states.

# 🚀 Features
 ### ♟ Chess Mechanics

Implements Check, Checkmate, and Stalemate detection

Valid move detection for all chess pieces

2D array board structure (String[][] board)

### 🌐 Custom Network Framework

Built with Java Sockets Programming and Java Annotations

Modular architecture: client, server, and common packages

Lightweight message protocol for reliable communication

### 🔁 Pull-Based Update System

Clients periodically pull the latest game state using timers

Prevents unnecessary network flooding

Ensures synchronized board states between players

### 🧵 Multithreading

Each client connection is handled on a separate thread

Scalable — multiple clients can connect simultaneously

Non-blocking I/O for smooth gameplay

### ⚡ Quick Execution

.bat files (startup.bat and clientstart.bat) to simplify launching

Uses gradle build tool

# Folder Structure
```
ChessApp/
│
├── ChessClient/           # Client-side logic and GUI (Swing + AWT)
├── ChessCommon/           # Shared classes, constants, and models
├── ChessServer/           # Server-side logic and game coordination
│
├── nframework/            # Custom Networking Framework
│   ├── client/            # Client-side network handling
│   ├── common/            # Common message/protocol structures
│   └── server/            # Server-side networking and threading logic
│
├── utils/                 # Helper utilities (logging, configuration, etc.)
├── learn/                 # Experimental/test code
├── startup.bat          # Batch file to launch the server
├── clientstart.bat          # Batch file to launch the client
└── .gitignore
```

# Technology Used 

| Component      | Technology                      |
| -------------- | ------------------------------- |
| Language       | Java                            |
| GUI            | Swing, AWT                      |
| Networking     | Custom Socket Framework         |
| Architecture   | Client–Server                   |
| Communication  | Pull Mechanism                  |
| Multithreading | Java Threads                    |
| OS Support     | Windows (with `.bat` execution) |

# Getting Started

Clone this repository to your local machine:

```bash
git clone https://github.com/BrajRaj89/Chess-Application-Java.git

```
## Running the Game

```
1. Navigate to the project folder:
     cd ChessApp

2. Navigate Server directory and Start Server
     cd ChessServer
     Compile - use gradle build command
     then use - startup command to start 

3. Navigate Client directory and Start Client
     cd ChessClient
     Compile - use gradle build command
     then use - clientstart command to start 

```
## How to Play

Player 1 and Player 2 can connect and play in real-time.

The game supports Check, Checkmate, and Stalemate detection.

