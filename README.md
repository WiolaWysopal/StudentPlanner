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

* Mark tasks as completed from the main task list

* Mark tasks as completed from the task details screen

* Persist task completion status using Room Database

* Visually distinguish completed tasks with a strikethrough title

* Sort tasks by due date

* Sort tasks from newest to oldest

* Sort tasks from oldest to newest

* Filter tasks by completion status

* Display all, active, or completed tasks

* Filter tasks by subject

* Display subjects dynamically based on existing tasks

* Display all subjects or tasks from a selected subject

* Filter tasks by due date

* Display tasks due today

* Display upcoming tasks

* Display overdue tasks

* Display tasks without a due date

* Combine due date filtering with subject and completion status filtering

* Combine task filtering with sorting options

* Keep the selected filtering and sorting options when returning from the task details screen

* Preserve the selected subject, due date filter, completion status filter, and sorting option when the main activity is recreated

* Display tasks in a RecyclerView

* Delete tasks with a confirmation dialog

* Store tasks locally using Room Database

* Load individual task details from the database

* Restore saved tasks and completion status after restarting the application

* Automatically refresh task data after editing

* Display an empty state when no tasks match the selected filters

* Validate task titles before saving

## 🔮 Planned Features

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

The core task management flow is implemented, including task creation, task details, editing, deletion, local persistence, subject assignment, task completion, task sorting, completion status filtering, subject filtering, due date filtering, and task display.

The application currently supports the basic CRUD operations for tasks:

* **Create** - add new tasks with a title, optional subject, description, and due date

* **Read** - display saved tasks and their subjects, and open a dedicated task details screen

* **Update** - edit task titles, subjects, descriptions, and due dates, and update task completion status

* **Delete** - remove tasks with confirmation

Task details are loaded from Room Database using the task ID. Changes made during editing are persisted locally and immediately reflected on the task details screen.

Tasks can include an optional subject, description, and due date. Subjects are displayed in both the main task list and the task details screen. When optional task details are not provided, the task details screen displays appropriate placeholders.

Tasks can be marked as completed from both the main task list and the task details screen. Completion status is persisted in Room Database and restored after restarting the application. Completed tasks are visually distinguished in the main task list with a strikethrough title.

Tasks can be sorted directly from the main screen using the **Sort** button. The application supports sorting tasks by due date, from newest to oldest, and from oldest to newest. Tasks without a due date are placed after tasks with due dates when sorting by due date.

Tasks can be filtered by completion status directly from the main screen using the **Filter** button. The application supports displaying all tasks, only active tasks, or only completed tasks.

Tasks can also be filtered by subject using the **Subject** button. Available subjects are generated dynamically from the subjects assigned to existing tasks. Users can display tasks from a selected subject or return to **All subjects**.

Tasks can also be filtered by due date using the **Date** button. The application supports displaying all tasks regardless of due date, tasks due today, upcoming tasks, overdue tasks, or tasks without a due date.

Due date filtering works together with subject filtering, completion status filtering, and the existing sorting options. For example, users can display only active overdue tasks from a selected subject while keeping the selected sorting option.

When a task's completion status changes while the Active or Completed filter is selected, the task list is automatically refreshed to reflect the selected filter. The selected subject, due date filter, completion status filter, and sorting option are preserved when the main activity is recreated.

Further development will focus on reminders for upcoming deadlines.

## 📋 Requirements

* Android 7.0 (API 24) or newer

* Android Studio

## 🚀 Development

The project follows a feature-based Git workflow. New functionality is developed on separate branches and merged into `main` through pull requests.