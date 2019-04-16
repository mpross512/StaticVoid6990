package org.firstinspires.ftc.teamcode.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "MarkerOp", group = "Autonomous")
public class MarkerAutoOp extends AutoOp {

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();
        lowerBot();

        longitudinalDistance(8);
        rotatePreciseDegrees(-80);
        //Locates the gold mineral from one of the three given locations
        prospect();

        //rotateDegrees(90);
        //Move bot based on where the Gold Mineral is, knocks it, and moves to above the rightmost mineral
        //intake.lockIntake();
        switch(cam.getGoldPosition()) {
            case LEFT:
                //longitudinalDistance(12);
                rotatePreciseDegrees(45);
                longitudinalDistance(27);
                longitudinalDistance(-3);
                rotatePhoneMount(.48);
                strafe(8, .2, "left");
                rotatePreciseDegrees(-90);
                longitudinalDistance(-24);
                rotatePreciseDegrees(10);
                strafe(20, .2, "right");
                dispenseMarker();
                //strafe(4, .3, "left");
                longitudinalDistance(30, 0.5f);
                strafe(15, .3, "right");
                longitudinalDistance(36, 0.5f);
                park();
                break;
            case RIGHT:
                rotatePreciseDegrees(130);
                longitudinalDistance(24);
                longitudinalDistance(-13);
                rotatePhoneMount(.48);
                rotatePreciseDegrees(50);
                longitudinalDistance(-39);
                rotatePreciseDegrees(140);
                strafe(16, .3, "right");
                longitudinalDistance(-40, 0.9f);
                dispenseMarker();
                rotatePreciseDegrees(3);
                strafe(18,.3, "right");
                strafe(2, .3, "left");
                longitudinalDistance(36, 0.9f);
                //strafe(8,.3, "right");
                longitudinalDistance(20, 0.5f);
                park();
                break;
            case CENTER:
            case NULL:
                rotatePreciseDegrees(-85);
                longitudinalDistance(-40);
                rotatePhoneMount(.48);
                dispenseMarker();
                rotatePreciseDegrees(45);
                strafe(20, .3, "right");
                longitudinalDistance(45, 0.9f);
                strafe(10, .3, "right");
                longitudinalDistance(25, 0.9f);
                park();
                break;

        }
    }
}
