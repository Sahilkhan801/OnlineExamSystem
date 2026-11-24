import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ExamSystem: the "brain" of the project. It stores all the data.
 * The GUI windows only show this data and call these methods.
 *
 * ArrayList -> question bank, registered students, and exam results.
 * Queue     -> the waiting line of students (first come, first served).
 * Sorting   -> Bubble Sort creates the rank list from the results.
 * (The Stack is used in ExamFrame for the "Previous" button.)
 */
public class ExamSystem {

    // ArrayList #1: the question bank. It grows when the admin adds a question.
    private ArrayList<Question> questionBank = new ArrayList<>();

    // ArrayList #2: every student who has registered (needed later for results)
    private ArrayList<Student> students = new ArrayList<>();

    // QUEUE: students waiting for their turn. LinkedList implements the Queue interface.
    private Queue<Student> waitingQueue = new LinkedList<>();

    // ArrayList #3: students who finished the exam (in the order they finished)
    private ArrayList<Student> results = new ArrayList<>();

    private int nextStudentId = 1;   // gives each student a unique ID

    public ExamSystem() {
        loadSampleQuestions();
    }

    // Pre-loads a few questions so the system is not empty on first run
    private void loadSampleQuestions() {
        addQuestion(new Question("Which data structure follows FIFO?",
                new String[]{"Stack", "Queue", "Array", "Tree"}, 2));
        addQuestion(new Question("Which data structure follows LIFO?",
                new String[]{"Queue", "Graph", "Stack", "Heap"}, 3));
        addQuestion(new Question("Which of these is a sorting algorithm?",
                new String[]{"Bubble Sort", "Binary Tree", "Linked List", "Hashing"}, 1));
        addQuestion(new Question("Which Java class gives a resizable array?",
                new String[]{"int[]", "ArrayList", "String", "Scanner"}, 2));
        addQuestion(new Question("What is the time complexity of Bubble Sort (worst case)?",
                new String[]{"O(n)", "O(log n)", "O(n^2)", "O(1)"}, 3));
    }

    // ---------- ArrayList operations ----------

    // Adds a question to the end of the list
    public void addQuestion(Question q) {
        questionBank.add(q);
    }

    // Removes the question at the given position. Returns true if it worked.
    public boolean deleteQuestion(int index) {
        if (index >= 0 && index < questionBank.size()) {
            questionBank.remove(index);
            return true;
        }
        return false;
    }

    public ArrayList<Question> getQuestionBank() {
        return questionBank;
    }

    public int getQuestionCount() {
        return questionBank.size();
    }

    // ---------- Queue operations ----------

    // Registers a new student. They are added to the ArrayList AND join the BACK of the queue.
    public Student registerStudent(String name) {
        Student s = new Student(nextStudentId++, name);
        students.add(s);
        waitingQueue.offer(s);      // offer() = enqueue (add at the back)
        return s;
    }

    // Takes the student at the FRONT of the line. Returns null if nobody is waiting.
    public Student callNextStudent() {
        return waitingQueue.poll();   // poll() = dequeue (remove from the front)
    }

    // Looks at the front student WITHOUT removing them. Returns null if the queue is empty.
    public Student peekNextStudent() {
        return waitingQueue.peek();   // peek() = look at the front
    }

    public int getQueueSize() {
        return waitingQueue.size();
    }

    public Queue<Student> getWaitingQueue() {
        return waitingQueue;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    // ---------- Results and Sorting ----------

    // Called when a student submits the exam
    public void addResult(Student s) {
        results.add(s);
    }

    public int getResultCount() {
        return results.size();
    }

    // Returns a SORTED COPY of the results (highest score first).
    // We sort a copy so the original "finished order" is not lost.
    public ArrayList<Student> getRankList() {
        ArrayList<Student> list = new ArrayList<>(results);
        bubbleSortByScoreDescending(list);
        return list;
    }

    // BUBBLE SORT: repeatedly compare neighbours and swap them if they are in the wrong order.
    // After each full pass, the smallest remaining score "bubbles" to the end.
    private void bubbleSortByScoreDescending(ArrayList<Student> list) {
        int n = list.size();
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - pass; j++) {
                // wrong order = left student has a LOWER score than the right student
                if (list.get(j).getScore() < list.get(j + 1).getScore()) {
                    Student temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;   // no swaps in a full pass = already sorted, stop early
            }
        }
    }
}
