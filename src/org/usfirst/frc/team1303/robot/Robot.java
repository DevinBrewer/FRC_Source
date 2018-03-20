/**
 * 		File: 		Robot.java
 * 		Created:	January 10, 2018
 * 		Author:		Devin Brewer & Michael P.
 * 		Team:		Wyohazard 1303
 * 
 * 		Notes:
 * 					1. The drive motors are connected to Spark motor drivers on the CAN bus
 * 					2. It is assumed that the robot will be driven in a tank-drive
 * 					   configuration, with the left and right joysticks controlling
 *                     the left and right drive motors, respectively.
 */

package org.usfirst.frc.team1303.robot;

// FIRST imports
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
	// Init. physical peripherals
	private DifferentialDrive chassisDrive;
	
	// Create the two controllers
	private Joystick joystickOne;
	private Joystick joystickTwo;
	
	// Create all the mechanism instance variables
	private Spark leftDrive;
	private Spark rightDrive;
	private Relay mechWhip;
	private Spark mechArm;
	private Spark mechClaw;
	private Spark mechWinch;
	
	// Create an encoder
	private AnalogInput armEncoder;
	/*Min = 1200; Max = 2100;*/

	/*---- ROBOT INITALIZER----*/
	@Override
	public void robotInit() {
		// Define physical peripherals
		joystickOne = new Joystick(0);	// Driver controller
		joystickTwo = new Joystick(1);	// Mechanism controller
		leftDrive 	= new Spark(0);	// Spark 0 controls the left set of wheels
		rightDrive 	= new Spark(1);	// Spark 1 controls the right set of wheels
		mechWhip 	= new Relay(2);	// Spark 2 controls the releasing of the whip *CHANGE TO WHATEVER CONTROLS THE WHIP*
		mechArm		= new Spark(3);	// Spark 3 controls the movement of the claw
		mechWinch 	= new Spark(4);	// Spark 4 controls the pulling of the winch in one direction
		mechClaw 	= new Spark(5);	// Spark 5 Controls the movement of the claw
		
		// Initialize the chassis drive
		chassisDrive = new DifferentialDrive(leftDrive, rightDrive);
		
		// Create a camera server
		CameraServer.getInstance().startAutomaticCapture();
		
		// Setup the encoder
		armEncoder = new AnalogInput(0);
		armEncoder.setGlobalSampleRate(62500);
	}

	/*---- TELEOP FUNCTIONS ----*/
	@Override
	public void teleopPeriodic() {
		//---CHASSIS DRIVE---//
		// Set the values of chassis drive based on the left and right joystick on controller one
		chassisDrive.tankDrive(joystickOne.getRawAxis(1) * -1.0, joystickOne.getRawAxis(5) * -1.0);
		
		//---WHIP CONTROL---//
		// If on controller two both 'SELECT'(6) and 'START'(7) are pressed release the whip
		if (joystickTwo.getRawButton(1) && joystickTwo.getRawButton(2)) {
			// Release the whip
			mechWhip.set(Relay.Value.kOn);
		} else {
			mechWhip.set(Relay.Value.kOff);
		}
		
		//---CLAW CONTROL---//
		// Controller the claw with button 'LeftBumper'(4) and 'RightBumber'(5)
		if (joystickTwo.getRawButton(4)) {
			mechClaw.set(-0.5);
		} else if (joystickTwo.getRawButton(5)) {
			mechClaw.set(0.5);
		}
		
		//---ARM CONTROL---//
		// Get the values from the encoder and use it to control the arm
		int raw = armEncoder.getValue();
		if (raw > 1200 && joystickTwo.getRawAxis(1) < 0) {
			// Allow the arm to move down
			mechArm.set(joystickTwo.getRawAxis(1) * 0.5);
		} else if (raw < 2000 && joystickTwo.getRawAxis(1) > 0 ) {
			mechArm.set(joystickTwo.getRawAxis(1) * 0.5);
		}
		
		//---WINCH CONTROL---//
		// If on controller one both 'SELECT'(6) and 'START'(7) are pressed winch the robot up
		if (joystickOne.getRawButton(1) && joystickOne.getRawButton(2)){ 
			mechWinch.set(-0.5);
		} else { 
			mechWinch.set(0);
		}
	}
	
	/*---- AUTONOMOUS FUNCITONS ----*/
	private DriverStation.Alliance color;	// Holds the alliance color
	private int station;	// Holds the station
	private Timer timer;
	
	
	@Override
	public void autonomousInit() {
		// Get and set the team COLOR
		color = DriverStation.getInstance().getAlliance();
		
		// Get and set the STATION number
		station = DriverStation.getInstance().getLocation();
		
		// Setup the TIMER
		timer.reset();
		timer.start();
	}
	
	@Override
	public void autonomousPeriodic() {
		
		// For now we will do a simple drive from position
		/*---- FAILSAFE ----*/
		if (station == 1 || station == 3) {
			// Drive forward for delay
			if (timer.get() < 2.0) {
				chassisDrive.tankDrive(-1, -1);
			} else {
				// Set the motor power back to 0
				chassisDrive.tankDrive(0, 0);
				timer.stop();
			}
		} else if (station == 2) {
			if (timer.get() < 1.0) {
				// Rotate Left/Right
				chassisDrive.tankDrive(0.25, -0.25);
			} else if (timer.get() < 3.0) {
				// Drive forward for 2 seconds
				chassisDrive.tankDrive(-1, -1);
			} else if (timer.get() < 4.0) {
				// Rotate Left/Right
				chassisDrive.tankDrive(-0.25, 0.25);
			} else if (timer.get() < 6.0) {
				// Drive forward for 2 seconds
				chassisDrive.tankDrive(-1, -1);
			} else {
				// Stop the robot
				chassisDrive.tankDrive(0, 0);
				timer.stop();
			}
		} else {
			// Something went wrong with the station number
			System.out.println("ERROR: INVALID STATION NUMBER " + station);
		}
	}
	
	/**
	 * 		Other available methods:
	 * 		1. autonomousInit()
	 * 		2. autonomousPeriodic()
	 * 		3. teleopInit()
	 * 		4. testPeriodic()
	 */
}
