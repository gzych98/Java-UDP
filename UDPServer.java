package networking3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.Tools;
import v1.Alarm;
import v1.Packet;
import v1.Request;
import v1.Spectrum;
import v1.TimeHistory;

public class UDPServer {
    public static void main(String[] args) throws ClassNotFoundException {
    	DatagramSocket aSocket = null;
      Packet read = null;
      try {
        // args contain message content and server hostname
        aSocket = new DatagramSocket(7777);
        byte[] buffer = new byte[1024];
        while(true) {
          DatagramPacket request = new DatagramPacket(buffer, buffer.length);
          System.out.println("Waiting for request...");
          aSocket.receive(request);
          buffer = request.getData();
          try {
            read = (Packet) Tools.deserialize(buffer);
            System.out.println("    Reply socket: " + read);
          } catch (ClassNotFoundException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
          }  
          String path = null;  

          //sprawdź jaki objekt          
          
          boolean timAlaSpe = false;

          if (read instanceof TimeHistory) {
            path = "./src/data/" + read.fileName() + ".tim";
            timAlaSpe = true;                
          } else if (read instanceof Alarm) {
            path = "./src/data/" + read.fileName() + ".ala";
            timAlaSpe = true;  
            DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
          		request.getAddress(), request.getPort());
            aSocket.send(reply); 
          } else if (read instanceof Spectrum) {
            path = "./src/data/" + read.fileName() + ".spe";
            timAlaSpe = true;  
          } else if (read instanceof Request) {
            String deviceName = ((Request)read).getDevice();
            String description = ((Request)read).getDescription();
            String deviceType = ((Request)read).getType();
            long date = ((Request)read).getDate();
            int requestType = ((Request)read).getRequestType();
            // request - get list
            if(requestType == 1){
              String[] search = new String[1024];
              long x = new Date().getTime()/1000;
              int y = 0;
              File fileFind = new File("./src/data/");
              List<File> matchFiles = new ArrayList<>();
              while (x >= date){
                search[y] = deviceName + "-" + description + "-" + x + "." + deviceType;
                File[] matchingFiles = fileFind.listFiles(new FilenameFilter() {
                  public boolean accept(File dir, String name) {
                    return name.startsWith(search[y]);
                  }
                });
                matchFiles.addAll(Arrays.asList(matchingFiles));  
                x = x-1;
              }
              timAlaSpe = false;
              buffer = Tools.serialize(matchFiles);
              DatagramPacket reply = new DatagramPacket(buffer, buffer.length, 
              request.getAddress(), request.getPort());
              aSocket.send(reply);  
            // request - get file data
            } else if (requestType == 2){
              String path2 = "./src/data/" + deviceName + "-" + description + "-" + date + "." + deviceType;
              try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(path2));
                Packet obj = (Packet) in.readObject();
                in.close();
                buffer = Tools.serialize(obj);
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length, 
                request.getAddress(), request.getPort());
                aSocket.send(reply);  
              } catch (FileNotFoundException e) {
                System.out.println("File not found");
                System.exit(1);
              }

                             
            } 
          } else {System.out.println("Class not found");}
          //zapisz do ścieżki jeśli TimeHistory, Alarm lub Spectrum
          if (timAlaSpe){
              File f = new File(path);  
              boolean openNew;
              try {
                openNew = f.createNewFile(); 
              if (openNew) {
                System.out.println("utworzono plik " + path);
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
                out.writeObject(read);
                out.close();
              } else {
                System.out.println("Failed");
              }
            } catch (IOException e) {e.printStackTrace();}  
          }         
        }
      } catch (SocketException ex) {
        Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
				aSocket.close();
			}
      
    }
}
