import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DiscussionForumUI implements ActionListener {
  JFrame frame;
  JPanel panel;
  JTextArea textArea;
  JTextField textField;
  JButton submitButton;
  JScrollPane scrollPane;

  public DiscussionForumUI() {
    frame = new JFrame("Discussion Forum");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 400);

    panel = new JPanel();
    panel.setLayout(new BorderLayout());

    textArea = new JTextArea();
    textArea.setEditable(false);
    scrollPane = new JScrollPane(textArea);
    panel.add(scrollPane, BorderLayout.CENTER);

    textField = new JTextField();
    textField.setPreferredSize(new Dimension(500, 30));
    panel.add(textField, BorderLayout.NORTH);

    submitButton = new JButton("Submit");
    submitButton.addActionListener(this);
    panel.add(submitButton, BorderLayout.EAST);

    frame.add(panel);
    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String message = textField.getText();
    if (!message.isEmpty()) {
      textArea.append(message + "\n");
      textField.setText("");
    }
  }

  public static void main(String[] args) {
    new DiscussionForumUI();
  }
}
