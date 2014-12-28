// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

public class Traffic
{
	static final int EAST = 0;
	static final int SOUTH = 1;
	static final int WEST = 2;
	static final int NORTH = 3;

	final CityLocation from;
	final CityLocation to;
	Commodity type;
	int slot;
	int count;
	int demand;
	int [] pathTaken;

	public Traffic(CityLocation from, CityLocation to)
	{
		this.from = from;
		this.to = to;
	}

	public static int [] parsePath(String s)
	{
		int [] els = new int[s.length()];
		for (int i = 0; i < els.length; i++) {
			char c = s.charAt(i);
			els[i] = c == 'E' ? EAST :
				c == 'W' ? WEST :
				c == 'N' ? NORTH :
				c == 'S' ? SOUTH : 0;
		}
		return els;
	}

	public static String pathAsString(int [] path)
	{
		char [] cc = new char[path.length];
		for (int i = 0; i < path.length; i++) {
			cc[i] = path[i] == EAST ? 'E' :
				path[i] == NORTH ? 'N' :
				path[i] == WEST ? 'W' :
				path[i] == SOUTH ? 'S' : '?';
		}
		return new String(cc);
	}
}
