# 📅 Student Planner

Student Planner is an Android application designed to help students organize their academic tasks, deadlines, and subjects.

The project is being developed as a practical way to learn Android application development using Java and Android Studio.

## 🛠️ Current Features

* Create tasks with a title, optional subject, description, and due date
* View task details on a dedicated screen
* Display task title, subject, description, and due date
* Display placeholders when optional task details are not provided
* Edit existing tasks from the task details screen
* Update task title, subject, description, and due date
* Display task subjects in the main task list
* Display tasks in a RecyclerView
* Delete tasks with a confirmation dialog
* Store tasks locally using Room Database
* Load individual task details from the database
* Restore saved tasks after restarting the application
* Automatically refresh task data after editing
* Display an empty state when no tasks are available
* Validate task titles before saving

## 🔮 Planned Features

* Mark tasks as completed
* Organize tasks by subject and due date
* Add sorting and filtering options
* Display reminders for upcoming deadlines

## ⚙️ Tech Stack

* Java
* Android SDK
* XML
* Android Studio
* Gradle
* AndroidX
* RecyclerView
* Room Database

## 📊 Project Status

🚧 **In development**

The core task management flow is implemented, including task creation, task details, editing, deletion, local persistence, subject assignment, and task display.

The application currently supports the basic CRUD operations for tasks:

* **Create** - add new tasks with a title, optional subject, description, and due date
* **Read** - display saved tasks and their subjects, and open a dedicated task details screen
* **Update** - edit task titles, subjects, descriptions, and due dates from the task details screen
* **Delete** - remove tasks with confirmation

Task details are loaded from Room Database using the task ID. Changes made during editing are persisted locally and immediately reflected on the task details screen.

Tasks can include an optional subject, description, and due date. Subjects are displayed in both the main task list and the task details screen. When optional task details are not provided, the task details screen displays appropriate placeholders.

Further development will focus on task completion, organizing tasks by subject and due date, sorting and filtering, and reminders for upcoming deadlines.

## 📋 Requirements

* Android 7.0 (API 24) or newer
* Android Studio

## 🚀 Development

The project follows a feature-based Git workflow. New functionality is developed on separate branches and merged into `main` through pull requests.
