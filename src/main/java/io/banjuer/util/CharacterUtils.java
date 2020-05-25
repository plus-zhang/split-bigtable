package io.banjuer.util;

/**
 * 英文字符工具
 * @author guochengsen
 */
public class CharacterUtils {

	private static int GAP = 'a' - 'A';

	public static boolean isLetter(char c) {
		return c >= 'A' && c <= 'z';
	}

	public static char toLower(char c) {
		if (!isLetter(c))
			return c;
		if (isUpper(c)) {
			return (char) (c + GAP);
		}
		return c;
	}

	public static char toUpper(char c) {
		if (!isLetter(c))
			return c;
		if (isLower(c)) {
			return (char) (c - GAP);
		}
		return c;
	}

	public static boolean isLower(char c) {
		return !isUpper(c);
	}

	public static boolean isUpper(char c) {
		if (!isLetter(c))
			return false;
		return c - 'Z' <= 0;
	}

	public static void main(String[] args)
	{
		System.out.println('i'>'a');
	}

}
