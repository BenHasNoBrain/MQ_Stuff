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

			str = "HELO\n";
			dout.write(("HELO\n").getBytes());
			dout.flush();

			

			while(!str.equals("stop"))	{
				System.out.println(brin.readLine());
				str = brout.readLine();

				dout.write((str + "\n").getBytes());
				dout.flush();
			}


		} catch (Exception e)	{
			System.out.println(e);
		}
	}
}
