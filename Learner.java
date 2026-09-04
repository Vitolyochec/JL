import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class Learner extends JFrame {

    public static int WIDTH = 1000;
    public static int HEIGHT = 700;
    public static Font font = new Font("Courier New", Font.PLAIN, 50);
    public static ArrayList<String> questions = new ArrayList<>();
    public static ArrayList<String> answers = new ArrayList<>();
    public static HashSet<Integer> mistakenQuestions = new HashSet<>();
    public static ArrayList<Integer> activeQuestionsIndexes = new ArrayList<>();
    
    public static int questionIndex = 0;
    public static Learner frame = new Learner();
    public static myPanel panel;
    public static String currentTestName;
    public static String testTitle;

    public static void loadTest(String testname) {
        
        try (Scanner scanner = new Scanner(new File("tests/" + testname.strip() + ".txt"))) {
            questionIndex = 0;
            currentTestName = testname;
            testTitle = scanner.nextLine();
            while (scanner.hasNextLine()) {
                String question = scanner.nextLine();
                String answer = scanner.nextLine();
                questions.add(question);
                answers.add(answer);
            }
        } catch (Exception e) {
            System.out.println("[Something went wrong while reading the test file!]\n >\t" + e);
        }

        for (int i = 0; i < questions.size(); i++)
            activeQuestionsIndexes.add(i);
        
    }

    public static void saveResult() {
        try (FileWriter file = new FileWriter("results/" + currentTestName + "_result.txt")) {
            for (int i : mistakenQuestions) {
                file.write("Q: " + questions.get(i) + "\n");
                file.write("A: " + answers.get(i) + "\n");
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
    public InfoLabel infoLabel = new InfoLabel();

    public myPanel() {
        super();
        repaint();
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null);
        
        add(question);
        add(answerField);
        add(infoLabel);

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
        if (Learner.activeQuestionsIndexes.size() != 0) {
            Learner.questionIndex = Learner.activeQuestionsIndexes.get(random.nextInt(Learner.activeQuestionsIndexes.size()));
            setText(Learner.questions.get(Learner.questionIndex));
        } else {
            setText("[ENTER TEST NAME]");
        }
    }

}

class InfoLabel extends JLabel {
    
    public InfoLabel() {
        super();
        setFont(Learner.font);
        setForeground(Color.WHITE);
    }

    public void updateInfo() {
        setText(String.format(
            "<html>\"%s\"<br>&gt[%d/%d] (%d%%)&lt</html>", 
            Learner.testTitle,
            Learner.questions.size() - Learner.activeQuestionsIndexes.size(),
            Learner.questions.size(),
            (int) Math.ceil((1.d - (double) Learner.activeQuestionsIndexes.size() / (double) Learner.questions.size()) * 100.d)
        ));
        setBounds(0, 0, getPreferredSize().width, getPreferredSize().height);
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
        int width = Learner.WIDTH / 2 + Learner.WIDTH / 4;
        setBounds(
            (Learner.WIDTH - width) / 2,
            Learner.HEIGHT - Learner.HEIGHT / 3,
            width,
            getPreferredSize().height
        );
    }

    public void checkAnswer() {
        if (getText().strip().equals(Learner.answers.get(Learner.questionIndex))) {
            if (getForeground() != Color.RED) {
                Learner.activeQuestionsIndexes.remove(Learner.activeQuestionsIndexes.indexOf(Learner.questionIndex));
                if (Learner.activeQuestionsIndexes.size() == 0)
                    Learner.saveResult();
            }
            Learner.panel.question.nextQuestion();
            setForeground(Color.WHITE);
            setText(""); // clearing the text field
        } else {
            setForeground(Color.RED);
            setText(Learner.answers.get(Learner.questionIndex));
            Learner.mistakenQuestions.add(Learner.questionIndex);

        }
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getExtendedKeyCode() == 10) {
            if (Learner.activeQuestionsIndexes.size() == 0) { // loading test
                if (!getText().isBlank()) {
                    Learner.loadTest(getText());
                    Learner.panel.question.nextQuestion();
                    // Learner.panel.question.setText(Learner.questions.get(Learner.questionIndex));
                    setText("");
                }
            } else {
                checkAnswer();
            }
            Learner.panel.infoLabel.updateInfo();
        }
        
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

}