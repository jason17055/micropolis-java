package micropolisj.build_tool;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class TileImage
{
	public static final int STD_SIZE = 16;

	public abstract void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY);
	/**
	 * @return the end-time of the animation frame identified by frameTime;
	 *   -1 if not an animation, or if frameTime is past the end of the animation
	 */
	public abstract int getFrameEndTime(int frameTime);


	static class TileImageLayer extends TileImage
	{
		TileImageLayer below;
		TileImage above;

		@Override
		public void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			if (below != null) {
				below.drawTo(gr, destX, destY, srcX, srcY);
			}
			above.drawTo(gr, destX, destY, srcX, srcY);
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

	static class TileImageSprite extends TileImage
	{
		TileImage source;
		int offsetX;
		int offsetY;

		@Override
		public void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY)
		{
			source.drawTo(gr, destX, destY, srcX+offsetX, srcY+offsetY);
		}

		@Override
		public int getFrameEndTime(int frameTime) {
			return source.getFrameEndTime(frameTime);
		}
	}

	static class SourceImage extends TileImage
	{
		Image image;
		int basisSize;
		int targetSize;

		SourceImage(Image image, int basisSize, int targetSize) {
			this.image = image;
			this.basisSize = basisSize;
			this.targetSize = targetSize;
		}

		@Override
		public void drawTo(Graphics2D gr, int destX, int destY, int srcX, int srcY)
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
