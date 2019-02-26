package com.faculty.pcd.homework1.server.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.faculty.pcd.homework1.globals.Constants;

public class ServerTCPStopAndWait implements Runnable {
	private ServerSocket serverSocket = null;
	
	public ServerTCPStopAndWait() throws IOException {
		serverSocket = new ServerSocket(Constants.SERVER_PORT);
		System.out.println("SERVER (ack) ----> Server socket created.");
	}
	
	public void run() {
		// running infinite loop for getting client request
		while (true) {
			Socket client = null;

			try {
				// socket object to receive incoming client requests
				client = serverSocket.accept();

				System.out.println("SERVER (ack) ----> A new client is connected : " + client);

				// obtaining input and out streams
				DataInputStream input = new DataInputStream(client.getInputStream());
				DataOutputStream output = new DataOutputStream(client.getOutputStream());

				System.out.println("SERVER (ack) ----> Assigning new thread for this client: " + client);

				// create a new thread object
				Thread t = new ClientTCPStopAndWaitHandler(client, input, output);

				// Invoking the start() method
				t.start();

			} catch (Exception e) {
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
}
