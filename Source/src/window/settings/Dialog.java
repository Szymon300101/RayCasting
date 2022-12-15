package window.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import window.AppFrame;
import javax.swing.border.LineBorder;

import light.DotSource;
import light.SunSource;
import util.Material;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class Dialog implements ActionListener
{
	AppFrame frame;
	JDialog d;
	
	JSpinner refSpinner;
	JSpinner percSpinner;
	JSpinner mpSpinner;
	
	public ArrayList<JSpinner> materialRefSpins=new ArrayList<JSpinner>();
	public ArrayList<JSpinner> materialDiffSpins=new ArrayList<JSpinner>();
	public ArrayList<JCheckBox> materialMesCbx=new ArrayList<JCheckBox>();
	
	JSpinner lightTypeSpinner;
	JSpinner raySpinner;
	public JSpinner angleSpinner;
	
	public Dialog(AppFrame frame)
	{
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if(s.equals("openSettings"))
		{
			if(d == null)
				buildDialog();
			else
				d.setVisible(true);
		}else if(s.equals("applySettings"))
		{
			Settings.REFLECTIONS = (int)refSpinner.getValue();
			Settings.PERC_LIGHT = (double)percSpinner.getValue();
			Settings.MIN_POWER = (double)mpSpinner.getValue();
			
			for (int i = 0; i < frame.materials.size(); i++) {
				frame.materials.get(i).difusion = (double) materialDiffSpins.get(i).getValue();
				frame.materials.get(i).reflect = (double) materialRefSpins.get(i).getValue();
				frame.materials.get(i).isMesuring = materialMesCbx.get(i).isSelected();
			}
			
			String lightType = (String) lightTypeSpinner.getValue();
			int rayCount = (int) raySpinner.getValue();
			int angle = (int) angleSpinner.getValue();
			if(lightType.equals("Sun"))
			{
				frame.sources.set(0, new SunSource(frame, rayCount,1));
				frame.sources.get(0).setDir(Math.toRadians(angle));
			}else
			{
				frame.sources.set(0, new DotSource(frame, rayCount));
			}
			
			frame.isMousePosValid = false;
		    frame.graphic.repaint();
		}
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void buildDialog()
	{
		d = new JDialog(frame, "Settings");
		d.setSize(506, 400);
		d.setVisible(true);
//		JLabel l = new JLabel("Label");
//        d.add(l);
//
//        JSpinner spinner = new JSpinner(model);
//        l.setLabelFor(spinner);
//        d.add(spinner);
//        JSpinner spinner2 = new JSpinner(model);
//        d.add(spinner2);
		JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 3));
        JPanel midPanel = new JPanel();
        midPanel.setSize(200, 1000);
        midPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        JLabel midTitle = new JLabel("Materials:");
        midTitle.setBounds(11, 12, 47, 14);
        JPanel rightPanel = new JPanel();
        rightPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        rightPanel.setLayout(new GridLayout(4, 1, 0, 0));
        JLabel rightTitle = new JLabel("Lights:");
        rightTitle.setHorizontalAlignment(SwingConstants.CENTER);
        rightTitle.setVerticalAlignment(SwingConstants.TOP);
        rightPanel.add(rightTitle);
        
        
        JPanel left_panel = new JPanel();
        left_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        mainPanel.add(left_panel);
        left_panel.setLayout(new GridLayout(4, 1, 0, 0));
        
        JLabel lblSettings = new JLabel("Settings:");
        lblSettings.setHorizontalAlignment(SwingConstants.CENTER);
        lblSettings.setVerticalAlignment(SwingConstants.TOP);
        left_panel.add(lblSettings);
        
        JPanel panel = new JPanel();
        left_panel.add(panel);
        panel.setLayout(null);

		SpinnerModel refModel =new SpinnerNumberModel(Settings.REFLECTIONS,0,100,1);
        refSpinner = new JSpinner(refModel);
        refSpinner.setBounds(39, 36, 49, 20);
        panel.add(refSpinner);
        
        JLabel lblNewLabel = new JLabel("Reflections");
        lblNewLabel.setBounds(10, 11, 81, 14);
        panel.add(lblNewLabel);
        
        JPanel panel_1 = new JPanel();
        panel_1.setLayout(null);
        left_panel.add(panel_1);

		SpinnerModel percModel =new SpinnerNumberModel(Settings.PERC_LIGHT,1,10,0.2);
		percSpinner = new JSpinner(percModel);
		percSpinner.setBounds(39, 36, 49, 20);
        panel_1.add(percSpinner);
        
        JLabel lblBrightness = new JLabel("Brightness");
        lblBrightness.setBounds(10, 11, 81, 14);
        panel_1.add(lblBrightness);
        
        JPanel panel_2 = new JPanel();
        panel_2.setLayout(null);
        left_panel.add(panel_2);

		SpinnerModel mpModel =new SpinnerNumberModel(Settings.MIN_POWER,0.01,0.5,0.01);
        mpSpinner = new JSpinner(mpModel);
        mpSpinner.setBounds(39, 36, 49, 20);
        panel_2.add(mpSpinner);
        
        JLabel lblMinPower = new JLabel("Min. Power");
        lblMinPower.setBounds(10, 11, 81, 14);
        panel_2.add(lblMinPower);
        //mainPanel.add(midPanel);
        JPanel scrolablePanel = new JPanel();
        scrolablePanel.setLayout(null);
        //scrolablePanel.add(midPanel);
        //int i=0;
        for (int i = 0; i < frame.materials.size();i++) {
        	JPanel matPanel = new JPanel();
        	matPanel.setBounds(1, 44 + 130*i, 150, 111);
        	matPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
            matPanel.setLayout(new MigLayout("", "[70][40]", "[20px][20][20][20]"));
            
            JLabel Mat_Name = new JLabel(frame.materials.get(i).name);
            matPanel.add(Mat_Name, "cell 0 0,alignx left,aligny center");
            
            JLabel lblNewLabel_2 = new JLabel("Diffusion");
            matPanel.add(lblNewLabel_2, "cell 0 1");

    		SpinnerModel matDiffModel =new SpinnerNumberModel(frame.materials.get(i).difusion,0,1,0.1);
            JSpinner matDiffSpiner = new JSpinner(matDiffModel);
            matPanel.add(matDiffSpiner, "cell 1 1,growx");
            materialDiffSpins.add(matDiffSpiner);
            
            JLabel lblNewLabel_1 = new JLabel("Reflectivity");
            matPanel.add(lblNewLabel_1, "cell 0 2");

    		SpinnerModel matReffModel =new SpinnerNumberModel(frame.materials.get(i).reflect,0,1,0.1);
            JSpinner matRefSpinner = new JSpinner(matReffModel);
            matPanel.add(matRefSpinner, "cell 1 2,growx,aligny center");
            materialRefSpins.add(matRefSpinner);
            
            JLabel lblNewLabel_1_1 = new JLabel("Is Mesuring");
            matPanel.add(lblNewLabel_1_1, "cell 0 3");
            
            JCheckBox MesuringCbx = new JCheckBox("");
            MesuringCbx.setSelected(frame.materials.get(i).isMesuring);
            matPanel.add(MesuringCbx, "cell 1 3");
            materialMesCbx.add(MesuringCbx);
            midPanel.setLayout(null);
            midPanel.add(midTitle);
            midPanel.add(matPanel);
            
		}
        midPanel.repaint();
        midPanel.setPreferredSize(new Dimension(100,180 + 130*frame.materials.size()));
        JScrollPane vertical = new JScrollPane(midPanel);
        vertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(vertical);
        mainPanel.add(rightPanel);
        
        JPanel panel_3 = new JPanel();
        rightPanel.add(panel_3);
        panel_3.setLayout(null);

        String[] types = {"Sun", "Point"};
		SpinnerModel lightTypeModel =new SpinnerListModel(types);
        lightTypeSpinner = new JSpinner(lightTypeModel);
        lightTypeSpinner.setBounds(37, 32, 79, 20);
        panel_3.add(lightTypeSpinner);
        
        JLabel lblNewLabel_3 = new JLabel("Light Type");
        lblNewLabel_3.setBounds(10, 7, 106, 14);
        panel_3.add(lblNewLabel_3);
        
        JPanel panel_3_1 = new JPanel();
        panel_3_1.setLayout(null);
        rightPanel.add(panel_3_1);

		SpinnerModel rayModel =new SpinnerNumberModel(frame.sources.get(0).r_num,1,1000,1);
        raySpinner = new JSpinner(rayModel);
        raySpinner.setBounds(37, 32, 79, 20);
        panel_3_1.add(raySpinner);
        
        JLabel lblNewLabel_3_1 = new JLabel("Ray Count");
        lblNewLabel_3_1.setBounds(10, 7, 106, 14);
        panel_3_1.add(lblNewLabel_3_1);
        
        JPanel panel_3_1_1 = new JPanel();
        panel_3_1_1.setLayout(null);
        rightPanel.add(panel_3_1_1);

		SpinnerModel angleModel =new SpinnerNumberModel(0,0,360,1);
        angleSpinner = new JSpinner(angleModel);
        angleSpinner.setBounds(37, 32, 79, 20);
        panel_3_1_1.add(angleSpinner);
        
        JLabel lblNewLabel_3_1_1 = new JLabel("Sun Angle");
        lblNewLabel_3_1_1.setBounds(10, 7, 106, 14);
        panel_3_1_1.add(lblNewLabel_3_1_1);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("Apply");
        buttonPanel.add(okButton);
        okButton.addActionListener(this);
        okButton.setActionCommand("applySettings");

        d.setSize(500,400);
        d.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        d.getContentPane().add(mainPanel, BorderLayout.CENTER);
        d.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        d.setLocationRelativeTo(null);
	}
}
