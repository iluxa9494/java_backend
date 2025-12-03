package com.skillbox.cryptobot;

import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

public class CryptoBotApplicationTests {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)");

	@Test
	void testNumberPatternMatchingValidNumbers() {
		assertTrue(NUMBER_PATTERN.matcher("9999999.12").matches());
		assertTrue(NUMBER_PATTERN.matcher("0.0").matches());
		assertTrue(NUMBER_PATTERN.matcher("-123.456").matches());
		assertTrue(NUMBER_PATTERN.matcher(".123").matches());
		assertTrue(NUMBER_PATTERN.matcher("-0.1").matches());
	}

	@Test
	void testNumberPatternRejectInvalidNumbers() {
		assertFalse(NUMBER_PATTERN.matcher("abc").matches());
		assertFalse(NUMBER_PATTERN.matcher("123abc").matches());
		assertFalse(NUMBER_PATTERN.matcher("...123").matches());
	}

	@Test
	void testCommandProcessing() {
		String command = "/subscribe 9999999.12";
		String processed = command.replaceAll("(/subscribe|\\s)", "");
		assertEquals("9999999.12", processed);
	}

	@Test
	void testCommandProcessingWithSpaces() {
		String command = "/subscribe     123.45";
		String processed = command.replaceAll("(/subscribe|\\s)", "");
		assertEquals("123.45", processed);
	}

	@Test
	void testCommandWithoutNumber() {
		String command = "/subscribe";
		String processed = command.replaceAll("(/subscribe|\\s)", "");
		assertEquals("", processed);
	}

	@Test
	void testEmptyCommand() {
		String command = "";
		String processed = command.replaceAll("(/subscribe|\\s)", "");
		assertEquals("", processed);
	}

	@Test
	void contextLoads() {
		Pattern p = Pattern.compile("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)");
		String t = "/subscribe 9999999.12";
		System.out.println(t.replaceAll("(/subscribe|\\s)", ""));
	}
}