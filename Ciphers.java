import java.util.Scanner;
import java.util.*;
import java.io.*;

public class Ciphers {
	public static String readFile( String file ) throws IOException {
    	BufferedReader reader = new BufferedReader( new FileReader (file));
    	String         line = null;
    	StringBuilder  stringBuilder = new StringBuilder();
    	String         ls = System.getProperty("line.separator");
	
	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

    	return stringBuilder.toString();
	}
	
	public static char[] alphabet = { 
										'a', 'b', 'c', 'd', 'e', 'f', 
										'g', 'h', 'i', 'j', 'k', 'l', 'm', 
										'n', 'o', 'p', 'q', 'r', 's', 't', 
										'u', 'v', 'w', 'x', 'y', 'z'
									};
	
	// Frequencies of each character in the English language
	public static double[] frequencyArray  = { 
												0.08167, 0.01492, 0.02782, 0.04253,
												0.12702, 0.02228, 0.02015, 0.06094, 
												0.06996, 0.00153, 0.00772, 0.04025,
												0.02406, 0.06749, 0.07507, 0.01929, 
												0.00095, 0.05987, 0.06327, 0.09056, 
												0.02758, 0.00978, 0.02360, 0.00150, 
												0.01974, 0.00074 
											};
	
	public static int letterToNum(char input) {
		for(int i = 0; i < 26; i++){
			if (alphabet[i] == Character.toLowerCase(input)) {
				return (i+1);
			}
		}
		return -1;
	}
	
	public static char numToLetter(int input) {
		int index = (input - 1) % 26;
		while (index < 0) {
			index += 26;
		}
		return alphabet[index];
	}
	
	public static int letterDistance(char A, char B) {
		return Math.abs(letterToNum(A) - letterToNum(B));
	}
	
	public static double frequencyOfChar(char input) {
		return frequencyArray[letterToNum(input)];
	}

	public static char caesarShiftChar(char input, int shift_amount) {
		int num_in = letterToNum(input);
		return numToLetter(shift_amount + num_in);
	}

	public static String caesarShift(String input, int shift_amount) {
		char[] arrayIn = input.toCharArray();
		int n = arrayIn.length;
		char[] arrayOut = new char[n];
		for (int i = 0; i < n; i++)
		{
			arrayOut[i] = caesarShiftChar(arrayIn[i] , shift_amount);
		}
		String rv = new String(arrayOut);
		return rv;
	}

	public static String caesarShiftDecrypt(String input, int shift_amount) {
		return caesarShift(input, (-1)* shift_amount);
	}

	public static String vigenereEncrypt(String input, String key) {
		char[] arrayIn = input.toCharArray();
		char[] keyArray = key.toCharArray();
		int n = arrayIn.length;
		int key_length = keyArray.length;
		char[] arrayOut = new char[n];
		int shift_amount = 0;
		for (int i = 0; i < n; i++) {
			// Note the 'minus one'; 
			// this is to match the convention that a shift of 'A' in the key leads to 'no change'
			shift_amount = letterToNum(keyArray[i % key_length]) - 1;
			arrayOut[i] = caesarShiftChar(arrayIn[i], shift_amount);
		}
		String rv = new String(arrayOut);
		return rv;
	}

	public static String vigenereDecrypt(String input, String key) {
		char[] arrayIn = input.toCharArray();
		char[] keyArray = key.toCharArray();
		int n = arrayIn.length;
		int key_length = keyArray.length;
		char[] arrayOut = new char[n];
		int shift_amount = 0;
		for (int i = 0; i < n; i++) {
			// Note the 'minus one'; 
			// this is to match the convention that a shift of 'A' in the key leads to 'no change'
			shift_amount = letterToNum(keyArray[i % key_length]) - 1;
			arrayOut[i] = caesarShiftChar(arrayIn[i], (-1) * shift_amount);
		}
		String rv = new String(arrayOut);
		return rv;
	}
	
	// Returns list of hypotheses for key length by seeking repetitions of letter sequences
	public static List<Integer> kasiskiExamine(String input, int minimum_match_length) {
		// The return value
		Map<String, Integer> repetition_dict = new HashMap<String, Integer>();
		// For later analysis
		List<Integer> key_hypotheses = new Vector<Integer>(); 
		
		// Step one: find repetitions in the text of length greater than 3
		char[] arrayIn = input.toCharArray();
		int n = arrayIn.length;
		System.out.println("n = " + n);
		// If the first m characters of input 2 matches input 1, save n
		// i iterates over arrayIn
		for (int i = 0; i < n; i++) {
			int num_matches = 0;
			for(int j = 0; j < n; j++) {
				if (j != i) {
					if (arrayIn[i] != arrayIn[j]) {
						num_matches = 0;
						continue;
					}
					int k = i;
					char[] tmpArray = new char[20];
					int z = 0;
					while ((Math.max(j, k) < n) && (arrayIn[k] == arrayIn[j])) {
						if (z < 20) {
							tmpArray[z] = arrayIn[j];
							z++;
						}
						num_matches++;
						j++;
						k++;
					}
					if (num_matches >= minimum_match_length) {
						 // Note that we're printing i instead of k
						System.out.println(num_matches + " matches at i = " + i + ", j = " + j);
						String tmpString = new String(tmpArray);
						System.out.println("Match string: " + tmpString);
						Integer tmpInt = new Integer(Math.abs(i - j));
						repetition_dict.put(tmpString, tmpInt);
						key_hypotheses.add(tmpInt);
						
						// Important line which prevents annoying duplicates
						i += num_matches;
					}
					num_matches = 0;
				}
			}
		}
		// At this point, we have a list of hypothesized key lengths
		// (The distance between repetitions is a key length candidate)
		Collections.sort(key_hypotheses);
		System.out.println("--------------");
		System.out.print("Key hypotheses: ");
		for (Integer x : key_hypotheses) System.out.print(x + "; ");
		System.out.println();
		return key_hypotheses;
	}
	
