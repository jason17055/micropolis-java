package micropolisj.build_tool;

import java.awt.Graphics2D;
import java.io.*;
import java.util.*;
import javax.xml.stream.*;
import static micropolisj.XML_Helper.*;

class Animation extends MakeTiles.TileImage
{
	static final int DEFAULT_DURATION = 125;
	List<AnimationFrame> frames = new ArrayList<AnimationFrame>();

	public static Animation load(File aniFile)
		throws IOException
	{
		FileInputStream fis = new FileInputStream(aniFile);
		Animation self = new Animation();
		self.load(fis);
		return self;
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
				frames.add(
					new AnimationFrame(
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

	static class AnimationFrame
	{
		MakeTiles.TileImage frame;
		int duration;

		public AnimationFrame(MakeTiles.TileImage frame, int duration)
		{
			this.frame = frame;
			this.duration = duration;
		}
	}

	@Override
	void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY)
	{
		if (frames.isEmpty()) { return; }
		frames.get(0).frame.drawTo(gr, destX, destY, srcX, srcY);
	}
}
