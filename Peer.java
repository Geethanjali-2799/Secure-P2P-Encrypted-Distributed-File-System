import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Peer {

	@SuppressWarnings({"unchecked", "rawtypes", "resource", "unused"})

	public static void main(String args[]) throws Exception {
		Peer peer = new Peer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}

	public Peer(int peerServerPort, int peerid) throws NumberFormatException, IOException {
		Socket socket;
		ArrayList<FileInfo> arrList = new ArrayList<FileInfo>();
		ObjectInputStream inputStream;
		ObjectOutputStream outputStream;
		String dirName, dirPath = null;

		try {
			BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Welcome to the Client ::");
			System.out.println(" ");
			socket = new Socket("10.0.0.125", 7799);
			System.out.println("Connection has been established with the Server");

			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			while (true) {
				int flag = validation(outputStream, inputStream, peerid);
				if (flag == 1) {
					System.out.println("User Verified");
					break;
				} else {
					System.out.println("User not Verified!!!\n PLEASE TRY AGAIN");
				}
			}

			System.out.println("Enter the directory that contain the files -->");
			dirName = sc.readLine();
			dirPath = "./peer_to_peer_Files/" +dirName+ "/";

			ServerDownload serverDownload = new ServerDownload(peerServerPort, dirPath);
			serverDownload.start();


			File folder = new File(dirPath);
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

			outputStream.writeObject(arrList);


			System.out.println("Enter the operation you want to perform:\n1.Create File\n2.Read File\n3.Write File \n4.Delete File \n5.Download File \n0.To Quit");
			int var2 = Integer.parseInt(sc.readLine());
			while (var2 != 0) {
				String file_name;
				if (var2 == 1) {
					System.out.println("Enter the file name to Create:");

					// Reading File name
					file_name = sc.readLine();
					//function to create a new file..
					newFile(dirPath, file_name);
					System.out.println("Enter the operation you want to perform:\n1.Create File\n2.Read File\n3.Write File \n4.Delete File \n5.Download File \n0.To Quit");
					var2 = Integer.parseInt(sc.readLine());
				} else if (var2 == 2) {


					System.out.println("Enter the file name to Read:");

					// Reading File name
					file_name = sc.readLine();
					readFile(dirPath, file_name);
					System.out.println("Enter the operation you want to perform:\n1.Create File\n2.Read File\n3.Write File \n4.Delete File \n5.Download File \n0.To Quit");
					//System.out.println("1. Create File\n 2. Read File");
					var2 = Integer.parseInt(sc.readLine());

				} else if (var2 == 3) {


					//function to write a file...
					System.out.println("Enter the file name to Write:");

					// Reading File name
					file_name = sc.readLine();
					writeFile(dirPath, file_name);
					System.out.println("Enter the operation you want to perform:\n1.Create File\n2.Read File\n3.Write File \n4.Delete File \n5.Download File \n0.To Quit");
					var2 = Integer.parseInt(sc.readLine());
				} else if (var2 == 4) {


					//function to delete a file...
					System.out.println("Enter the file name to Delete:");

					// Reading File name
					file_name = sc.readLine();
					deleteFile(dirPath, file_name);
					System.out.println("Enter the operation you want to perform:\n1.Create File\n2.Read File\n3.Write File \n4.Delete File \n5.Download File \n0.To Quit");
					var2 = Integer.parseInt(sc.readLine());

				} else if (var2 == 5) {
					System.out.println("Enter the desired file name that you want to downloaded from the list of the files available in the Server ::");
					String fileName = sc.readLine();
					outputStream.writeObject(fileName);

					System.out.println("Waiting for the reply from Server...!!");

					ArrayList<FileInfo> peers = new ArrayList<FileInfo>();
					peers = (ArrayList<FileInfo>) inputStream.readObject();

					int result = peers.get(0).peerid;
					int port = peers.get(0).portNumber;
					System.out.println("Connecting to Peer " + result + " on port " + port);
					int otherPeerPort = port;

					String otherPeerIP = "10.0.0.125";

					download(otherPeerIP, otherPeerPort, fileName, dirPath);

					System.out.println("Enter the operation you want to perform:\n1.Create File\n2.Read File\n3.Write File \n4.Delete File \n5.Download File \n0.To Quit");
					var2 = Integer.parseInt(sc.readLine());
				}
			}
		} catch (Exception e) {
			System.out.println("Error in establishing the Connection between the Client and the Server!! ");
			System.out.println("Please cross-check the host address and the port number..");
		}
	}

	public static void download(String otherPeerIP, int otherPeerPort, String fileName, String dirPath) throws ClassNotFoundException {
		try {
			@SuppressWarnings("resource")
			Socket clientAsServersocket = new Socket(otherPeerIP, otherPeerPort);


			ObjectOutputStream outputStream = new ObjectOutputStream(clientAsServersocket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(clientAsServersocket.getInputStream());
			outputStream.writeObject(fileName);
			int readBytes = (int) inputStream.readObject();


			byte[] b = new byte[readBytes];

			inputStream.readFully(b, 0, readBytes);

			OutputStream fileOPstream = new FileOutputStream(dirPath + "//" + fileName + ".txt");

			@SuppressWarnings("resource")

			BufferedOutputStream BOS = new BufferedOutputStream(fileOPstream);
			BOS.write(b, 0, (int) readBytes);

			System.out.println("Requested file - " + fileName + ", has been downloaded to your desired directory " + dirPath);
			System.out.println(" ");
			System.out.println("Display Name: " + fileName + ".txt");

			BOS.flush();
			clientAsServersocket.close();
		} catch (IOException ex) {
			Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int validation(ObjectOutputStream outputStream,ObjectInputStream inputStream, int peerid) throws IOException {
		int flag=0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter your Username");
			String username = br.readLine();
			System.out.println("Enter your Password");
			String pwd = br.readLine();

			outputStream.writeObject(username);
			outputStream.writeObject(pwd);
			outputStream.writeObject(peerid);
			flag = (int)inputStream.readObject();

			}
		catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return flag;
	}

	public void readFile(String strPath, String strName) {

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(System.in));

			File file = new File(
					strPath + "" + strName + ".txt");

			Scanner sc=new Scanner(file);
			sc.useDelimiter("\\Z");

			System.out.println(sc.next());

		}
		catch(IOException e) {
			System.out.println("An error occurred.");
		}
	}
	public void newFile(String strPath, String strName) {

		try {

			BufferedReader br = new BufferedReader(
					new InputStreamReader(System.in));

			File create_f
					= new File(strPath + "" + strName + ".txt");
			if (create_f.createNewFile()) {
				System.out.println("File created: " + create_f.getName());
				System.out.println("Absolute path: " + create_f.getAbsolutePath());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	public void writeFile(String strPath, String strName) throws IOException
	{
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));

		readFile(strPath,strName);

		String text;
		System.out.println("Enter the following the data");
		text=br.readLine();

		Path fileName = Path.of(strPath + "" + strName + ".txt"
		);

		Writer output;
		output = new BufferedWriter(new FileWriter(fileName.toFile(),true));  //clears file every time

		output.append("\n"+ text);
		output.close();
	}
	public void deleteFile(String strPath, String strName) throws IOException
	{
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		File file
				= new File(strPath + "" + strName + ".txt");

		if (file.delete()) {
			System.out.println("File deleted successfully");
		}
		else {
			System.out.println("Failed to delete the file");
		}
	}
}


