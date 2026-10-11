//=========Libraries======
// javax.swing .* - Ito yung main GUI library natin. Dito galing ang JFrame (window),
// JButton, JTextField, JPanel, KeyStroke, at iba pang visual components.
import javax.swing .*;
import java.awt .*;
// ActionEvent - This is the "event" object na pinapasa kpag may action na nangyari,
// halimbawa kapag pinindot ang isang button o key.
import java.awt.event.ActionEvent;
// KeyEvent - With list ng constants para sa bwat key sa keyboard
// (VK_ENTER, VK_ESCAPE, VK_BACK_SPACE, etc.). This is needed para sa keyboard support.
import java.awt.event.KeyEvent;
// BigDecimal - Para sa precise na decimal numbers. Ginagamit natin ito para
// ayusin ang display, para ang 0.1 + 0.2 ay "0.3" at hindi "0.30000000000000004".
import java.math.BigDecimal;
// MathContext - Sinasabi nito kay BigDecimal kung ilang digits ang i-keep
// kapag nagraround (into, 12 significant digits).
import java.math.MathContext;




//======CALCULATOR CLASS======
//Ito na yung buong calcualtor app

public class Calculator {
    private static final Font FONT = new Font("Segoe UI", Font.BOLD, 22); //font of buttons and display
    private static final Color BACKGROUND = new Color(240, 240, 240); //color of window background
    private static final Color FUNCTION_BG = new Color(220, 220, 220); //Opertor button's color background
    private static final String ERROR = "Error"; //pop-up text when an error occurs
    private static final MathContext DISPLAY_PRECISION = new MathContext(12); //12 digit only will shown
    private static final int MAX_DISPLAY_LENGTH = 12;
     

    //=======DISPLAY=========   
    //The text in the upper part, where the numbers and result are shown
    private final JTextField display = new JTextField();

    // calculator state (calculator's memory)
    private double accumulator = 0;
    private char pendingOp = 0;
    private boolean startNewNumber = true;
    private boolean error = false;

    //======CONSTRUCTOR========
    //Here we set up the whole window, such as display, button, and keyboard support
    

