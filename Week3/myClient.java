import java.io.*;
import java.net.*;

public class myClient	{
	public static void main(String[] args)	{
		try	{
			Socket s = new Socket("localhost", 12345);
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());

			String str=""; 

			dout.writeUTF("HELO");
			dout.flush();

			while (!str.equals("stop"))	{
				str = din.readUTF();
				switch(str)	{
					case "G'DAY":
						System.out.println("Server: " + str);
						dout.writeUTF("BYE");
						dout.flush();
						break;
					case "BYE":
						System.out.println("Server: " + str);
						str = "stop";
						break;
				}
			}

			System.out.println("Client closed.");
			din.close();
			dout.close();
			s.close();
		}catch(Exception e)	{
			System.out.println(e);
		}
	}
}
