/**
 * DatabaseManager.java contains all the functions needed to communicate with the
 * HBase database
 * @author: Siddharth Kothari & Lakshya Agrawal
**/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;


public class DatabaseManager {
	// stores the name of our HBase table
	static final String TABLE_NAME = "messages";
	
	// instance variables for the Configuration and HTable objects
	Configuration config;
	HTable table;
	HBaseAdmin admin;
	
	// initialize connection with database
	public DatabaseManager() {
		
		config = HBaseConfiguration.create();
		
		try {
			// create instance of HBaseAdmin. Needed to dynamically create database
			admin = new HBaseAdmin(config);
			
			// check if HBase table exists, if not then create it
			if(!admin.tableExists(TABLE_NAME)) {
				
				// basically code below dynamically creates our HBase table
				HTableDescriptor tableDescriptor = new HTableDescriptor(Bytes.toBytes(TABLE_NAME));
				HColumnDescriptor msg = new HColumnDescriptor(Bytes.toBytes("MSG"));
				tableDescriptor.addFamily(msg);
				admin.createTable(tableDescriptor);
				
				System.out.println("New table created");
			} else {
				
				System.out.println("Table already exists");
			}
			// reference the HBase table
			table = new HTable(config, TABLE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// releases any database resources
	public void finalize() {
		System.out.println("Database resources de-allocated");
		try {
			admin.close();
			table.close();
		} catch (IOException e) {
			System.out.println("Error closing table");
			e.printStackTrace();
		}
	}
	
	public void storeMessage(Message msg) {
		// create composite rowKey with receiver email id, date and time
		byte[] rowKey = Bytes.toBytes(msg.getReceiverID() + "_" + msg.getDate() + "_" + msg.getTime() + "_" + msg.getMessageTitle());
		
		Put p = new Put(rowKey);
		
		// add information into column family - MSG
		p.addColumn(Bytes.toBytes("MSG"), Bytes.toBytes("receiver_id"), Bytes.toBytes(msg.getReceiverID()));
		p.addColumn(Bytes.toBytes("MSG"), Bytes.toBytes("sender_id"), Bytes.toBytes(msg.getSenderID()));
		p.addColumn(Bytes.toBytes("MSG"), Bytes.toBytes("title"), Bytes.toBytes(msg.getMessageTitle()));
		p.addColumn(Bytes.toBytes("MSG"), Bytes.toBytes("body"), Bytes.toBytes(msg.getMessageBody()));
		p.addColumn(Bytes.toBytes("MSG"), Bytes.toBytes("date"), Bytes.toBytes(msg.getDate()));
		p.addColumn(Bytes.toBytes("MSG"), Bytes.toBytes("time"), Bytes.toBytes(msg.getTime()));
		
		try {
			// insert data into database
			table.put(p);
			System.out.println("Data inserted");
		} catch (IOException e) {
			System.out.println("Error storing data into database");
			e.printStackTrace();
		}
	}
	
	public List<Message> getAllMessages(String receiverId) {
		// store messages in an array list of type Message
		List<Message> messageArray = new ArrayList<Message>();
		
		// add a filter to check the prefix of row key for the receiver email id
		Filter filter = new PrefixFilter(Bytes.toBytes(receiverId));
		Scan s = new Scan();
		s.setFilter(filter);
		ResultScanner scanner;
		
		try {
			scanner = table.getScanner(s);
			
			for(Result result : scanner) {
				
				String receiver_id = Bytes.toString(result.getValue(Bytes.toBytes("MSG"), Bytes.toBytes("receiver_id")));
				String sender_id = Bytes.toString(result.getValue(Bytes.toBytes("MSG"), Bytes.toBytes("sender_id")));
				String date = Bytes.toString(result.getValue(Bytes.toBytes("MSG"), Bytes.toBytes("date")));
				String time = Bytes.toString(result.getValue(Bytes.toBytes("MSG"), Bytes.toBytes("time")));
				String messageBody = Bytes.toString(result.getValue(Bytes.toBytes("MSG"), Bytes.toBytes("body")));
				String messageTitle = Bytes.toString(result.getValue(Bytes.toBytes("MSG"), Bytes.toBytes("title")));
				
				// add retrieved message object into array list
				messageArray.add(new Message(receiver_id, sender_id, date, time, messageTitle, messageBody));
			}
			scanner.close();
			return messageArray;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}


