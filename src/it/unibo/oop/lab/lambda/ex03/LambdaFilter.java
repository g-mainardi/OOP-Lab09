package it.unibo.oop.lab.lambda.ex03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.function.Function;
import java.util.Arrays;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * 
 * 1) Convert to lowercase
 * 
 * 2) Count the number of chars
 * 
 * 3) Count the number of lines
 * 
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    private static final long serialVersionUID = 1760990730218643730L;

    private static final String ERROR_MSG = "Insert something to translate!";

    private enum Command {
        IDENTITY("No modifications", Function.identity()), 
        LOWER_CASE("Lower case", s -> s.lines()
                .map(line -> line.toLowerCase())
                .reduce((l1, l2) -> l1 + System.lineSeparator() + l2).orElse(ERROR_MSG)),
        CHARS_COUNT("Chars count", s -> Long.toString(s.chars().count())),
        LINES_COUNT("Lines count", s -> Long.toString(s.lines().count())),
        WORD_LIST("Word list", s -> s.lines()
                .flatMap(line  -> Arrays.asList(line.split(" ")).stream())
                .sorted()
                .reduce((w1, w2) -> w1 + System.lineSeparator() + w2).orElse(ERROR_MSG)),
        WORD_COUNT_LIST("Word count list", s -> WORD_LIST.translate(s).lines()
                .map(word -> word + " -> " + Long.toString(WORD_LIST.translate(s).lines()
                        .filter(w -> w.equals(word)).count()))
                .distinct()
                .reduce((w1, w2) -> w1 + System.lineSeparator() + w2).orElse(ERROR_MSG));

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            this.commandName = name;
            this.fun = process;
        }

        @Override
        public String toString() {
            return this.commandName;
        }

        public String translate(final String s) {
            return this.fun.apply(s);
        }
    }

    private LambdaFilter() {
        // Frame Initialization
        super("Lambda filter GUI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Panel Layout Setting
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);

        // Combo Component
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);

        // Central Panel Layout Setting
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));

        // Text Areas Setting
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);

        // Add Central Panel Components
        centralPanel.add(left);
        centralPanel.add(right);

        // Add Central to Main Panel
        panel1.add(centralPanel, BorderLayout.CENTER);

        // Apply Button Setting
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
        panel1.add(apply, BorderLayout.SOUTH);

        // Frame Setting
        this.setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        this.setSize(sw / 4, sh / 4);
        this.setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
