package com.faculty.pcd.homework1.client.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.faculty.pcd.homework1.globals.Constants;
import com.faculty.pcd.homework1.utils.CustomFileReader;

public class ClientTCPStopAndWait {
	private Socket socket;
	private CustomFileReader fileReader;
	private String workingFilename;

	private int nrOfSentMessages = 0;
	private int nrOfBytesSent = 0;
	long startTime;
	long endTime;

	// obtaining input and out streams
	DataInputStream serverInput;
	DataOutputStream serverOutput;

	public ClientTCPStopAndWait(String filename) throws IOException {
		workingFilename = filename;
		fileReader = new CustomFileReader(workingFilename);
		fileReader.readFile();

		openConnectionWithServer();

		serverInput = new DataInputStream(socket.getInputStream());
		serverOutput = new DataOutputStream(socket.getOutputStream());
	}

	private void openConnectionWithServer() {
		try {
			System.out.println("CLIENT (ack) ----> Connecting to server...");
			socket = new Socket(InetAddress.getByName(Constants.SERVER_ADDRESS), Constants.SERVER_PORT);
			System.out.println("CLIENT (ack) ----> Connected to server...");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessages() throws IOException {
		// the following loop performs the exchange of information between client and client handler
		while (true) {
			String fileInformation = fileReader.getFileDataLength() + ">>||<<" + fileReader.getFileName() + ">>||<<"
					+ fileReader.getFileExtension();
			serverOutput.writeUTF(fileInformation);
			nrOfSentMessages++;
			
			String outputOfServer = serverInput.readUTF();
			if ("File informations received".equals(outputOfServer)) {
				sendPackagesOfMessagesToServer();
				break;
			}		
		}
		closeConnectionWithServer();

		System.out.println("CLIENT (ack) SAYS: the time elapsed to send all messages is " + (endTime - startTime) + " nanoseconds");
		System.out.println("CLIENT (ack) SAYS: the number of sent messages is " + nrOfSentMessages);
		System.out.println("CLIENT (ack) SAYS: the number of bytes sent is " + nrOfBytesSent);

	}

	private void sendPackagesOfMessagesToServer() throws IOException {
		serverOutput.writeUTF("OK");
		
		List<Byte[]> messagesToSend = fileReader.splitFileInBytesMessages();
		int nrOfMessages = messagesToSend.size();

		startTime = System.nanoTime();
		for (Byte[] message : messagesToSend) {
			byte[] messageBytes = new byte[message.length];
			int counter = 0;
			for (Byte b : message) {
				if (b != null) {
					messageBytes[counter] = b.byteValue();
					counter++;
				}
			}
			
			String outputOfServer = serverInput.readUTF();
			if ( "Okey".equals(outputOfServer) ) {
				// Write the message
				serverOutput.write(messageBytes);
				System.out.println("CLIENT (ack) ----> Message " + ++nrOfSentMessages + " of " + (nrOfMessages+1) + "  sent");

				nrOfBytesSent += counter;
			}			
		}
		endTime = System.nanoTime();
		System.out.println("CLIENT (ack) ----> Transmision done.");
	}

	private void closeConnectionWithServer() {
		try {
			socket.close();
			System.out.println("CLIENT (ack) ----> Connection of client with server is closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

