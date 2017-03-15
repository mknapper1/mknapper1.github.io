
package org.usfirst.frc.team4409.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.AxisCamera;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String lineAuto = "Line";
	final String leftPegAuto = "Left Peg Auto";
	final String midPegAuto = "Middle Peg Auto";
	final String rightPegAuto = "Right Peg Auto";
	final double wheelradius = 2; // this is the value of the wheel radius for using encoders
									
	final double EncoderTo360 = 0.17578125;
	boolean autoComplete = false;
	String autoSelected;
	SendableChooser<String> chooser;
	RobotDrive myDrive;
	Spark FrontRight, BackLeft;
	TalonSRX BackRight, FrontLeft;
	Talon Winch1, Winch2, Flywheel;
	Victor Spinner;
	Joystick Stick, Gamepad;
	Timer timer;
	DoubleSolenoid Funnel, Hatch;
	Compressor Pump;
	DigitalInput Rangefinder;
	Encoder LeftEnc;
	Thread visionThread;
	ADXRS450_Gyro Gyro;
	Boolean flywheelToggle;
	Boolean flywheelDebounce;
	Boolean winchToggle;
	Boolean winchDebounce;
	Boolean db180;
	Double currentAngle;
	Double targetAngle;
	Boolean dbMoveZ;
	Boolean spinDebounce;
	Boolean spinToggle;
	Boolean dbMoveZ2;
	Boolean tankDrive;
	Boolean hatchTog;
	Boolean hatchdb;
	Boolean turnTog;
	Boolean turnTogdb;
	Double lastTime;
	Double angleToTurn;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser = new SendableChooser<String>();
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("Line Auto", lineAuto);
		chooser.addObject("Left Peg Auto", leftPegAuto);
		chooser.addObject("Middle Peg Auto", midPegAuto);
		chooser.addObject("Right Peg Auto", rightPegAuto);
		SmartDashboard.putData("Options", chooser);
		SmartDashboard.putNumber("Gyroscope Angle", 0);
		SmartDashboard.putNumber("Turn Angle", 0.00);
		FrontRight = new Spark(2);
		BackLeft = new Spark(1);
		BackRight = new TalonSRX(4);
		FrontLeft = new TalonSRX(3);
		myDrive = new RobotDrive(FrontLeft, BackLeft, FrontRight, BackRight);
		Winch1 = new Talon(5);
		Winch2 = new Talon(6);
		Flywheel = new Talon(7);
		Spinner = new Victor(8);
		Gamepad = new Joystick(0);
		Stick = new Joystick(1);
		timer = new Timer();
		Pump = new Compressor(0);
		Pump.setClosedLoopControl(true);
		Funnel = new DoubleSolenoid(0, 1);
		Hatch = new DoubleSolenoid(2, 3);
		LeftEnc = new Encoder(2, 3);
		Rangefinder = new DigitalInput(1);
		Gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
		flywheelToggle = false;
		flywheelDebounce = true;
		winchToggle = false;
		winchDebounce = true;
		db180 = true;
		dbMoveZ = true;
		spinToggle = false;
		spinDebounce = true;
		dbMoveZ2 = true;
		tankDrive = false;
	
		hatchTog = false;
		hatchdb = true;
		turnTog = true;
		turnTogdb = true;
		lastTime = 0.00;
		angleToTurn = 0.00;
		Gyro.calibrate();
		visionThread = new Thread(() -> {
			// Get the Axis camera from CameraServer
			// origanal Ip address of camera one is: 10.44.9.82
			AxisCamera camera = CameraServer.getInstance().addAxisCamera("10.44.9.11");
			// AxisCamera camera2 = CameraServer.getInstance().addAxisCamera("
			// 10.44.9.11");
			// Set the resolution
			camera.setResolution(640, 480);
			// camera2.setResolution(640, 480);

			// Get a CvSink. This will capture Mats from the camera
			CvSink cvSink = CameraServer.getInstance().getVideo();
			// Setup a CvSource. This will send images back to the Dashboard
			CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 640, 480);

			// Mats are very memory expensive. Lets reuse this Mat.
			Mat mat = new Mat();

			// This cannot be 'true'. The program will never exit if it is. This
			// lets the robot stop this thread when restarting robot code or
			// deploying.
			while (!Thread.interrupted()) {
				// Tell the CvSink to grab a frame from the camera and put it
				// in the source mat. If there is an error notify the output.
				if (cvSink.grabFrame(mat) == 0) {
					// Send the output the error.
					outputStream.notifyError(cvSink.getError());
					// skip the rest of the current iteration
					continue;
				}
				// Put a rectangle on the image
				// Imgproc.rectangle(mat, new Point(100, 100), new Point(400,
				// 400), new Scalar(255, 255, 255), 5);
				// Give the output stream a new image to display
				outputStream.putFrame(mat);
			}
		});
		visionThread.setDaemon(true);
		visionThread.start();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		DriverStation.reportWarning("Auto selected: " + autoSelected, true);
		autoComplete = true;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void updateDash() {
		SmartDashboard.putNumber("Gyroscope Angle", Math.floor(Gyro.getAngle() * 100) / 100);
		tankDrive = SmartDashboard.getBoolean("Tank Drive", false);
		SmartDashboard.putNumber("Left Encoder", LeftEnc.get());
		SmartDashboard.putNumber("Timer", Math.floor(timer.get() * 100) / 100);
		angleToTurn = SmartDashboard.getNumber("Turn Angle", 0.00);
		// .autoComplete./autoComplete =
		// SmartDashboard.getBoolean("AutoCompleteStatus", false);

	}

	public boolean turnAngle(Double angle) {
		Gyro.reset();
		angle *= 0.5;
		if (angle >= 0) {
			while (Gyro.getAngle() < angle) {
				if (Gamepad.getRawButton(2)) {
					break;
				}
				myDrive.tankDrive(-0.75, 0.75);
			}
			myDrive.tankDrive(0.1, -0.1);
		} else {
			while (Gyro.getAngle() > angle) {
				if (Gamepad.getRawButton(2)) {
					break;
				}
				myDrive.tankDrive(0.75, -0.75);
			}
			myDrive.tankDrive(-0.1, 0.1);
		}
		return true;
	}

	public boolean moveZ(long dur, Boolean forward) {
		// System.out.println("run");
		Timer time = new Timer();
		time.start();
		while (time.get() < dur) {
			if (forward) {
				myDrive.tankDrive(0.75, 0.75);
			} else {
				myDrive.tankDrive(-0.75, -0.75);
			}
		}
		myDrive.arcadeDrive(0, 0);
		return true;
	}

	public void driveForward(double seconds) {
		timer.reset();
		myDrive.tankDrive(0, 0);
		while (timer.get() < seconds) {
			DriverStation.reportWarning(Double.toString(timer.get()), true);
			myDrive.tankDrive(-0.75, -0.75);
		}
		myDrive.tankDrive(0, 0);
	}

	public void driveBackwards(double seconds) {
		timer.reset();
		myDrive.tankDrive(0, 0);
		while (timer.get() < seconds) {
			DriverStation.reportWarning(Double.toString(timer.get()), true);
			myDrive.tankDrive(0.75, 0.75);
		}
		myDrive.tankDrive(0, 0);
	}

	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case lineAuto:
			updateDash();
			if (timer.get() >= 0 && timer.get() < 1) {
				myDrive.tankDrive(-0.5, -0.5);
			} else if (timer.get() >= 7 && timer.get() < 8) {
				myDrive.tankDrive(0, 0);
			} else if (timer.get() >= 15) {
				break;
			}
		case leftPegAuto:
			updateDash();

			break;
		case rightPegAuto:
			updateDash();

			break;
		case midPegAuto:
			updateDash();

			break;
		case defaultAuto:
		default:
			updateDash();
			EncoderDrive(93.25, false);
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		timer.stop();
		updateDash();

		// debuging things(we really should make a dashboard)to
		if (Gamepad.getRawButton(11) && turnTogdb) {
			turnTogdb = false;
			turnTog = turnTog == true ? false : true;
		}
		if (Gamepad.getRawButton(11) == false) {
			turnTogdb = true;
		}
		// DriverStation.reportWarning(Double.toString(LeftEnc.get() *
		// 0.17578125),true);//that fancy number worked last year
		if (Gamepad.getRawButton(3) == true && db180 == true && turnTog) {
			db180 = false;
			db180 = turnAngle(angleToTurn);
		}
		if (Gamepad.getRawButton(5) == true && db180 == true && turnTog) {
			db180 = false;
			db180 = turnAngle(90.00);
		}
		if (Gamepad.getRawButton(4) == true && db180 == true && turnTog) {
			db180 = false;
			db180 = turnAngle(-90.00);
		}
		if (Gamepad.getRawButton(1) == true) {
			if (tankDrive) {
				myDrive.tankDrive(Gamepad.getRawAxis(1) * 0.8, Stick.getRawAxis(1) * 0.8);
			} else {
				myDrive.arcadeDrive(Gamepad.getRawAxis(1) * 0.8, Gamepad.getRawAxis(0) * 0.8);
			}
		} else {
			if (tankDrive) {
				myDrive.tankDrive(Gamepad.getRawAxis(1), Stick.getRawAxis(1));
			} else {
				myDrive.arcadeDrive(Gamepad.getRawAxis(1), Gamepad.getRawAxis(0));
			}
		}
		// DriverStation.reportWarning(Double.toString(Gyro.getAngle()),true);

		// only drive winch forward
		if (Stick.getRawButton(2) == true && winchDebounce == true) {
			winchToggle = winchToggle == true ? false : true;
			winchDebounce = false;
		}
		if (winchToggle == true) {
			Winch1.set((-Stick.getRawAxis(2) + 3) / 4);
			Winch2.set((-Stick.getRawAxis(2) + 3) / 4);
		} else {
			Winch1.set(0);
			Winch2.set(0);
		}
		if (Stick.getRawButton(2) == false) {
			winchDebounce = true;
		}
		if (Stick.getRawButton(5) == true && spinDebounce == true) {
			spinToggle = spinToggle == true ? false : true;
			spinDebounce = false;
		}
		if (spinToggle == true) {
			Spinner.set(1.0);
		} else {
			Spinner.set(0.0);
		}
		if (Stick.getRawButton(5) == false) {
			spinDebounce = true;
		}

		/*
		 * if (Stick.getRawAxis(1) <= 0) { Winch1.set(Stick.getRawAxis(1));
		 * Winch2.set(Stick.getRawAxis(1)); } else { Winch1.set(0.0);
		 * Winch2.set(0.0); }
		 */
		if (Stick.getRawButton(1) == true && hatchdb == true) {
			hatchTog = hatchTog == true ? false : true;
			hatchdb = false;
		}
		if (hatchTog == true) {
			Hatch.set(DoubleSolenoid.Value.kReverse);
		} else {
			Hatch.set(DoubleSolenoid.Value.kForward);
		}
		if (Stick.getRawButton(1) == false) {
			hatchdb = true;
		}
		/*
		 * if (Stick.getRawButton(1) == true) {
		 * Hatch.set(DoubleSolenoid.Value.kReverse); } else {
		 * Hatch.set(DoubleSolenoid.Value.kForward); }
		 */
		if (Stick.getRawButton(3) == true) {
			Funnel.set(DoubleSolenoid.Value.kReverse);
		} else {
			Funnel.set(DoubleSolenoid.Value.kForward);
		}
		if (Stick.getRawButton(4) == true && flywheelDebounce == true) {
			flywheelToggle = flywheelToggle == true ? false : true;
			flywheelDebounce = false;
		}
		if (flywheelToggle == true) {
			Flywheel.set((-Stick.getRawAxis(2) + 3) / 4);

		} else {
			Flywheel.set(0);
		}
		if (Stick.getRawButton(4) == false) {
			flywheelDebounce = true;
		}

		// SmartDashboard.putNumber("Ultrasonic",Rangefinder.getRangeInches());
	}


	public void EncoderDrive(double Distance, boolean Foward) {
		if (autoComplete == false) {

			LeftEnc.reset();

			// figure out the distance to drive
			double OneRotation = Math.PI * 2 * wheelradius;
			double NumberRotations = Distance / OneRotation;

			SmartDashboard.putNumber("rotationstarget", NumberRotations);
			while ((LeftEnc.get() * EncoderTo360) < NumberRotations) {
				myDrive.tankDrive(-0.5, -0.52);
				updateDash();
			}
			myDrive.tankDrive(0.0, 0.0);
			autoComplete = true;
			
		}
	}

}
