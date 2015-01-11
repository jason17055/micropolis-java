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

	interface MultiPart
	{
		MultiPart makeEmptyCopy();
		Iterable<? extends Part> parts();
		void addPartLike(TileImage m, Part p);
		TileImage asTileImage();
	}

	interface Part
	{
		TileImage getImage();
	}

	/**
	 * Draws a part of this tile image to the given graphics context.
	 */
	public abstract void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY);

	public final void drawTo(Graphics2D gr, int destX, int destY)
	{
		this.drawFragment(gr, destX, destY, 0, 0);
	}

	/**
	 * Brings any internal Animation object to the top of the hierarchy.
	 */
	public TileImage normalForm()
	{
		// subclasses should override this behavior
		return this;
	}

	public static class TileImageLayer extends TileImage
	{
		public final TileImage below;
		public final TileImage above;

		public TileImageLayer(TileImage below, TileImage above)
		{
			assert below != null;
			assert above != null;

			this.below = below;
			this.above = above;
		}

		@Override
		public TileImage normalForm()
		{
			TileImage below1 = below.normalForm();
			TileImage above1 = above.normalForm();

			if (above1 instanceof MultiPart) {

				MultiPart rv = ((MultiPart)above1).makeEmptyCopy();
				for (Part p : ((MultiPart)above1).parts()) {
					TileImageLayer m = new TileImageLayer(
						below1,
						p.getImage()
						);
					rv.addPartLike(m, p);
				}
				return rv.asTileImage();
			}
			else if (below1 instanceof MultiPart) {

				MultiPart rv = ((MultiPart)below1).makeEmptyCopy();
				for (Part p : ((MultiPart)below1).parts()) {
					TileImageLayer m = new TileImageLayer(
						p.getImage(),
						above1
						);
					rv.addPartLike(m, p);
				}
				return rv.asTileImage();
			}
			else {

				return new TileImageLayer(below1, above1);
			}
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			below.drawFragment(gr, destX, destY, srcX, srcY);
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
		public TileImage normalForm()
		{
			TileImage source_n = source.normalForm();
			if (source_n instanceof MultiPart) {

				MultiPart rv = ((MultiPart)source_n).makeEmptyCopy();
				for (Part p : ((MultiPart)source_n).parts()) {
					TileImageSprite m = sameTransformFor(p.getImage());
					rv.addPartLike(m, p);
				}
				return rv.asTileImage();
			}
			else {
				return sameTransformFor(source_n);
			}
		}

		private TileImageSprite sameTransformFor(TileImage img)
		{
			TileImageSprite m = new TileImageSprite(img);
			m.offsetX = this.offsetX;
			m.offsetY = this.offsetY;
			return m;
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
			srcImage.drawFragment(gr, destX, destY,
				srcX+offsetX, srcY+offsetY);
		}
	}

	public interface LoaderContext
	{
		SourceImage getDefaultImage()
			throws IOException;
		SourceImage getImage(String name)
			throws IOException;

		TileImage parseFrameSpec(String tmp)
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

	static TileImage readLayeredImage(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		TileImage result = null;

		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			assert in.isStartElement();

			TileImage newImg = readTileImage(in, ctx);
			if (result == null) {
				result = newImg;
			}
			else {
				result = new TileImageLayer(
					result,            //below
					newImg             //above
				);
			}
		}

		if (result == null) {
			throw new XMLStreamException("layer must have at least one image");
		}

		return result;
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
			return Animation.read(in, ctx);
		}
		else if (tagName.equals("layered-image")) {
			return readLayeredImage(in, ctx);
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
				tagName.equals("animation") ||
				tagName.equals("layered-image"))
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
