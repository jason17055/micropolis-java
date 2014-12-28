// This file is part of MicropolisJ.
// Copyright (C) 2014 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

public class BusinessTile extends Tile
{
	public int funds;
	///Unspent production points from previous week.
	public int production;

	public BusinessTile(int funds, Tile next)
	{
		this.funds = funds;
		this.next = next;
	}

	@Override
	public Tile alterNext(Tile newNext)
	{
		if (newNext == next) { return this; }

		BusinessTile bt = new BusinessTile(this.funds, newNext);
		bt.production = this.production;
		return this;
	}
}
