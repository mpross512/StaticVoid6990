package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import static java.lang.Thread.sleep;

public class Intake{

    private DcMotorEx lift, depositorSlide, intakeSlide, depositor, intakeLift;
    private Servo markerDepositor, trapdoor, phoneMount;
    private CRServo intake;
    private Telemetry telemetry;
    private ElapsedTime runtime;

    private IntakePosition intakePosition;
    private SlidePosition slidePosition;
    private DigitalChannel digitalTouch;

    private int baseDepositorPosition, baseScorePosition, baseIntakeBasketPosition;
    public int baseDepositorSlidePosition, baseIntakeSlidePosition;

    public Intake (DcMotorEx lift, DigitalChannel digitalTouch, DcMotorEx depositorSlide, DcMotorEx intakeSlide, DcMotorEx intakeLift, Servo markerDepositor, CRServo intake, Servo trapdoor, Servo phoneMount) {
        this.lift = lift;
        this.digitalTouch = digitalTouch;
        this.digitalTouch.setMode(DigitalChannel.Mode.INPUT);
        this.depositorSlide = depositorSlide;
        this.intakeSlide = intakeSlide;
        this.intakeLift = intakeLift;
        this.markerDepositor = markerDepositor;
        this.trapdoor = trapdoor;
        this.phoneMount = phoneMount;
        runtime = new ElapsedTime();
        if(markerDepositor != null) {
            markerDepositor.setDirection(Servo.Direction.FORWARD);
            markerDepositor.setPosition(0);
        }
        this.intake = intake;
        if(depositorSlide != null) {
            //depositorSlide.setDirection(DcMotor.Direction.REVERSE);
            depositorSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            depositorSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            baseDepositorSlidePosition = depositorSlide.getCurrentPosition();
        }
        if(intakeSlide != null) {
            intakeSlide.setDirection(DcMotor.Direction.REVERSE);
            intakeSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            baseIntakeSlidePosition = intakeSlide.getCurrentPosition();
        }
        intakeSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        phoneMount.setPosition(.48);
        intakePosition = IntakePosition.UP;
        slidePosition = SlidePosition.IN;
        baseDepositorPosition = intakeLift.getCurrentPosition();
    }

    public enum IntakePosition {
        UP,
        HORIZONTAL,
        DOWN
    }

    public enum SlidePosition {
        IN,
        OUT
    }

    public IntakePosition getIntakePosition() {
        return intakePosition;
    }

    public void setIntakePosition (IntakePosition position) {
        intakePosition = position;
    }

    public SlidePosition getSlidePosition() {
        return slidePosition;
    }

    public void setSlidePosition(SlidePosition position) {
        this.slidePosition = position;
    }

    public int getDepositorSlideEncoderPosition() { return depositorSlide.getCurrentPosition(); }

    public int getIntakeSlideEncoderPosition() { return intakeSlide.getCurrentPosition(); }

    public void setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public double lift(double power) {
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setPower(power);
        return lift.getCurrentPosition();
    }

