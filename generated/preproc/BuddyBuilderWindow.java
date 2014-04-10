import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class BuddyBuilderWindow extends JFrame implements MouseListener, ActionListener, KeyListener, Runnable{
	
	Graphics g;
	BufferStrategy bf;
	
	enum partType {RECTANGLE, CIRCLE, NULL};
	
	boolean mouseDown = false;
	
	partType currentPartType = partType.RECTANGLE;
	JPanel buttonPanel = new JPanel();
	
	JButton rectButton = new JButton("Rectangle");
	JButton circleButton = new JButton("Circle");
	JButton headButton = new JButton("Head");
	JButton jointButton = new JButton("Joint");
	JButton appendageButton = new JButton("New Appendage");
	JButton doneButton = new JButton("Done");
	

	public static void main(String[] args){
		new BuddyBuilderWindow();
	}
	
	public BuddyBuilderWindow(){
		super("Buddy Builder v 1.0");
		setSize(300,550);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		
		JPanel masterPane = new JPanel();
		masterPane.setSize(getSize());
		add(masterPane);
		masterPane.setLayout(new BoxLayout(masterPane, BoxLayout.Y_AXIS));
		//masterPane.setBackground(new Color(20,140,60));
		
		Canvas c = new Canvas();
		c.setSize(300, 400);
		//c.setBackground(Color.ORANGE);
		masterPane.add(c);
		c.requestFocusInWindow();
		c.createBufferStrategy(2);
		bf = c.getBufferStrategy();
		g = bf.getDrawGraphics();
		c.addMouseListener(this);
		c.addKeyListener(this);
		
		buttonPanel.setSize(300, 150);
		//j.setBackground(Color.BLUE);
		masterPane.add(buttonPanel);
		
		masterPane.revalidate();
		
		add(new Component[]{rectButton, circleButton, jointButton, /*headButton,*/ appendageButton, doneButton});
		
		//new Thread(this).start();
	}
	
	int startX = -1;
	int startY = -1;

	public void mouseClicked(MouseEvent arg0) {
		// TODO don't use this
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("pressed");
		mouseDown = true;
		startX = arg0.getPoint().x;
		startY = arg0.getPoint().y;
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("released");
		mouseDown = false;
		
		g.setColor(Color.GRAY);
		switch (currentPartType){
			case CIRCLE:
				int diameter = Math.abs(arg0.getX() - startX) < Math.abs(arg0.getY() - startY) ? Math.abs(arg0.getX() - startX) : Math.abs(arg0.getY() - startY);
				
				g.fillOval(startX < arg0.getX() ? startX : arg0.getX(), startY < arg0.getY() ? startY : arg0.getY(), diameter, diameter);
				g.setColor(Color.GREEN);
				g.drawOval(startX < arg0.getX() ? startX : arg0.getX(), startY < arg0.getY() ? startY : arg0.getY(), diameter, diameter);
				break;
			case RECTANGLE:
				g.fillRect(startX < arg0.getX() ? startX : arg0.getX(), startY < arg0.getY() ? startY : arg0.getY(), Math.abs(arg0.getX() - startX), Math.abs(arg0.getY() - startY));
				g.setColor(Color.GREEN);
				g.drawRect(startX < arg0.getX() ? startX : arg0.getX(), startY < arg0.getY() ? startY : arg0.getY(), Math.abs(arg0.getX() - startX), Math.abs(arg0.getY() - startY));
				break;
		}
		bf.show();
		
	}

	public void run() {
		while(true){
			if(mouseDown){
				g.setColor(Color.BLUE);
				//g.drawRect(startX, startY, MouseInfo.getPointerInfo().getLocation().x - this.getX(), MouseInfo.getPointerInfo().getLocation().y - this.getY());
				bf.show();
			}
			try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(rectButton)){
			currentPartType = partType.RECTANGLE;
		}
		if(e.getSource().equals(circleButton)){
			currentPartType = partType.CIRCLE;
		}
		
	}
	
	void add(Component[] c){
		for(Component comp : c){
			buttonPanel.add(comp);
			if(comp instanceof JButton) {
				((JButton) comp).addActionListener(this);
			}
		}
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getKeyChar()){
		case '1':
			currentPartType = partType.RECTANGLE;
			break;
		case '2':
			currentPartType = partType.CIRCLE;
			break;
		}
		
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
