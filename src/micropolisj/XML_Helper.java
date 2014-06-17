package micropolisj;

import java.io.*;
import javax.xml.stream.*;

public class XML_Helper
{
	private XML_Helper() {}

	public static void skipToEndElement(XMLStreamReader in)
		throws XMLStreamException
	{
		if (!in.isStartElement()) {
			return;
		}

		int tagDepth = 1;
		while (tagDepth > 0 && in.hasNext()) {
			in.next();
			if (in.isStartElement()) {
				tagDepth++;
			}
			else if (in.isEndElement()) {
				tagDepth--;
			}
		}
	}

	public static Reader readElementText(XMLStreamReader in)
	{
		return new ElementTextReader(in);
	}

	static class ElementTextReader extends Reader
	{
		XMLStreamReader xsr;
		int tagDepth;
		char [] buf;
		int buf_start;
		int buf_end;

		ElementTextReader(XMLStreamReader xsr)
		{
			this.xsr = xsr;
			this.tagDepth = 1;
		}

		private void readMore()
			throws XMLStreamException
		{
			while (tagDepth > 0 && buf_start == buf_end) {

				int nodeType = xsr.next();
				if (nodeType == XMLStreamConstants.START_ELEMENT) {
					tagDepth++;
				}
				else if (nodeType == XMLStreamConstants.END_ELEMENT) {
					tagDepth--;
				}
				else if (nodeType == XMLStreamConstants.CDATA ||
					nodeType == XMLStreamConstants.CHARACTERS ||
					nodeType == XMLStreamConstants.ENTITY_REFERENCE ||
					nodeType == XMLStreamConstants.SPACE)
				{
					buf = xsr.getTextCharacters();
					buf_start = xsr.getTextStart();
					buf_end = buf_start + xsr.getTextLength();
				}
			}
	
		}

		@Override
		public int read(char[] cbuf, int off, int len)
			throws IOException
		{
			if (buf_start == buf_end) {

				try {
					readMore();
				}
				catch (XMLStreamException e) {
					throw new IOException("XML stream error: "+ e, e);
				}

				if (tagDepth == 0) {
					// reached closing tag
					return -1;
				}
			}

			if (buf_start + len <= buf_end) {
				// already have the text loaded
				System.arraycopy(buf, buf_start, cbuf, off, len);
				buf_start += len;
				return len;
			}
			else {
				// not enough text available for entire request,
				// so just return what we have until the next
				// request

				len = buf_end - buf_start;
				assert len > 0;

				System.arraycopy(buf, buf_start, cbuf, off, len);
				buf_start += len;
				assert buf_start == buf_end;
				return len;
			}
		}

		@Override
		public void close()
			throws IOException
		{
			buf_start = 0;
			buf_end = 0;

			try {

			while (tagDepth > 0 && xsr.hasNext()) {
				xsr.next();
				if (xsr.isStartElement()) {
					tagDepth++;
				}
				else if (xsr.isEndElement()) {
					tagDepth--;
				}
			}
			}
			catch (XMLStreamException e) {
				throw new IOException("XML stream error: "+e, e);
			}
		}
	}
}
