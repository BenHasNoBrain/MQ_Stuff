public class charFreq	{
	
	public static void main(String[] args)	{
		String strs="weeheehee poot";
		char str[] = strs.toCharArray();
		int[] freq = new int[str.length];
		
		for (int i = 0; i < str.length; i++)	{
			freq[i] = 1; //Visit char

			for (int j = i + 1; j < str.length; j++)	{
				if (str[i] == str[j])	{freq[i]++;str[j]='0';}
			}
		}


		System.out.println("Result:");
		for (int i = 0; i < freq.length; i++)	{
			System.out.println(str[i] + ": " + freq[i]);
		}
	}

}
