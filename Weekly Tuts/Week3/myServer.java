import java.io.*;
import java.net.*;


public class myServer	{
	public static void main(String[] args) throws Exception	{
		try	{
			ServerSocket ss = new ServerSocket(12345);
			Socket s = ss.accept();
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String str = "";

			while(!str.equals("stop"))	{
				str = din.readUTF();
				switch(str)	{
					case "HELO":
						System.out.println("Client: " + str);
						dout.writeUTF("G'DAY");
						dout.flush();
						break;
					case "BYE":
						System.out.println("Client: " + str);
						dout.writeUTF("BYE");
						dout.flush();
						str = "stop";
						break;
				}
			}

			System.out.println("Server closed.");

			din.close();
			dout.close();
			s.close();
			ss.close();

		}	catch(Exception e){
			System.out.println(e);
		}
	}
}
