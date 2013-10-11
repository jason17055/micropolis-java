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

	static enum TrafficType
	{
		EMPLOYMENT,  // connects RES to a job-providing zone
		RETAIL,      // connects COM to customers
		WHOLESALE;   // connects IND to commercial zones
	}

	final CityLocation from;
	final CityLocation to;
	TrafficType type;
	int slot;
	int count;
	int [] pathTaken;

	public Traffic(CityLocation from, CityLocation to)
	{
		this.from = from;
		this.to = to;
	}
}
