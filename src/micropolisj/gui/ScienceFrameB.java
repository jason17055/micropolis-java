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

public class ScienceFrameB extends JFrame{

	Micropolis engine;
	
	JPanel panel;
	JButton jbStreetUpgrade;
	JButton jbRailUpgrade;
	JButton jbPoliceUpgrade;
	JButton jbFireDepUpgrade;
	JButton jbTwoLaneRoad;
	JButton jbAirport;

//UNIVERSITAET FUER INFRASTRUKTUR
	
	public ScienceFrameB(Micropolis m){
		super("Support local research at your University for Infrastructure!");
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
			

		
		jbStreetUpgrade = new JButton("<html>Street Upgrade<br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbRailUpgrade = new JButton ("<html>Rail Upgrade<br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbPoliceUpgrade = new JButton ("<html>Police Upgrade<br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbFireDepUpgrade = new JButton ("<html>Fire Department<br>Upgrade<br><br><br><font color=#666666>[XX points]</font></html>");
		jbTwoLaneRoad = new JButton ("<html>Research<br>Two-Lane Roads<br><br><br><font color=#666666>[XX points]</font><html>");
		jbAirport = new JButton ("<html>Research Airports<br><br><br><br><font color=#666666>[XX points]</font></html>");

		jbStreetUpgrade.setEnabled(true);
		jbRailUpgrade.setEnabled(true);
		jbPoliceUpgrade.setEnabled(true);
		jbFireDepUpgrade.setEnabled(true);
		jbTwoLaneRoad.setEnabled(true);
		jbAirport.setEnabled(true);
		
		jbStreetUpgrade.setToolTipText("Let more money flow towards road construction. The quality of your roads will improve.");
		jbRailUpgrade.setToolTipText("Let more money flow towards rail construction. The quality of your roads will improve.");
		jbPoliceUpgrade.setToolTipText("Let more money flow towards police stations. Police officers will be much more efficient.");
		jbFireDepUpgrade.setToolTipText("Let more money flow towards fire departments. Fire fighters will be much more efficient.");
		jbTwoLaneRoad.setToolTipText("Let more money flow towards rail construction. Eventually someone will find a way to reduce heavy traffic on your roads.");
		jbAirport.setToolTipText("Let more money flow towards research. You will be able to build an airport... sooner or later...");
		
		jbStreetUpgrade.setPreferredSize(new Dimension(150,110));
		jbRailUpgrade.setPreferredSize(new Dimension(150,110));
		jbPoliceUpgrade.setPreferredSize(new Dimension(150,110));
		jbFireDepUpgrade.setPreferredSize(new Dimension(150,110));
		jbTwoLaneRoad.setPreferredSize(new Dimension(150,110));
		jbAirport.setPreferredSize(new Dimension(150,110));
		
		
		jbStreetUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
		});
		
		jbRailUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
			
		jbPoliceUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
		
		jbFireDepUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
		});
		
		jbTwoLaneRoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});

		jbAirport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
		
		panel  = new JPanel(null,true);
		getContentPane().add(panel);
		
		panel.add(jbStreetUpgrade);
		panel.add(jbRailUpgrade);
		panel.add(jbPoliceUpgrade);
		panel.add(jbFireDepUpgrade);
		panel.add(jbTwoLaneRoad);
		panel.add(jbAirport);
		
		
		panel.setLayout(new FlowLayout());
		
		pack();
		setSize(350,390);			
		setLocationRelativeTo(getParent());
		
	}	
	
	
}
