import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class Learner extends JFrame {

    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    public static Font font = new Font("Calibri", Font.PLAIN, 40);
    public static ArrayList<String> originalQuestions = new ArrayList<>();
    public static ArrayList<String> originalAnswers = new ArrayList<>();
    // public static ArrayList<Integer> mistakenQuestions = new ArrayList<>(); 
    public static ArrayList<String> taskQuestions = new ArrayList<>();
    public static HashSet<Integer> mistakenQuestions = new HashSet<>();
    public static ArrayList<String> taskAnswers = new ArrayList<>();
    public static int questionIndex = 0;
    public static Learner frame = new Learner();
    public static myPanel panel;
    public static String currentTestName;

    public static void loadTest(String testname) {
        
        try (Scanner scanner = new Scanner(new File("tests/" + testname.strip() + ".txt"))) {
            questionIndex = 0;
            currentTestName = testname;
            while (scanner.hasNextLine()) {
                String question = scanner.nextLine();
                String answer = scanner.nextLine();
                taskQuestions.add(question);
                originalQuestions.add(question);
                taskAnswers.add(answer);
                originalAnswers.add(answer);
            }
        } catch (Exception e) {
            System.out.println("[Something went wrong while reading the test file!]\n >\t" + e);
        }
        
    }

    public static void saveResult() {
        try (FileWriter file = new FileWriter("results/" + currentTestName + "_result.txt")) {
            for (int i : mistakenQuestions) {
                file.write("Q: " + originalQuestions.get(i));
                file.write("A: " + originalAnswers.get(i));
            }
        } catch (Exception e) {
            System.out.println("[Something went wrong while writing results for the test \"" + currentTestName + "\"]\n >\t" + e);
        }
    }

    public static void main(String[] args) {
        System.out.println("[START]");

        frame.setTitle("Learner");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new myPanel();
        frame.add(panel);
        
        frame.setVisible(true);

    }
}

class myPanel extends JPanel {

    public QuestionLabel question = new QuestionLabel();
    public AnswerField answerField = new AnswerField();

    public myPanel() {
        super();
        repaint();
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null);
        
        add(question);
        add(answerField);

    }

}


class QuestionLabel extends JLabel {

    public QuestionLabel() {
        super();
        setFont(Learner.font);
        setForeground(Color.WHITE);
        setText("[ENTER TEST NAME]");
    }

    @Override
    public void setText(String newText) {
        super.setText(newText);
        setBounds(
            (Learner.WIDTH - getPreferredSize().width) / 2, 
            (Learner.HEIGHT - getPreferredSize().height) / 2 - 50, 
            getPreferredSize().width, 
            getPreferredSize().height
        );
    }

    public void nextQuestion() {
        Random random = new Random();
        Learner.questionIndex = random.nextInt(Learner.taskQuestions.size());
        // Learner.questionIndex = Learner.questions.size() == 0 ? 0 : (Learner.questionIndex + 1) % Learner.questions.size();
        setText(Learner.taskQuestions.size() == 0 ? "[ENTER TEST NAME]" : Learner.taskQuestions.get(Learner.questionIndex));
    }

}


class AnswerField extends JTextField implements KeyListener {

    public boolean answering = true;

    public AnswerField() {
        super();
        setFocusable(true);
        addKeyListener(this);

        // Style
        setFont(Learner.font);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setCaretColor(Color.LIGHT_GRAY);
        setHorizontalAlignment(JTextField.CENTER);

        // Size
        int width = Learner.WIDTH / 2;
        setBounds(
            (Learner.WIDTH - width) / 2,
            Learner.HEIGHT - Learner.HEIGHT / 3,
            width,
            getPreferredSize().height
        );


    }

    public void checkAnswer() {
        if (getText().strip().equals(Learner.taskAnswers.get(Learner.questionIndex))) {
            if (getForeground() != Color.RED) {
                Learner.taskQuestions.remove(Learner.questionIndex);
                Learner.taskAnswers.remove(Learner.questionIndex);
                if (Learner.taskQuestions.size() == 0)
                    Learner.saveResult();
            }
            Learner.panel.question.nextQuestion();
            setForeground(Color.WHITE);
            setText(""); // clearing the text field
        } else {
            setForeground(Color.RED);
            setText(Learner.taskAnswers.get(Learner.questionIndex));
            Learner.mistakenQuestions.add(Learner.questionIndex);

        }

    }

    

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getExtendedKeyCode() == 10) {
            if (Learner.taskQuestions.size() == 0) { // loading test
                if (!getText().isBlank()) {
                    Learner.loadTest(getText());
                    Learner.panel.question.setText(Learner.taskQuestions.get(Learner.questionIndex));
                    setText("");
                }
            } else {
                checkAnswer();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}