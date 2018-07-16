package com.application ;
	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.model.FixedSizeStack;



public class Server {
	private static int count;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// an ArrayList to keep a list of Books
	private ArrayList<FixedSizeStack<String>> books;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	
	
	public Server(int port) {
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();

		books = new ArrayList<FixedSizeStack<String>>();

		FixedSizeStack<String> sample = new FixedSizeStack<String>(3);

		books.add(sample);
	}
	
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);

			// wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // create client thread
				al.add(t);									// add thread in the ArrayList
				t.start();									// start thread
			}

			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.input.close();
					tc.output.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		

	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
	}
	/*
	 *  add Points to all Players
	 */

	private synchronized void addPoints(int count) {

		String pointsMessage = "You just won 10 points !";
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			if(!ct.writeMsg(pointsMessage)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
			ct.score += 10;
		}
	}
	private synchronized String getBoard() {

		StringBuilder board = new StringBuilder();
		sort();
		for(int i = 0; i<al.size(); i++) {
			ClientThread ct = al.get(i);
			board.append("Username: "+ct.username + " has a score of: "+ct.score+"\n");
		}
		return board.toString();
	}

	//show help menu
	private String menu() {

		StringBuilder sb = new StringBuilder();
	    sb.append( "Choose:\n" );
	    sb.append( "GET a book\n" );
	    sb.append( "ADD to add a sentence\n");
	    sb.append( "SCORE to display score\n");
	    sb.append( "EXIT the game\n");

	    return sb.toString();

	}

	private synchronized void remove(int id) {

		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}

	private void sort() {

        CompareByScore comparator = new CompareByScore();
        Collections.sort(al, comparator);
    }
	
	/*
	 *  Usage: 
	 * > java Server
	 * > java Server portNumber
	 * Default port number is 9999 
	 */ 

	public static void main(String[] args) {

		int portNumber = 9999;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		BufferedReader input;
    	PrintWriter output;
		// connection id
		int id;
		// the Username of the Client
		int score;
		// the only type of message a will receive

		String username;

		String command;
		// the date I connect
		String date;

		// Constructor
		ClientThread(Socket socket) {
			id = ++count;
			this.socket = socket;
			try
			{
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(),true);		
				username = input.readLine();
				score = 0;
                display(username + " just connected.");
                output.println( "Co-writing book game menu");
                output.println( "choose type add or get or score");
      			output.flush();
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
            date = new Date().toString() + "\n";
		}

		public void run() {
			boolean keepGoing = true;
			while(keepGoing) {
				// read input
				try {
					command = input.readLine();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				command = command.trim();
				if(command.equalsIgnoreCase("GET")) {
					if (books != null && !books.isEmpty()) {
  						FixedSizeStack<String> current =  books.get(books.size()-1);
  						if(current.elements() != 0) {
  							String last = current.peek();
  							writeMsg("Here is your line: "+last);
  						}
  						else
  							writeMsg("Book is empty! Type add to write the first line !");
  					}
  				}
  				else if (command.equalsIgnoreCase("add")) {

  					writeMsg("Add a sentence");
  					try {
  						String line = input.readLine();
						if (books != null && !books.isEmpty()) {

							//get latest book
							FixedSizeStack<String> current =  books.get(books.size()-1);
							// check if there is still once sentence to add
							if(current.elements() == current.size()-2) {
								System.out.println("ADDING points");
								current.push(line);
								books.set(books.size()-1, current);
								//create new book
								FixedSizeStack<String> newbook = new FixedSizeStack<String>(3);
								books.add(newbook);
								addPoints(10);
							}
							else if(current.elements() < current.size()) {
								System.out.println(username+" just wrote a line");
								current.push(line);
								books.set(books.size()-1, current);
							}
						}
					}
					catch (IOException e) {
						break;
					}
  				}
  				else if (command.equalsIgnoreCase("score"))
  					writeMsg(getBoard());
  				else if (command.equalsIgnoreCase("help"))
  					writeMsg(menu());
  				else if (command.equalsIgnoreCase("exit")) {
  					display(username + " just quit the game");
  					writeMsg("Thank you for playing ");
                    keepGoing = false;
                    break;
  				}
  				else
  					writeMsg("Please enter a valid command. Hint: type help for more info");
  			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		private void close() {
			try {
				if(output!= null) output.close();
			}
			catch(Exception e) {}
			try {
				if(input != null) input.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write to a client
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
                close();
                return false;
            }
            output.println(msg);
            return true;
				
		}
	}
	// comparator to compare ClientThread scores
	class CompareByScore implements Comparator<ClientThread> {
		@Override
		public int compare(ClientThread c1, ClientThread c2) {
			return -Integer.compare(c1.score, c2.score);
		}
	}
}


