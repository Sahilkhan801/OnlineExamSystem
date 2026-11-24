# Online Examination System

A desktop Online Examination System built in **Java (Swing)** as a college project, to demonstrate core Data Structures and Algorithms: **ArrayList, Queue, Stack and Sorting**.

## Features
- **Admin:** login, view / add / delete questions, view the rank list
- **Student:** register, wait in a line, take a timed exam, see the score
- Exam window with Next / Previous buttons and a question navigator
- Rank list of all students, highest score first

## Data Structures and Algorithms Used
| Concept | Where it is used | Why |
|---|---|---|
| ArrayList | Question bank, registered students, exam results | Size changes at runtime; access by index |
| Queue | Waiting line of students (`offer`, `poll`, `peek`) | First come, first served (FIFO) |
| Stack | "Previous" button in the exam (`push`, `pop`) | Returns to the most recently visited question (LIFO) |
| Sorting (Bubble Sort) | Rank list sorted by score, highest first | Turns raw results into a leaderboard |

## How to Run
1. Install JDK 17 or higher.
2. Clone this repository.
3. Compile and run:
   ```
   javac *.java
   java Main
   ```

## How to Use
1. **Admin Login** (password: `admin123`): add or delete questions.
2. **Student Portal:** register one or more students (they join the queue).
3. Click **Call Next Student and Start Exam**: the first student in the line takes the exam.
4. Submit the exam to see the score; click **View Rank List** to see the ranking.

## Project Structure
- `Main.java` - starts the program
- `ExamSystem.java` - stores the data and holds the ArrayLists, Queue and Bubble Sort
- `Question.java`, `Student.java` - data classes
- `LoginFrame.java` - first window (Admin or Student)
- `AdminFrame.java` - question bank management
- `StudentFrame.java` - registration and waiting line
- `ExamFrame.java` - the exam window (uses the Stack)
- `ResultFrame.java` - rank list
# OnlineExamSystem
