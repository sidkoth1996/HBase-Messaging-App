/**
 * DataCreation.java contains function generateData(DatabaseManager) that is responsible for 
 * populating the HBase database with mock data (5000 in number. 1 email sends 10 other email messages)
 * Note: this class reads from two text files - Body and Emails. Make sure HBase is running before calling main method
 * @author: Siddharth Kothari & Lakshya Agrawal
**/

import java.io.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class DataCreation
{
	// class defines email-weight combo. Weight is required to determine how many times the email has been used
	static class EmailWeight {
		private String email;
		private int weight;
		
		public EmailWeight(String email, int weight) {
			this.email = email;
			this.weight = weight;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public void setWeight(int weight) {
			this.weight = weight;
		}
		
		public String getEmail() {
			return this.email;
		}
		
		public int getWeight() {
			return this.weight;
		}
	}
	
	
	public static void generateData(DatabaseManager db) throws IOException
	{	
		EmailWeight[] id = new EmailWeight[500]; // contains  500 unique email id values and weight to ensure that each user sends 10 messages
		String[] body = new String[5000]; // contains 5000 non-unique message body
		String[] title = new String[5000]; //  contains 5000 non-unique message subject
		String[] date=new String[5000]; // contains 5000 date values of format dd/mm/yyyy
		String[] time=new String[5000]; // contains 5000 time values of format hh:mm am/pm
		
		
		String directory = System.getProperty("user.dir"); // get absolute directory
		// read email ids from text file 
		File emailFile = new File(directory + "/src/Emails");
		Scanner sc1 = new Scanner(emailFile);
		
		// read message body from text file
		File bodyFile=new File(directory + "/src/Body");
		Scanner sc2=new Scanner(bodyFile);
		
		// create an array of type EmailWeight where we store email along with associated weight (0 in the beginning)
		for(int i = 0; sc1.hasNextLine(); i++)
		{
			EmailWeight emailWeight = new EmailWeight(sc1.nextLine(), 0);
			id[i] = emailWeight;
		}
		
		String[] ampm={"AM","PM"}; // stores AM and PM strings in array
		
		for(int i = 0; i < 5000; i++)
		{	
			body[i] = sc2.nextLine();
			String[] temp = body[i].split(" ");
			
			if(temp.length == 1) {
				title[i] = temp[0];
			} else if(temp.length == 2) {
				title[i] = temp[0] + " " + temp[1];
			} else { 
				title[i] = temp[0] + " " + temp[1] + " " + temp[2];
			}
			
			LocalDate randomDate = createDate(2010, 2019);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/LLLL/yyyy");
			date[i] = randomDate.format(formatter);
			
			int h = createInt(1,12);
			int m = createInt(1,60);
			int ap = createInt(0,1);
			time[i] = h + ":" + m + " " + ampm[ap];
		}
		
		for(int i=0; i<500; i++) {
			String senderEmail = id[i].getEmail();
			
			int emailsSent = 0; // variable to limit the number of emails sent by each sender
			int j = 0; // variable to check index of inner while loop
			
			while(emailsSent < 10 && j < 500) {
				
				String receiverEmail = id[j].getEmail();
				int rWeight = id[j].getWeight();
				
				if(rWeight <= 10 && i != j) {
					
					id[j].setWeight(rWeight + 1);
					int randomNumber = createInt(0,4999);
					
					db.storeMessage(new Message(receiverEmail, senderEmail, date[randomNumber], time[randomNumber], title[randomNumber], body[randomNumber]));
					emailsSent ++;
					
				}
				j++;
			}
		}
		
		sc1.close();
		sc2.close();
	}
		
	// helper function for random number creation between an interval
	private static int createInt(int a, int b)
	{
		return a + (int) Math.round(Math.random()*(b-a));
	}
	
	// helper function to create a random date
	private static LocalDate createDate(int sYear, int eYear)
	{
		int day=createInt(1,28);
		int month=createInt(1,12);
		int year=createInt(sYear,eYear);
		return LocalDate.of(year, month, day);
	}
	
	
	// main function that populates the database with mock data
	public static void main(String args[]) {
		try {
			generateData(new DatabaseManager());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

		