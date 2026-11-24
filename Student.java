/**
 * Student: represents ONE student.
 * Students will wait in a Queue, and later be stored and sorted in an ArrayList.
 */
public class Student {

    private int id;
    private String name;
    private int score;   // starts at 0, updated after the exam
    private int total;   // number of questions in the exam

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // Used when we print a student object
    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Score: " + score;
    }
}
