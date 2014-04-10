
import processing.core.*;
import processing.xml.*;


import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jbox2d.collision.ShapeType;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.p5.Physics;
import org.jbox2d.collision.*;
import org.jbox2d.common.Vec2;
import org.w3c.dom.css.Rect;

public class MovementSimulator2015 extends PApplet implements ActionListener, ChangeListener {
	
	/**
	 *  This is the main class for the MovementSimulator2015.
	 *  It contains many useful utility functions as well as
	 *  Much of the setup for the application.
	 */
	
	// Changeable values
	int generationSize = 8;
	int minGenerationSize = 1;
	int maxGenerationSize = 64;
	
	int maxTick = 60*10;
	int minTickSize = 10;
	int maxTickSize = 1000;
	
	float targetFPS = 60;
	float minTargetFPS = 1;
	float maxTargetFPS = 512;
	
	int gravity = -10;
	int minGravity = -50;
	int maxGravity = 50;
	
	int wind = 0;
	int minWind = -30;
	int maxWind = 30;
	
	float maxMotorTorque = 10000f;
	float deltaVariance = .95f;
	float startTimingVariance = 60;
	float startSpeedVariance = 3;
	float timeIncrease = 1.0f;
	// TODO make scrolling
	// TODO add floor variance
	// TODO friction variance
	//float frictionAmount = 0.0f;
	// TODO input a buddy
	// TODO make variance go up when improvements aren't made
	
	// Important variables for the program
	private Physics physics;
	private World world;
	
	private Preferences prefs;
	
	private int currentGeneration = 0;
	private int generationBuddyNumber = 0;
	private int currentTick = 0;
	private String parent1 = "";
	private String parent2 = "";

	private float score1 = -1000;
	private float score2 = -1000;
	private String nextGen1 = "";
	private String nextGen2 = "";
	
	private String buddySaveLoc = "data/savedBuddies.txt";
	private String optionsSaveLoc = "data/options.txt";
	
	private String examplePerson = "type,topWidth,topHeight/{appendage}{appendage}/walkingAlgorithm";
	private String theFirstBuddy = "0,50,50/{1,0,10|0,20,50|1,0,10|0,20,50}{1,0,10|0,20,50|1,0,10|0,20,50}/[2,2,3,1,2,2,3,1|30][-2,-2,-3,-1,-2,-2,-3,-1|30]";
	
	Buddy currentBuddy;
	
	int loadPosX = 200;
	int loadPosY = 300;
	
	float startPixel = 0;
	
	// GUI Elements
	JFrame controlFrame;
	JPanel controlPanel;
	
	JLabel generationEditLabel = new JLabel("Generation size:");
	JSlider generationSizeEdit;
	JLabel torqueEditLabel = new JLabel("Max motor torque:");
	JSlider torqueEditSlider;
	JLabel tickEditLabel = new JLabel("Ticks per sibling:");
	JSlider tickEditSlider;
	JLabel fpsTargetLabel = new JLabel("Target FPS:");
	JSlider fpsTargetSlider;
	JLabel gravityEditLabel = new JLabel("Force of gravity:");
	JSlider gravityEditSlider;
	JLabel windEditLabel = new JLabel("Force of wind");
	JSlider windEditSlider;
	// TODO replay system
	JLabel controlsList = new JLabel("<HTML>Keyboard controls:<BR>Replay best buddies: NONE</HTML>");
	JButton resetButton = new JButton("reset simulation");
	
