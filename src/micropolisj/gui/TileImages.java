// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.gui;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.stream.*;

import micropolisj.engine.*;
import micropolisj.graphics.TileImage;
import static micropolisj.engine.TileConstants.*;
import static micropolisj.XML_Helper.*;
import static micropolisj.graphics.TileImage.LoaderContext;

public class TileImages
{
	final String name;
	final int TILE_WIDTH;
	final int TILE_HEIGHT;
	TileImage [] tileImageMap;
	Map<SpriteKind, Map<Integer, Image> > spriteImages;

	static class SimpleTileImage extends TileImage
	{
		BufferedImage srcImage;
		int offsetY;

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY) {
			throw new UnsupportedOperationException();
		}
	}

	static class AnimatedTile extends TileImage
	{
		SimpleTileImage [] frames;

		public SimpleTileImage getFrameByTime(int acycle)
		{
			return frames[acycle % frames.length];
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY) {
			throw new UnsupportedOperationException();
		}
	}

	private TileImages(String name, int size)
	{
		this.name = name;
		this.TILE_WIDTH = size;
		this.TILE_HEIGHT = size;

		initTileImageMap();
	}

	String getResourceName()
	{
		return "/" + name + "/tiles.png";
	}

	static SimpleTileImage readSimpleImage(XMLStreamReader in, LoaderContext ctx)
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

	static AnimatedTile readAnimation(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
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

	static TileImage readTileImage(XMLStreamReader in, LoaderContext ctx)
		throws XMLStreamException
	{
		TileImage img = null;

		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			assert in.isStartElement();
			if (in.getLocalName().equals("image")) {
				img = readSimpleImage(in, ctx);
			}
			else if (in.getLocalName().equals("animation")) {
				img = readAnimation(in, ctx);
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

	class MyLoaderContext implements LoaderContext
	{
		Map<String,BufferedImage> images = new HashMap<String,BufferedImage>();

		//implements LoaderContext
		public BufferedImage getDefaultImage()
		{
			return getImage("tiles.png");
		}

		//implements LoaderContext
		public BufferedImage getImage(String fileName)
		{
			if (!images.containsKey(fileName)) {
				images.put(fileName, loadImage("/"+name+"/"+fileName));
			}
			return images.get(fileName);
		}
	}

	void initTileImageMap()
	{
		if (this.spriteImages != null) {
			// already loaded
			return;
		}

		LoaderContext ctx = new MyLoaderContext();

		try
		{

		// load tile->image mapping
		this.tileImageMap = new TileImage[Tiles.getTileCount()];
		String resourceName = "/" + name + "/tiles.idx";

		InputStream inStream = TileImages.class.getResourceAsStream(resourceName);
		XMLStreamReader in = XMLInputFactory.newInstance().createXMLStreamReader(inStream, "UTF-8");

		in.nextTag();
		if (!(in.getEventType() == XMLStreamConstants.START_ELEMENT &&
			in.getLocalName().equals("micropolis-tiles-index"))) {
			throw new IOException("Unrecognized file format");
		}

		while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
			assert in.isStartElement();

			String tagName = in.getLocalName();
			if (!tagName.equals("tile")) {
				skipToEndElement(in);
				continue;
			}

			String tileName = in.getAttributeValue(null, "name");
			TileImage img = readTileImage(in, ctx);

			assert tileName != null;
			assert img != null;

			TileSpec ts = Tiles.load(tileName);
			tileImageMap[ts.tileNumber] = img;

			assert in.isEndElement() && in.getLocalName().equals("tile");
		}

		in.close();
		inStream.close();

		}
		catch (XMLStreamException e) {
			throw new Error("unexpected: "+e, e);
		}
		catch (IOException e) {
			throw new Error("unexpected: "+e, e);
		}
	}

	static Map<Integer,TileImages> savedInstances = new HashMap<Integer,TileImages>();

	public static TileImages getInstance(int size)
	{
		TileImages self = getInstance(String.format("%dx%d", size, size), size);
		self.loadSpriteImages();
		return self;
	}

	public static TileImages getInstance(String name, int size)
	{
		if (!savedInstances.containsKey(size)) {
			savedInstances.put(size, new TileImages(name, size));
		}
		return savedInstances.get(size);
	}

	public class ImageInfo
	{
		BufferedImage srcImage;
		int offsetY;
		boolean animated;

		ImageInfo(BufferedImage srcImage, int offsetY, boolean animated) {
			this.srcImage = srcImage;
			this.offsetY = offsetY;
			this.animated = animated;
		}

		boolean isAnimated() { return animated; }

		public void drawTo(Graphics gr, int destX, int destY)
		{
			gr.drawImage(getImage(),
				destX, destY,
				null);
		}

		public void drawToBytes(BufferedImage img, int x, int y)
		{
			for (int yy = 0; yy < TILE_HEIGHT; yy++)
			{
				for (int xx = 0; xx < TILE_WIDTH; xx++)
				{
					img.setRGB(x+xx,y+yy,
						srcImage.getRGB(xx,offsetY+yy));
				}
			}
		}

		public Image getImage()
		{
			return srcImage.getSubimage(
				0, offsetY,
				TILE_WIDTH, TILE_HEIGHT
				);
		}
	}

	public ImageInfo getTileImageInfo(int tileNumber)
	{
		return getTileImageInfo(tileNumber, 0);
	}

	public ImageInfo getTileImageInfo(int tileNumber, int acycle)
	{
		assert (tileNumber & LOMASK) == tileNumber;
		assert tileNumber >= 0 && tileNumber < tileImageMap.length;

		TileImage ti = tileImageMap[tileNumber];
		if (ti instanceof SimpleTileImage) {
			final SimpleTileImage sti = (SimpleTileImage) ti;

			return new ImageInfo(sti.srcImage, sti.offsetY, false);
		}
		else if (ti instanceof AnimatedTile) {
			final AnimatedTile anim = (AnimatedTile) ti;
			final SimpleTileImage sti = anim.getFrameByTime(acycle);

			return new ImageInfo(sti.srcImage, sti.offsetY, true);
		}
		else {
			throw new Error("no image for tile "+tileNumber);
		}
	}

	public Image getTileImage(int tile)
	{
		return getTileImageInfo(tile).getImage();
	}

	public Image getSpriteImage(SpriteKind kind, int frameNumber)
	{
		assert spriteImages != null;

		return spriteImages.get(kind).get(frameNumber);
	}

	private void loadSpriteImages()
	{
		if (this.spriteImages != null) {
			// already loaded
			return;
		}

		spriteImages = new EnumMap<SpriteKind, Map<Integer,Image> >(SpriteKind.class);
		for (SpriteKind kind : SpriteKind.values())
		{
			HashMap<Integer,Image> imgs = new HashMap<Integer,Image>();
			for (int i = 0; i < kind.numFrames; i++) {
				Image img = loadSpriteImage(kind, i);
				if (img != null) {
					imgs.put(i, img);
				}
			}
			spriteImages.put(kind, imgs);
		}
	}

	Image loadSpriteImage(SpriteKind kind, int frameNo)
	{
		String resourceName = "/obj"+kind.objectId+"-"+frameNo;

		// first, try to load specific size image
		URL iconUrl = TileImages.class.getResource(resourceName+"_"+TILE_WIDTH+"x"+TILE_HEIGHT+".png");
		if (iconUrl != null) {
			return new ImageIcon(iconUrl).getImage();
		}

		iconUrl = TileImages.class.getResource(resourceName+".png");
		if (iconUrl == null)
			return null;

		if (TILE_WIDTH==16 && TILE_HEIGHT==16) {
			return new ImageIcon(iconUrl).getImage();
		}

		// scale the image ourselves
		ImageIcon ii = new ImageIcon(iconUrl);
		int destWidth = ii.getIconWidth() * TILE_WIDTH / 16;
		int destHeight = ii.getIconHeight() * TILE_HEIGHT / 16;

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice dev = env.getDefaultScreenDevice();
		GraphicsConfiguration conf = dev.getDefaultConfiguration();
		BufferedImage bi = conf.createCompatibleImage(destWidth, destHeight, Transparency.TRANSLUCENT);
		Graphics2D gr = bi.createGraphics();

		gr.drawImage(ii.getImage(),
			0, 0, destWidth, destHeight,
			0, 0,
			ii.getIconWidth(), ii.getIconHeight(),
			null);
		return bi;
	}

	static BufferedImage loadImage(String resourceName)
	{
		URL url = TileImages.class.getResource(resourceName);
		try {

		BufferedImage bi = ImageIO.read(url);
		return bi;

		}
		catch (IOException e) {

			throw new RuntimeException(e);
		}
	}
}
