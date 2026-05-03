# Java Database Simulator

Java-based mini SQL database simulator with a JavaFX graphical user interface.

## Overview

Java Database Simulator is a desktop application that simulates basic relational database behavior.  
The project includes a custom in-memory database model, SQL-like query execution, table visualization, and file-based persistence.

The goal of the project was to better understand how database systems work internally, including table structures, records, query parsing, query execution, and saving/loading database state.

## Key Features

- JavaFX graphical user interface
- In-memory database representation
- SQL-like query execution
- Table creation and data manipulation
- Display of database tables and query results
- Support for executing multiple queries separated by semicolons
- File-based save and load functionality
- Custom SQL-like persistence format

## Technologies

- Java
- JavaFX
- Object-Oriented Programming
- Custom query parsing
- File I/O

## Functional Scope

The application supports basic database operations through a SQL-like editor.  
Users can write queries, execute them, view results, inspect available tables, and save or load database content from a file.

The GUI is divided into:
- table list panel
- selected table preview
- SQL query input area
- query result table
- save/load controls

## Architecture

The project is organized around several core components:

- `Database` - represents the database and stores tables
- `Table` - represents a table with columns and records
- `Record` - represents a single row of data
- `Statement` - parses and executes SQL-like commands
- `Result` / `Data` - represent query execution results
- `SQLFormat` - handles saving and loading database content
- `MainApp` - JavaFX entry point and graphical user interface

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/bucalonina/java-database-simulator.git
