package micropolisj.build_tool;

import micropolisj.engine.TileSpec;
import micropolisj.graphics.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.imageio.*;
import javax.swing.ImageIcon;
import javax.xml.stream.*;

import static micropolisj.engine.TileSpec.generateTileNames;
import static micropolisj.graphics.TileImage.*;
import static micropolisj.XML_Helper.*;

public class MakeTiles
{
	static HashMap<String,String> tileData = new HashMap<String,String>();

	static final Charset UTF8 = Charset.forName("UTF-8");
	static int SKIP_TILES = 0;
	static int COUNT_TILES = -1;
	static int TILE_SIZE = STD_SIZE;

	public static void main(String [] args)
		throws Exception
	{
		if (args.length != 2) {
			throw new Exception("Wrong number of arguments");
		}

		if (System.getProperty("tile_size") != null) {
			TILE_SIZE = Integer.parseInt(System.getProperty("tile_size"));
		}
		if (System.getProperty("skip_tiles") != null) {
			SKIP_TILES = Integer.parseInt(System.getProperty("skip_tiles"));
		}
		if (System.getProperty("tile_count") != null) {
			COUNT_TILES = Integer.parseInt(System.getProperty("tile_count"));
		}

		File recipeFile = new File(args[0]);
		File outputDir = new File(args[1]);

		generateFromRecipe(recipeFile, outputDir);
	}

	static class TileMapping {
		String tileName;
		TileImage ref;
		TileImage dest;

		TileMapping(String tileName, TileImage ref, TileImage dest) {
			this.tileName = tileName;
			this.ref = ref;
			this.dest = dest;
		}
	}

	static class ComposeFrame extends TileImageSprite
	{
		TileImage refImage;

		ComposeFrame(ComposeBuffer parentBuffer, TileImage refImage)
		{
			super(parentBuffer);
			this.refImage = refImage;
		}
	}

	static class ComposeBuffer extends TileImage
	{
		File outFile;
		String fileName;
		boolean useAlpha;
		int maxWidth;
		int nextOffsetY;
		BufferedImage buf;
		Graphics2D gr;

		ComposeBuffer(File outputDir, String fileName, boolean useAlpha)
		{
			this.outFile = new File(outputDir, fileName);
			this.fileName = fileName;
			this.useAlpha = useAlpha;
		}

		TileImageSprite prepareTile(Dimension size, TileImage refImage)
		{
			TileImageSprite s = new ComposeFrame(this, refImage);
			s.offsetY = this.nextOffsetY + size.height - TILE_SIZE;
			this.nextOffsetY += size.height;
			this.maxWidth = Math.max(maxWidth, size.width);
			return s;
		}

		void createBuffer()
		{
			this.buf = new BufferedImage(maxWidth,nextOffsetY,
				useAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			this.gr = buf.createGraphics();
		}

		void writeFile()
			throws IOException
		{
			System.out.println("Generating tiles array: "+outFile);
			ImageIO.write(buf, "png", outFile);
		}

		@Override
		public void drawFragment(Graphics2D gr, int destX, int destY, int srcX, int srcY) { throw new UnsupportedOperationException(); }
	}

	static class Composer
	{
		ComposeBuffer stanTiles;

		Composer(File outputDir)
		{
			this.stanTiles = new ComposeBuffer(outputDir, "tiles.png", false);
		}

		TileImageSprite prepareTile(TileImage refImage)
		{
			Dimension size = new Dimension(TILE_SIZE, TILE_SIZE);
			return stanTiles.prepareTile(size, refImage);
		}

		void createBuffers()
		{
			stanTiles.createBuffer();
		}

		Graphics2D getGr(TileImageSprite s)
		{
			ComposeBuffer cb = (ComposeBuffer) s.source;
			return cb.gr;
		}

		void writeFiles()
			throws IOException
		{
			stanTiles.writeFile();
		}
	}

	static void generateFromRecipe(File recipeFile, File outputDir)
		throws IOException
	{
		Properties recipe = new Properties();
		recipe.load(
			new InputStreamReader(
				new FileInputStream(recipeFile),
				UTF8
			));

		// count number of images
		String [] tileNames = generateTileNames(recipe);
		int ntiles = COUNT_TILES == -1 ? tileNames.length : COUNT_TILES;

		// prepare mapping data
		Composer c = new Composer(outputDir);
		ArrayList<TileMapping> mappings = new ArrayList<TileMapping>();

		for (int i = 0; i < ntiles; i++) {
			int tileNumber = SKIP_TILES + i;
			if (!(tileNumber >= 0 && tileNumber < tileNames.length)) {
				continue;
			}

			String tileName = tileNames[tileNumber];
			String rawSpec = recipe.getProperty(tileName);
			assert rawSpec != null;

			TileSpec tileSpec = TileSpec.parse(tileNumber, tileName, rawSpec, recipe);
			TileImage ref = parseFrameSpec(tileSpec);
			if (ref == null) {
				// tile is defined, but it has no images
				continue;
			}

			ref = ref.normalForm();

			TileImage dest = prepareFrames(ref, c);
			TileMapping m = new TileMapping(tileName, ref, dest);
			mappings.add(m);
		}

		// actually assemble the image
		c.createBuffers();

		for (TileMapping m : mappings) {

			drawFrames(m.dest, c);
		}

		// make parent directories if necessary
		outputDir.mkdirs();

		// output the composed images
		c.writeFiles();

		// output an index of all tile names and their offset into
		// the composed tile array
		File indexFile = new File(outputDir, "tiles.idx");
		System.out.println("Generating tiles index: "+indexFile);
		writeIndexFile(mappings, indexFile);
	}

