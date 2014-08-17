package micropolisj.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.xml.stream.*;

import micropolisj.engine.*;
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
	public abstract void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight);

	public final void drawTo(Graphics2D gr, int destX, int destY)
	{
		Graphics2D g1 = (Graphics2D) gr.create();
		g1.translate(destX, destY);
		this.drawFragment(gr, 0, 0, STD_SIZE, STD_SIZE);
	}

	/**
	 * @return the width and height needed for this tile image.
	 */
	public abstract Dimension getBounds();

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
		public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight)
		{
			below.drawFragment(gr, srcX, srcY, srcWidth, srcHeight);
			above.drawFragment(gr, srcX, srcY, srcWidth, srcHeight);
		}

		@Override
		public Dimension getBounds()
		{
			if (below == null) {
				return above.getBounds();
			}

			Dimension belowBounds = below.getBounds();
			Dimension aboveBounds = above.getBounds();
			return new Dimension(
				Math.max(belowBounds.width, aboveBounds.width),
				Math.max(belowBounds.height, aboveBounds.height)
				);
		}
	}

	public static class TileImageSprite extends TileImage
	{
		public final TileImage source;
		public final int targetSize;
		public int offsetX;
		public int offsetY;
		public int overlapNorth;
		public int overlapEast;

		public TileImageSprite(TileImage source, int targetSize)
		{
			this.source = source;
			this.targetSize = targetSize;
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
			TileImageSprite m = new TileImageSprite(img, this.targetSize);
			m.offsetX = this.offsetX;
			m.offsetY = this.offsetY;
			return m;
		}

		@Override
		public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight)
		{
			Graphics2D g1 = (Graphics2D) gr.create();
			g1.translate(0, -overlapNorth*targetSize/STD_SIZE);
			source.drawFragment(g1,
				srcX+offsetX, srcY+offsetY-overlapNorth,
				srcWidth + overlapEast, srcHeight + overlapNorth);
		}

		@Override
		public Dimension getBounds() {
			Dimension d = source.getBounds();
			return new Dimension(
				d.width + overlapEast*targetSize/STD_SIZE,
				d.height + overlapNorth*targetSize/STD_SIZE
				);
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
		public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight)
		{
			gr.drawImage(
				image.getSubimage(srcX, srcY, srcWidth, srcHeight),
				0, 0, null);
		}

		@Override
		public Dimension getBounds()
		{
			return new Dimension(basisSize, basisSize);
		}

		public int getTargetSize()
		{
			return STD_SIZE;
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
		public int getTargetSize()
		{
			return targetSize;
		}

		@Override
		public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight)
		{
			int aSrcX = srcX * basisSize / STD_SIZE;
			int aSrcY = srcY * basisSize / STD_SIZE;
			int aSrcWidth = srcWidth * basisSize / STD_SIZE;
			int aSrcHeight = srcHeight * basisSize / STD_SIZE;

			int aDestWidth = srcWidth * targetSize / STD_SIZE;
			int aDestHeight = srcHeight * targetSize / STD_SIZE;

			gr.drawImage(
				image,
				0,
				0,
				aDestWidth,
				aDestHeight,
				aSrcX,
				aSrcY,
				aSrcX + aSrcWidth,
				aSrcY + aSrcHeight,
				null);
		}

		@Override
		public Dimension getBounds()
		{
			return new Dimension(targetSize, targetSize);
		}
	}

	public static class SimpleTileImage extends TileImage
	{
		public SourceImage srcImage;
		public int offsetX;
		public int offsetY;
		public int overlapNorth;
		public int overlapEast;

		@Override
		public Dimension getBounds() {
			Dimension b = srcImage.getBounds();
			if (overlapNorth != 0 || overlapEast != 0) {
				int targetSize = srcImage.getTargetSize();
				return new Dimension(
					b.width + overlapEast*targetSize/STD_SIZE,
					b.height + overlapNorth*targetSize/STD_SIZE
					);
			}
			else {
				return b;
			}
		}

		@Override
		public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight) {
			int targetSize = srcImage.getTargetSize();
			Graphics2D g1 = (Graphics2D) gr.create();
			g1.translate(0, -overlapNorth*targetSize/STD_SIZE);
			srcImage.drawFragment(g1,
				srcX+offsetX, srcY+offsetY-overlapNorth,
				srcWidth + overlapEast, srcHeight + overlapNorth);
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

		String tmp1 = in.getAttributeValue(null, "overlap-north");
		img.overlapNorth = tmp1 != null ? Integer.parseInt(tmp1) : 0;

		String tmp2 = in.getAttributeValue(null, "overlap-east");
		img.overlapEast = tmp2 != null ? Integer.parseInt(tmp2) : 0;

		skipToEndElement(in);
		return img;
	}

	public static class SwitchTileImage extends TileImage
	{
		public ArrayList<Case> cases = new ArrayList<Case>();
		public TileImage defaultCase;

		public static class Case
		{
			public String key;
			public String value;
			public TileImage img;

			public boolean matches(Micropolis city, CityLocation loc)
			{
				assert key.equals("tile-west"); //only supported one for now
				CityLocation nloc = new CityLocation(loc.x-1,loc.y);
				if (!city.testBounds(nloc.x, nloc.y)) {
					return false;
				}

				TileSpec ts = Tiles.get(city.getTile(nloc.x, nloc.y));
				return ts.name.equals(value);
			}
		}

		@Override
		public Dimension getBounds() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight) {
			throw new UnsupportedOperationException();
		}
	}

	static TileImage readSwitchImage(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		SwitchTileImage img = new SwitchTileImage();
		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			String tagName = in.getLocalName();
			if (tagName.equals("case")) {
				img.cases.add(readSwitchImageCase(in, ctx));
			}
			else if (tagName.equals("default")) {
				img.defaultCase = readTileImageM(in, ctx);
			}
			else {
				skipToEndElement(in);
			}
		}
		return img;
	}

	static SwitchTileImage.Case readSwitchImageCase(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		SwitchTileImage.Case c = new SwitchTileImage.Case();

		String s;
		s = in.getAttributeValue(null, "tile-west");
		if (s != null) {
			c.key = "tile-west";
			c.value = s;
		}

		c.img = readTileImageM(in, ctx);
		return c;
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
		else if (tagName.equals("switch")) {
			return readSwitchImage(in, ctx);
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
				tagName.equals("switch") ||
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
