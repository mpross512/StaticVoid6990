package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;


@TeleOp(name = "TeleOp", group = "TeleOp")
public class DerpTeleOp extends OpMode {

    private boolean isSurprising, eightDirectional;
    private PIDControl driveTrainPID;
    private double targetXPower, targetYPower, averagePower, targetRotatePower;
    private DcMotorEx rearLeft, rearRight, frontLeft, frontRight;
    private boolean prevLeftBumper, prevRightBumper;

    @Override
    public void init() {
        driveTrainPID = new PIDControl(0,0,0);
        targetXPower = 0;
        targetYPower = 0;
        averagePower = 0;

        rearLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rearRight = hardwareMap.get(DcMotorEx.class, "backRight");
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        isSurprising = false;
        eightDirectional = false;
        prevLeftBumper = false;
        prevRightBumper = false;
    }

    @Override
    public void loop() {
        //resetMotors();
        sendTelemetry();
        fourDirectionalMovement();
        checkForSurprise();

        prevRightBumper = gamepad1.right_bumper;
        prevLeftBumper = gamepad1.left_bumper;
    }


    /*
     * Moves the Robot in only the four cardinal directions, depending on which controller axis is being pushed the furthest.
     * Goes forward, backward, left, or right.
     */
    public void fourDirectionalMovement() {
        targetXPower = gamepad1.left_stick_x;
        targetYPower = -gamepad1.left_stick_y;

        if(targetXPower != 0 || targetYPower != 0) {
            if (Math.abs(targetXPower) > Math.abs(targetYPower)) {
                rearRight.setPower(-targetXPower);
                rearLeft.setPower(-targetXPower);
                frontRight.setPower(targetXPower);
                frontLeft.setPower(targetXPower);
            } else {
                frontLeft.setPower(targetYPower);
                frontRight.setPower(-targetYPower);
                rearLeft.setPower(targetYPower);
                rearRight.setPower(-targetYPower);
            }
        } else {
            targetRotatePower = gamepad1.right_stick_x;
            rearRight.setPower(targetRotatePower);
            frontRight.setPower(targetRotatePower);
            rearLeft.setPower(targetRotatePower);
            frontLeft.setPower(targetRotatePower);
        }
    }


    /*
     * Moves the Robot in all eight direction.
     * Left stick controls forward/backward movement, Right Stick Controls lateral movement
     */
    //Untested
    public void eightDirectionalMovement() {
        targetYPower = -gamepad1.left_stick_y;
        targetXPower = gamepad1.left_stick_x;
        averagePower = (targetXPower + targetYPower)/2f;

        //Set the Wheels Diagonal to each other to the same power value
        rearLeft.setPower(averagePower);
        frontRight.setPower(averagePower);

        rearRight.setPower(-averagePower);
        frontLeft.setPower(-averagePower);
    }

    public void rotate() {

    }


    public void checkForSurprise() {
        if(gamepad1.start && gamepad1.left_stick_button && !isSurprising) {
            FtcRobotControllerActivity.surprise.start();
            isSurprising = true;
        } else if(gamepad1.back && gamepad1.left_stick_button && isSurprising) {
            FtcRobotControllerActivity.surprise.stop();
            isSurprising = false;
        }
    }

    public void resetMotors() {
        rearLeft.setPower(0);
        rearRight.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
    }

    public void sendTelemetry() {
        telemetry.addData("Eight Directional Movement" , eightDirectional);
        telemetry.addData("Gamepad1 Left Stick X", gamepad1.left_stick_x);
        telemetry.addData("Gamepad1 Left Stick Y", gamepad1.left_stick_y);
        telemetry.addData("Front Motors", "Left: (%.2f) | Right: (%.2f)", frontLeft.getPower(), frontRight.getPower());
        telemetry.addData("Rear Motors", "Left: (%.2f) | Right: (%.2f)", rearLeft.getPower(), rearRight.getPower());
        telemetry.update();
    }
}
