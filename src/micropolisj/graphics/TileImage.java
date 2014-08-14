package micropolisj.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
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

	/**
	 * Supports rescaling of tile images.
	 */
	public static class SourceImage extends TileImage
	{
		public final BufferedImage image;
		public final int basisSize;
		public final int targetSize;

		public SourceImage(BufferedImage image, int basisSize, int targetSize)
		{
			this.image = image;
			this.basisSize = basisSize;
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
		public BufferedImage srcImage;
		public int offsetY;

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY) {
			throw new UnsupportedOperationException();
		}
	}

	public interface LoaderContext
	{
		BufferedImage getDefaultImage();
		BufferedImage getImage(String name);
	}

	public static SimpleTileImage readSimpleImage(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		SimpleTileImage img = new SimpleTileImage();
		String srcImageName = in.getAttributeValue(null, "src");
		if (srcImageName != null) {
			img.srcImage = ctx.getImage(srcImageName);
		}
		else {
			img.srcImage = ctx.getDefaultImage();
		}
		String tmp = in.getAttributeValue(null, "offsetY");
		img.offsetY = tmp != null ? Integer.parseInt(tmp) : 0;

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

	public static AnimatedTile readAnimation(XMLStreamReader in, LoaderContext ctx)
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
}
