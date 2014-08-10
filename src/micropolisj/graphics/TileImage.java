package micropolisj.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

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

	public interface LoaderContext
	{
		BufferedImage getDefaultImage();
		BufferedImage getImage(String name);
	}
}
