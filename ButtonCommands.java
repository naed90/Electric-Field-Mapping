import java.awt.Point;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class ButtonCommands {
	Display d;
	ButtonCommands(Display d) {
		this.d = d;
	}

	abstract void execute(int caseNum);
}

class pauseBallMovement extends ButtonCommands {
	initialDisplay newD = (initialDisplay) d;// Done to get access to stuff in initialDisplay and not just Display


	pauseBallMovement(initialDisplay d) {
		super(d); //Useless in this place, cuz we are using an initialDisplay.
		//Only kept here if we need to use in future.
	}


	@Override
	void execute(int caseNum) {
		switch(caseNum%2){
		case 0:
			newD.ballsMoving = true;
			break;
		case 1:
			newD.ballsMoving = false;
			break;
		}
	}
}

class Reset extends ButtonCommands{
	Reset(initialDisplay d) {
		super(d); //Useless in this place, cuz we are using an initialDisplay.
		//Only kept here if we need to use in future.
	}

	@Override
	void execute(int caseNum) {
		d.removeAll();
		d.init();
	}
}


class VoltageOnOff extends ButtonCommands{
	private final initialDisplay newD = (initialDisplay) d;// Done to get access to stuff in initialDisplay and not just Display

	VoltageOnOff(initialDisplay d) {
		super(d); //Useless in this place, cuz we are using an initialDisplay.
		//Only kept here if we need to use in future.
	}

	void execute(int caseNum) {
		switch(caseNum%2){
		case 0:
			newD.drawVoltage = true;
			newD.voltageCalcing = true;
			newD.voltageBarMax.setVisible(true);
			newD.voltageBarMin.setVisible(true);
			break;
		case 1:
			newD.drawVoltage = false;
			newD.voltageCalcing = false;
			newD.voltageBarMax.setVisible(false);
			newD.voltageBarMin.setVisible(false);
			break;
		}
	}
}

class toogleElasticWalls extends ButtonCommands{
	initialDisplay newD = (initialDisplay) d;// Done to get access to stuff in initialDisplay and not just Display
	toogleElasticWalls(initialDisplay d) {
		super(d); //Useless in this place, cuz we are using an initialDisplay.
		//Only kept here if we need to use in future.
	}

	@Override
	void execute(int caseNum) {
		switch(caseNum%2){
		case 0:
			newD.elasticWalls = false;
			break;
		case 1:
			newD.elasticWalls = true;
			break;
		}
	}
}

class addBallCommand extends ButtonCommands {
	private final JFrame callingFrame;
	private final initialDisplay newD = (initialDisplay) d;
	private final addBallDisplay d2;
	private double mass;
	private int X;
	private int Y;
	private double xspeed;
	private double yspeed;
	private double charge;


	addBallCommand(JFrame callingFrame, Display d, addBallDisplay d2) {
		super(d); //Useless in this place, cuz we are using an initialDisplay.
		//Only kept here if we need to use in future.
		this.callingFrame = callingFrame;

		this.d2 = d2;
	}

	@Override
	void execute(int caseNum) {
		//System.out.println("HI");
		this.mass = d2.getMass();
		this.X = d2.getX();
		this.Y = d2.getY();
		this.xspeed = d2.getDX();
		this.yspeed = d2.getDY();
		this.charge = d2.getCharge();
		newD.toAdd.add(new Ball(newD, mass, X, Y, xspeed, yspeed, charge));
		callingFrame.dispatchEvent(new WindowEvent(callingFrame, WindowEvent.WINDOW_CLOSING));
	}
}

class addOrEditCommand extends ButtonCommands{

	private final initialDisplay newD = (initialDisplay) d;
	addOrEditCommand(Display d) {
		super(d);
	}

	@Override
	void execute(int caseNum) {
		switch(caseNum%2){
		case 0:
			newD.addOrEditBoolean = false;
			//Going to edit.
			break;
		case 1:
			newD.addOrEditBoolean = true;
			//Going to add.
			break;
		}
	}
}

class updateBallCommand extends ButtonCommands{
	private final JFrame callingFrame;
	private final initialDisplay newD = (initialDisplay) d;
	private final Ball b;
	private final int ballIndex;

