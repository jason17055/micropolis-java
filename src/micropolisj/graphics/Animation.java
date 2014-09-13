package micropolisj.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.*;
import java.util.*;
import javax.xml.stream.*;
import static micropolisj.XML_Helper.*;

public class Animation extends TileImage implements TileImage.MultiPart
{
	static final int DEFAULT_DURATION = 125;

	public List<Frame> frames = new ArrayList<Frame>();
	public int totalDuration;

	public static Animation load(File aniFile, LoaderContext ctx)
		throws IOException
	{
		FileInputStream fis = new FileInputStream(aniFile);

		try {

			XMLStreamReader in = XMLInputFactory.newInstance().createXMLStreamReader(fis, "UTF-8");
			in.nextTag();
			if (!(in.getEventType() == XMLStreamConstants.START_ELEMENT &&
				in.getLocalName().equals("micropolis-animation"))) {
				throw new IOException("Unrecognized file format");
			}

			Animation a = read(in, ctx);

			in.close();

			return a;
		}
		catch (XMLStreamException e)
		{
			throw new IOException(aniFile.toString()+": "+e.getMessage(), e);
		}
		finally
		{
			fis.close();
		}
	}

	public static Animation read(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		Animation a = new Animation();
		a.load(in, ctx);
		return a;
	}

	//implements MultiPart
	public MultiPart makeEmptyCopy()
	{
		return new Animation();
	}

	//implements MultiPart
	public Iterable<? extends Part> parts()
	{
		return frames;
	}

	//implements MultiPart
	public void addPartLike(TileImage image, Part refPart)
	{
		addFrame(image, ((Frame)refPart).duration);
	}

	//implements MultiPart
	public TileImage asTileImage()
	{
		return this;
	}

	public void addFrame(TileImage img, int duration)
	{
		totalDuration += duration;
		Frame f = new Frame(img, totalDuration, duration);
		frames.add(f);
	}

	public TileImage getFrameByTime(int acycle)
	{
		assert frames.size() >= 1;
		assert totalDuration > 0;

		int t = (acycle*125) % totalDuration;
		int nframesLessOne = frames.size() - 1;
		for (int i = 0; i < nframesLessOne; i++) {
			Frame f = frames.get(i);
			t -= f.duration;
			if (t < 0) {
				return f.frame;
			}
		}
		return frames.get(nframesLessOne).frame;
	}

	void load(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			assert in.isStartElement();

			String tagName = in.getLocalName();
			if (tagName.equals("frame")) {

				String tmp = in.getAttributeValue(null, "duration");
				int duration = tmp != null ? Integer.parseInt(tmp) : DEFAULT_DURATION;

				TileImage frameImage = TileImage.readTileImageM(in, ctx);
				addFrame( frameImage, duration );
			}
			else {
				// unrecognized element
				skipToEndElement(in);
			}
		}
	}

	public class Frame implements Part
	{
		public final TileImage frame;
		public final int endTime;
		public final int duration;

		public Frame(TileImage frame, int endTime, int duration)
		{
			this.frame = frame;
			this.endTime = endTime;
			this.duration = duration;
		}

		//implements TileImage.Part
		public TileImage getImage() {
			return frame;
		}
	}

	@Override
	public TileImage realize(DrawContext dc)
	{
		int t = 0;
		for (int i = 0; i < frames.size(); i++) {
			Frame f = frames.get(i);
			int d = f.endTime;
			if (dc.time < d) {
				return f.frame.realize(dc);
			}
			t = d;
		}

		// time not found
		return getDefaultImage().realize(dc);
	}

	@Override
	protected Iterator<SwitchTileImage.Case> realizeAll_iterator()
	{
		throw new UnsupportedOperationException();
	}

	private TileImage getDefaultImage()
	{
		return frames.get(0).frame;
	}

	@Override
	public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight)
	{
		// Warning: drawing without context specified...
		// Use realize() first to get the right frame.
		getDefaultImage().drawFragment(gr, srcX, srcY, srcWidth, srcHeight);
	}

	@Override
	public Dimension getBounds()
	{
		int width = 0, height = 0;
		for (Frame f : frames) {
			Dimension b = f.frame.getBounds();
			width = Math.max(width, b.width);
			height = Math.max(height, b.height);
		}
		return new Dimension(width, height);
	}

	@Override
	public Dimension getSize()
	{
		return getDefaultImage().getSize();
	}
}