	static TileImage prepareFrames(TileImage ref, Composer c)
	{
		if (ref instanceof Animation) {

			Animation mc = (Animation) ref;
			Animation dest = new Animation();
			for (Animation.Frame f : mc.frames) {
				TileImage s = prepareFrames(f.frame, c);
				dest.addFrame(s, f.duration);
			}
			return dest;
		}
		else {
			TileImageSprite s = c.prepareTile(ref);
			return s;
		}
	}

	static void drawFrames(TileImage dest, Composer c)
	{
		if (dest instanceof Animation) {
			Animation ani = (Animation) dest;
			for (Animation.Frame f : ani.frames) {
				drawFrames(f.frame, c);
			}
		}
		else {
			assert dest instanceof ComposeFrame;

			ComposeFrame f = (ComposeFrame) dest;
			f.refImage.drawTo(c.getGr(f), f.offsetX, f.offsetY);
		}
	}

	static void writeIndexFile(Collection<TileMapping> mappings, File indexFile)
		throws IOException
	{
		try {

		FileOutputStream outStream = new FileOutputStream(indexFile);
		XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream, "UTF-8");
		out.writeStartDocument();
		out.writeStartElement("micropolis-tiles-index");
		for (TileMapping m : mappings) {
			out.writeStartElement("tile");
			out.writeAttribute("name", m.tileName);

			writeImageTags(out, m.dest);

			out.writeEndElement();
		}
		out.writeEndElement();
		out.writeEndDocument();
		out.close();
		outStream.close(); //because XMLStreamWriter does not call it for us

		}
		catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	static void writeImageTags(XMLStreamWriter out, TileImage dest)
		throws XMLStreamException
	{
		if (dest instanceof Animation) {

			Animation ani = (Animation) dest;
			out.writeStartElement("animation");
			for (Animation.Frame f : ani.frames) {
				TileImageSprite s = (TileImageSprite) f.frame;
				out.writeStartElement("frame");
				out.writeAttribute("duration", Integer.toString(f.duration));
				writeImageTags(out, f.frame);
				out.writeEndElement();
			}
			out.writeEndElement();
		}
		else { //assume it is a simple sprite

			TileImageSprite s = (TileImageSprite ) dest;
			out.writeStartElement("image");
			out.writeAttribute("at", String.format("%d,%d", s.offsetX, s.offsetY));
			out.writeEndElement();
		}
	}
	static TileImage parseFrameSpec(TileSpec spec)
		throws IOException
	{
		return parseFrameSpec(spec.getImages());
	}

	static TileImage parseFrameSpec(String rawSpec)
		throws IOException
	{
		String [] parts = rawSpec.split("\\|");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		return parseFrameSpec(parts);
	}

	static TileImage parseFrameSpec(String [] layerStrings)
		throws IOException
	{
		TileImage result = null;

		for (String layerStr : layerStrings) {

			TileImage newLayer = parseLayerSpec(layerStr);
			if (result == null) {
				result = newLayer;
			}
			else {
				result = new TileImageLayer(
					result,
					newLayer);
			}
		}

		return result;
	}

	static TileImage parseLayerSpec(String layerStr)
		throws IOException
	{
		String [] parts = layerStr.split("@", 2);
		TileImage img = loadAnimation(parts[0]);

		if (parts.length >= 2) {
			TileImageSprite sprite = new TileImageSprite(img);

			String offsetInfo = parts[1];
			parts = offsetInfo.split(",");
			if (parts.length >= 1) {
				sprite.offsetX = Integer.parseInt(parts[0]);
			}
			if (parts.length >= 2) {
				sprite.offsetY = Integer.parseInt(parts[1]);
			}
			return sprite;
		}//endif something given after '@' in image specifier

		return img;
	}