	updateBallCommand(JFrame callingFrame,Display d, Ball b, int ballIndex) {
		super(d);
		this.b = b;
		this.ballIndex = ballIndex;
		this.callingFrame = callingFrame;
	}
	@Override
	void execute(int caseNum) {
		b.setColor(Ball.defualtColor);
		newD.ballarray.set(ballIndex, b);
		if (b.mass == 0) {
			int index = newD.ballarray.indexOf(b);
			newD.ballarray.remove(index);
			newD.remove(newD.chargeDisplay.get(index));
			newD.chargeDisplay.remove(index);
			//newD.repaint();
			//System.out.println("dd");
		}
		callingFrame.dispatchEvent(new WindowEvent(callingFrame, WindowEvent.WINDOW_CLOSING));
	}
}

class SaveToFile extends ButtonCommands {
	private final initialDisplay newD = (initialDisplay) d;

	SaveToFile(initialDisplay d) {
		super(d);
	}

	@Override
	void execute(int caseNum) {
		Scanner s = new Scanner(System.in);
		System.out.print("Please input file name: ");
		Path file = Paths.get("Save Data/" + s.next());
		try (BufferedWriter out = Files.newBufferedWriter(file, Charset.forName("US-ASCII"))) {
			out.write(String.valueOf(newD.ballarray.size()) + '\n');
			for (Ball a : newD.ballarray) {
				out.write(a.toString() + '\n');
			}
			out.write("ballsMoving: " + newD.ballsMoving + '\n');
			out.write("voltageCalcing: " + newD.voltageCalcing + '\n');
			out.write("drawVoltage: " + newD.drawVoltage + '\n');
			out.write("drawBalls: " + newD.drawBalls + '\n');
			out.write("elasticWalls: " + newD.elasticWalls + '\n');
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			newD.messages.addMessage("File not found.", onScreenMessage.CENTER);
		}
	}
}

class LoadFromFile extends ButtonCommands {
	private final initialDisplay newD = (initialDisplay) d;

	LoadFromFile(initialDisplay d) {
		super(d);
	}

	@Override
	void execute(int caseNum) {
		Path file = Paths.get("Save Data/" + newD.presetSelected);
		try (Scanner in = new Scanner(file);) {
			int n = in.nextInt();
			newD.ballarray.clear();
			for (int i = 0; i < n; i++) {
				newD.ballarray.add(new Ball(newD, in.nextDouble(), in.nextInt(), in.nextInt(),
						in.nextDouble(), in.nextDouble(), in.nextDouble()));
			}
			in.next();
			newD.ballsMoving = in.nextBoolean();
			in.next();
			newD.voltageCalcing = in.nextBoolean();
			in.next();
			newD.drawVoltage = in.nextBoolean();
			in.next();
			newD.drawBalls = in.nextBoolean();
			in.next();
			newD.elasticWalls = in.nextBoolean();
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			newD.messages.addMessage("File not found.", onScreenMessage.CENTER);
		}
	}
}
class ballOrWallCommand extends ButtonCommands{
	private final initialDisplay newD = (initialDisplay) d;
	ballOrWallCommand(Display d) {
		super(d);
	}

	@Override
	void execute(int caseNum) {
		switch(caseNum%2){
		case 0:
			newD.ballOrWall = false;
			//Going to wall.
			break;
		case 1:
			newD.ballOrWall = true;
			//Going to ball.
			break;
		}
	}
}
class addInanimateCommand extends ButtonCommands{
	private final initialDisplay d;
	private final Program p;
	private final double charge;
	private final ArrayList<Point> v;
	private final JFrame callingFrame;

	addInanimateCommand(initialDisplay d, JFrame callingFrame, Program host, double charge, ArrayList<Point> vertecies) {
		super(d);
		this.d = d;
		this.p = host;
		this.charge = charge;
		this.v = vertecies;
		this.callingFrame = callingFrame;
	}

	@Override
	void execute(int caseNum) {
		d.inAnimates.add(new inanimateObject(p, d, charge, v));
		callingFrame.dispatchEvent(new WindowEvent(callingFrame, WindowEvent.WINDOW_CLOSING));
	}
}
