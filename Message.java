/**
 * Message.java contains a class Message with instance variables for the message object along with all its 
 * getter and setter methods
 * Author: Siddharth Kothari & Lakshya Agrawal
**/

public class Message {
	private String receiver_id;
	private String sender_id;
	private String date;
	private String time;
	private String messageTitle;
	private String messageBody;
	
	// empty constructor
	public Message() {
	}
	
	// parameterized constructor.Note: pass date as date object
	public Message(String receiver_id, String sender_id, String date, String time, String messageTitle, String messageBody) {
		this.receiver_id = receiver_id;
		this.sender_id = sender_id;
		this.date = date;
		this.time = time;
		this.messageTitle = messageTitle;
		this.messageBody = messageBody;
	}
	
	@Override
	public String toString() {
		return "From: " + this.getSenderID() + " Title: " + this.getMessageTitle() + " Body: " + this.getMessageBody();
	}
	
	public void setReceiverID(String id) {
		this.receiver_id = id;
	}
	
	public void setSenderID(String id) {
		this.sender_id = id;
	}
	
	public void setDateAndTime(String date, String time) {
		this.date = date;
		this.time = time;
	}
	
	public void setMessageTitle(String title) {
		this.messageTitle = title;
	}
	
	public void setMessageBody(String body) {
		this.messageBody = body;
	}
	
	public String getReceiverID() {
		return this.receiver_id;
	}
	
	public String getSenderID() {
		return this.sender_id;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public String getMessageTitle() {
		return this.messageTitle;
	}
	
	public String getMessageBody() {
		return this.messageBody;
	}
}
