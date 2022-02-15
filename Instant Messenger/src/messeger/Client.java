package messeger;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	
	//constructor
		public Client(String host) {
			super(" Client Messenger");
			serverIP = host;
			userText = new JTextField();
			userText.setEditable(false);
			userText.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							sendMessage(event.getActionCommand());
							userText.setText("");
						}
					}
					);
			add(userText, BorderLayout.NORTH);
			chatWindow = new JTextArea();
			add(new JScrollPane(chatWindow), BorderLayout.CENTER);
			setSize(300,150);
			setVisible(true);
		}
		
		
		//set up and run the server
		
		public void startRunning() {
			try {
						
						connectToServer();
						setupStreams();
						whileChatting();
					}catch(EOFException eofException) {
						showMessage("\n Server ended the connection");
						
					}catch(IOException ioException) {
						ioException.printStackTrace();
			}finally {
				closeCrap();
			}
		}
		
		
		//connect to server
		private void connectToServer() throws IOException {
			showMessage("Waiting for someone to connect.... \n");
			connection = new Socket(InetAddress.getByName(serverIP), 6789);
			showMessage("Now connected to " + connection.getInetAddress().getHostName());
		}
		
		
		//get stream to send and receive data
		
		private void setupStreams() throws IOException{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage("\n Streams are now connected! \n");
		}
		
		
		//during the chat conversion
		
		private void whileChatting() throws IOException{
			//String message = " You are now connected! ";
			//sendMessage(message);
			ableToType(true);
			do {
				try {
					message = (String) input.readObject();
					showMessage("\n" + message);
				}catch(ClassNotFoundException classNotFoundException) {
					showMessage("\n idk wtf that user sent!");
				}
			}while(!message.equals("SERVER - END"));
		}
		
		
		// close stream and sockets after you are done chatting
		
		private void closeCrap() {
			showMessage("\n Closing connection... \n");
			ableToType(false);
			try {
				output.close();
				input.close();
				connection.close();
			}catch(IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		
		//send msg to server
		private void sendMessage(String message) {
			try {
				output.writeObject("CLIENT - " + message);
				output.flush();
				showMessage("\nCLIENT -  " + message);
			}catch(IOException ioException) {
				chatWindow.append("\n ERROR: I CANT SEND THAT MESSAGE BOY!");
			}
		}
		
		
		//change/update chatWindow
		private void showMessage(final String text) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							chatWindow.append(text);
					}
				}
			);
		}
		
		
		//give user permission to type crap into txt box
		private void ableToType(final boolean tof) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							userText.setEditable(tof);
					}
				}
			);
		}

}
