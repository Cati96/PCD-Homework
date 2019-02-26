package com.faculty.pcd.homework1.client.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import com.faculty.pcd.homework1.globals.Constants;
import com.faculty.pcd.homework1.utils.CustomFileReader;

public class ClientUDPStreaming {

	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private byte[] outData;

	private CustomFileReader fileReader;
	private String workingFilename;
	
	private int nrOfSentMessages = 0;
	private int nrOfBytesSent = 0;
	long startTime;
	long endTime;

	public ClientUDPStreaming(String filename) throws IOException {
		clientSocket = new DatagramSocket();
		IPAddress = InetAddress.getByName(Constants.SERVER_ADDRESS);

		workingFilename = filename;
		fileReader = new CustomFileReader(workingFilename);
		fileReader.readFile();
	}

	private void closeClient() {
		clientSocket.close();
	}

	public void sendMessages() {
		// the following loop performs the exchange of information between client and
		// client handler
		while (true) {
			try {
				String fileInformation = fileReader.getFileDataLength() + ">>||<<" + fileReader.getFileName() + ">>||<<"
						+ fileReader.getFileExtension();

				outData = new byte[65535];
				outData = fileInformation.getBytes();

//				Next we create a datagram packet which will allow us send our message back to our datagram server
				DatagramPacket out = new DatagramPacket(outData, outData.length, IPAddress, Constants.SERVER_PORT);
				clientSocket.send(out);

				nrOfSentMessages++;
				
//				byte[] inData = new byte[20];
//				DatagramPacket in = new DatagramPacket(inData, inData.length);
//				clientSocket.receive(in);
				
//				if( "Information received".equals(new String(in.getData()))) {
					sendPackagesOfMessagesToServer();
					break;
//				}	

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		closeClient();
		
		System.out.println("CLIENT SAYS: the time elapsed to send all messages is " + (endTime - startTime) + " nanoseconds");
		System.out.println("CLIENT SAYS: the number of sent messages is " + nrOfSentMessages);
		System.out.println("CLIENT SAYS: the number of bytes sent is " + nrOfBytesSent);
	}
	
	private void sendPackagesOfMessagesToServer() throws IOException {
		List<Byte[]> messagesToSend = fileReader.splitFileInBytesMessages();
		int nrOfMessages = messagesToSend.size();

		startTime = System.nanoTime();
		for (Byte[] message : messagesToSend) {
			byte[] messageBytes = new byte[message.length];
			int counter = 0;
			for (Byte b : message) {
				if (b != null) {
					messageBytes[counter++] = b.byteValue();
				}
			}

			DatagramPacket out = new DatagramPacket(messageBytes, messageBytes.length, IPAddress, Constants.SERVER_PORT);
			clientSocket.send(out);
			System.out.println("CLIENT ----> Message " + ++nrOfSentMessages + " of " + (nrOfMessages+1) + "  sent");

			nrOfBytesSent += counter;
		}
		endTime = System.nanoTime();
		System.out.println("CLIENT ----> Transmision done.");
	}
}
