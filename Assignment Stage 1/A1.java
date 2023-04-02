/*Don't forget to run ds-server in -n mode*/


import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class A1 {
	public static Socket s;
	public static BufferedReader brin;
	public static DataOutputStream dout;
	public static String str;
	public static String res;
	public static ArrayList<String> largeList = new ArrayList<String>();
	public static int jobCount = 0;
	public static int totalRecs;
	public static int totalLargest;

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

			/*-------------Get largest available core--------*/
			//JOBN = submitTime jobID runtime core mem disk
			//GETS data = type ID state curStartTime core mem disk
			res = brin.readLine();
			getLargest();
			// # of largest systems stored in "totalLargest" variable
			/*-----------------------------------------------*/


			/*-----------Start scheduling------------------------*/
			while (!res.equals("NONE"))	{
				int counter = (jobCount % totalLargest);
				str = "SCHD " + String.valueOf(jobCount) + " " + largeList.get(counter).split(" ")[0] + " " + largeList.get(counter).split(" ")[1];
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

	static void  getLargest() throws Exception	{
		try	{
			str = "GETS All";
			doSend();
			res = brin.readLine();
			totalRecs = Integer.valueOf(res.split(" ")[1]); //DATA x y
			str = "OK"; doSend();

			//Find largest core
			String largest = "";
			int noCores = 0;
			String[] totalList = new String[totalRecs];
			for (int i = 0; i < totalRecs; i++)	{
				res = brin.readLine();
				totalList[i] = res;

				if (Integer.valueOf(res.split(" ")[4]) > noCores)	{
					noCores = Integer.valueOf(res.split(" ")[4]);
					largest = res;
				}
			}

			//Get all instances of the largest core
			for (int i = 0; i < totalList.length; i++)	{
				if (totalList[i].split(" ")[0].equals(largest.split(" ")[0]))	{
					largeList.add(totalList[i]);
				}
			}
			totalLargest = largeList.size();


			str = "OK"; doSend();
			brin.readLine();
		} catch (Exception e)	{
			System.out.println(e);
		}
	}

	static void prepNext() throws Exception	{
		try	{
			switch (res.split(" ")[0])	{
				case "NONE":
					break;
				case "JOBN":
					jobCount++;
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

