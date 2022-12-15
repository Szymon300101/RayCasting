package window;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

import org.kabeja.dxf.DXFArc;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLine;

import light.DotSource;
import light.Source;
import light.SunSource;
import serialization.Serializer;
import shape.Arc;
import shape.Bezier;
import shape.Line;
import shape.Shape;
import util.CColor;
import util.DXFProcessor;
import util.Material;
import window.settings.*;

public class MenuToolBar implements ActionListener{
	
	AppFrame mFrame;
	public JToolBar bar = new JToolBar("Menu Toolbar");
	public Dialog settingsDialog;
	public JButton mesurementReadout;
	
	public MenuToolBar(AppFrame frame)
	{
		this.mFrame=frame;
		settingsDialog = new Dialog(mFrame);
		bar.add(newButton("LOAD FILE","OPEN","Load DXF file to the project",Color.cyan, this));
		bar.add(newButton("LOAD FRAME","LOAD","Load DXF file to the project",Color.cyan, this));
		
		bar.setBackground(Color.getHSBColor(0,(float)0.1,(float)0.2));
		bar.setBorderPainted(false);
	}
	
	public JButton newButton(String name, String command, String tipText,Color color, ActionListener listener)
	{
		JButton button=new JButton(name);
		button.addActionListener(listener);
		button.setActionCommand(command);
		button.setToolTipText(tipText);
		button.setBackground(color);
		//button.setBorderPainted(false);
		return button;
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		
		if(cmd.equals("SAVE"))
		{
			Serializer.SaveFrame(mFrame);
		}else
		if(cmd.equals("LOAD"))
		{
			Serializer.LoadFrame(mFrame);
		}else
		if(cmd.equals("OPEN"))
		{
			FileDialog dialog = new FileDialog((Frame) mFrame, "Select File to Open");
		    dialog.setFile("*.dxf");
		    dialog.setMode(FileDialog.LOAD);
		    dialog.setVisible(true);
		    if(dialog.getFile()==null) return;
		    String file = dialog.getDirectory() + dialog.getFile();
		    System.out.println(file + " chosen.");
		    mFrame.reader=new DXFProcessor(file);
		    mFrame.reader.getInfo(); 
		    mFrame.fileOpen = true;
		    
		    mFrame.walls=new ArrayList<Shape>();
		    mFrame.materials=new ArrayList<Material>();
		    mFrame.sources=new ArrayList<Source>();
		    
		    for(DXFLayer layer: mFrame.reader.getLayers())
		    {
		    	Material layer_mat=new Material(layer.getName(),0,0,CColor.gray(255));
		        mFrame.materials.add(layer_mat);
		        
			    ArrayList<DXFLine> lines=mFrame.reader.getLines(layer);
			    if(lines!=null)
			    for(int i=0;i<lines.size();i++)
			      mFrame.walls.add((Shape) new Line(lines.get(i),layer_mat));
			    
			    ArrayList<Arc> arcs=mFrame.reader.getArcs(layer,layer_mat);
			    if(arcs!=null)
			    for(Arc a: arcs)
			      mFrame.walls.add((Shape) a);
			    
			    ArrayList<Bezier> beziers=mFrame.reader.getBeziers(layer,layer_mat);
			    if(beziers!=null)
			    for(Bezier b: beziers)
			      mFrame.walls.add((Shape) b);
			    
		    }
		    mFrame.graphic.getScale(mFrame.reader.getBounds());
		    mFrame.walls.addAll(mFrame.graphic.makeBorders());
//		    mFrame.walls.add((Shape)new Arc(2140,1800,70,350,120,new Material("",1,1,CColor.gray(255))));
			mFrame.sources.add((Source) new SunSource(mFrame,100,1));
		    
		    bar.remove(1);
		    bar.remove(0);
		    mFrame.repaint();
		    
		    bar.add(newButton("SETTINGS","openSettings","",Color.cyan, settingsDialog));
			bar.add(newButton("SAVE","SAVE","",Color.cyan, this));
			
			mesurementReadout=new JButton("");
			mesurementReadout.setBackground(Color.white);
			mesurementReadout.setEnabled(false);
			bar.add(mesurementReadout);
	   }
		
	}
	
	public void setMesurement(double value)
	{
		mesurementReadout.setText("Mesured Sum: " + value);
		mesurementReadout.repaint();
	}

}