	public void setup() {
		loadPrefs();
		// Set the window size
		size(900, 600);
		// Create the physics environment for the program
		physics = new Physics(this, 4000, getHeight());
		physics.setDensity(1.0f);
		world = physics.getWorld();
		
		initializeGUI();
		// set background 
		
		// Locks the framerate.
		frameRate(targetFPS);
		parent1 = parent2 = theFirstBuddy;
		
		try {
			File f = new File(buddySaveLoc);
			if(!f.exists())
				f.createNewFile();
			
			Scanner sc = new Scanner(f);
			if(sc.hasNext())
				parent1 = sc.nextLine();
			if(sc.hasNext())
				parent2 = sc.nextLine();
			if(sc.hasNext())
				currentGeneration = sc.nextInt();
			if(sc.hasNext())
				score1 = sc.nextFloat();
			if(sc.hasNext())
				score2 = sc.nextFloat();
		} catch (FileNotFoundException e) {
			// Well it creates a new file if it doesn't exist sooooo....
			e.printStackTrace();
		} catch (IOException e) {
			// When your computer sucks this happens
			e.printStackTrace();
		}
		
		// Set the first parents to be the input
		nextGen1 = parent1;
		nextGen2 = parent2;
		
			
		
		currentBuddy = loadBuddy(parent1);
		// Base the score on how far away the buddy gets from this point
		startPixel = currentBuddy.parts.get(0).getPosition().x;
		
	}
	int fallTime = 140;
	// Draw is basically used as a new thread with a wile loop for this program
	public void draw() {
		// Draw the background so that it doesn't have the previous frame as the background
		background(255, 255, 255);
		
		// This draws some helpful and informative text on the screen at all times. 
		drawGUI();
		// no
		//this.getGraphics().drawImage(loadImage("/Users/kenjitanaka/Desktop/rainybow.jpg").getImage(), 0, 0, null);
		g.image(loadImage("/Users/kenjitanaka/Desktop/rainybow.jpg"),0,0);
		
		// Scrolling code
		// TODO
		if(currentBuddy.getHead().getPosition().x > 0){
			float delta = currentBuddy.getHead().getPosition().x;
			
			for(Body b : currentBuddy.parts){
				b.setPosition(new Vec2(b.getPosition().x - delta, b.getPosition().y));
			}
		}
			
		if(currentTick >= fallTime){
			int currentStage = currentBuddy.getStage();
			int i = 0;
			
			for(RevoluteJoint j : currentBuddy.joints){
				j.setMotorSpeed(currentBuddy.instructions.speeds.get(currentStage).get(i));
				i++;
			}
		}
		
		currentTick++;
		if(currentTick >= maxTick + fallTime){
			//System.out.println(currentBuddy.toString());
			// Parents of the next generation
			float myScore = (currentBuddy.getDistanceTravelled(startPixel));
			if(myScore > score1 || myScore > score2){
				//System.out.println(currentBuddy.toString());
				if(score1 < score2){
					nextGen1 = currentBuddy.toString();
					score1 = myScore;
				} else {
					nextGen2 = currentBuddy.toString();
					score2 = myScore;
				}
			}
			// When a new generation happens
			generationBuddyNumber++;
			if(generationBuddyNumber == generationSize){
				incrementGeneration();
			}
			// Resetting variables
			currentTick = 0;
			// Deleting the old buddy
			for(Body b : currentBuddy.parts){
				physics.removeBody(b);
			}
			// Creating a new buddy
			currentBuddy = breedBuddy(parent1, parent2);
			startPixel = currentBuddy.parts.get(0).getPosition().x;
			
		}
		
	}
	
