package micropolisj.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.xml.stream.*;

import static micropolisj.XML_Helper.*;

public abstract class TileImage
{
	public static final int STD_SIZE = 16;

	/**
	 * Draws a part of this tile image to the given graphics context.
	 */
	public abstract void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY);

	public final void drawTo(Graphics2D gr, int destX, int destY)
	{
		this.drawFragment(gr, destX, destY, 0, 0);
	}

	public static class TileImageLayer extends TileImage
	{
		public final TileImageLayer below;
		public final TileImage above;

		public TileImageLayer(TileImageLayer below, TileImage above)
		{
			this.below = below;
			this.above = above;
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			if (below != null) {
				below.drawFragment(gr, destX, destY, srcX, srcY);
			}
			above.drawFragment(gr, destX, destY, srcX, srcY);
		}
	}

	public static class TileImageSprite extends TileImage
	{
		public final TileImage source;
		public int offsetX;
		public int offsetY;

		public TileImageSprite(TileImage source)
		{
			this.source = source;
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			source.drawFragment(gr, destX, destY, srcX+offsetX, srcY+offsetY);
		}
	}

	public static class SourceImage extends TileImage
	{
		public final BufferedImage image;
		public final int basisSize;

		public SourceImage(BufferedImage image, int basisSize)
		{
			this.image = image;
			this.basisSize = basisSize;
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			gr.drawImage(
				image.getSubimage(srcX, srcY, basisSize, basisSize),
				destX,
				destY,
				null);
		}
	}

	/**
	 * Supports rescaling of tile images.
	 */
	public static class ScaledSourceImage extends SourceImage
	{
		public final int targetSize;

		public ScaledSourceImage(BufferedImage image, int basisSize, int targetSize)
		{
			super(image, basisSize);
			this.targetSize = targetSize;
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			srcX = srcX * basisSize / STD_SIZE;
			srcY = srcY * basisSize / STD_SIZE;

			gr.drawImage(
				image,
				destX, destY,
				destX+targetSize, destY+targetSize,
				srcX, srcY,
				srcX+basisSize, srcY+basisSize,
				null);
		}
	}

	public static class SimpleTileImage extends TileImage
	{
		public SourceImage srcImage;
		public int offsetX;
		public int offsetY;

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY) {
			throw new UnsupportedOperationException();
		}
	}

	public interface LoaderContext
	{
		SourceImage getDefaultImage()
			throws IOException;
		SourceImage getImage(String name)
			throws IOException;
	}

	static SimpleTileImage readSimpleImage(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		SimpleTileImage img = new SimpleTileImage();
		try {
			String srcImageName = in.getAttributeValue(null, "src");
			if (srcImageName != null) {
				img.srcImage = ctx.getImage(srcImageName);
			}
			else {
				img.srcImage = ctx.getDefaultImage();
			}
		}
		catch (IOException e) {
			throw new XMLStreamException("image source not found", e);
		}

		String tmp = in.getAttributeValue(null, "at");
		if (tmp != null) {
			String [] coords = tmp.split(",");
			if (coords.length == 2) {
				img.offsetX = Integer.parseInt(coords[0]);
				img.offsetY = Integer.parseInt(coords[1]);
			}
			else {
				throw new XMLStreamException("Invalid 'at' syntax");
			}
		}

		skipToEndElement(in);
		return img;
	}

	public static class AnimatedTile extends TileImage
	{
		public SimpleTileImage [] frames;

		public SimpleTileImage getFrameByTime(int acycle)
		{
			return frames[acycle % frames.length];
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY) {
			throw new UnsupportedOperationException();
		}
	}

	static AnimatedTile readAnimation(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		assert in.getLocalName().equals("animation");

		ArrayList<SimpleTileImage> frames = new ArrayList<SimpleTileImage>();

		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			String tagName = in.getLocalName();
			if (tagName.equals("frame")) {
				frames.add(readSimpleImage(in, ctx));
			}
			skipToEndElement(in);
		}

		AnimatedTile anim = new AnimatedTile();
		anim.frames = frames.toArray(new SimpleTileImage[0]);
		return anim;
	}

	public static TileImage readTileImage(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		assert in.isStartElement();
		String tagName = in.getLocalName();

		if (tagName.equals("image")) {
			return readSimpleImage(in, ctx);
		}
		else if (tagName.equals("animation")) {
			return readAnimation(in, ctx);
		}
		else {
			throw new XMLStreamException("unrecognized tag: "+tagName);
		}
	}

	/**
	 * @param in an XML stream reader with the parent tag of the tag to be read
	 *  still selected
	 */
	public static TileImage readTileImageM(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		TileImage img = null;

		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			assert in.isStartElement();
			String tagName = in.getLocalName();
			if (tagName.equals("image") ||
				tagName.equals("animation"))
			{
				img = readTileImage(in, ctx);
			}
			else {
				skipToEndElement(in);
			}
		}

		if (img == null) {
			throw new XMLStreamException(
				"missing image descriptor"
				);
		}

		return img;
	}
}
