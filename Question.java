/**
 * Question: represents ONE multiple-choice question.
 * Objects of this class will be stored inside an ArrayList (question bank).
 */
public class Question {

    // Fields (the data every question holds)
    private String text;          // the question itself
    private String[] options;     // exactly 4 answer choices
    private int correctOption;    // the correct answer: 1, 2, 3 or 4

    // Constructor: runs when we create a new Question
    public Question(String text, String[] options, int correctOption) {
        this.text = text;
        this.options = options;
        this.correctOption = correctOption;
    }

    // Getters: let other classes read the private fields
    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    // Checks whether the student's answer is right
    public boolean isCorrect(int answer) {
        return answer == correctOption;
    }

    // Prints the question and its options on the screen
    public void display() {
        System.out.println(text);
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
    }
}
