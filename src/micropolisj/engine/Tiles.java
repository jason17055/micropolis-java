// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Provides global methods for loading tile specifications.
 */
public class Tiles
{
	static final Charset UTF8 = Charset.forName("UTF-8");
	static TileSpec [] tiles;
	static Map<String,TileSpec> tilesByName = new HashMap<String,TileSpec>();
	static {
		try {
			readTiles();
			checkTiles();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static final String TILE_ALIASES = "/tiles/aliases.txt";
	static Map<String,String> loadTileUpgradeMap()
	{
		try {
		Properties p = new Properties();
		p.load(
			new InputStreamReader(
				Tiles.class.getResourceAsStream(TILE_ALIASES),
				UTF8
				)
			);

		HashMap<String,String> rv = new HashMap<String,String>();
		for (Map.Entry<Object,Object> e : p.entrySet()) {
			rv.put((String)e.getKey(), (String)e.getValue());
		}
		return rv;

		}
		catch (IOException e) {
			// probably means the resource file was not found
			System.err.println(TILE_ALIASES + ": "+e);
			return Collections.emptyMap();
		}
	}

	static void readTiles()
		throws IOException
	{
		ArrayList<TileSpec> tilesList = new ArrayList<TileSpec>();

		Properties tilesRc = new Properties();
		tilesRc.load(
			new InputStreamReader(
				Tiles.class.getResourceAsStream("/tiles.rc"),
				UTF8
				)
			);

		String [] tileNames = TileSpec.generateTileNames(tilesRc);
		tiles = new TileSpec[tileNames.length];

		for (int i = 0; i < tileNames.length; i++) {
			String tileName = tileNames[i];
			String rawSpec = tilesRc.getProperty(tileName);
			if (rawSpec == null) {
				break;
			}

			TileSpec ts = TileSpec.parse(i, tileName, rawSpec, tilesRc);
			tilesByName.put(tileName, ts);
			tiles[i] = ts;
		}

		for (int i = 0; i < tiles.length; i++) {
			tiles[i].resolveReferences(tilesByName);

			TileSpec.BuildingInfo bi = tiles[i].getBuildingInfo();
			if (bi != null) {
				for (int j = 0; j < bi.members.length; j++) {
					TileSpec memberTile = bi.members[j];
					int offx = (bi.width >= 3 ? -1 : 0) + j % bi.width;
					int offy = (bi.height >= 3 ? -1 : 0) + j / bi.width;

					if (memberTile.owner == null &&
						(offx != 0 || offy != 0)
						)
					{
						memberTile.owner = tiles[i];
						memberTile.ownerOffsetX = offx;
						memberTile.ownerOffsetY = offy;
					}
				}
			}
		}
	}

	public static TileSpec load(String tileName)
	{
		return tilesByName.get(tileName);
	}

	public static TileSpec loadByOrdinal(int tileNumber)
	{
		return load(Integer.toString(tileNumber));
	}

	/**
	 * Access a tile specification by index number.
	 *
	 * @return a tile specification, or null if there is no tile
	 * with the given number
	 */
	public static TileSpec get(int tileNumber)
	{
		if (tileNumber >= 0 && tileNumber < tiles.length) {
			return tiles[tileNumber];
		}
		else {
			return null;
		}
	}

	public static int getTileCount()
	{
		return tiles.length;
	}

	static void checkTiles()
	{
		for (int i = 0; i < tiles.length; i++) {
			// do something here
		}
	}
}
