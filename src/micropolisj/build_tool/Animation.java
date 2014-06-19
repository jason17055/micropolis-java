package micropolisj.build_tool;

import java.awt.Graphics2D;
import java.io.*;
import java.util.*;
import javax.xml.stream.*;
import static micropolisj.XML_Helper.*;

class Animation extends TileImage
{
	static final int DEFAULT_DURATION = 125;
	List<Frame> frames = new ArrayList<Frame>();

	public static Animation load(File aniFile)
		throws IOException
	{
		FileInputStream fis = new FileInputStream(aniFile);
		Animation self = new Animation();
		self.load(fis);
		return self;
	}

	public void addFrame(Frame f)
	{
		frames.add(f);
	}

	void load(InputStream inStream)
		throws IOException
	{
		try {

		XMLStreamReader in = XMLInputFactory.newInstance().createXMLStreamReader(inStream, "UTF-8");
		in.nextTag();
		if (!(in.getEventType() == XMLStreamConstants.START_ELEMENT &&
			in.getLocalName().equals("micropolis-animation"))) {
			throw new IOException("Unrecognized file format");
		}

		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			assert in.isStartElement();

			String tagName = in.getLocalName();
			if (tagName.equals("frame")) {

				String tmp = in.getAttributeValue(null, "duration");
				int duration = tmp != null ? Integer.parseInt(tmp) : DEFAULT_DURATION;

				tmp = in.getElementText();
				addFrame(
					new Frame(
						MakeTiles.parseFrameSpec(tmp),
						duration
					));
			}
			else {
				// unrecognized element
				skipToEndElement(in);
			}
		}

		in.close();
		inStream.close();

		}
		catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	static class Frame
	{
		TileImage frame;
		int duration;

		public Frame(TileImage frame, int duration)
		{
			this.frame = frame;
			this.duration = duration;
		}
	}

	@Override
	public void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY)
	{
		if (frames.isEmpty()) { return; }
		frames.get(0).frame.drawTo(gr, destX, destY, srcX, srcY);
	}

	@Override
	public int getFrameEndTime(int frameTime)
	{
		int t = 0;
		for (int i = 0; i < frames.size(); i++) {
			t += frames.get(i).duration;
			if (frameTime < t) {
				return t;
			}
		}
		return -1;
	}
}
