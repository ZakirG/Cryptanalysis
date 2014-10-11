import java.util.Scanner;

public class Ciphers {
	public static char[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 
						'g', 'h', 'i', 'j', 'k', 'l', 'm', 
						'n', 'o', 'p', 'q', 'r', 's', 't', 
						'u', 'v', 'w', 'x', 'y', 'z'
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
		return alphabet[index];
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
		String test_case = "ATTACKATDAWN";
		String test_key = "LEMON";
		System.out.println(test_case);
		String cipherText = vigenereEncrypt(test_case, test_key);
		System.out.println(cipherText);
		
	}

}