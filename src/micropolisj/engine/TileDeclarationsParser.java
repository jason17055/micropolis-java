package micropolisj.engine;

import java.io.*;
import java.util.*;

public class TileDeclarationsParser
{
	Scanner scanner;
	Token nextToken;

	public List<TileDeclarations> parse(Reader in)
		throws IOException
	{
		scanner = new Scanner(in);
		return parseRules();
	}

	List<TileDeclarations> parseRules()
		throws IOException
	{
		ArrayList<TileDeclarations> list = new ArrayList<TileDeclarations>();

		while (hasRule()) {
			list.add(parseRule());
		}

		eatToken(Token.EOF);
		return list;
	}

	TileDeclarations parseRule()
		throws IOException
	{
		TileDeclarations td = new TileDeclarations();
		td.selector = eatToken(Token.BAREWORD);
		eatToken(Token.OPEN_BRACE);

		while (peekToken() == Token.BAREWORD) {
			String attrName = eatToken(Token.BAREWORD);
			String attrValue = attrName;
			if (peekToken() == Token.COLON) {
				eatToken(Token.COLON);
				attrValue = parseAttributeValue();
			}
			td.attributes.put(attrName, attrValue);
		}
			
		eatToken(Token.CLOSE_BRACE);
		return td;
	}

	String parseAttributeValue()
		throws IOException
	{
		return eatToken(Token.BAREWORD);
	}

	boolean hasRule()
		throws IOException
	{
		return peekToken() == Token.BAREWORD;
	}

	Token peekToken()
		throws IOException
	{
		if (nextToken == null) {
			nextToken = scanner.nextToken();
		}
		return nextToken;
	}

	String eatToken(Token tokenType)
		throws IOException
	{
		peekToken();
		if (nextToken != tokenType) {
			throw new IOException("Syntax error: found "+nextToken+" but expected "+tokenType);
		}
		nextToken = null;
		return scanner.sb.toString();
	}

	static enum Token
	{
		BAREWORD,
		OPEN_BRACE,
		CLOSE_BRACE,
		SEMI,
		COLON,
		EOF;
	}

	static class Scanner
	{
		BufferedReader reader;
		StringBuilder sb;
		int [] unreadChars = new int[8];
		int unreadCharsLen = 0;

		Scanner(Reader reader)
		{
			this.reader = new BufferedReader(reader);
		}

		int nextChar()
			throws IOException
		{
			if (unreadCharsLen != 0) {
				return unreadChars[--unreadCharsLen];
			}
			return reader.read();
		}

		void pushBack(int c)
		{
			if (unreadCharsLen >= unreadChars.length) {
				unreadChars = Arrays.copyOf(unreadChars, unreadCharsLen*2);
			}
			unreadChars[unreadCharsLen++] = c;
		}

		public Token nextToken()
			throws IOException
		{
			sb.setLength(0);
			int st = 0;
			for (;;) {
				int c = nextChar();
				switch(st) {
				case 0:
					if (c == '{') {
						sb.append((char)c);
						return Token.OPEN_BRACE;
					}
					else if (c == '}') {
						sb.append((char)c);
						return Token.CLOSE_BRACE;
					}
					else if (c == ':') {
						sb.append((char)c);
						return Token.COLON;
					}
					else if (c == ';') {
						sb.append((char)c);
						return Token.SEMI;
					}
					else if (Character.isWhitespace(c)) {
						// ignore
						continue;
					}
					else if (c == -1) {
						return Token.EOF;
					}
					else if (Character.isJavaIdentifierStart(c)) {
						sb.append((char)c);
						st = 1;
						continue;
					}
					else {
						throw new IOException("Invalid character '"+((char)c)+"'");
					}

				case 1: // X
					if (Character.isJavaIdentifierPart(c)) {
						sb.append((char)c);
						continue;
					}
					else {
						pushBack(c);
						st = 0;
						return Token.BAREWORD;
					}
				}
			}
		}
	}

	public static void main(String [] args)
		throws Exception
	{
		new TileDeclarationsParser().parse(new InputStreamReader(System.in));
	}
}
