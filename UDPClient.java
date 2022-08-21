package networking3;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.Tools;
import v1.Alarm;
import v1.Packet;
//import v1.TimeHistory;
import v1.Request;



public class UDPClient {
	public static void main(String[] args) throws ClassNotFoundException, ParseException {
		DatagramSocket socket = null;
		InetAddress server = null;
		//TimeHistory packet = new TimeHistory();
		Scanner scanner =  null;
		byte[] buffer = new byte[1024];	
		DatagramSocket aSocket = null;
		int serverPort = 7777;
		int choice;

	boolean option = false;
	System.out.println("---> [1] create Packet object\n---> [2] get file list\n---> [3] get file data\n---> [4] exit ");
	scanner = new Scanner(System.in);
	while (!option){
		choice = scanner.nextInt();
		
		if (choice == 1) {
			Packet read = null;
			System.out.println("Wybrano 1");
			option = false;
			Alarm packet2 = new Alarm();

			try {
				scanner = new Scanner(System.in);
				server = InetAddress.getByName("localhost");
				
				//zapisz do pliku
				//byte[] data = Tools.serialize(packet);
				byte[] data2 = Tools.serialize(packet2);						
				socket = new DatagramSocket();				
				DatagramPacket request = new DatagramPacket(data2, data2.length, server, serverPort);
				socket.send(request);
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				socket.receive(reply);
				buffer = reply.getData();
				read = (Packet) Tools.deserialize(buffer);
				System.out.println("Reply socket: " + read);
				System.out.println("---> [1] create Packet object\n---> [2] get file list\n---> [3] get file data \n---> [4] exit ");
					
				} catch (SocketException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnknownHostException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					socket.close();
				}		
		} else if (choice == 2){
			System.out.println("Wybrano 2");
			option = false;
			String device = "";
			String description = "";
			String dateString = "";
			Long dateLong;
			String type = "";
			List<File> fileReceived;
			try {	
				aSocket = new DatagramSocket();
				scanner = new Scanner(System.in);
				server = InetAddress.getByName("localhost");
				System.out.println("Enter file type: Alarm [ala], TimeHistory [tim], Spectrum [spe]: ");
				type = scanner.nextLine();
				System.out.println("Enter device: ");
				device = scanner.nextLine();
				System.out.println("Enter descritpion: ");
				description = scanner.nextLine();
				System.out.println("Enter start date [dd/MM/yyyy-HH:mm]: ");
				dateString = scanner.nextLine(); //5 kwietnia 15:00
				dateLong = Tools.stringDateToLong(dateString)/1000;				
				System.out.print("Looking for: " + device + "-" + description + "-XXX." + type + " since: " + dateLong + ", please wait . . .\n");
				Request req = new Request(device, description, dateLong, type, 1);
				byte[] data = Tools.serialize(req);
				DatagramPacket request = new DatagramPacket(data, data.length, server, serverPort);
				aSocket.send(request);
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				buffer = reply.getData();
				fileReceived = (List<File>) Tools.deserialize(buffer);

				//odczytać tablicę matchingFilse pętlą foreach
				System.out.println("----------------- Wyniki wyszukiwania -----------------");
				for (File x : fileReceived){
					System.out.println("   " + x);
				}
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++\n---> [1] create Packet object\n---> [2] get file list\n---> [3] get file data \n---> [4] exit ");					
				} catch (SocketException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnknownHostException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					//socket.close();
					aSocket.close();
				}		
		} else if (choice == 3) {
			System.out.println("Wybrano 3");
			option = false;
			String device = "";
			String description = "";
			Long dateLong;
			String type = "";
			Packet fileReceived;
			try {	
				aSocket = new DatagramSocket();
				scanner = new Scanner(System.in);
				server = InetAddress.getByName("localhost");
				System.out.println("Enter file type: Alarm [ala], TimeHistory [tim], Spectrum [spe]: ");
				type = scanner.nextLine();
				System.out.println("Enter device: ");
				device = scanner.nextLine();
				System.out.println("Enter descritpion: ");
				description = scanner.nextLine();
				System.out.println("Enter date [long]: ");
				dateLong = scanner.nextLong();				
				System.out.print(device + "-" + description + "-" + dateLong + "." + type + "\n");
				
				Request req = new Request(device, description, dateLong, type, 2);
				byte[] data = Tools.serialize(req);
				DatagramPacket request = new DatagramPacket(data, data.length, server, serverPort);
				aSocket.send(request);
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				buffer = reply.getData();
				fileReceived = (Packet) Tools.deserialize(buffer);
				System.out.println("   " + fileReceived);
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++\n---> [1] create Packet object\n---> [2] get file list\n---> [3] get file data\n---> [4] exit ");					
				} catch (SocketException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnknownHostException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					//socket.close();
					aSocket.close();
				}		
		} else if (choice == 4) {
			System.exit(0);
		} else {
			option = false;
		}
	}
	scanner.close();
	}
}
