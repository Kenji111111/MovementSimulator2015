import java.awt.Color;
import java.awt.Graphics;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import processing.core.PApplet;


public class customDrawer extends DebugDraw{
	
	Color myColor = new Color(255,255,255);
	PApplet p;
	Graphics g;
	
	public static int CIRCLE_POINTS = 20;
	
	public customDrawer(PApplet app){
		p = app;
		g = p.getGraphics();
	}
	
	
	

	@Override
	public void drawCircle(Vec2 arg0, float radius, Color3f arg2) {
		// TODO Auto-generated method stub
		System.out.println("circul");
		g.setColor(myColor);
		g.drawOval(Math.round(arg0.x), Math.round(arg0.y), Math.round(radius * 2), Math.round(radius * 2));
		
	}

	@Override
	public void drawPoint(Vec2 arg0, float radius, Color3f arg2) {
		System.out.println("point");
		// TODO Auto-generated method stub
		//drawSolidCircle(arg0, radius, arg0, arg2);
		
	}
	
	

	@Override
	public void drawPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		// TODO Auto-generated method stub
		System.out.println("ply");
        if(vertexCount == 1){
            drawSegment(vertices[0], vertices[0], color);
            return;
	    }
	    
	    for(int i=0; i<vertexCount-1; i+=1){
	            drawSegment(vertices[i], vertices[i+1], color);
	    }
	    
	    if(vertexCount > 2){
	            drawSegment(vertices[vertexCount-1], vertices[0], color);
	    }
		
	}

	@Override
	public void drawSegment(Vec2 p0, Vec2 p1, Color3f color) {
		// TODO Auto-generated method stub
		System.out.println("secement");
		g.setColor(myColor);
		g.drawLine(Math.round(p0.x), Math.round(p0.y), Math.round(p0.x), Math.round(p1.y));
		
	}

	@Override
	public void drawSolidCircle(Vec2 arg0, float radius, Vec2 arg2, Color3f arg3) {
		System.out.println("Filcircel");
		// TODO Auto-generated method stub

		g.setColor(myColor);
		g.fillOval(Math.round(arg0.x), Math.round(arg0.y), Math.round(radius * 2), Math.round(radius * 2));
		
	}

	@Override
	public void drawSolidPolygon(Vec2[] arg0, int arg1, Color3f arg2) {
		System.out.println("Fillply");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawString(float arg0, float arg1, String arg2, Color3f arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawXForm(XForm arg0) {
		// TODO Auto-generated method stub
		
		
	}
	

}
