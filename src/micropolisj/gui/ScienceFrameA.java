package micropolisj.gui;

import javax.swing.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.ArrayList;

import micropolisj.engine.Micropolis;
import micropolisj.engine.techno.GeneralTechnology;

public class ScienceFrameA extends JDialog {

	Micropolis engine;
	
	JPanel panel;
	JButton jbPollution;
	JButton jbNuclear;
	JButton jbPowerEfficiency;
	JButton jbSolar;
	JButton jbWind;
    JButton jbReset;
    JProgressBar progressBar;
    JLabel noTechSelectedLabel;
    ArrayList<JButton> buttonList;

    Color col2= new Color(255,229,168);
    Color col3= new Color(169,125,19);



    //UNIVERSITAET FUER NATURWISSENSCHAFT


    private void updateProgressBar(){
        if(engine.getSelectedEETech() != null){
            if(progressBar == null){
                progressBar = new JProgressBar(0, (int) engine.getSelectedEETech().getPointsNeeded());
                panel.add(progressBar);
            }
            if(noTechSelectedLabel != null) panel.remove(this.noTechSelectedLabel);
            progressBar.setMaximum((int) engine.getSelectedEETech().getPointsNeeded());
            progressBar.setValue((int) engine.getSelectedEETech().getPointsUsed());
            progressBar.setStringPainted(true);
        } else  {
            if(this.noTechSelectedLabel != null){
                panel.add(this.noTechSelectedLabel);
            } else {
                this.noTechSelectedLabel = new JLabel("No Technology selected to be researched.");
                panel.add(this.noTechSelectedLabel);
            }
        }
        this.repaint();
    }

    private void disableAllButtonsBut(JButton bNotToBeDisabled){
        for(JButton b : buttonList){
            b.setEnabled(false);
            b.setBackground(col2);
        }
        bNotToBeDisabled.setEnabled(true);
        bNotToBeDisabled.setBackground(col3);
        jbPowerEfficiency.setEnabled(engine.windTech.getIsResearched() && engine.solarTech.getIsResearched());
    }
	
	public ScienceFrameA(Window owner, Micropolis m){
		super(owner);
		this.engine=m;


        buttonList = new ArrayList<JButton>();
        jbReset = new JButton("Reset current Research.");
		jbPollution = new JButton ("<html>Reduce Pollution<br><br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbNuclear = new JButton ("<html>Reduce Pollution Of<br>Nuclear Power Plant<br><br><br><br><font color=#666666>[XX points]</font></html>");
		jbPowerEfficiency = new JButton ("<html>Improve<br> Efficiency Of<br>Wind And Solar<br>Power Stations<br><br><font color=#666666>[XX points]</font></html>");
		jbSolar = new JButton ("<html>Research Solar<br>Power Stations<br><br><br><br><font color=#666666>["+(int)engine.solarTech.getPointsUsed()+"/"+(int)engine.solarTech.getPointsNeeded()+" points]</font></html>");
		jbWind = new JButton ("<html>Research Wind<br>Power Stations<br><br><br><br><font color=#666666>["+(int)engine.windTech.getPointsUsed()+"/"+(int)engine.windTech.getPointsNeeded()+" points]</font></html>");
        buttonList.add(jbPollution);
        buttonList.add(jbNuclear);
        buttonList.add(jbPowerEfficiency);
        buttonList.add(jbSolar);
        buttonList.add(jbWind);

        GeneralTechnology selectedTech = engine.getSelectedEETech();
        if(selectedTech != null){
            if(selectedTech.isSame(engine.reducePollutionTech)){
                disableAllButtonsBut(jbPollution);
            } else if(selectedTech.isSame(engine.solarTech)){
                disableAllButtonsBut(jbSolar);
            } else if(selectedTech.isSame(engine.windTech)){
               disableAllButtonsBut(jbWind);
            }
        }

		jbNuclear.setEnabled(true);
		jbPowerEfficiency.setEnabled(engine.windTech.getIsResearched() && engine.solarTech.getIsResearched());
		jbSolar.setEnabled(true);
		jbWind.setEnabled(true);
        jbReset.setEnabled(true);
		
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
        jbReset.setToolTipText("You will lose all current research progress, but can research something differnt.");
		
		jbPollution.setPreferredSize(new Dimension(180,125));
		jbNuclear.setPreferredSize(new Dimension(180,125));
		jbPowerEfficiency.setPreferredSize(new Dimension(180,125));
		jbSolar.setPreferredSize(new Dimension(180,125));
		jbWind.setPreferredSize(new Dimension(180,125));
		



		jbPollution.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
                engine.setSelectedEETech(engine.reducePollutionTech);
                disableAllButtonsBut(jbPollution);
                updateProgressBar();

			}
		});
		
		jbNuclear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				engine.setSelectedEETech(engine.meltdownTech);
                disableAllButtonsBut(jbNuclear);
                updateProgressBar();

			}
			
		});
		
		jbPowerEfficiency.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
                engine.setSelectedEETech(engine.improveWindSolarTech);
                disableAllButtonsBut(jbPowerEfficiency);
                updateProgressBar();

			}
		});
		
		jbSolar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			
			
			engine.setSelectedEETech(engine.solarTech);
            disableAllButtonsBut(jbSolar);
            updateProgressBar();
			}
			
		});
		
		jbWind.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){

            engine.setSelectedEETech(engine.windTech);
            disableAllButtonsBut(jbWind);
            updateProgressBar();
			}
		});

        jbReset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){

                engine.getSelectedEETech().resetResearchPoints();
               for(JButton b : buttonList){
                   b.setEnabled(true);
               }
            }
        });
		
		
		panel  = new JPanel(null,true);	
		getContentPane().add(panel);
		
		panel.add(jbPollution);
		panel.add(jbNuclear);
		panel.add(jbPowerEfficiency);
		panel.add(jbSolar);
		panel.add(jbWind);
        panel.add(jbReset);
        updateProgressBar();


		
		panel.setLayout(new FlowLayout());
		
		pack();
		setSize(410,435);
		setLocationRelativeTo(getParent());
		Color color=new Color(245,181,28);  
		panel.setBackground(color);
	}	
	

}