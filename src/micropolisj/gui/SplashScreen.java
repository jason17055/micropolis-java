
package micropolisj.gui;

import static micropolisj.gui.MainWindow.EXTENSION;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

import micropolisj.Main;
import micropolisj.gui.*;

import micropolisj.engine.Micropolis;
import javax.swing.JDialog;
import java.io.File;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.event.*;

public class SplashScreen extends JFrame{
	
	JButton jbNewGame;
	JButton jbLoadGame;
	JButton jbLastGame;
	JButton jbExit;
	JButton jbScenario;
	JPanel panel;
	static final ResourceBundle strings = MainWindow.strings;
	
	public SplashScreen(){
		super("Willkommen");
		
		jbLastGame = new JButton("Letztes Spiel laden");
		jbLastGame.setEnabled(false);
		
		jbNewGame = new JButton("Neues Spiel starten");
		
		jbLoadGame = new JButton("Spiel laden");
		
		jbExit = new JButton("Spiel verlassen");
		
		jbScenario = new JButton("Szenario spielen");
		jbScenario.setEnabled(false);
		
		jbExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				System.exit(0);
			}
		});
		
		jbNewGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				setVisible(false);
				MainWindow win = new MainWindow();
				
				win.setVisible(false);
				win.doNewCity(true);
			}
		});
		
		jbLoadGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				try
				{	
					JFileChooser fc = new JFileChooser();
					FileNameExtensionFilter filter1 = new FileNameExtensionFilter(strings.getString("cty_file"), EXTENSION);
					fc.setFileFilter(filter1);

					int rv = fc.showOpenDialog(SplashScreen.this);
					if (rv == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						Micropolis newEngine = new Micropolis();
						newEngine.load(file);
						startPlaying(newEngine, file);

					}
				}
				catch (Exception e)
				{
					e.printStackTrace(System.err);
					JOptionPane.showMessageDialog(SplashScreen.this, e, strings.getString("main.error_caption"),
						JOptionPane.ERROR_MESSAGE);
				}
			}
			
		});
		
		
		
		
		panel  = new JPanel();
		//panel.setLayout(new BorderLayout());
		panel.add(jbNewGame);
		panel.add(jbLastGame);
		panel.add(jbLoadGame);
		panel.add(jbScenario);
		panel.add(jbExit);
		getContentPane().add(panel, BorderLayout.SOUTH);
		pack();
		
		
	}
	void startPlaying(Micropolis newEngine, File file)
	{
		
		MainWindow win = new MainWindow();
		win.setVisible(true);
		win.setEngine(newEngine);
		win.currentFile = file;
		win.makeClean();
		dispose();
	}

}
