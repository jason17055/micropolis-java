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
	JButton jbStreetUpgrade;
	JButton jbRailUpgrade;
	JButton jbPollution;
	JButton jbPoliceUpgrade;
	JButton jbFireDepUpgrade;
	JButton jbNuclear;
	JButton jbPowerEfficiency;
	JButton jbSolar;
	JButton jbWind;
	JButton jbTwoLaneRoad;
	JButton jbAirport;

	
	public ScienceFrameA(Micropolis m){
		super("Fördere die Forschung!");
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
			

		
		jbStreetUpgrade = new JButton("Street Upgrade");
		jbRailUpgrade = new JButton ("Rail Upgrade");
		jbPollution = new JButton ("Reduce Pollution");
		jbPoliceUpgrade = new JButton ("Police Upgrade");
		jbFireDepUpgrade = new JButton ("Fire Department Upgrade");
		jbNuclear = new JButton ("Reduce Pollution Of Nuclear Power Plant");
		jbPowerEfficiency = new JButton ("Improce Efficiency  Of Wind And Solar Power Stations");
		jbSolar = new JButton ("Research Solar Power Stations");
		jbWind = new JButton ("Research Wind Power Stations");
		jbTwoLaneRoad = new JButton ("Research Two-Lane Roads");
		jbAirport = new JButton ("Research Airports");

		jbStreetUpgrade.setEnabled(true);
		jbRailUpgrade.setEnabled(true);
		jbPollution.setEnabled(true);
		jbPoliceUpgrade.setEnabled(true);
		jbFireDepUpgrade.setEnabled(true);
		jbNuclear.setEnabled(true);
		jbPowerEfficiency.setEnabled(jbSolar.isEnabled() || jbWind.isEnabled());
		jbSolar.setEnabled(true);
		jbWind.setEnabled(true);
		jbTwoLaneRoad.setEnabled(true);
		jbAirport.setEnabled(true);
		
		jbStreetUpgrade.setToolTipText("Let more money flow towards road construction. You will have immediate results.");
		jbRailUpgrade.setToolTipText("Let more money flow towards rail construction. You will have immediate results.");
		jbPollution.setToolTipText("Let more money flow towards research. They will discover new methods of decreasing air pollution.");
		jbPoliceUpgrade.setToolTipText("Let more money flow towards police stations. Police officers will be much more efficient.");
		jbFireDepUpgrade.setToolTipText("Let more money flow towards fire departments. Fire fighters will be much more efficient.");
		jbNuclear.setToolTipText("Let more money flow towards nuclear research. It will decrease air pollution immediately.");
		jbPowerEfficiency.setToolTipText("Let more money flow towards research. You will have immediate results.");
		jbSolar.setToolTipText("Let more money flow towards rail construction. You will have immediate results.");
		jbWind.setToolTipText("Let more money flow towards rail construction. You will have immediate results.");
		jbTwoLaneRoad.setToolTipText("Let more money flow towards rail construction. You will have immediate results.");
		jbAirport.setToolTipText("Let more money flow towards rail construction. You will have immediate results.");
		
		jbStreetUpgrade.setPreferredSize(new Dimension(150,90));
		jbRailUpgrade.setPreferredSize(new Dimension(150,90));
		jbPollution.setPreferredSize(new Dimension(150,90));
		jbPoliceUpgrade.setPreferredSize(new Dimension(150,90));
		jbFireDepUpgrade.setPreferredSize(new Dimension(150,90));
		jbNuclear.setPreferredSize(new Dimension(150,90));
		jbPowerEfficiency.setPreferredSize(new Dimension(150,90));
		jbSolar.setPreferredSize(new Dimension(150,90));
		jbWind.setPreferredSize(new Dimension(100,90));
		jbTwoLaneRoad.setPreferredSize(new Dimension(150,90));
		jbAirport.setPreferredSize(new Dimension(150,90));
		
		
		jbStreetUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
		});
		
		jbRailUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
		
		jbPollution.addActionListener(new ActionListener(){
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
		
		jbTwoLaneRoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});

		jbAirport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			}
			
		});
		
		panel  = new JPanel(null,true);
		setSize(600,400);		
		getContentPane().add(panel);
		
		panel.add(jbStreetUpgrade);
		panel.add(jbRailUpgrade);
		panel.add(jbPollution);
		panel.add(jbPoliceUpgrade);
		panel.add(jbFireDepUpgrade);
		panel.add(jbNuclear);
		panel.add(jbPowerEfficiency);
		panel.add(jbSolar);
		panel.add(jbWind);
		panel.add(jbTwoLaneRoad);
		panel.add(jbAirport);
		
		
		panel.setLayout(new FlowLayout());
		
		pack();
		
		
	}	
	
	
}
