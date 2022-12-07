 
import java.io.*;
import java.net.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Integer;

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
    				//serverSocket.close();
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
   	ObjectOutputStream oos;
	ObjectInputStream ois;
	String str;
	int index;

    @SuppressWarnings("unchecked")
	public void run()
    {
    	try
    	{  
    		InputStream is=socket.getInputStream();
    		oos = new ObjectOutputStream(socket.getOutputStream());
    		ois = new ObjectInputStream(is);

			String username = (String)ois.readObject();
			String pwd = (String)ois.readObject();
			int peerid=(int)ois.readObject();
			int flag=0;
			Scanner file=new Scanner(new File("/Users/tarunkrishnareddykolli/Desktop/SEFS/Validation.txt"));
			while(file.hasNext())
			{
				String a=file.next();
				String b=file.next();
				if(a.toLowerCase().equals(username.toLowerCase()))
				{
					if(b.equals(pwd))
					{
						flag=1;
						System.out.println("PeerID:"+peerid+" Verified and connected to the SERVER");
					}

				}
			}
			oos.writeObject(flag);

    		filesList=(ArrayList<FileInfo>)ois.readObject();
    		for(int i=0;i<filesList.size() ;i++)
    		{
    			globalArray.add(filesList.get(i));
    		}
    		System.out.println("Total number of files available in the Server that are received from all the connected clients: " +globalArray.size());
    	}
    	catch(IndexOutOfBoundsException e){
    		System.out.println("Index out of bounds exception");
    	}
    	catch(IOException e){
    		System.out.println("I/O exception");
    	}
    	catch(ClassNotFoundException e){
    		System.out.println("Class not found exception");
    	}

    	try {
    			str = (String) ois.readObject();
    	}
    	catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        ArrayList<FileInfo> sendingPeers = new ArrayList<FileInfo>();
        System.out.println("Searching for the file name...!!!"+ str+".txt");

           
        for(int j=0;j<globalArray.size();j++)
        {

           FileInfo fileInfo=globalArray.get(j);
		   String s=str+".txt";
           Boolean tf=(fileInfo.fileName.equals(s));
           if(tf)
           {	
        	   index = j;
        	   sendingPeers.add(fileInfo);
           }
        }

        try {
        	oos.writeObject(sendingPeers);
			System.out.println("Sending information to CLient");
        } 
        catch (IOException ex) {
         Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
 

