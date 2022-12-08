 
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")

public class Server
{
    public static ArrayList<FileInfo> globalArray = new ArrayList<FileInfo>();
 
    @SuppressWarnings("resource")
	public static void main(String args[]) throws Exception{
		Server server = new Server();
	}
    public Server() throws NumberFormatException, IOException
    {
    	
		
    	ServerSocket serverSocket=null;
    	Socket socket = null;
    	try{
    			serverSocket = new ServerSocket(7799);
    			System.out.println("Server started!! ");
    			System.out.println(" ");
    			System.out.println("Waiting for the Client to be connected ..");

    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	while(true)
    	{
    		try{
    				socket = serverSocket.accept();
    		}
    		catch(IOException e)
    		{
    			System.out.println("I/O error: " +e);
    		}
    		new ServerTestClass(socket,globalArray).start();
    	}
    }
}

class ServerTestClass extends Thread
{
	protected Socket socket;
	ArrayList<FileInfo> globalArray;
	public ServerTestClass(Socket clientSocket,ArrayList<FileInfo> globalArray)
	{
		this.socket=clientSocket;
		this.globalArray=globalArray;
	}

	ArrayList<FileInfo> filesList=new ArrayList<FileInfo>();
   	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;
	String str;
	int index;

    @SuppressWarnings("unchecked")
	public void run() {
		try {
			InputStream inputStream = socket.getInputStream();
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(inputStream);

			String username = (String) this.inputStream.readObject();
			String pwd = (String) this.inputStream.readObject();
			int peerid = (int) this.inputStream.readObject();
			int flag = 0;
			Scanner file = new Scanner(new File("/Users/tarunkrishnareddykolli/Desktop/SEFS/Validation.txt"));
			while (file.hasNext()) {
				String a = file.next();
				String b = file.next();
				if (a.toLowerCase().equals(username.toLowerCase())) {
					if (b.equals(pwd)) {
						flag = 1;
						System.out.println("PeerID:" + peerid + " Verified and connected to the SERVER");
					}

				}
			}
			outputStream.writeObject(flag);

			filesList = (ArrayList<FileInfo>) this.inputStream.readObject();
			for (int i = 0; i < filesList.size(); i++) {
				globalArray.add(filesList.get(i));
			}
			System.out.println("Total number of files available in the Server that are received from all the connected clients: " + globalArray.size());
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index out of bounds exception");
		} catch (IOException e) {
			System.out.println("I/O exception");
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
		}
		int choice=0;
		try {
			choice=(int) inputStream.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		while (choice==5) {
			try {
				str = (String) inputStream.readObject();
			} catch (IOException | ClassNotFoundException ex) {
				Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
			}

			ArrayList<FileInfo> sendingPeers = new ArrayList<FileInfo>();
			System.out.println("Searching for the file name...!!!" + str + ".txt");


			for (int j = 0; j < globalArray.size(); j++) {

				FileInfo fileInfo = globalArray.get(j);
				String s = str + ".txt";
				Boolean tf = (fileInfo.fileName.equals(s));
				if (tf) {
					index = j;
					sendingPeers.add(fileInfo);
				}
			}

			try {
				outputStream.writeObject(sendingPeers);
				System.out.println("Sending information to CLient");
			} catch (IOException ex) {
				Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}


 

