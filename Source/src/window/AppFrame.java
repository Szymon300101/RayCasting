package window;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import light.DotSource;
import light.Source;
import shape.Shape;
import util.DXFProcessor;
import util.Material;


public class AppFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public boolean fileOpen = false;
	
	public ArrayList<Shape> walls=new ArrayList<Shape>();
	public ArrayList<Material> materials=new ArrayList<Material>();
	public ArrayList<Source> sources=new ArrayList<Source>();
	
	public DXFProcessor reader;
	public GraphicPanel graphic = new GraphicPanel(this);
	public MouseListener mListener = new MouseListener(this);
	public MenuToolBar menu = new MenuToolBar(this);
	public double sumMesuredPower;
	
	public boolean isMousePosValid = true;
	
	public AppFrame()
	{
		this.setSize(1000,700);
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(graphic);
		this.addMouseListener(mListener);
		this.add(menu.bar,BorderLayout.PAGE_START);
	}
	
	public void incrementmesurement(double increment)
	{
		sumMesuredPower += increment;
		menu.setMesurement(sumMesuredPower);
	}
}