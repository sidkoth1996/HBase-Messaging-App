import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginScreen implements ActionListener {
		JFrame frame;
		JPanel panel;
		JButton loginButton;
		JTextField emailTextField;
		JLabel errorLabel;
		
		public LoginScreen() {
			frame = new JFrame("Please login to email application");
			panel = new JPanel();
			
			panel.setLayout(new FlowLayout()); // TODO: change to another layout
			JLabel loginLabel = new JLabel("Login email:");
			emailTextField = new JTextField("", 10);
			
			loginButton = new JButton("Login");
			
			errorLabel = new JLabel("Please enter email");
			errorLabel.setVisible(false);
			
			loginButton.addActionListener(this);
			
			panel.add(loginLabel);
			panel.add(emailTextField);
			panel.add(loginButton);
			panel.add(errorLabel);
			
			frame.add(panel);
			frame.setSize(500, 500);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		public void actionPerformed(ActionEvent ae) {
			if(!emailTextField.getText().isEmpty()) {
				errorLabel.setText("Connecting to server. Please wait.");
				errorLabel.setVisible(true);
				DatabaseManager db = new DatabaseManager();
				new MainWindow(emailTextField.getText(), db);
				closeLoginScreen();
			} else {
				errorLabel.setVisible(true);
			}
		}
		
		public void closeLoginScreen() {
			frame.setVisible(false);
			frame.dispose();
		}
	}