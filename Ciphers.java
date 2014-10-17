import java.util.Scanner;
import java.util.*;
import java.io.*;

public class Ciphers {
	
	// Credit to stack overflow user OscarRyz for this file reading function
	public static String readFile(String file) throws IOException {
    		BufferedReader reader = new BufferedReader( new FileReader (file));
    		String line = null;
    		StringBuilder stringBuilder = new StringBuilder();
    		String ls = System.getProperty("line.separator");
		    
		    while(( line = reader.readLine()) != null ) {
		        stringBuilder.append(line);
		        stringBuilder.append(ls);
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
	
	// Returns population variance of array
	public static double populationVariance(double[] arrayIn) {
		double var = 0;
		int n = arrayIn.length;
		double sum = 0.0;
		for(double x : arrayIn) sum += x;
		double mean = sum / ((double)n);
		
		double variance = 0.0;
		for(double x: arrayIn) variance += Math.pow(x - mean, 2);
		variance = variance / ((double) n);
		return variance;
		
	}
	
	// Returns array of frequencies of each letter in the input array
	public static double[] letterFreqArray(char[] arrayIn) {
		int n = arrayIn.length;
		// Step one: find frequencies of letters in input
		int[] charCounts = new int[26];
		double[] charFrequencies = new double[26];
		
		int z = 0;
		for(char letter : arrayIn) {
			if ((z = letterToNum(letter)) != -1) {
				charCounts[z - 1] += 1;
			}
		}
		
		for(int i = 0; i < 26; i++) {
			charFrequencies[i] = (charCounts[i] / (double)n);
		}
		
		return charFrequencies;
	}
	
	// Overload of above function
	public static double[] letterFreqArray(String input) {
		char[] arrayIn = input.toCharArray();
		return letterFreqArray(arrayIn);
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
	
	// Small helper function: gives index of largest element in array
	public static int maxInArray(int[] arrayIn) {
		int max = 0;
		int max_pos = 0;
		int n = arrayIn.length;
		int i = 0;
		for( ; i < n; i++) {
			if(max < arrayIn[i]) {
				max = arrayIn[i];
				max_pos = i;
			}
		}
		return max_pos;
	}
	
	// Overload of above, for doubles
	public static int maxInArray(double[] arrayIn) {
		double max = 0;
		int max_pos = 0;
		int n = arrayIn.length;
		int i = 0;
		for( ; i < n; i++) {
			if(max < arrayIn[i]) {
				max = arrayIn[i];
				max_pos = i;
			}
		}
		return max_pos;
	}
	
	// Crack Caesar cipher using frequency analysis; attempts to minimize distance between frequency arrays
	// Takes a char array instead of a String (for convenience of use by the vigenere function)
	public static int caesarCrack(char[] arrayIn) {

		int n = arrayIn.length;
		// Step one: find frequencies of letters in input
		
		double[] charFrequencies = letterFreqArray(arrayIn);
		// Now we have frequencies of each letter in the input
		
		double[] fArrayCopy = new double[frequencyArray.length];
		for(int i = 0; i < 26; i++) fArrayCopy[i] = frequencyArray[i];
		
		int best_shift = 0;
		double least_deviation = 100.0;
		
		int max_char = maxInArray(charFrequencies);
		for(int iterations = 0; iterations < 26; iterations++) {
			
			int max_charf = maxInArray(fArrayCopy);
			//System.out.println("On: " + numToLetter(max_charf + 1));
			int shift_hypothesis = Math.abs((max_char + 1) - (max_charf + 1));
			double sum_of_deviation = 0.0;
			for(int i = 0; i < 26; i++) { 
				// cipherFreq: frequency of (decrypted)letter in cipher text
				double cipherFreq = charFrequencies[i]; 
				// englishFreq: frequency of this letter (when decrypted using the current rule) in English language
				double englishFreq = fArrayCopy[letterToNum(caesarShiftChar(numToLetter(i + 1) , (-1)*shift_hypothesis)) - 1];
				double deviation = (Math.abs(cipherFreq - englishFreq));
				sum_of_deviation += deviation;
			}
			sum_of_deviation = Math.sqrt(sum_of_deviation);
			//System.out.println("Shift of " + shift_hypothesis + " yields deviation: " + sum_of_deviation);
			if (least_deviation > sum_of_deviation) {
				least_deviation = sum_of_deviation;
				best_shift = shift_hypothesis;
			}
			fArrayCopy[max_charf] = 0; 	// Ensures that we go down through the most common letters -- E, T, so on
		}
		
		return best_shift;
	}
	
	// Overload of above function
	public static int caesarCrack(String input) {
		char[] arrayIn = input.toCharArray();
		return caesarCrack(arrayIn);
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
						//System.out.println(num_matches + " matches at i = " + i + ", j = " + j);
						String tmpString = new String(tmpArray);
						//System.out.println("Match string: " + tmpString);
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
	
	/* 	
	* 	Treats input as <key_length> number of parallel ciphers; finds variances for each of these ciphers, then returns the mean
	* 	Idea is that the correct key length will yield caesar ciphers, which have pop var. close to that of English
	* 	So this one number is a metric for how well the key_length fits a cipher to English
	* 	This serves as an alternative to the Kasiski Examination 
	*/
	public static double vigenereMeanVariance(String input, int key_length) {
		char[] arrayIn = input.toCharArray();
		double meanVariance = 0.0;
		int n = arrayIn.length;
		int caesarArrayLength = (n / key_length) + (key_length - 1);
		for(int i = 0; i < key_length; i++) {
			char[] caesarArray = new char[caesarArrayLength];
			int z = 0;
			for(int j = i; j < n; j += key_length) {
				caesarArray[z] = arrayIn[j];
				z++;
			}
			double caesarVar = populationVariance(letterFreqArray(caesarArray));
			meanVariance += caesarVar;
		}		
		meanVariance = (meanVariance / ((double)key_length));
		return meanVariance;	
	}
	
	// Returns key length guess which brings cipher text's mean variance closest to English letters' population variance
	// The return value will likely be the key length or a multiple of the key length.
	public static int vigenereVarianceAttack(String input, int upper_bound) {
		double min_diff = 1000.0;	// The minimum difference between cipher text mean variance and English population variance
		int best_key_length = -1;
		double englishPopVariance = populationVariance(frequencyArray);
		for(int key_length_guess = 1; key_length_guess < upper_bound; key_length_guess++) {
			double dist = Math.abs(vigenereMeanVariance(input, key_length_guess) - englishPopVariance);
			if (dist < min_diff) {
				min_diff = dist;
				best_key_length = key_length_guess;
			}
		}
		return best_key_length;
	}
	
	// Treats input as a <key_length> number of parallel caesar ciphers, then uses caesarCrack function
	public static String vigenereCrack(String input, int key_length) {
		char[] arrayIn = input.toCharArray();
		char[] keyArray = new char[key_length];
		int n = arrayIn.length;
		int caesarArrayLength = (n / key_length) + (key_length - 1);
		for(int i = 0; i < key_length; i++) {
			char[] caesarArray = new char[caesarArrayLength];
			int z = 0;
			for(int j = i; j < n; j += key_length) {
				caesarArray[z] = arrayIn[j];
				z++;
			}
			int caesarKey = caesarCrack(caesarArray);
			keyArray[i] = numToLetter(caesarKey + 1);
			
		}		
		String keyString = new String(keyArray);
		System.out.println("Key hypothesis: " + keyString);
		return keyString;
	}
	
	public static void main(String[] args) {
		
		// Testing vigenereCrack
		
		String vigenerePlainText = null;
		try {
			vigenerePlainText = readFile("vigenereSampleText.txt");
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
			System.exit(0);
		}
		vigenerePlainText = vigenerePlainText.replace("\n", "").replace("\r", "");
		String vigenereCipherText = vigenereEncrypt(vigenerePlainText, "abcdefg");
		
		// Now crack it!
		int key_length_cracked = vigenereVarianceAttack(vigenereCipherText, 10);
		System.out.println("Variance attack gives key length of: " + key_length_cracked);
		//kasiskiExamine(vigenereCipherText, 6);
		String key = vigenereCrack(vigenereCipherText, 7);
		
		

		
		
		
	}

}