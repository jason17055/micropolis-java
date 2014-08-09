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

	/**
	 * @return the end-time of the animation frame identified by frameTime;
	 *   -1 if not an animation, or if frameTime is past the end of the animation
	 */
	public abstract int getFrameEndTime(int frameTime);


	public static class TileImageLayer extends TileImage
	{
		public TileImageLayer below;
		public TileImage above;

		@Override
		public void drawWithTimeTo(Graphics2D gr, int time, int destX, int destY, int srcX, int srcY)
		{
			if (below != null) {
				below.drawWithTimeTo(gr, time, destX, destY, srcX, srcY);
			}
			above.drawWithTimeTo(gr, time, destX, destY, srcX, srcY);
		}

		@Override
		public int getFrameEndTime(int frameTime)
		{
			if (below == null) {
				return above.getFrameEndTime(frameTime);
			}

			int belowEnd = below.getFrameEndTime(frameTime);
			int aboveEnd = above.getFrameEndTime(frameTime);

			if (belowEnd < 0) {
				return aboveEnd;
			}
			else if (aboveEnd < 0 || belowEnd < aboveEnd) {
				return belowEnd;
			}
			else {
				return aboveEnd;
			}
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

		@Override
		public int getFrameEndTime(int frameTime) {
			return source.getFrameEndTime(frameTime);
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

		@Override
		public int getFrameEndTime(int frameTime) {
			return -1;
		}
	}
}
