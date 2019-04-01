import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow implements ActionListener, ListSelectionListener {
		JFrame frame;
		JPanel panelSend;
		JPanel panelReceive;
		JTabbedPane tabbedPane;
		JButton sendButton;
		JButton refreshButton;
		JTextField toTextField;
		JTextField titleTextField;
		JTextField bodyTextField;
		String loginEmail;
		JList emailList;
		JScrollPane scrollPane;
		JLabel successLabel; 
		JLabel errorLabel;
		JTextArea messageDisplay;
		
		DatabaseManager db;
		
		DefaultListModel<Message> model;
		List<Message> messageList;
		
		
		public MainWindow(String email, DatabaseManager db) {
			this.db = db;
			this.loginEmail = email;
			
			frame = new JFrame("Email client");
			panelSend = new JPanel();
			panelReceive = new JPanel();
			tabbedPane = new JTabbedPane();
			
			// initialize components for the send panel
			JLabel l1 = new JLabel("To: ");
			JLabel l2 = new JLabel("Title: ");
			JLabel l3 = new JLabel("Body: ");
			successLabel = new JLabel("Message sent!");
			errorLabel = new JLabel("Please enter all fields");
			
			// initially set the status labels to invisible
			errorLabel.setVisible(false);
			successLabel.setVisible(false);
			
			toTextField = new JTextField("", 10);
			titleTextField = new JTextField("",10);
			bodyTextField = new JTextField("", 10);
			sendButton = new JButton("Send");
			
			sendButton.addActionListener(this);
			
			// initialize components for the receive panel
			emailList = new JList();
			emailList.addListSelectionListener(this);
			emailList.setVisibleRowCount(10);
			emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			emailList.setFixedCellWidth(300);
			
			
			scrollPane = new JScrollPane(emailList);
			refreshButton = new JButton("Refresh");
			messageDisplay = new JTextArea();
			
			messageDisplay.setEditable(false);
			messageDisplay.setLineWrap(true);
			messageDisplay.setRows(10);
				
			refreshButton.addActionListener(this);
			
			// add components to the send panel
			panelSend.add(l1);
			panelSend.add(toTextField);
			panelSend.add(l2);
			panelSend.add(titleTextField);
			panelSend.add(l3);
			panelSend.add(bodyTextField);
			panelSend.add(sendButton);
			panelSend.add(successLabel);
			panelSend.add(errorLabel);
			
			panelReceive.add(scrollPane);
			panelReceive.add(messageDisplay);
			panelReceive.add(refreshButton);
			
			tabbedPane.add("Send", panelSend);
			tabbedPane.add("Receive", panelReceive);
			frame.add(tabbedPane);
			frame.setSize(500, 500);
			frame.setVisible(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand() == "Send") {
				String receiverId = toTextField.getText();
				String title = titleTextField.getText();
				String body = bodyTextField.getText();
				
				toTextField.setText("");
				titleTextField.setText("");
				bodyTextField.setText("");
				
				if(!receiverId.isEmpty() && !title.isEmpty() && !body.isEmpty()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");
					Date now = new Date();
					String date = dateFormat.format(now);
					String time = timeFormat.format(now);
					
					Message msg = new Message(receiverId, loginEmail, date, time, title, body);
					db.storeMessage(msg);
					successLabel.setVisible(true);
				} else {
					errorLabel.setVisible(true);
				}
				
			} 
			
			if(ae.getActionCommand() == "Refresh") {
				updateEmailList();
			}
		}
		
		public void valueChanged(ListSelectionEvent le) {
			JList list = (JList) le.getSource();
			Message msg = (Message) list.getSelectedValue();
		
			
			StringBuilder sb = new StringBuilder();
			sb.append("From: " + msg.getSenderID()).append('\n');
			sb.append("When: " + msg.getTime() + " " + msg.getDate()).append('\n');
			sb.append("Title: " + msg.getMessageTitle()).append('\n');
			sb.append("Body: " + msg.getMessageBody());
			
			String temp = sb.toString();
			
			messageDisplay.setText(temp);
		}
		
		public void updateEmailList() {
			messageList = db.getAllMessages(loginEmail);
			
			model = new DefaultListModel<Message>();
			
			for(Message m: messageList) {
				model.addElement(m);
			}
			
			emailList.setModel(model);
			emailList.setSelectedIndex(0);
		}
	}