    //======CONSTRUCTOR========
    //Here we set up the whole window, such as display, button, and keyboard support
    public Calculator() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 550);
        frame.setResizable(false);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(BACKGROUND);

        //======DISPLAY SET-UP========
        display.setBounds(50, 25, 300, 60);
        display.setFont(FONT);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(Color.WHITE);
        display.setFocusable(false);

        //======MAIN KEYPAD========
        JPanel keypad = new JPanel(new GridLayout(4, 4, 10, 10));
        keypad.setBounds(50, 100, 300, 300);
        keypad.setBackground(BACKGROUND);

        //labels of button in order from the standard Numpad layout
        String[] keys = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            ".", "0", "=", "+" 
        };

        //Root Pane....the windows base
        JRootPane root = frame.getRootPane();
        for(String key : keys) {
            char c = key.charAt(0);
            JButton button;

            if(Character.isDigit(c)){
                button = makeButton(key, Color.WHITE, () -> appendDigit(c));
            } else if (c == '.') {
                button = makeButton(key, FUNCTION_BG, this::appendDecimal);
                bindKey(root, KeyStroke.getKeyStroke(','), button);
            } else if (c == '=') {
                button = makeButton(key, FUNCTION_BG, this::calculateResult);
                bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), button);
            } else {
                button = makeButton(key, FUNCTION_BG, () -> setOperator(c));
            }
            bindKey(root, KeyStroke.getKeyStroke(c), button);
            keypad.add(button);
        }
        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 10, 0));
        bottomRow.setBounds(50, 415, 300, 60);
        bottomRow.setBackground(BACKGROUND);

        JButton negButton = makeButton("(-)", FUNCTION_BG, this::negate);
        JButton delButton = makeButton("(Del)", FUNCTION_BG, this::delete);
        JButton clrButton = makeButton("(Clr)", FUNCTION_BG, this::clear);
        bottomRow.add(negButton);
        bottomRow.add(delButton);
        bottomRow.add(clrButton);

        //====KEYBOARD SHORTCUT FOR THE BOTTOM ROW=======
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), negButton);
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), delButton);
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), clrButton);
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), clrButton);

        //place all in the window and show it all//
        frame.add(display);
        frame.add(keypad);
        frame.add(bottomRow);
        frame.setVisible(true);
    }

    //=====HELPER METHODS FOR THE GUI======
    //makeButton...can work in one button in same style
    //so we cannot repeat to make button for each button interface
    //Runnable action....this code can run once the button is pressed
    private JButton makeButton(String label, Color background, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(FONT);
        button.setFocusable(false);
        button.setBackground(background);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void bindKey(JComponent root, KeyStroke keyStroke, JButton button) {
        String actionKey = "press" + keyStroke;
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        root.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick(50);
            }
        });
    }

    private void appendDigit(char digit) {
        if (error || startNewNumber) {
            display.setText("");
            error = false;
            startNewNumber = false;
        }

        String text = display.getText();
        if (text.length() >= MAX_DISPLAY_LENGTH) return;
        if (text.equals("0")) text = "";
        else if (text.equals("-0")) text = "-";

        display.setText(text + digit);
    }

        private void appendDecimal() {
            if (error || startNewNumber) {
                display.setText("0.");
                error = false;
                startNewNumber = false;
                return;
            }
            String text = display.getText();
            if (text.contains(".")) return;
            display.setText(text.isEmpty() ? "0." : text + ".");
        }

        private void setOperator(char op) {
            if (error || display.getText().isEmpty()) return;
            if (!startNewNumber && !applyPending(currentValue())) return;

            pendingOp = op;
            startNewNumber = true;
        }

        private void calculateResult(){
            if (error || pendingOp == 0 || startNewNumber || display.getText().isEmpty()) return;
            if (!applyPending(currentValue())) return;

            pendingOp = 0;
            startNewNumber = true;
        }

        private void clear() {
            display.setText("");
            accumulator = 0;
            pendingOp = 0;
            startNewNumber = true;
            error = false;
        }

        private void delete() {
            if (error) {
                clear();
                return;
            } if (startNewNumber) return;
            String text = display.getText();
            if (text.isEmpty()) return;

            text = text.substring(0, text.length() -1);
            if (text.equals("-")) text = "";
            display.setText(text);
        }

        private void negate() {
            if (error || display.getText().isEmpty()) return;
            if (startNewNumber) {
                accumulator = -accumulator;
                display.setText(format(accumulator));
            } else {
                String text = display.getText();
                display.setText(text.startsWith("-") ? text.substring(1) : "-" + text);
            }
        }

        ////////////////ARITHMETIC THE ACTUAL COMPUTATION///////////////
        private boolean applyPending(double rhs) {
            double result;
            switch (pendingOp) {
                case '+': result = accumulator + rhs; break;
                case '-': result = accumulator - rhs; break;
                case '*': result = accumulator * rhs; break;
                case '/':
                    if (rhs == 0) {
                        showError();
                        return false;
                    }
                    result = accumulator / rhs;
                    break;
                default: result = rhs;
            }

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                showError();
                return false;
            }
            accumulator = result;
            display.setText(format(result));
            return true;
        }
        
        private double currentValue() {
            return Double.parseDouble(display.getText());
        }

        private void showError() {
            display.setText(ERROR);
            error = true;
            accumulator = 0;
            pendingOp = 0;
            startNewNumber = true;
        }

        private static String format(double value) {
            if (value == 0) return "0";
            BigDecimal bd = new BigDecimal(value)
                        .round(DISPLAY_PRECISION)
                        .stripTrailingZeros();
            String plain = bd.toPlainString();
            return plain.length() <= MAX_DISPLAY_LENGTH ? plain : bd.toString();  
        }
        /////MAIN METHOD//////
        public static void main(String[] args) {
            SwingUtilities.invokeLater(Calculator::new);
        }
        

}