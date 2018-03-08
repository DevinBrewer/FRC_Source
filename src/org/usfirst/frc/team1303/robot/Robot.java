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

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.AnalogInput;

public class Robot extends IterativeRobot {
	// Init. physical peripherals
	private DifferentialDrive chassisDrive;
	
	// Create the two controllers
	private Joystick joystickOne;
	private Joystick joystickTwo;
	
	// Create all the Sparks
	private Spark leftDrive;
	private Spark rightDrive;
	private Spark mechWhip;
	private Spark mechArm;
	private Spark mechClaw;
	private Spark mechWinch;
	
	// Create an encoder
	private AnalogInput armEncoder = new AnalogInput(0);
	/*Min = 1200; Max = 2100;*/

	// Called when first started
	@Override
	public void robotInit() {
		// Define physical peripherals
		joystickOne = new Joystick(0);
		joystickTwo = new Joystick(1);
		leftDrive 	= new Spark(0);	// Spark 0 controls the left set of wheels
		rightDrive 	= new Spark(1);	// Spark 1 controls the right set of wheels
		mechWhip 	= new Spark(2);	// Spark 2 controls the releasing of the whip
		mechArm		= new Spark(3);	// Spark 3 controls the movement of the claw
		mechWinch 	= new Spark(4);	// Spark 4 controls the pulling of the winch in one direction
		mechClaw 	= new Spark(5);	// Spark 5 Controls the movement of the claw
		
		// Initialize the chassis drive
		chassisDrive = new DifferentialDrive(leftDrive, rightDrive);
		
		// Create a camera server
		CameraServer.getInstance().startAutomaticCapture();
		
		// Setup the encoder
		armEncoder.setGlobalSampleRate(62500);
	}

	// Teleop loop
	@Override
	public void teleopPeriodic() {
		//---CHASSIS DRIVE---//
		// Set the values of chassis drive based on the left and right joystick on controller one
		chassisDrive.tankDrive(joystickOne.getRawAxis(1) * -1.0, joystickOne.getRawAxis(5) * -1.0);
		
		//---WHIP CONTROL---//
		// If on controller two both 'SELECT'(6) and 'START'(7) are pressed release the whip
		if (joystickTwo.getRawButton(1) && joystickTwo.getRawButton(2)) {
			// Release the whip, i.e. power the motor for 1 rotation
			mechWhip.set(1);
		} else {
			mechWhip.set(0);
		}
		
		//---CLAW CONTROL---//
		// Controller the claw with button 'LeftBumper'(4) and 'RightBumber'(5)
		if (joystickTwo.getRawButton(4))
			mechClaw.set(-0.5);
		if (joystickTwo.getRawButton(5))
			mechClaw.set(0.5);
		
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
		if (joystickOne.getRawButton(1) && joystickOne.getRawButton(2))
		{ mechWinch.set(-0.5);}
		else
		{ mechWinch.set(0);}
	}
	
	/**
	 * 		Other available methods:
	 * 		1. autonomousInit()
	 * 		2. autonomousPeriodic()
	 * 		3. teleopInit()
	 * 		4. testPeriodic()
	 */
}
