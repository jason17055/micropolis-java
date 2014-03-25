package micropolisj.gui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.Dimension;

import micropolisj.engine.Micropolis;

public class ScienceFrameA extends JFrame{

	Micropolis engine;
	
	JPanel panel;
	JButton jbPollution;
	JButton jbNuclear;
	JButton jbPowerEfficiency;
	JButton jbSolar;
	JButton jbWind;

	//UNIVERSITAET FUER ENERGIE UND UMWELT
	
	public ScienceFrameA(Micropolis m){
		super("Support local research at your University for Environment and Energy!");
		engine=m;

/*			image = ImageIO.read(new File("graphics/splash.png"));
			jlStreetUpgrade = new JLabel("Street Upgrade");
			jlRailUpgrade = new JLabel("Rail Upgrade");
			jlPollution = new JLabel("Reduce Pollution");
			jlPoliceUpgrade = new JLabel("Police Upgrade");
			jlFireDepUpgrade = new JLabel("Fire Department Upgrade");
			jlNuclear = new JLabel("Reduce Pollution Of Nuclear Power Plant");
			jlPowerEfficiency = new JLabel("Improce Efficiency Of Wind And Solar Power Stations");
			jlSolar = new JLabel("Research Solar Power Stations");
			jlWind = new JLabel("Research Wind Power Stations");
			jlTwoLaneRoad = new JLabel("Research Two-Lane Roads");
			jlAirport = new JLabel("Research Airports");
*/
			

		
		jbPollution = new JButton ("<html>Reduce Pollution<br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbNuclear = new JButton ("<html>Reduce Pollution Of<br>Nuclear Power Plant<br><br><br><font color=#666666>[XX points]</font></html>");
		jbPowerEfficiency = new JButton ("<html>Improce Efficiency<br>Of Wind And Solar<br>Power Stations<br><br><font color=#666666>[XX points]</font></html>");
		jbSolar = new JButton ("<html>Research Solar<br>Power Stations<br><br><br><font color=#666666>[XX points]</font></html>");
		jbWind = new JButton ("<html>Research Wind<br>Power Stations<br><br><br><font color=#666666>[XX points]</font></html>");

		jbPollution.setEnabled(true);
		jbNuclear.setEnabled(true);
		jbPowerEfficiency.setEnabled(jbSolar.isEnabled() || jbWind.isEnabled());
		jbSolar.setEnabled(true);
		jbWind.setEnabled(true);
		
		jbPollution.setToolTipText("Let more money flow towards research. They will discover new methods of decreasing air pollution.");
		jbNuclear.setToolTipText("Let more money flow towards nuclear research. It will decrease air pollution immediately.");
		jbPowerEfficiency.setToolTipText("Let more money flow towards research. You will have immediate results.");
		jbSolar.setToolTipText("Let more money flow towards research. Researchers will work on finding a way to use solar power in your town.");
		jbWind.setToolTipText("Let more money flow towards research. Researchers will work on finding a way to use wind power in your town.");
		
		jbPollution.setPreferredSize(new Dimension(150,110));
		jbNuclear.setPreferredSize(new Dimension(150,110));
		jbPowerEfficiency.setPreferredSize(new Dimension(150,110));
		jbSolar.setPreferredSize(new Dimension(150,110));
		jbWind.setPreferredSize(new Dimension(150,110));
		
		
		jbPollution.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
		});
		
		jbNuclear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
		
		jbPowerEfficiency.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
		});
		
		jbSolar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
		
		jbWind.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
		});
		
		
		panel  = new JPanel(null,true);	
		getContentPane().add(panel);
		
		panel.add(jbPollution);
		panel.add(jbNuclear);
		panel.add(jbPowerEfficiency);
		panel.add(jbSolar);
		panel.add(jbWind);
	
		
		panel.setLayout(new FlowLayout());
		
		pack();
		setSize(350,390);
		setLocationRelativeTo(getParent());
	}	
	
	
}