	// Crack Caesar cipher using (naive) frequency analysis
	// Takes a char array instead of a String (for convenience of use by the vigenere function)
	public static void caesarCrack(char[] arrayIn) {
		int n = arrayIn.length;
		
		// Step one: find frequencies of letters in input
		int[] charCounts = new int[26];
		double[] charFrequencies = new double[26];
		
		for(char letter : arrayIn) {
			charCounts[letterToNum(letter) - 1] += 1;
		}
		
		for(int i = 0; i < 26; i++) {
			charFrequencies[i] = (charCounts[i] / (double)n);
		}
		// Now we have frequencies of each letter in the input
		
		// Naive guessing method: Find the most frequent letter and assume it is E
		double max = 0;
		int max_char = 0;
		for (int i = 0; i < 26; i++) {
			if(max < charFrequencies[i]) {
				max = charFrequencies[i];
				max_char = i;
			}
		}
		
		int shift_hypothesis_one = Math.abs((max_char + 1) - letterToNum('e'));
		
		System.out.println("Shift hypothesis one: shift of " + shift_hypothesis_one);
		System.out.print("Interpretation of text using this hypothesis: ");
		// Decrypt first 30 characters (or all the text, if shorter than 30 characters)
		int upper_bound = Math.min(30, n);
		for(int i = 0; i < upper_bound; i++) {
			System.out.print(caesarShiftChar(arrayIn[i], (-1)*shift_hypothesis_one));
		}
		System.out.println();
		//for (double x: charFrequencies) System.out.print(x + "; ");
	}
	
	/*
	public static vigenereCrack(String input, int key_length) {
		char[] arrayIn = input.toCharArray();
		int n = arrayIn.length;
		for(int i = 0; i < key_length; i++) {
			for(int j = 0; j < n; j += key_length) {
				arrayIn[]
			}
		}		
		
	}
	*/
	
	public static void main(String[] args) {
		//String input = args[0];
		//char[] inputArray = input.toCharArray();
		
		//System.out.println(letterToNum(inputArray[0]));
		
		//int input_num = Integer.parseInt(args[0]);
		//System.out.println(input_num);
		//System.out.println(numToLetter(input_num));
		
		//Testing Caesar Shift (Char)
		//System.out.println(inputArray[0]);
		//System.out.println(caesarShift(inputArray[0], 1));
		//System.out.println(caesarShift(inputArray[0], 2));
		//System.out.println(caesarShift(inputArray[0], 52));
		
		// Testing Caesar Shift (String)
		//System.out.println(input);
		//String cipherText = caesarShift(input, 1);
		//System.out.println(cipherText);
		//System.out.println(caesarShiftDecrypt(cipherText, 1));
		
		// Testing Vigenere Encryption
		// Using the example from wikipedia
		/* 
		String test_case = "ATTACKATDAWN";
		String test_key = "LEMON";
		System.out.println(test_case);
		String cipherText = vigenereEncrypt(test_case, test_key);
		System.out.println(cipherText);
		*/
		
		// Testing Vigenere Decryption, same case as above
		/*
		String test_case = "ATTACKATDAWN";
		String test_key = "LEMON";
		String cipherText = vigenereEncrypt(test_case, test_key);
		System.out.println(cipherText);
		String plainText = vigenereDecrypt(cipherText, test_key);
		System.out.println(plainText);
		*/
		
		// Testing file reader function
		String cipherFileText = null; 
		try {
			cipherFileText = readFile("vigenereCipherText.txt");
			cipherFileText = cipherFileText.replace("\n", "");
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
			System.exit(0);
		}
		
		// Testing Kasiski reader with min match length = 6
		// Running this function on the example cipher implies that the key length is 7, due to common factors
		//kasiskiExamine(cipherFileText, 6);
		
		// Testing caesarCrack
		String caesarPlainText = null;
		try {
			caesarPlainText = readFile("caesarSampleText.txt");
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
			System.exit(0);
		}
		String caesarCipherText = caesarShift(caesarPlainText, 15);
		char[] arrayTmp = caesarCipherText.toCharArray();
		caesarCrack(arrayTmp);
		
		
		
	}

}