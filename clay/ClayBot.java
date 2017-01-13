package examplefuncsplayer;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        
        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                runLumberjack();
                break;
            case SCOUT:
            	runScout();
            	break;
            case TANK:
            	runTank();
            	break;
        }
	}
    static Direction dir = randomDirection();
    static int i = 1;
    
    static void runTank() throws GameActionException {
    	while (true) {
    		try {
    			if(rc.canMove(dir)) {
    				rc.move(dir);
    			} else {
    				dir = dir.rotateRightDegrees(90);
    				tryMove(dir);
    			}
    			 Team enemy = rc.getTeam().opponent();
                 RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                 if (robots.length > 0) {
                 	//Broadcast your location for other soldiers to go to
                 	rc.broadcast(2, (int)robots[0].getLocation().x);
                 	rc.broadcast(3, (int)robots[0].getLocation().y);
                 	if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        System.out.println("lol");
                    	rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                 }
                 Clock.yield();
    		} catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
    	}
    }
    static void runScout() throws GameActionException{
    	System.out.println("I'm a scout!");
    	while (true) {
    		try {
    			if(rc.canMove(dir)) {
    				rc.move(dir);
    			} else {
    				dir = dir.rotateRightDegrees(90);
    				tryMove(dir);
    			}
    			 Team enemy = rc.getTeam().opponent();
                 RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                 if (robots.length > 0) {
                 	//Broadcast your location for other soldiers to go to
                 	rc.broadcast(2, (int)robots[0].getLocation().x);
                 	rc.broadcast(3, (int)robots[0].getLocation().y);
                 	if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        System.out.println("lol");
                    	rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                 }
                 Clock.yield();
    		} catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
    	}
    }

    static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {
        	
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                System.out.println(i);
                i++;
                // Generate a random direction
                Direction dir = randomDirection();
                Team enemy = rc.getTeam().opponent();
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                if (robots.length > 0) {
                	//Broadcast your location for other soldiers to go to
                	rc.broadcast(2, (int)robots[0].getLocation().x);
                	rc.broadcast(3, (int)robots[0].getLocation().y);
                }
                // Randomly attempt to build a gardener in this direction
                if (i%200 ==0 && rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                }

                // Move randomly
                tryMove(randomDirection());

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(0,(int)myLocation.x);
                rc.broadcast(1,(int)myLocation.y);
               
                

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

	static void runGardener() throws GameActionException {
		int spawnCounter = 0;
        System.out.println("I'm a gardener!");
        int surroundingTreeCount = 0;
        Team enemy = rc.getTeam().opponent();
        Direction initialEnemyArchonLoc = (rc.getLocation().directionTo(rc.getInitialArchonLocations(enemy)[0]));
        // The code you want your robot to perform every round should be in this loop
        while (true) { 
        	try {
        		/*if (!tryWaterTree() && i%4==1){
        			TreeInfo[] trees = rc.senseNearbyTrees(-1, rc.getTeam());
        			if (trees.length != 0){
        				MapLocation me = rc.getLocation();
        				TreeInfo closest = trees[0];
        				float min = me.distanceTo(closest.getLocation());
        				for (int i = 1; i < trees.length; i++){
        					float distance = me.distanceTo(trees[i].getLocation());
        					if (distance < min){
        						min = distance;
        						closest = trees[i];
        					}
        				}*/
                //Randomly attempt to plant a tree in this direction
        		if (surroundingTreeCount < 5 && rc.canPlantTree(initialEnemyArchonLoc.rotateRightDegrees(60))) {
        			initialEnemyArchonLoc=initialEnemyArchonLoc.rotateRightDegrees(60);
        			rc.plantTree(initialEnemyArchonLoc);
        			surroundingTreeCount++;
        		}

                Direction randomDir = randomDirection();
        			if(!tryWaterTree() && rc.getTeamBullets() > 150 && rc.canPlantTree(randomDir)) {
/*        				rc.plantTree(randomDir);*/
        			}
/*        			if (rc.canMove(dir)) {
        			rc.move(dir);
        		} else {
        			dir = dir.rotateRightDegrees(90);
        			tryMove(dir);
        		}*/
        			

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            		if (rc.getTeamBullets()>300 && rc.isBuildReady()) {
            			if (spawnCounter%8==0 | spawnCounter%8==1) {
            				tryBuildRobot(RobotType.LUMBERJACK, dir, 20, 6);
            			}
            			if (spawnCounter%8==2) {
            				tryBuildRobot(RobotType.TANK, dir, 20, 6);
            			}
            			if (spawnCounter%8== 3 | spawnCounter%8==4|spawnCounter%8==5) {
            				tryBuildRobot(RobotType.SOLDIER, dir, 20, 6);
            			}
            			if (spawnCounter%8==6|spawnCounter%8==7) {
            				tryBuildRobot(RobotType.SCOUT, dir, 20, 6);
            			}
            		}
            	 // Randomly attempt to build a soldier or lumberjack or scout in this direction
               /* if (rc.getTeamBullets() > 101 && rc.canBuildRobot(RobotType.SOLDIER, dir) && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                } else if (rc.canBuildRobot(RobotType.SCOUT, dir) && rc.isBuildReady()) {
                	rc.buildRobot(RobotType.SCOUT, dir);
                }*/
                 RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                 if (robots.length > 0) {
                 	//Broadcast your location for other soldiers to go to
                 	rc.broadcast(2, (int)robots[0].getLocation().x);
                 	rc.broadcast(3, (int)robots[0].getLocation().y);
                 }
                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                
                spawnCounter++;

                


                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
             // Try to move to a preexisting broadcast location, otherwise move randomly
                tryMove(rc.getLocation().directionTo(new MapLocation(rc.readBroadcast(2),rc.readBroadcast(3))));

                // If there are some...
                if (robots.length > 0) {
                	//Broadcast your location for other soldiers to go to
                	rc.broadcast(2, (int)robots[0].getLocation().x);
                	rc.broadcast(3, (int)robots[0].getLocation().y);
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        System.out.println("lol");
                    	rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }
                

                // Try to move to a broadcast location, otherwise move randomly


                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    static void runLumberjack() throws GameActionException {
        System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,enemy);

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        MapLocation myLocation = rc.getLocation();
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        tryMove(toEnemy);
                    } else {
                        // Move Randomly
                        tryMove(randomDirection());
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    
    static boolean tryBuildRobot (RobotType robo, Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
    	// First, try intended direction
    	if (rc.canBuildRobot(robo, dir)) {
    		rc.buildRobot(robo, dir);
    		return true;
    	}
    	
    	//Now try a bunch of similar angles
    	boolean moved = false;
    	int currentCheck = 1;
    	
    	while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canBuildRobot(robo, dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.buildRobot(robo, dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canBuildRobot(robo, dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.buildRobot(robo, dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    static boolean tryPlantTree(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
    	// First, try intended direction
        if (rc.canPlantTree(dir)) {
            rc.plantTree(dir);
            return true;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canPlantTree(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.plantTree(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canPlantTree(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.plantTree(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    	
    }
    
    /**
     * Try to water a nearby tree if it needs watering.
     * 
     */
    static boolean tryWaterTree() throws GameActionException{
    	TreeInfo[] trees = rc.senseNearbyTrees(-1, rc.getTeam());
        for (TreeInfo t : trees){
        	if (rc.canWater(t.getID()) && t.getHealth() < 5){
        		rc.water(t.getID());
        		System.out.println("watered!");
        		return true;
        	}  	
        }
        return false;
    }
    
    /**
     * Attempts to move towards a given location that contains an object,
     * while avoiding small obstacles direction in the path.
     * 
     */
    static boolean tryMoveToLoc(MapLocation loc) throws GameActionException {
    	Direction dir = rc.getLocation().directionTo(loc);
        return tryMove(dir);
    }
}