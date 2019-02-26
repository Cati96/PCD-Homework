package com.faculty.pcd.homework1;

import java.io.File;
import java.io.IOException;

import com.faculty.pcd.homework1.client.tcp.ClientTCPStopAndWait;
import com.faculty.pcd.homework1.client.tcp.ClientTCPStreaming;
import com.faculty.pcd.homework1.client.udp.ClientUDPStreaming;
import com.faculty.pcd.homework1.globals.Constants;
import com.faculty.pcd.homework1.server.tcp.ServerTCPStopAndWait;
import com.faculty.pcd.homework1.server.tcp.ServerTCPStreaming;
import com.faculty.pcd.homework1.server.udp.ServerUDPStreaming;

public class App {

	public static void createClientAndSendFileToServer(File folder, String protocol, String mechanism)
			throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				createClientAndSendFileToServer(fileEntry, protocol, mechanism);
			} else {
				System.out.println("Next file to be sent: " + fileEntry.getName());
				if ("TCP".equals(protocol)) {
					if ("streaming".equals(mechanism)) {
						ClientTCPStreaming clientStreaming = new ClientTCPStreaming(
								Constants.DIRECTORY_PATH + "/" + fileEntry.getName());
						clientStreaming.sendMessages();
					} else if ("ack".equals(mechanism)) {
						ClientTCPStopAndWait clientAck = new ClientTCPStopAndWait(
								Constants.DIRECTORY_PATH + "/" + fileEntry.getName());
						clientAck.sendMessages();
					}
				} else if ("UDP".equals(protocol)) {
					ClientUDPStreaming client = new ClientUDPStreaming(
							Constants.DIRECTORY_PATH + "/" + fileEntry.getName());
					client.sendMessages();
				}
			}
			System.out.println("\n----------------------------------=====================----------------------------------\n");
		}
	}

	public static void main(String[] args) throws IOException {
		String arguments = "";
		for (int i = 0; i < args.length; i++) {
			arguments += args[i] = " ";
		}
		System.out.println(arguments);
		System.out.println(args.length);

		if (args.length != 2) {
			System.exit(0);
		} else {
			String protocol = args[0];
			String mechanism = args[1];
			if ("TCP".equals(protocol)) {
				if ("streaming".equals(mechanism)) {
					ServerTCPStreaming serverStreaming = new ServerTCPStreaming();
					Thread threadServerStreaming = new Thread(serverStreaming);
					threadServerStreaming.start();
				} else if ("ack".equals(mechanism)) {
					ServerTCPStopAndWait serverAck = new ServerTCPStopAndWait();
					Thread threadServerAck = new Thread(serverAck);
					threadServerAck.start();
				}

				File folder = new File(Constants.DIRECTORY_PATH);
				createClientAndSendFileToServer(folder, protocol, mechanism);
			} else if ("UDP".equals(protocol)) {
				ServerUDPStreaming server = new ServerUDPStreaming();
				Thread threadServer = new Thread(server);
				threadServer.start();

				File folder = new File(Constants.DIRECTORY_PATH);
				createClientAndSendFileToServer(folder, protocol, "");
			} else {
				System.exit(1);
			}
		}
	}
}
