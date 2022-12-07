import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.FileInputStream;

import java.lang.Thread;

public class Client {

	@SuppressWarnings({"unchecked", "rawtypes", "resource", "unused"})

	private static Socket clientThread;

	public static void main(String args[]) throws Exception {
		Client client = new Client(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}

	public Client(int peerServerPort, int peerid) throws NumberFormatException, IOException {
		Socket socket;
		ArrayList al;
		ArrayList<FileInfo> arrList = new ArrayList<FileInfo>();
		Scanner scanner = new Scanner(System.in);
		ObjectInputStream ois;
		ObjectOutputStream oos;
		String string;
		Object o, b;
		String d, directoryPath = null;
		//int peerServerPort=0;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Welcome to the Client ::");
			System.out.println(" ");

			//System.out.println("Enter the port number on which the peer should act as server ::");
			//peerServerPort=Integer.parseInt(br.readLine());

			ServerDownload objServerDownload = new ServerDownload(peerServerPort, directoryPath);
			objServerDownload.start();

			this.clientThread = new Socket("localhost", 7799);

			ObjectOutputStream objOutStream = new ObjectOutputStream(clientThread.getOutputStream());
			ObjectInputStream objInStream = new ObjectInputStream(clientThread.getInputStream());

			al = new ArrayList();

			socket = new Socket("localhost", 7799);
			System.out.println("Connection has been established with the Server");

			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			while(true) {
				int flag=validation(oos,ois,peerid);
				if(flag==1) {
					System.out.println("User Verified");
					break;
				}
				else
				{
					System.out.println("User not Verified!!!\n PLEASE TRY AGAIN");
				}
			}

			System.out.println("Enter the directory that contain the files -->");
			d = br.readLine();
			directoryPath = "/Users/tarunkrishnareddykolli/Desktop/SEFS/peer_to_peer_Files/" + d;


			File folder = new File(directoryPath);
			File[] listofFiles = folder.listFiles();
			FileInfo currentFile;
			File file;

			for (int i = 0; i < listofFiles.length; i++) {
				currentFile = new FileInfo();
				file = listofFiles[i];
				currentFile.fileName = file.getName();
				currentFile.peerid = peerid;
				currentFile.portNumber = peerServerPort;
				arrList.add(currentFile);
			}

			oos.writeObject(arrList);
			//System.out.println("The complete ArrayList :::"+arrList);
			int choice = 0;
			System.out.println("Enter your Choice:\n1:Create\n2:Download");
			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {
				System.out.println("Creation done");
			}
			if (choice == 2) {


				System.out.println("Enter the desired file name that you want to downloaded from the list of the files available in the Server ::");
				String fileNameToDownload = br.readLine();
				oos.writeObject(fileNameToDownload);

				System.out.println("Waiting for the reply from Server...!!");

				ArrayList<FileInfo> peers = new ArrayList<FileInfo>();
				peers = (ArrayList<FileInfo>) ois.readObject();
			
			/*for(int i=0;i<peers.size();i++)
			{  
				int result = peers.get(i).peerid;
				int port = peers.get(i).portNumber;
				System.out.println("The file is stored at peer id " +result+ " on port "+port);
			}
			
			System.out.println("Enter the respective port number of the above peer id :");
			int clientAsServerPortNumber = Integer.parseInt(br.readLine());
			
			System.out.println("Enter the desired peer id from which you want to download the file from :");
			int clientAsServerPeerid = Integer.parseInt(br.readLine());*/

				int result = peers.get(0).peerid;
				int port = peers.get(0).portNumber;
				System.out.println("Connecting to Peer " + result + " on port " + port);
				int clientAsServerPortNumber = port;
				int clientAsServerPeerid = result;

				download(clientAsServerPeerid, clientAsServerPortNumber, fileNameToDownload, directoryPath);
			}
		} catch (Exception e) {
			System.out.println("Error in establishing the Connection between the Client and the Server!! ");
			System.out.println("Please cross-check the host address and the port number..");
		}
	}

	public static void download(int clientAsServerPeerid, int clientAsServerPortNumber, String fileNamedwld, String directoryPath) throws ClassNotFoundException {
		try {
			@SuppressWarnings("resource")
			Socket clientAsServersocket = new Socket("localhost", clientAsServerPortNumber);

			ObjectOutputStream clientAsServerOOS = new ObjectOutputStream(clientAsServersocket.getOutputStream());
			ObjectInputStream clientAsServerOIS = new ObjectInputStream(clientAsServersocket.getInputStream());

			clientAsServerOOS.writeObject(fileNamedwld);
			int readBytes = (int) clientAsServerOIS.readObject();

			//System.out.println("Number of bytes that have been transferred are ::"+readBytes);

			byte[] b = new byte[readBytes];

			clientAsServerOIS.readFully(b, 0, readBytes);


			for (byte buf : b) {

				// convert byte to char
				char c = (char) buf;

				// prints character
				System.out.print(c);
			}

			OutputStream fileOPstream = new FileOutputStream(directoryPath + "//" + fileNamedwld + ".txt");

			@SuppressWarnings("resource")

			BufferedOutputStream BOS = new BufferedOutputStream(fileOPstream);
			BOS.write(b, 0, (int) readBytes);

			System.out.println("Requested file - " + fileNamedwld + ", has been downloaded to your desired directory " + directoryPath);
			System.out.println(" ");
			System.out.println("Display Name: " + fileNamedwld + ".txt");

			BOS.flush();
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int validation(ObjectOutputStream oos,ObjectInputStream ois, int peerid) throws IOException {
		int flag=0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter your Username");
			String username = br.readLine();
			System.out.println("Enter your Password");
			String pwd = br.readLine();

			oos.writeObject(username);
			oos.writeObject(pwd);
			oos.writeObject(peerid);
			flag = (int)ois.readObject();

			}
		catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return flag;
	}
}


