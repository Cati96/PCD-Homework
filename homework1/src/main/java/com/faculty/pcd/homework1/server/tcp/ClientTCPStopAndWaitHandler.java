package com.faculty.pcd.homework1.server.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Pattern;

import com.faculty.pcd.homework1.utils.CustomFileWriter;

public class ClientTCPStopAndWaitHandler extends Thread {
	private final Object lock = new Object();

	private final DataInputStream clientInput;
	private final DataOutputStream clientOutput;
	private final Socket client;

	private String session;
	private byte[] clientMessage;
	private int nrOfReceivedMessages;
	private int clientMessageLength;
	private int fileDataLength;
	private String fileName;
	private String fileExtension;

	public ClientTCPStopAndWaitHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.client = s;
		clientInput = dis;
		clientOutput = dos;

		session = "newSession";
		clientMessage = null;
		nrOfReceivedMessages = 0;
		clientMessageLength = 0;
		fileDataLength = 0;
		fileName = "";
		fileExtension = "";
	}

	@Override
	public void run() {
		synchronized (lock) {
			while (true) {
				String temporaryReadValue = null;
				try {
					temporaryReadValue = clientInput.readUTF();

					if (temporaryReadValue != null) {
						nrOfReceivedMessages++;

						System.out.println("SERVER (ack) ----> File information received...");
						String[] array = temporaryReadValue.split(Pattern.quote(">>||<<"));
						// Save file data length
						fileDataLength = Integer.parseInt(array[0]);
						// Save file name
						fileName = array[1];
						// Save file extension
						fileExtension = array[2];

						clientMessage = new byte[fileDataLength];
						session = "oldSession";

						clientOutput.writeUTF("File informations received");
						
						while(true) {
							clientOutput.writeUTF("Okey");
							String ack = clientInput.readUTF();
							
							if ("OK".equals(ack)) {
								byte[] messageReceivedInBytes = new byte[65535];
								clientInput.read(messageReceivedInBytes);
								nrOfReceivedMessages++;

//									messageReceivedInBytes = new String(messageReceivedInBytes).replaceAll("\0", "").getBytes();	

								System.out.println("SERVER (ack) ----> File data received...");
								for (int i = 0; i < messageReceivedInBytes.length && messageReceivedInBytes[i] != 0; i++) {
									clientMessage[clientMessageLength++] = messageReceivedInBytes[i];
								}

								if (clientMessageLength == fileDataLength) {
									session = "closedSession";
									break;
								}
							}
						}

						if ("closedSession".equals(session)) {
							System.out.println("SERVER (ack) ----> Client's output stream is shutdown");

							CustomFileWriter fileWriter = new CustomFileWriter(fileName, fileExtension);
							int bytesWritten = 0;
							try {
								bytesWritten = fileWriter.writeFile(clientMessage);
							} catch (IOException e) {
								e.printStackTrace();
							}

							System.out.println("SERVER (ack) SAYS: Client " + client + " closed.");
							System.out.println("SERVER (ack) SAYS: Used protocol is TCP");
							System.out.println("SERVER (ack) SAYS: the number of messages received from client is "
									+ nrOfReceivedMessages);
							System.out.println("SERVER (ack) SAYS: the number of bytes read from client is "
									+ clientMessageLength);
							System.out.println(
									"SERVER (ack) SAYS: the number of bytes written in file is " + bytesWritten);

							try {
								clientInput.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								clientOutput.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								client.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}

	}
}
