# 📅 Student Planner

Student Planner is an Android application designed to help students organize their academic tasks, deadlines, and subjects.

The project is being developed as a practical way to learn Android application development using Java and Android Studio.

## 🛠️ Current Features

- Create tasks
- Edit existing tasks
- Display tasks in a RecyclerView
- Delete tasks with a confirmation dialog
- Store tasks locally using Room Database
- Restore saved tasks after restarting the application
- Display an empty state when no tasks are available
- Validate task titles before saving

## 🔮 Planned Features

- Assign tasks to subjects
- Set deadlines for assignments and exams
- Mark tasks as completed
- Organize tasks by subject and due date
- Display reminders for upcoming deadlines

## ⚙️ Tech Stack

- Java
- Android SDK
- XML
- Android Studio
- Gradle
- AndroidX
- RecyclerView
- Room Database

## 📊 Project Status

🚧 **In development**

The core task management flow is implemented, including task creation, editing, deletion, local persistence, and task display.

The application currently supports the basic CRUD operations for tasks:

- **Create** — add new tasks
- **Read** — display saved tasks
- **Update** — edit existing tasks
- **Delete** — remove tasks with confirmation

Further development will focus on expanding task details, adding subjects and deadlines, implementing task completion, and adding reminders.

## 📋 Requirements

- Android 7.0 (API 24) or newer
- Android Studio

## 🚀 Development

The project follows a feature-based Git workflow. New functionality is developed on separate branches and merged into `main` through pull requests.