import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class firstFit	{
	public static Socket s;
	public static BufferedReader brin;
	public static DataOutputStream dout;
	public static String theRequest;
	public static String theResponse;
	public static ArrayList<String[]> serversList = new ArrayList<String[]>();	//Modified at runtime

	public static void main(String[] args) throws Exception	{
		try	{
			s = new Socket("localhost", 50000);
			brin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			dout = new DataOutputStream(s.getOutputStream());
			

			/*------------Handshake etc------------*/
			theRequest = "HELO";
			doSend();
			theResponse = brin.readLine();
			if (theResponse.equals("OK"))	{theRequest = "AUTH " + System.getProperty("user.name");doSend();}
			theResponse = brin.readLine();
			if (theResponse.equals("OK"))	{theRequest = "REDY";doSend();}

			theResponse = brin.readLine();
			/*-------------------------------------*/

			theRequest = "REDY";
			doSend();
			theResponse = brin.readLine();
			assignAllJobs();

			/*-----------------------------------------*/

			theRequest = "QUIT";
			doSend();
		} catch(Exception e)	{
			System.out.println(e);
			e.printStackTrace();
		}
	}

	static void doSend() throws Exception	{
		try	{
			theRequest += "\n";
			dout.write(theRequest.getBytes());
			dout.flush();
		} catch (Exception e)	{
			System.out.println(e);
			e.printStackTrace();
		}
	}


	static void assignAllJobs() throws Exception {
		doJob:
		while(!theResponse.equals("NONE"))	{
			String[] job = theResponse.split(" ");
			while (!job[0].equals("JOBN"))	{
				job = brin.readLine().split(" ");
				if (job[0].equals("JCPL"))	{
					theRequest = "REDY";
					doSend();
				}
				if (job[0].equals("NONE"))	{break doJob;}
			}
			String[] bestFitServer = {};
			
			int tmp = 0;
			int bestFit = 999999;

			serversList.clear();
			getAllServers();

		forLoop:	for (String[] server : serversList)	{
				tmp = Integer.valueOf(server[4]) - Integer.valueOf(job[4]);
				if ((tmp < bestFit) && (tmp >= 0))	{
					if (Integer.valueOf(server[5]) > Integer.valueOf(job[5]))	{
						if (Integer.valueOf(server[6]) > Integer.valueOf(job[6]))	{
							bestFit = tmp;
							bestFitServer = server;
							break forLoop;
						}
					}
				}
			}

			if (bestFitServer.length == 0)	{
				theRequest = "GETS Capable " + job[4] + " " + job[5] + " " + job[6];
				doSend();
				int capable = Integer.valueOf(brin.readLine().split(" ")[1]);
				theRequest = "OK";
				doSend();

				for (int i = 0; i < capable; i++)	{
					theResponse = brin.readLine();
					if (i == 0)	{bestFitServer = theResponse.split(" ");}
				}

				theRequest = "OK";
				doSend();
				brin.readLine();

			}
			
			theRequest = "SCHD " + job[2] + " " + bestFitServer[0] + " " + bestFitServer[1];
			doSend();
			brin.readLine();

			theRequest = "OK";
			doSend();
			brin.readLine();


			theRequest = "REDY";
			doSend();
			theResponse = brin.readLine();
		}

		theRequest = "QUIT";
		doSend();
	}

	static void getAllServers() throws Exception	{
		theRequest = "GETS All";
		doSend();
		int allData = Integer.valueOf(brin.readLine().split(" ")[1]); //DATA x y
		theRequest ="OK";
		doSend();

		for (int i = 0; i < allData; i++)	{
			serversList.add(brin.readLine().split(" "));
		}

		theRequest = "OK";
		doSend();
		brin.readLine();
	}
}