	public void initializeGUI(){
		// GUI
		controlFrame = new JFrame("Controls");
		controlPanel = new JPanel();
		controlFrame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width + getWidth()) / 2 + 20, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2 + getLocation().y);
		controlFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		controlPanel
				.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		// Generation Size Sliders
		generationSizeEdit = new JSlider(minGenerationSize, maxGenerationSize, generationSize);
		Hashtable labelTable = new Hashtable();
		labelTable.put(new Integer(generationSize), new JLabel(Integer.toString(generationSize)));
		generationSizeEdit.setLabelTable(labelTable);
		generationSizeEdit.setPaintLabels(true);
		generationSizeEdit.addChangeListener(this);
		// Torque
		torqueEditSlider = new JSlider();
		labelTable = new Hashtable();
		labelTable.put(new Integer((int) maxMotorTorque/200), new JLabel(Integer.toString((int) (maxMotorTorque/200))));
		torqueEditSlider.setLabelTable(labelTable);
		torqueEditSlider.setPaintLabels(true);
		torqueEditSlider.addChangeListener(this);
		// Ticks
		tickEditSlider = new JSlider(minTickSize, maxTickSize, maxTick);
		labelTable = new Hashtable();
		labelTable.put(new Integer(maxTick), new JLabel(Integer.toString(maxTick + fallTime)));
		tickEditSlider.setLabelTable(labelTable);
		tickEditSlider.setPaintLabels(true);
		tickEditSlider.addChangeListener(this);
		// FPS
		fpsTargetSlider = new JSlider((int) minTargetFPS, (int) maxTargetFPS, (int) targetFPS);
		labelTable = new Hashtable();
		labelTable.put(new Integer((int) targetFPS), new JLabel(Integer.toString((int) targetFPS)));
		fpsTargetSlider.setLabelTable(labelTable);
		fpsTargetSlider.setPaintLabels(true);
		fpsTargetSlider.addChangeListener(this);
		// Gravity
		gravityEditSlider = new JSlider(minGravity, maxGravity, gravity);
		labelTable = new Hashtable();
		labelTable.put(new Integer(gravity), new JLabel(Integer.toString(gravity)));
		gravityEditSlider.setLabelTable(labelTable);
		gravityEditSlider.setPaintLabels(true);
		gravityEditSlider.addChangeListener(this);
		// TODO evolution variance
		windEditSlider = new JSlider(minWind, maxWind, wind);
		labelTable = new Hashtable();
		labelTable.put(new Integer(wind), new JLabel(Integer.toString(wind)));
		windEditSlider.setLabelTable(labelTable);
		windEditSlider.setPaintLabels(true);
		windEditSlider.addChangeListener(this);

		// Reset Button
		resetButton.addActionListener(this);
		//resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		controlPanel.add(generationEditLabel);
		controlPanel.add(generationSizeEdit);
		controlPanel.add(torqueEditLabel);
		controlPanel.add(torqueEditSlider);
		controlPanel.add(tickEditLabel);
		controlPanel.add(tickEditSlider);
		controlPanel.add(fpsTargetLabel);
		controlPanel.add(fpsTargetSlider);
		controlPanel.add(gravityEditLabel);
		controlPanel.add(gravityEditSlider);
		controlPanel.add(windEditLabel);
		controlPanel.add(windEditSlider);
		//controlPanel.add(controlsList);
		controlPanel.add(resetButton);
		
		controlFrame.add(controlPanel);
		controlFrame.pack();
		controlFrame.setVisible(true);
	}
	
	public void drawGUI(){
		this.getGraphics().setColor(Color.BLACK);
		this.text(" FPS: " + Math.round(this.frameRate) +
				"\n Current Generation: " + currentGeneration + 
				"\n Sibling #: " + (generationBuddyNumber + 1) + "/" + generationSize + 
				"\n Tick: " + currentTick + "/" + (maxTick + fallTime) + 
				"\n Distance Travelled: " + currentBuddy.getDistanceTravelled(startPixel) +
				/*"\n Velocity: " + currentBuddy.getHead().getLinearVelocity() + "\n " + landing +*/ 
				"\n Max Scores: " + score1 + ", " + score2, 20, 20);
	}
	
	public void incrementGeneration(){
		generationBuddyNumber = 0;
		currentGeneration++;
		
		parent1 = nextGen1;
		parent2 = nextGen2;

		speedVariance *= deltaVariance;
		timingVariance *= deltaVariance;
		maxTick *= timeIncrease;
		// System.out.println(parent1 + "\n" + parent2);
		// Saving the buddies to a string
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("data/savedBuddies.txt"));
			bw.write(parent1 + "\n" + parent2 + "\n" + currentGeneration + "\n" + score1 + "\n" + score2);
			bw.close();
		} catch (IOException e) {
			// An error probably means that java needs reinstallings
			e.printStackTrace();
		}
	
	}
	
	// The main method is important for many java programs!
		static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#ECE9D8", "MovementSimulator2015" });
	}
	
	// This is allows you to load a buddy without specifying it to be not verbose
	public Buddy loadBuddy(String buddy){
		return loadBuddy(buddy, false);
	}
	
	/**
	 *  Load a buddy saved to a string
	 */
	public Buddy loadBuddy(String buddy, boolean verbose){
		return loadBuddy(buddy.substring(0, buddy.lastIndexOf('/')), loadWalkingData(buddy.substring(buddy.lastIndexOf('/') + 1), verbose), verbose);
	}
	
	/**
	 * Load a buddy saved to a string 
	 */
	public Buddy loadBuddy(String buddy, BuddyInstructions instructions, boolean verbose){
		
		int collisionmask = 1;
		int category = 1;
		
		Buddy b = new Buddy();
		b.dna = buddy;
		
		// HEAD
		if(verbose)
			System.out.println("Head data: " + buddy.substring(0, buddy.indexOf('/')));
		
		String torso = buddy.substring(buddy.indexOf(',') + 1, buddy.indexOf('/'));
		int type = Integer.parseInt(buddy.substring(0, buddy.indexOf(',')));
		b.parts.add(loadPart(torso, partType.values()[type], category, 31, verbose));
		
		// APPENDAGES
		if(verbose)
			System.out.println("\nAppendage data: " + buddy.substring(buddy.indexOf('/') + 1));
		
		String appendages = buddy.substring(buddy.indexOf('/') + 1);
		
		ArrayList<String> appendage = new ArrayList<String>();
		int appendageStart = -1;
		for(int i = 0; i < appendages.length(); i ++){
			char c = appendages.charAt(i);
			if(c == '{')
				appendageStart =  i;
			
			if(c == '}')
				appendage.add(appendages.substring(appendageStart, i + 1));
			
		}
		
		for (String s : appendage){
			collisionmask *= 2;
			category *=2;
			loadApendage(s, b, category, collisionmask + 1, verbose);
		}
		
		b.instructions = instructions;
		return b;
	}
	
	private BuddyInstructions loadWalkingData(String program, boolean verbose){
		
		if(verbose)
			System.out.println("\nWalking data: " + program);
		
		ArrayList<Float> timings = new ArrayList<Float>();
		ArrayList<ArrayList<Float>> speeds = new ArrayList<ArrayList<Float>>();
		
		
		while(!program.equals("")){
			
			ArrayList<Float> currentTorques = new ArrayList<Float>();
			
			String currentData = program.substring(0, program.indexOf("]") + 1);
			currentData = currentData.substring(1, currentData.length() - 1);
			
			String torques = currentData.substring(0, currentData.indexOf('|'));
			String timing = currentData.substring(currentData.indexOf('|') + 1);
			
			timings.add(Float.parseFloat(timing));
			
			//System.out.println(torques);
			
			for(int i = 0;; i ++){

				//System.out.println(torques.substring(0, torques.indexOf(',')));
				if(torques.indexOf(',') != -1){
					currentTorques.add(Float.parseFloat(torques.substring(0, torques.indexOf(','))));
					torques = torques.substring(torques.indexOf(',') + 1);
				} else {
					currentTorques.add(Float.parseFloat(torques));
					break;
				}
			}
			
			speeds.add(currentTorques);
			
			program = program.substring(program.indexOf("]") + 1);
		}
		if(verbose){
			for(ArrayList<Float> a : speeds){
				for(Float f : a)
					System.out.print(f + "    ");
				System.out.print("  |  ");
			}
			System.out.println();
			for(Float f : timings)
				System.out.print(f + "     ");
			System.out.println();
		}

		BuddyInstructions bi = new BuddyInstructions(speeds, timings);
		return bi;
	}
	
	private Buddy breedBuddy(String mom, String dad){
		return breedBuddy(mom, dad, false);
	}
	
	float timingVariance = startTimingVariance;
	float speedVariance = startSpeedVariance;
	
	private Buddy breedBuddy(String mom, String dad, boolean verbose){
		
		Random r = new Random();
		
		BuddyInstructions momCode = loadWalkingData(mom.substring(mom.lastIndexOf('/') + 1), verbose);
		BuddyInstructions dadCode = loadWalkingData(dad.substring(dad.lastIndexOf('/') + 1), verbose);
		
		ArrayList<ArrayList<Float>> childSpeeds = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> timings = new ArrayList<Float>();
		
		int childInstructionsLength = (momCode.speeds.size() + dadCode.speeds.size()) / 2;
		
		for(int i = 0; i < childInstructionsLength; i ++){
			// timings
			if(r.nextInt(100) > 50 && momCode.timings.size() > i){
				timings.add(momCode.timings.get(i));
				childSpeeds.add(momCode.speeds.get(i));
			}
			else if(dadCode.timings.size() > i){
				timings.add(dadCode.timings.get(i));
				childSpeeds.add(dadCode.speeds.get(i));
			} else {
				timings.add(momCode.timings.get(i));
				childSpeeds.add(momCode.speeds.get(i));
			}
			
			timings.set(i, (float) (timings.get(i) + r.nextFloat() * speedVariance * Math.pow(-1, (double) r.nextInt())));
			
			for(int j = 0; j < childSpeeds.get(i).size(); j++){
				childSpeeds.get(i).set(j, (float) (childSpeeds.get(i).get(j) + speedVariance * r.nextFloat() * Math.pow(-1, (double) r.nextInt())));
			}
		}
		
		BuddyInstructions childInstructions = new BuddyInstructions(childSpeeds, timings);
		
		return loadBuddy(mom.substring(0, mom.lastIndexOf('/')), childInstructions, verbose);
	
	}
	
	private void loadApendage(String s, Buddy b, int category, int collisionmask, boolean verbose){
		
		if(verbose)
			System.out.println(s);
		
		s = s.substring(1, s.length() - 1);
		
		Body previousPart = b.getHead();
		
		while(s != ""){
			//System.out.println(p);
			String p = "";
			
			if(s.indexOf('|') != -1){
				p = s.substring(0, s.indexOf('|'));
				s = s.substring(s.indexOf('|') + 1);
			} else {
				p = s;
				s = "";
			}
			
			float xPos = physics.getPosition(previousPart).x;
			float yPos = physics.getPosition(previousPart).y;
			
			if(previousPart.getShapeList().getType() == ShapeType.CIRCLE_SHAPE){
				yPos += (((CircleShape) previousPart.getShapeList()).getRadius() * 10);
				xPos -= Float.parseFloat(p.substring(p.indexOf(',') + 1, p.indexOf(',', p.indexOf(',') + 1)))/2;
			} else {
				//System.out.println(previousPart.getShapeList().getType()); 
				float yMax = 0;
				for (Vec2 v : ((PolygonShape) previousPart.getShapeList()).getVertices()){
					if(v.y > yMax)
						yMax = v.y * 10;
				}
				yPos += yMax;
			}
			
			b.addPart(loadPart(p.substring(p.indexOf(',') + 1), partType.values()[(Integer.parseInt(p.substring(0, p.indexOf(','))))],xPos, yPos, category, collisionmask, verbose));
			b.addJoint(physics.createRevoluteJoint(previousPart, b.parts.get(b.parts.size() - 1), physics.getPosition(previousPart).x, yPos), maxMotorTorque);
			previousPart = b.parts.get(b.parts.size() - 1);
		}
	}
	
	private Body loadPart(String part, partType type, int category, int collisionmask, boolean verbose){
		return loadPart(part, type, loadPosX, loadPosY, category, collisionmask, verbose);
	}
	
	private Body loadPart(String part, partType type, float locX, float locY, int category, int collisionmask, boolean verbose){
		if(verbose)
			System.out.println("Loading part: " + part + " (" + type + ") at " + locX + ", " + locY + "  category: " + category + "  collision mask: " + collisionmask);
		
		// RECTangle
		if(type == partType.RECTANGLE){
			
			float width = Float.parseFloat(part.substring(0, part.indexOf(',')));
			float height = Float.parseFloat(part.substring(part.indexOf(',') + 1));
			
			BodyDef bd = new BodyDef();
			bd.position.set((locX - getWidth()/  2 + width/2) / 10, 0 - (locY - (getHeight())/2 + height/2) / 10);
			
			
			PolygonDef pd = new PolygonDef();
			pd.filter.categoryBits = category;
			pd.filter.maskBits = collisionmask;
			pd.setAsBox(width / 20, height / 20);
			pd.density = 1.0f;
			
			Body b = world.createBody(bd);
			
			b.createShape(pd);
			b.setMassFromShapes();
			
			return b;
			//return physics.createRect(locX, locY, locX + width, locY + height);
		}

		// CIRCLE
		if(type == partType.CIRCLE){
			float radius = Float.parseFloat(part.substring(part.indexOf(',') + 1));
			
			CircleDef cd = new CircleDef();
			cd.filter.categoryBits = category;
			cd.filter.maskBits = collisionmask;
			cd.radius = radius / 10;
			cd.density = 1.0f;
			
			BodyDef bd = new BodyDef();
			
			bd.position.set((locX - getWidth()/  2) / 10, 0 - (locY - (getHeight())/2 + radius/2) / 10);
			
			Body b = world.createBody(bd);
			
			b.createShape(cd);
			b.setMassFromShapes();
			return b;
			//return physics.createCircle(locX, locY + radius, radius);
			
		}
		
		// POLYGON
		
		
		return null;
	}
	
	private void savePrefs(){
		File f = new File(optionsSaveLoc);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// Shouldn't happen
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			bf.write("generationSize=" + generationSize + "\nmaxTorque=" + maxMotorTorque + "\nticksPerSibling=" + maxTick + "\ntargetFPS="+ targetFPS + "\ngravity=" + gravity + "\nwind=" + wind);
			bf.close();
		} catch (IOException e) {
			// Won't happen because file is always created
			e.printStackTrace();
		}
		// Generation size
		// Max motor torque
		// Ticks per sibling
		// Target fps
		// Gravity
		// Wind
		
	}
	
	private void loadPrefs(){
		try {
			Scanner sc = new Scanner(new File(optionsSaveLoc));
			String s = sc.nextLine();
			generationSize = Integer.parseInt(s.substring(s.indexOf('=') + 1));
			s = sc.nextLine();
			maxMotorTorque = Float.parseFloat(s.substring(s.indexOf('=') + 1));
			s = sc.nextLine();
			maxTick = Integer.parseInt(s.substring(s.indexOf('=') + 1));
			s = sc.nextLine();
			targetFPS = Float.parseFloat(s.substring(s.indexOf('=') + 1));
			s = sc.nextLine();
			gravity = Integer.parseInt(s.substring(s.indexOf('=') + 1));
			s = sc.nextLine();
			wind = Integer.parseInt(s.substring(s.indexOf('=') + 1));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	enum partType {RECTANGLE, CIRCLE, POLYGON};
	
	/*
	private partType toEnum(String type){
		type = type.toUpperCase();
		if(type.equals("RECTANGLE")){
			return partType.RECTANGLE;
		} else if(type.equals("CIRCLE")){
			return partType.CIRCLE;
		}
		
		return partType.POLYGON;
	}*/
	
	public void keyPressed(){
		switch(key){
		case '1':
			this.frameRate(1000);
			break;
			
		case '2':
			break;
			
		case ' ':
			if(this.frameRateTarget == 1.0f)
				this.frameRate(120);
			else
				this.frameRate(1);
			break;
			
		}
	}
	
	public void reset(){
		System.out.println("resetting...");
		parent1 = theFirstBuddy;
		parent2 = theFirstBuddy;
		currentGeneration = 0;
		score1 = -1000;
		score2 = -1000;
		currentTick = 0;
		// Destroy the buddy 
		for(Body b : currentBuddy.parts){
			physics.removeBody(b);
		}
		// Add a new buddy
		currentBuddy = breedBuddy(parent1, parent2);
		// Empty the file
		File f = new File(buddySaveLoc);
		f.delete();
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mousePressed(){
		// TODO this
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource().equals(resetButton)){
			reset();
		}
		
	}

	public void stateChanged(ChangeEvent arg0) {
		if(arg0.getSource().equals(generationSizeEdit)){
			generationSize = generationSizeEdit.getValue();
			Hashtable h = new Hashtable();
			h.put(new Integer(generationSizeEdit.getValue()), new JLabel(Integer.toString(generationSizeEdit.getValue())));
			generationSizeEdit.setLabelTable(h);
		}
		
		if(arg0.getSource().equals(torqueEditSlider)){
			maxMotorTorque = torqueEditSlider.getValue() * 200;
			Hashtable h = new Hashtable();
			h.put(new Integer((int) maxMotorTorque/200), new JLabel(Integer.toString((int) (maxMotorTorque/200))));
			torqueEditSlider.setLabelTable(h);
		}
		if(arg0.getSource().equals(tickEditSlider)){
			maxTick = tickEditSlider.getValue();
			Hashtable h = new Hashtable();
			h.put(new Integer(tickEditSlider.getValue()), new JLabel(Integer.toString(tickEditSlider.getValue() + fallTime)));
			tickEditSlider.setLabelTable(h);
		}
		if(arg0.getSource().equals(fpsTargetSlider)){
			this.frameRate(fpsTargetSlider.getValue());
			Hashtable h = new Hashtable();
			h.put(new Integer(fpsTargetSlider.getValue()), new JLabel(Integer.toString(fpsTargetSlider.getValue())));
			fpsTargetSlider.setLabelTable(h);
		}
		if(arg0.getSource().equals(gravityEditSlider)){
			gravity = gravityEditSlider.getValue();
			physics.getWorld().setGravity(new Vec2(physics.getWorld().getGravity().x, gravity));
			Hashtable h = new Hashtable();
			h.put(new Integer(gravity), new JLabel(Integer.toString(gravity)));
			gravityEditSlider.setLabelTable(h);
		}
		if(arg0.getSource().equals(windEditSlider)){
			wind = windEditSlider.getValue();
			physics.getWorld().setGravity(new Vec2(wind, physics.getWorld().getGravity().y));
			Hashtable h = new Hashtable();
			h.put(new Integer(wind), new JLabel(Integer.toString(wind)));
			windEditSlider.setLabelTable(h);
		}
		this.savePrefs();
		
	}
}


