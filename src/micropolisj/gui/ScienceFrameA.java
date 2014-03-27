package micropolisj.gui;

import javax.swing.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.*;
import java.awt.Dimension;

import micropolisj.engine.Micropolis;

public class ScienceFrameA extends JDialog {

	Micropolis engine;
	
	JPanel panel;
	JButton jbPollution;
	JButton jbNuclear;
	JButton jbPowerEfficiency;
	JButton jbSolar;
	JButton jbWind;
	

	//UNIVERSITAET FUER NATURWISSENSCHAFT
	
	public ScienceFrameA(Window owner, Micropolis m){
		super(owner);
		this.engine=m;

		
		jbPollution = new JButton ("<html>Reduce Pollution<br><br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbNuclear = new JButton ("<html>Reduce Pollution Of<br>Nuclear Power Plant<br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbPowerEfficiency = new JButton ("<html>Improve<br> Efficiency Of<br>Wind And Solar<br>Power Stations<br><br><font color=#666666>[XX points]</font></html>");
		jbSolar = new JButton ("<html>Research Solar<br>Power Stations<br><br><br><br><font color=#666666>["+(int)engine.solarTech.getPointsUsed()+"/"+(int)engine.solarTech.getPointsNeeded()+" points]</font></html>");
		jbWind = new JButton ("<html>Research Wind<br>Power Stations<br><br><br><br><font color=#666666>["+(int)engine.windTech.getPointsUsed()+"/"+(int)engine.windTech.getPointsNeeded()+" points]</font></html>");

		jbPollution.setEnabled(true);
		jbNuclear.setEnabled(true);
		jbPowerEfficiency.setEnabled(jbSolar.isEnabled() || jbWind.isEnabled());
		jbSolar.setEnabled(true);
		jbWind.setEnabled(true);
		
		Color c1= new Color(255,229,168);
		
		jbPollution.setBackground(c1);
		jbNuclear.setBackground(c1);
		jbPowerEfficiency.setBackground(c1);
		jbSolar.setBackground(c1);
		jbWind.setBackground(c1);

		Color c2= new Color(169,125,19);
		
		jbPollution.setForeground(c2);
		jbNuclear.setForeground(c2);
		jbPowerEfficiency.setForeground(c2);
		jbSolar.setForeground(c2);
		jbWind.setForeground(c2);		
		
		jbPollution.setToolTipText("Let more money flow towards research. They will discover new methods of decreasing air pollution.");
		jbNuclear.setToolTipText("Let more money flow towards nuclear research. It will decrease air pollution immediately.");
		jbPowerEfficiency.setToolTipText("Let more money flow towards research. You will have immediate results.");
		jbSolar.setToolTipText("Let more money flow towards research. Researchers will work on finding a way to use solar power in your town.");
		jbWind.setToolTipText("Let more money flow towards research. Researchers will work on finding a way to use wind power in your town.");
		
		jbPollution.setPreferredSize(new Dimension(180,125));
		jbNuclear.setPreferredSize(new Dimension(180,125));
		jbPowerEfficiency.setPreferredSize(new Dimension(180,125));
		jbSolar.setPreferredSize(new Dimension(180,125));
		jbWind.setPreferredSize(new Dimension(180,125));
		
		
		jbPollution.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
                engine.setSelectedEETech(engine.reducePollutionTech);

			}
		});
		
		jbNuclear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

			}
			
		});
		
		jbPowerEfficiency.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
                engine.setSelectedEETech(engine.improveWindSolarTech);

			}
		});
		
		jbSolar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			
			
			engine.setSelectedEETech(engine.solarTech);
			
			jbPollution.setEnabled(true);
			jbNuclear.setEnabled(true);
			jbPowerEfficiency.setEnabled(true);
			jbSolar.setEnabled(false);
			jbWind.setEnabled(true);
			
			Color col2= new Color(169,125,19);
			Color col3= new Color(255,229,168);

			jbPollution.setBackground(col3);
			jbNuclear.setBackground(col3);
			jbPowerEfficiency.setBackground(col3);
			jbSolar.setBackground(col2);
			jbWind.setBackground(col3);
			}
			
		});
		
		jbWind.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

            engine.setSelectedEETech(engine.windTech);
			
			jbPollution.setEnabled(true);
			jbNuclear.setEnabled(true);
			jbPowerEfficiency.setEnabled(true);
			jbSolar.setEnabled(true);
			jbWind.setEnabled(false);
			
			Color col2= new Color(169,125,19);
			Color col3= new Color(255,229,168);

			jbPollution.setBackground(col3);
			jbNuclear.setBackground(col3);
			jbPowerEfficiency.setBackground(col3);
			jbSolar.setBackground(col3);
			jbWind.setBackground(col2);			
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
		setSize(410,435);
		setLocationRelativeTo(getParent());
		Color color=new Color(245,181,28);  
		panel.setBackground(color);
	}	
	

}