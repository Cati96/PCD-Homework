package com.faculty.pcd.homework1.server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.faculty.pcd.homework1.globals.Constants;
import com.faculty.pcd.homework1.utils.CustomFileWriter;

public class ServerUDPStreaming implements Runnable {

	private DatagramSocket serverSocket;

	private String session;

	private InetAddress IPAddress;
	int port;

	private byte[] clientMessage;
	private int nrOfReceivedMessages;
	private int clientMessageLength;
	private int fileDataLength;
	private String fileName;
	private String fileExtension;

	public ServerUDPStreaming() throws SocketException {
		serverSocket = new DatagramSocket(Constants.SERVER_PORT);

		session = "newSession";
		clientMessage = null;
		nrOfReceivedMessages = 0;
		clientMessageLength = 0;
		fileDataLength = 0;
		fileName = "";
		fileExtension = "";
	}

	public void run() {
		while (true) {
			try {
				byte[] in = new byte[65535];
				
//				Create inbound datagram packet
				DatagramPacket receivedPacket = new DatagramPacket(in, in.length);
				serverSocket.receive(receivedPacket);
				
				if ("newSession".equals(session)) {					
					in = new String(in).replaceAll("\0", "").getBytes();

//					Get the data from the packet we've just received
					String fileInformation = new String(receivedPacket.getData());

//					Retrieve the IP Address and port number of the datagram packet we've just received
					IPAddress = receivedPacket.getAddress();
					port = receivedPacket.getPort();

					nrOfReceivedMessages++;

					System.out.println("SERVER ----> File information received..." + fileInformation);
					String[] array = fileInformation.split(Pattern.quote(">>||<<"));
					// Save file data length
					fileDataLength = Integer.parseInt(array[0]);
					// Save file name
					fileName = array[1];
					// Save file extension
					fileExtension = array[2];

					clientMessage = new byte[fileDataLength];
					session = "oldSession";
					
//					byte[] outData = new String("Information received").getBytes();
//					DatagramPacket out = new DatagramPacket(outData, outData.length, IPAddress, Constants.SERVER_PORT);
//					serverSocket.send(out);
				} else {					
					in = new String(in).replaceAll("\0", "").getBytes();
					
					nrOfReceivedMessages++;
					System.out.println("SERVER ----> File data received..." + new String(receivedPacket.getData()));

					for (int i = 0; i < in.length; i++) {
						clientMessage[clientMessageLength++] = in[i];
					}

					if (clientMessageLength == fileDataLength) {
						session = "closedSession";
					}

					if ("closedSession".equals(session)) {
						System.out.println("SERVER ----> Client's output stream is shutdown");

						CustomFileWriter fileWriter = new CustomFileWriter(fileName, fileExtension);
						int bytesWritten = 0;
						try {
							bytesWritten = fileWriter.writeFile(clientMessage);
						} catch (IOException e) {
							e.printStackTrace();
						}

						System.out.println("SERVER SAYS: Client " + IPAddress + " and port " + port + " closed.");
						System.out.println("SERVER SAYS: Used protocol is UDP");
						System.out.println(
								"SERVER SAYS: the number of messages received from client is " + nrOfReceivedMessages);
						System.out
								.println("SERVER SAYS: the number of bytes read from client is " + clientMessageLength);
						System.out.println("SERVER SAYS: the number of bytes written in file is " + bytesWritten);
						
						session = "newSession";
						clientMessage = null;
						nrOfReceivedMessages = 0;
						clientMessageLength = 0;
						fileDataLength = 0;
						fileName = "";
						fileExtension = "";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
