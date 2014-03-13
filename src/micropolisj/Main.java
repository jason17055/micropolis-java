// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj;

import javax.swing.*;

import micropolisj.gui.MainWindow;
import micropolisj.gui.SplashScreen;

public class Main
{
	public static SplashScreen splash = new SplashScreen();
//	public static MainWindow win = new MainWindow();
	
	static void createAndShowGUI()
	{
		splash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		splash.setVisible(true);
		//MainWindow win = new MainWindow();
		//win.setVisible(true);
		//win.doNewCity(true);
	}

	public static void main(String [] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			createAndShowGUI();
		}});
	}
}
