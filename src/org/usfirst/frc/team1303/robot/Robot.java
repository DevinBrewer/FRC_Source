/**
 * 		File: 		Robot.java
 * 		Created:	January 10, 2018
 * 		Author:		Michael P.
 * 		Team:		Wyohazard 1303
 * 
 * 		Notes:
 * 					1. The drive motors are connected to talon motor drivers on the CAN bus
 * 					2. It is assumed that the robot will be driven in a tank-drive
 * 					   configuration, with the left and right joysticks controlling
 *                     the left and right drive motors, respectively.
 */

package org.usfirst.frc.team1303.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {
	// Init. physical peripherals
	private DifferentialDrive chassisDrive;
	private Joystick leftStick;
	private Joystick rightStick;

	// Called when first started
	@Override
	public void robotInit() {
		// Define physical peripherals
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		// NOTE: The definition for chassisDrive has been adapted from the
		//       Spark motor controller API and may not function properly.
		chassisDrive = new DifferentialDrive(new Talon(0), new Talon(1));
	}

	// Teleop loop
	@Override
	public void teleopPeriodic() {
		chassisDrive.tankDrive(leftStick.getY(), rightStick.getY());
	}
	
	/**
	 * 		Other available methods:
	 * 		1. autonomousInit()
	 * 		2. autonomousPeriodic()
	 * 		3. teleopInit()
	 * 		4. testPeriodic()
	 */
}
