import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Panneau extends JPanel{
	
	public Panneau()
	{
		super();
		
	}
	
	private static class Line{
	    final int x1; 
	    final int y1;
	    final int x2;
	    final int y2;

	    public Line(int x1, int y1, int x2, int y2) {
	        this.x1 = x1;
	        this.y1 = y1;
	        this.x2 = x2;
	        this.y2 = y2;
	    }               
	}
	
	private static class Rectangle{
	    Color color;
	    int x, y, w, h;
	    

	    public Rectangle(int x, int y, int w, int h, Color color) {
	        this.x = x;
	        this.y = y;
	        this.w = w;
	        this.h = h;
	        this.color = color;
	    }               
	}
	
	private ArrayList<Line> lines = new ArrayList<Line>();
	private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
	
	public void addLine(int x1, int y1, int x2, int y2)
	{
		Line l = new Line(x1, y1, x2, y2);
		this.lines.add(l);
	}
	
	public void addRectangle(int i, int j, int w, int h, Color color)
	{
		Rectangle r = new Rectangle(i, j, w, h, color);
		this.rectangles.add(r);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		for(Rectangle r: rectangles)
		{
			g.setColor(r.color);
			g.fillRect(r.x, r.y, r.w, r.h);
		}
		for(Line l: lines)
		{
			g.setColor(Color.BLACK);
			g.drawLine(l.x1, l.y1, l.x2, l.y2);
		}
    }

}