    public void resetEncoders() {
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void liftPosition(double ticks) {
        resetEncoders();
        lift.setTargetPosition((int) (ticks));
        lift.setPower(1);
        runtime.reset();
        while(lift.isBusy() && runtime.seconds() < 3.0) {
            telemetry.addData("Status", "Lifting");
            telemetry.addData("Desired Position", ticks);
            telemetry.addData("Current Position", lift.getCurrentPosition());
            telemetry.update();
        }
        lift.setPower(0);

    }

    public void intakeBasket(boolean transfer /*Refers to transfer of minerals to depositing bucker*/) {
        if(intakeLift.getMode() != DcMotor.RunMode.RUN_TO_POSITION)
            intakeLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (transfer) {
            intakeLift.setTargetPosition(baseDepositorPosition);
            intakeLift.setPower(.5);
        }

        else {
            intakeLift.setTargetPosition(baseDepositorPosition + 450);
            intakeLift.setPower(.15);
        }

        intakeLift.setPower(0);
    }

    public double intakeBasket(double power) {
        if(intakeLift.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            intakeLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        /*if(power < 0 && intakeLift.getCurrentPosition() < baseDepositorPosition)
            intakeLift.setPower(0);
        else if(power > 0 && intakeLift.getCurrentPosition() > baseDepositorPosition + 200)
            intakeLift.setPower(0);
        else*/
            intakeLift.setPower(.80 * power);
        return intakeLift.getCurrentPosition();
    }

    public void lockIntake() {
        intakeLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeLift.setPower(0);
    }

    public void outtake(double power) {
        intake.setPower(-power);
    }

    public void intake(double power) {
        intake.setPower(power);
    }

    public String moveDepositorSlide(String position) {
        int finalDepositorSlidePosition = baseDepositorSlidePosition - 4650;
        /*boolean upperLimitReached = depositorSlide.getCurrentPosition() >= finalDepositorSlidePosition;
        boolean lowerLimitReached = depositorSlide.getCurrentPosition() >= baseDepositorSlidePosition - 250;*/

        //if ((position.equals("up") || position.equals("down")) && depositorSlide.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
        //depositorSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //if(Math.abs(depositorSlide.getCurrentPosition() - baseDepositorPosition) <= 250)
        //    setTrapDoorPosition(.25);

        if ((position.equals("neutral")) && depositorSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
            depositorSlide.setPower(0);
            return "No power" + " Digital Touch Sensor: " + digitalTouch.getState();
        } else if (position.equals("encoderDown") /*&& !digitalTouch.getState()*/) {
            trapdoor.setPosition(0);
            depositorSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            depositorSlide.setTargetPosition(baseDepositorSlidePosition - 250);
            depositorSlide.setPower(-1);
            return "Moving Down w/ Encoders";
        } else if (position.equals("encoderUp")) {
            depositorSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            depositorSlide.setTargetPosition(finalDepositorSlidePosition);
            trapdoor.setPosition(0);
            //depositorSlide.setPower(1);
            return "Moving Up w/ Encoders";
        }
        else if(depositorSlide.getMode() == DcMotor.RunMode.RUN_TO_POSITION
                && Math.abs(depositorSlide.getTargetPosition() - depositorSlide.getCurrentPosition()) >= 150
                && digitalTouch.getState()) {
            trapdoor.setPosition(0);
            depositorSlide.setPower(1);
            return "Moving With Encoders";
        } else if(depositorSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION || !digitalTouch.getState()) {
            depositorSlide.setPower(0);
            depositorSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            depositorSlide.setTargetPosition(depositorSlide.getCurrentPosition());
        }
        if(Math.abs(depositorSlide.getTargetPosition() - depositorSlide.getCurrentPosition()) <= 75) {
            depositorSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            depositorSlide.setPower(0);
            return "No power because of Encoders";
        }

        return depositorSlide.getCurrentPosition() + " Digital Touch Sensor: " + digitalTouch.getState();
    }

    public double moveDepositorSlideFreely(String position) {
        if(position.equals("up") && depositorSlide.getCurrentPosition() < baseDepositorSlidePosition + 4650) {
            trapdoor.setPosition(0);
            depositorSlide.setPower(-1);
        }
        else if(position.equals("down") && digitalTouch.getState()) {
            trapdoor.setPosition(0);
            depositorSlide.setPower(1);
        }
        else
            depositorSlide.setPower(0);
        return depositorSlide.getCurrentPosition();
    }

    public double moveDepositorSlide(double power) {
        if(depositorSlide.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            depositorSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        depositorSlide.setPower(power);
        return depositorSlide.getCurrentPosition();
    }

    /*public void moveDepositor(double power) {
        if (power > 0 && depositor.getCurrentPosition() > baseScorePosition + 400)
            depositor.setPower(.05);
        else
            depositor.setPower(power);

        autoAdjustTrapDoor();
    }*/

    public double moveIntakeSlide(String position) {
        int finalIntakeSlidePosition = baseIntakeSlidePosition - 2700;
        boolean upperLimitReached = intakeSlide.getCurrentPosition() <= finalIntakeSlidePosition;
        boolean lowerLimitReached = intakeSlide.getCurrentPosition() >= baseIntakeSlidePosition - 600;

        if(intakeSlide.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            intakeSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (position.equals("up") && !upperLimitReached) {
            intakeSlide.setPower(-1);
        }
        else if (position.equals("down") && !lowerLimitReached)
            intakeSlide.setPower(1);
        else if (position.equals("neutral") || upperLimitReached || lowerLimitReached) {
            intakeSlide.setPower(0);
        }

        return intakeSlide.getCurrentPosition();
    }

    public double moveIntakeSlideFreely(String position) {
        if(intakeSlide.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            intakeSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if(position.equals("up"))
            intakeSlide.setPower(-.75);
        return intakeSlide.getCurrentPosition();
    }


    /*public void autoAdjustTrapDoor() {
        if(depositor.getCurrentPosition() < baseScorePosition + 200)
            setTrapDoorPosition(1);
    }*/

    public void setTrapDoorPosition(double position) {
        trapdoor.setPosition(position);
    }

    public void markerDepositor(double servo) {
        markerDepositor.setPosition(servo);
    }

    public void toggleTrapDoor(boolean initialize) {
        if (initialize)
            trapdoor.setPosition(.3);
        else if (trapdoor.getPosition() == 0)
            trapdoor.setPosition(.55);
        else
            trapdoor.setPosition(0);
    }

    public void rotatePhoneMount(double position) {
        phoneMount.setPosition(position);
    }

    public void resetEncoderPositions() {
        baseDepositorPosition = intakeLift.getCurrentPosition();
        baseIntakeSlidePosition = intakeSlide.getCurrentPosition();
        baseDepositorSlidePosition = depositorSlide.getCurrentPosition();
    }

}
