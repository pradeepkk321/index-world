package com.pk.indexworld;

import java.util.Arrays;

public class SentenceScramble {

	public static void main(String[] args) {
		String sentence = "Hello to my world";
		System.out.println(scrambleSentence(sentence));

		String sentence2 = "This is a sample sentence";
		System.out.println(scrambleSentence(sentence2));
		
		String sentence3 = "How long do contestants have to answer during the first two rounds of Jeopardy";
		System.out.println(scrambleSentence(sentence3));
	}

	private static String scrambleSentence(String sentence) {

		String newSentence = "";

		String[] words = sentence.split(" ");

		for (int i = 0; i < words.length; i++) {

			if (words[i].length() < 4) {
				if (newSentence.length() > 0)
					newSentence += " " + words[i];
				else
					newSentence += words[i];
			}

			else {
				char[] allCharacters = words[i].toCharArray();
				char[] middleCharacters = new char[words[i].length() - 2];
				for (int j = 0; j < middleCharacters.length; j++) {
					middleCharacters[j] = allCharacters[j + 1];
				}
				Arrays.sort(middleCharacters);

				String word = "" + allCharacters[0];
				if (i % 2 == 0) {
					for (int k = middleCharacters.length - 1; k >= 0; k--)
						word += middleCharacters[k];
				} else {
					for (int k = 0; k < middleCharacters.length; k++)
						word += middleCharacters[k];
				}
				word += allCharacters[allCharacters.length - 1];
				if (newSentence.length() > 0)
					newSentence += " " + word;
				else
					newSentence += word;

			}
		}
		return newSentence;
	}
}
