/*Don't forget to run ds-server in -n mode*/


import java.io.*;
import java.net.*;

public class A1 {
	public static Socket s;
	public static BufferedReader brin;
	public static DataOutputStream dout;
	public static String str;
	public static String res;
	public static int totalRecs;	
	public static int timer = 0;
	public static String prevSched = "";

	public static void main(String[] args) throws Exception	{
		try	{
			s = new Socket("localhost", 50000);
			brin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			dout = new DataOutputStream(s.getOutputStream());

			/*------------Handshake etc------------*/
			str = "HELO";
			doSend();
			res = brin.readLine();
			if (res.equals("OK"))	{str = "AUTH " + System.getProperty("user.name");doSend();}
			res = brin.readLine();
			if (res.equals("OK"))	{str = "REDY";doSend();}

			res = brin.readLine();
			String[] jobn = res.split(" ");
			/*-------------------------------------*/


			/*----------Get info------------------*/
			str = "GETS All";
			doSend();
			res = brin.readLine();
			String[] getData = res.split(" ");
			totalRecs = Integer.valueOf(getData[1]);
			int recLength = Integer.valueOf(getData[2]);

			str = "OK";
			doSend();
			String largest = "";
			int noCores = 0;
			for (int i = 0; i < totalRecs; i++)	{
				res = brin.readLine();
				if (Integer.valueOf(res.split(" ")[4]) > noCores)	{
					noCores = Integer.valueOf(res.split(" ")[4]);
					largest = res;
				}
			}
			str = "OK";
			doSend();
			/*-------------------------------------*/

			/*-------------Get largest available core and schedule--------*/
			//JOBN = submitTime jobID runtime core mem disk
			//curr largest = type ID state curStartTime core mem disk
			res = brin.readLine(); 
			String[] curr = getLargest(jobn[4], jobn[5], jobn[6]);
			while (!res.equals("NONE"))	{
				str = "SCHD " + String.valueOf(timer) + " " + curr[0] + " " + curr[1];
				timer++;
				doSend();
				brin.readLine(); //OK
				str = "REDY";
				doSend();
				res = brin.readLine(); //Get next JOBN
				prepNext();
				if (!res.equals("NONE"))	{
					jobn = res.split(" ");
				}
			}

			str = "QUIT";
			doSend();
			res = brin.readLine();
			System.out.println(res);
			/*-------------------------------------------------------------*/
 
 			
				


		} catch(Exception e)	{
			System.out.println(e);
		}
	}

	static void doSend() throws Exception	{
		try	{
			str += "\n";
			dout.write(str.getBytes());
			dout.flush();
		} catch (Exception e)	{
			System.out.println(e);
		}
	}

	static String[] getLargest(String cores, String mem, String disk) throws Exception	{
		try	{
			str = "GETS Capable " + cores + " " + mem + " " + disk;
			doSend();
			String largest = "";
			res = brin.readLine();
			totalRecs = Integer.valueOf(res.split(" ")[1]); //DATA x y
			str = "OK"; doSend();
			int noCores = 0;
			for (int i = 0; i < totalRecs; i++)	{
				res = brin.readLine();
				if (Integer.valueOf(res.split(" ")[4]) > noCores)	{
					if (!prevSched.equals(res))	{
						noCores = Integer.valueOf(res.split(" ")[4]);
						largest = res;
						prevSched = res;
					}
				}
			}
			
			str = "OK"; doSend();
			brin.readLine();
			return largest.split(" ");
		} catch (Exception e)	{
			System.out.println(e);
		}
		return null;
	}

	static void prepNext() throws Exception	{
		try	{
			switch (res.split(" ")[0])	{
				case "NONE":
					break;
				case "JOBN":
					break;
				case "JCPL":
					str = "REDY";doSend();
					res = brin.readLine();
					prepNext();
					break;
			}
		} catch (Exception e)	{
			System.out.println(e);
		}
	}
}

