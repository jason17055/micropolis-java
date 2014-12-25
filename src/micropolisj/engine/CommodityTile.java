// This file is part of MicropolisJ.
// Copyright (C) 2014 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

public class CommodityTile extends Tile
{
	public Commodity commodity;
	public int quantity;

	public CommodityTile(Commodity commodity, int quantity, Tile next)
	{
		this.commodity = commodity;
		this.quantity = quantity;
		this.next = next;
	}

	@Override
	public Tile alterNext(Tile newNext)
	{
		return new CommodityTile(this.commodity, this.quantity, newNext);
	}
}
