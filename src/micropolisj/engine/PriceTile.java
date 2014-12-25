// This file is part of MicropolisJ.
// Copyright (C) 2014 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

public class PriceTile extends Tile
{
	public Commodity commodity;
	public int price;

	public PriceTile(Commodity commodity, int price, Tile next)
	{
		this.commodity = commodity;
		this.price = price;
		this.next = next;
	}

	@Override
	public Tile alterNext(Tile newNext)
	{
		return new PriceTile(this.commodity, this.price, newNext);
	}
}