	static File findInkscape()
	{
		String exeName = "inkscape";
		if (System.getProperty("os.name").startsWith("Windows")) {
			exeName += ".exe";
		}

		File [] pathsToTry = {
			new File("/usr/bin"),
			new File("c:\\Program Files\\Inkscape"),
			new File("c:\\Program Files (x86)\\Inkscape")
			};
		for (File p : pathsToTry) {
			File f = new File(p, exeName);
			if (f.exists()) {
				return f;
			}
		}
		throw new Error("INKSCAPE not installed (or not found)");
	}

	static File stagingDir = new File("generated");
	static File renderSvg(String fileName, File svgFile)
		throws IOException
	{
		File pngFile = new File(stagingDir, fileName+"_"+TILE_SIZE+"x"+TILE_SIZE+".png");
		if (pngFile.exists() &&
			pngFile.lastModified() > svgFile.lastModified())
		{
			// looks like the PNG file is already up-to-date
			return pngFile;
		}

		File inkscapeBin = findInkscape();

		System.out.println("Generating raster image: "+pngFile);
		if (pngFile.exists()) {
			pngFile.delete();
		}
		else {
			pngFile.getParentFile().mkdirs();
		}

		String [] cmdline = {
			inkscapeBin.toString(),
			"--export-dpi="+(TILE_SIZE*90.0/STD_SIZE),
			"--export-png="+pngFile.toString(),
			svgFile.toString()
			};
		Process p = Runtime.getRuntime().exec(cmdline);
		int exit_value;
		try {
			exit_value = p.waitFor();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		if (exit_value != 0) {
			throw new RuntimeException("Helper exit status: "+exit_value);
		}

		if (!pngFile.exists()) {
			throw new RuntimeException("File not found: "+pngFile);
		}

		return pngFile;
	}

	static TileImage loadAnimation(String fileName)
		throws IOException
	{
		File f = new File(fileName + ".ani");
		if (f.exists()) {
			return Animation.load(f, loaderContext);
		}
		else {
			return loadImage(fileName);
		}
	}

	static class MyLoaderContext implements LoaderContext
	{
		HashMap<String,SourceImage> loadedImages = new HashMap<String,SourceImage>();

		//implements LoaderContext
		public SourceImage getDefaultImage()
		{
			throw new UnsupportedOperationException();
		}

		//implements LoaderContext
		public SourceImage getImage(String fileName)
			throws IOException
		{
			if (!loadedImages.containsKey(fileName)) {
				loadedImages.put(fileName,
					loadImageNoCache(fileName));
			}

			return loadedImages.get(fileName);
		}

		public TileImage parseFrameSpec(String tmp)
			throws IOException
		{
			return MakeTiles.parseFrameSpec(tmp);
		}
	}
	static MyLoaderContext loaderContext = new MyLoaderContext();

	static TileImage loadImage(String fileName)
		throws IOException
	{
		File xmlFile = new File(fileName + ".xml");
		if (xmlFile.exists()) {
			return loadImageXml(xmlFile);
		}

		return loaderContext.getImage(fileName);
	}

	static SourceImage loadImageReal(File pngFile, int basisSize)
		throws IOException
	{
		BufferedImage bi = ImageIO.read(pngFile);
		return new ScaledSourceImage(
			bi,
			basisSize,
			TILE_SIZE);
	}

	static SourceImage loadImageNoCache(String fileName)
		throws IOException
	{
		File svgFile, pngFile = null;

		svgFile = new File(fileName+"_"+TILE_SIZE+"x"+TILE_SIZE+".svg");

		if (svgFile.exists()) {
			pngFile = renderSvg(fileName, svgFile);
		}
		else {
			svgFile = new File(fileName+".svg");
			if (svgFile.exists()) {
				pngFile = renderSvg(fileName, svgFile);
			}
		}

		if (pngFile != null && pngFile.exists()) {
			return loadImageReal(pngFile, TILE_SIZE);
		}

		pngFile = new File(fileName+"_"+TILE_SIZE+"x"+TILE_SIZE+".png");
		if (pngFile.exists()) {
			return loadImageReal(pngFile, TILE_SIZE);
		}

		if (TILE_SIZE < 128) {
		pngFile = new File(fileName+"_128x128.png");
		if (pngFile.exists()) {
			return loadImageReal(pngFile, 128);
		}
		}

		pngFile = new File(fileName+".png");
		if (pngFile.exists()) {
			return loadImageReal(pngFile, STD_SIZE);
		}

		throw new IOException("File not found: "+fileName+".{svg,png}");
	}

	static TileImage loadImageXml(File xmlFile)
		throws IOException
	{
		FileInputStream inStream = new FileInputStream(xmlFile);
		try {

		XMLStreamReader in = XMLInputFactory.newInstance().createXMLStreamReader(inStream, "UTF-8");
		in.nextTag(); // get document tag
		return readTileImage(in, loaderContext);
		}

		catch (XMLStreamException e) {
			throw new IOException("XML Parse error", e);
		}
	}
}
