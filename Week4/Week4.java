import java.io.*;
import java.net.*;

public class Week4	{
	public static void main(String[] args) throws Exception	{
		try	{
			Socket s = new Socket("localhost", 50000);
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader brin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedReader brout = new BufferedReader(new InputStreamReader(System.in));

			String str = "";
			String res = "";
			int steps = 0;
			Boolean helo = false;
			Boolean auth = false;



			while(!str.equals("stop"))	{
				switch (steps)	{
					case -1:
						res = brin.readLine();
						System.out.println(res);
						if (res.equals("QUIT"))	{
							str = "stop";
							System.out.println("Quitting");
						} else {
							str = brout.readLine() + "\n";
							dout.write(str.getBytes());
							dout.flush();
						}

						break;
					case 0:
						System.out.println("Sending HELO");
						str = "HELO\n";
						dout.write(("HELO\n").getBytes());
						dout.flush();
						steps = 1;
						helo = true;
						break;
					case 1:
						System.out.println("Manually send AUTH and u/n");
						auth = true;
						steps = -1;
						break;
					default: 
						break;
				}
				
			}
		} catch (Exception e)	{
			System.out.println(e);
		}
	}
}
