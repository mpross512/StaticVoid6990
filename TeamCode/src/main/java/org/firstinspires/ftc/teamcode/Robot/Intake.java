package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import static java.lang.Thread.sleep;

public class Intake{

    private DcMotorEx lift, slide, intakeLift, depositor;
    private Servo basket, trapdoor;
    private CRServo intake;
    private Telemetry telemetry;
    public ElapsedTime runtime;

    private IntakePosition intakePosition;
    private SlidePosition slidePosition;

    private int baseDepositorPosition;
    public int baseSlidePosition;
    private static final double COUNTS_PER_REVOLUTION = 1680;

    public Intake (DcMotorEx lift, DcMotorEx slide, DcMotorEx intakeLift, DcMotorEx depositor, Servo basket, CRServo intake, Servo trapdoor) {
        this.lift = lift;
        this.slide = slide;
        this.intakeLift = intakeLift;
        this.depositor = depositor;
        this.basket = basket;
        this.trapdoor = trapdoor;
        runtime = new ElapsedTime();
        if(intakeLift != null) {
            intakeLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intakeLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if(basket != null) {
            basket.setDirection(Servo.Direction.FORWARD);
            basket.setPosition(1);
        }
        this.intake = intake;
        if(slide != null) {
            slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            baseSlidePosition = slide.getCurrentPosition();
        }
        intakePosition = IntakePosition.UP;
        slidePosition = SlidePosition.IN;
        baseDepositorPosition = depositor.getCurrentPosition();
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

    public int getDepositorPosition() { return depositor.getCurrentPosition(); }

    public void setSlidePosition(SlidePosition position) {
        this.slidePosition = position;
    }

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
        while(lift.isBusy() && runtime.seconds() < 5) {
            telemetry.addData("Status", "Lifting");
            telemetry.addData("Desired Position", ticks);
            telemetry.addData("Current Position", lift.getCurrentPosition());
            telemetry.update();
        }
        lift.setPower(0);

    }

    public void intakeBasket(boolean transfer /*Refers to transfer of minerals to depositing bucker*/) {
        /*if (rightBumper) { //down
            depositor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            depositor.setTargetPosition(330);

            depositor.setPower(.7);
            //while (depositor.isBusy()) {
            telemetry.addData("Motor Position", depositor.getCurrentPosition());
            telemetry.addData("Is Busy", depositor.isBusy());
            telemetry.update();
            //}
            //depositor.setPower(0);
        }
        else if (leftBumper) { //halfway
            depositor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            depositor.setTargetPosition(150);

            depositor.setPower(.1);
            telemetry.addData("Motor Position", depositor.getCurrentPosition());
            telemetry.addData("Is Busy", depositor.isBusy());
            telemetry.update();
        }
        else {
            depositor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            depositor.setPower(0);
        }*/


        //depositor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if(depositor.getMode() != DcMotor.RunMode.RUN_TO_POSITION)
            depositor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (transfer) {
            depositor.setTargetPosition(baseDepositorPosition);
            depositor.setPower(.5);
        }

        else {
            depositor.setTargetPosition(baseDepositorPosition + 450);
            depositor.setPower(.15);
        }

        /*switch(intakePosition) {
            case UP:
                depositor.setTargetPosition(baseDepositorPosition);
                depositor.setPower(.5);
                break;
            case HORIZONTAL:
                depositor.setTargetPosition(baseDepositorPosition - 150);
                depositor.setPower(.15);
                break;
            case DOWN:
            default:
                depositor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                depositor.setPower(0);
                break;*/
    }

    public void intakeBasket(double power) {
        if(depositor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            depositor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        depositor.setPower(.5 * power);
    }

    public void lockIntake() {
        intakeLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeLift.setPower(0);
    }

    public void outtake(double power) {
        intake.setPower(-power);
    }

    public void intake(double power) {
        intake.setPower(power);
    }

    public boolean moveSlide(boolean full) {
        //slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (full)
            slide.setTargetPosition(baseSlidePosition - 2400);
        else
            slide.setTargetPosition(baseSlidePosition - 1440);
        slide.setPower(.5);

        return true; //method finished
    }

    public double moveSlide(double power) {
        if(slide.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slide.setPower(power);
        return slide.getCurrentPosition();
    }

    public void moveIntake(double power) {
        if (power > 0)
            controlBasket(0);
        else if (power < 0)
            controlBasket(1);
        intakeLift.setPower(.5 * power);
    }

    public void controlBasket(float servo) {
        basket.setPosition(servo);
    }

    public void toggleTrapDoor() {
        trapdoor.setPosition(1 - trapdoor.getPosition());
    }

}
