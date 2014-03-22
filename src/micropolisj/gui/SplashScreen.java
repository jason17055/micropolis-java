
package micropolisj.gui;

import static micropolisj.gui.MainWindow.EXTENSION;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

import micropolisj.Main;
import micropolisj.gui.*;
import micropolisj.engine.Micropolis;

import javax.swing.JDialog;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class SplashScreen extends JFrame{
	
	/*JButton jbNewGame;   //Everything commented is the button stuff of the earlier version.
	JButton jbLoadGame;
	JButton jbLastGame;
	JButton jbExit;
	JButton jbScenario;*/
	JPanel panel;
	JPanel panelNewGame;
	JPanel panelPrevGame;
	JPanel panelLoad;
	JPanel panelScenario;
	JPanel panelLeave;
	BufferedImage image;
	JLabel jlSplashImage;
	JLabel jlStartNewGame;
	JLabel jlPreviousCity;
	JLabel jlLoadCity;
	JLabel jlScenario;
	JLabel jlLeaveGame;
	
	
	static final ResourceBundle strings = MainWindow.strings;
	
	public SplashScreen(){
		super("WELCOME");
		try{
			image = ImageIO.read(new File("graphics/splash.png"));
			jlStartNewGame = new JLabel(new ImageIcon(ImageIO.read(new File("graphics/splash_1.png"))));
			jlPreviousCity = new JLabel(new ImageIcon(ImageIO.read(new File("graphics/splash_2.png"))));
			jlLoadCity = new JLabel(new ImageIcon(ImageIO.read(new File("graphics/splash_3.png"))));
			jlScenario = new JLabel(new ImageIcon(ImageIO.read(new File("graphics/splash_4.png"))));
			jlLeaveGame = new JLabel(new ImageIcon(ImageIO.read(new File("graphics/splash_5.png"))));
		} catch(IOException e){
			e.printStackTrace();
		}

		jlSplashImage = new JLabel(new ImageIcon(image));
		jlSplashImage.setBounds(0,0,800,600);
		jlStartNewGame.setBounds(0,0,800,600);
		jlPreviousCity.setBounds(0,0,800,600);
		jlLoadCity.setBounds(0,0,800,600);
		jlScenario.setBounds(0,0,800,600);
		jlLeaveGame.setBounds(0,0,800,600);
		
		/*jbLastGame = new JButton("Load previous city");
		jbLastGame.setEnabled(false);
		
		jbNewGame = new JButton("Start a new city");
		
		jbLoadGame = new JButton("Load city");
		
		jbExit = new JButton("Leave game");
		
		jbScenario = new JButton("Play a scenario");
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
		*/
		
		
		
		panel  = new JPanel(null,true);
		panel.setSize(800,600);
		getContentPane().add(panel);
		panel.add(jlSplashImage);
		
		panelNewGame = new JPanel(null,true);
		panelNewGame.setBounds(490,220,200,30);
		HandlerNewGame handlerng = new HandlerNewGame();
		panelNewGame.addMouseListener(handlerng);
		panel.add(panelNewGame);
		
		panelPrevGame = new JPanel(null,true);
		panelPrevGame.setBounds(490,275,200,30);
		HandlerPrevGame handlerpg = new HandlerPrevGame();
		panelPrevGame.addMouseListener(handlerpg);
		panel.add(panelPrevGame);
		
		panelLoad = new JPanel(null,true);
		panelLoad.setBounds(490,335,200,30);
		HandlerLoad handlerl = new HandlerLoad();
		panelLoad.addMouseListener(handlerl);
		panel.add(panelLoad);
		
		panelScenario = new JPanel(null,true);
		panelScenario.setBounds(490,395,200,30);
		HandlerScenario handlers = new HandlerScenario();
		panelScenario.addMouseListener(handlers);
		panel.add(panelScenario);
		
		panelLeave = new JPanel(null,true);
		panelLeave.setBounds(490,455,200,30);
		HandlerLeave handlerle = new HandlerLeave();
		panelLeave.addMouseListener(handlerle);
		panel.add(panelLeave);
		
		//panel.setLayout(new BorderLayout());
		/*panel.add(jbNewGame);
		panel.add(jbLastGame);
		panel.add(jbLoadGame);
		panel.add(jbScenario);
		panel.add(jbExit);
		getContentPane().add(panel, BorderLayout.SOUTH);
		getContentPane().add(jlSplashImage, BorderLayout.NORTH);*/
		
		//pack();
		
		
	}
	
	private class HandlerNewGame implements MouseListener{
		public void mouseClicked(MouseEvent event){
			setVisible(false);
			MainWindow win = new MainWindow();
			
			win.setVisible(false);
			win.doNewCity(true);
		}
		public void mousePressed(MouseEvent event){
			
		}
		public void mouseReleased(MouseEvent event){
			
		}
		public void mouseEntered(MouseEvent event){
			panel.remove(jlSplashImage);
			removeBackgroundPanels();
			panel.add(jlStartNewGame);
			addBackgroundPanels();
			panel.repaint();
		}
		public void mouseExited(MouseEvent event){
			panel.remove(jlStartNewGame);
			removeBackgroundPanels();
			panel.add(jlSplashImage);
			addBackgroundPanels();
			panel.repaint();
		}
	}
	
	private class HandlerPrevGame implements MouseListener{
		public void mouseClicked(MouseEvent event){
			
		}
		public void mousePressed(MouseEvent event){
			
		}
		public void mouseReleased(MouseEvent event){
			
		}
		public void mouseEntered(MouseEvent event){
			panel.remove(jlSplashImage);
			removeBackgroundPanels();
			panel.add(jlPreviousCity);
			addBackgroundPanels();
			panel.repaint();
		}
		public void mouseExited(MouseEvent event){
			panel.remove(jlPreviousCity);
			removeBackgroundPanels();
			panel.add(jlSplashImage);
			addBackgroundPanels();
			panel.repaint();
		}
	}
	
	private class HandlerLoad implements MouseListener{
		public void mouseClicked(MouseEvent event){
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
		public void mousePressed(MouseEvent event){
			
		}
		public void mouseReleased(MouseEvent event){
			
		}
		public void mouseEntered(MouseEvent event){
			panel.remove(jlSplashImage);
			removeBackgroundPanels();
			panel.add(jlLoadCity);
			addBackgroundPanels();
			panel.repaint();
		}
		public void mouseExited(MouseEvent event){
			panel.remove(jlLoadCity);
			removeBackgroundPanels();
			panel.add(jlSplashImage);
			addBackgroundPanels();
			panel.repaint();
		}
	}
	
	private class HandlerScenario implements MouseListener{
		public void mouseClicked(MouseEvent event){
			
		}
		public void mousePressed(MouseEvent event){
			
		}
		public void mouseReleased(MouseEvent event){
			
		}
		public void mouseEntered(MouseEvent event){
			panel.remove(jlSplashImage);
			removeBackgroundPanels();
			panel.add(jlScenario);
			addBackgroundPanels();
			panel.repaint();
		}
		public void mouseExited(MouseEvent event){
			panel.remove(jlScenario);
			removeBackgroundPanels();
			panel.add(jlSplashImage);
			addBackgroundPanels();
			panel.repaint();
		}
	}
	
	private class HandlerLeave implements MouseListener{
		public void mouseClicked(MouseEvent event){
			System.exit(0);
		}
		public void mousePressed(MouseEvent event){
			
		}
		public void mouseReleased(MouseEvent event){
			
		}
		public void mouseEntered(MouseEvent event){
			panel.remove(jlSplashImage);
			removeBackgroundPanels();
			panel.add(jlLeaveGame);
			addBackgroundPanels();
			panel.repaint();
		}
		public void mouseExited(MouseEvent event){
			panel.remove(jlLeaveGame);
			removeBackgroundPanels();
			panel.add(jlSplashImage);
			addBackgroundPanels();
			panel.repaint();
		}
	}
	
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(image, 0, 0, null);           
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
	
	private void removeBackgroundPanels(){
		panel.remove(panelNewGame);
		panel.remove(panelPrevGame);
		panel.remove(panelLoad);
		panel.remove(panelScenario);
		panel.remove(panelLeave);
		
	}
	private void addBackgroundPanels(){
		panel.add(panelNewGame);
		panel.add(panelPrevGame);
		panel.add(panelLoad);
		panel.add(panelScenario);
		panel.add(panelLeave);
	}

}
