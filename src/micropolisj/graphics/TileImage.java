package micropolisj.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class TileImage
{
	public static final int STD_SIZE = 16;

	public abstract void drawWithTimeTo(Graphics2D gr, int time, int destX, int destY, int srcX, int srcY);
	public final void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY)
	{
		drawWithTimeTo(gr, 0, destX, destY, srcX, srcY);
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
		public void drawWithTimeTo(Graphics2D gr, int time, int destX, int destY, int srcX, int srcY)
		{
			if (below != null) {
				below.drawWithTimeTo(gr, time, destX, destY, srcX, srcY);
			}
			above.drawWithTimeTo(gr, time, destX, destY, srcX, srcY);
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
		public void drawWithTimeTo(Graphics2D gr, int time, int destX, int destY, int srcX, int srcY)
		{
			source.drawWithTimeTo(gr, time, destX, destY, srcX+offsetX, srcY+offsetY);
		}
	}

	public static class SourceImage extends TileImage
	{
		public final Image image;
		public final int basisSize;
		public final int targetSize;

		public SourceImage(Image image, int basisSize, int targetSize)
		{
			this.image = image;
			this.basisSize = basisSize;
			this.targetSize = targetSize;
		}

		@Override
		public void drawWithTimeTo(Graphics2D gr, int time, int destX, int destY, int srcX, int srcY)
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
}
