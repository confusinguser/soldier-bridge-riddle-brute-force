import java.util.Arrays;

public class Main {

    private static final int bridgeLen = 20;
    private static int currBridgeNum = -1; // Increases instantly in beginning of main

    public static void main(String[] args) {
        int currentMaxAmountOfStepsBridgeNum = 0;
        int currentMaxAmountOfSteps = 0;
        while (currBridgeNum < (1 << bridgeLen) - 1) { // same as 2^bridgeLen
            currBridgeNum++;
            Cell[] bridge = generateBridge(currBridgeNum); // 1048574
            System.out.println(getBridgeAsString(bridge));
            int steps = 0;
            while (Arrays.stream(bridge).anyMatch(cell -> cell.getDirection() != Direction.NONE)) {
                steps++;
                advanceBridge(bridge);
                System.out.println(getBridgeAsString(bridge));
                if (steps >= 200) break;
            }
            System.out.println("----------- " + currBridgeNum);

            if (currentMaxAmountOfSteps < steps) {
                currentMaxAmountOfStepsBridgeNum = currBridgeNum;
                currentMaxAmountOfSteps = steps;
            }

        }

        currBridgeNum = currentMaxAmountOfStepsBridgeNum;
        Cell[] bridge = generateBridge(currBridgeNum);
        System.out.println(getBridgeAsString(bridge));
        int steps = 0;
        while (Arrays.stream(bridge).anyMatch(cell -> cell.getDirection() != Direction.NONE)) {
            steps++;
            advanceBridge(bridge);
            System.out.println(getBridgeAsString(bridge));
            if (steps >= 300) break;
        }
    }

    /**
     * Just Counts in binary, 0 = left, 1 = right
     */
    public static Cell[] generateBridge(int bridgeNum) {
        Cell[] out = new Cell[bridgeLen];
        for (int i = 0; i < bridgeLen; i++) {
            int reversedI = bridgeLen - 1 - i;
            if ((bridgeNum & (1 << reversedI)) != 0) { // Translation: If the bit at the ith position is 1
                out[i] = new Cell(Direction.RIGHT);
            } else {
                out[i] = new Cell(Direction.LEFT);
            }
        }
        return out;
    }

    public static void advanceBridge(Cell[] bridge) {
        // One pass for all colliding and one for walking (right, left separate passes from diff directions) and then a both cell colliding one for things that happened after walking, think about this configuration: -> -> -> <-, correct result after 1 tick: . -> ♢ ->

        for (int i = 0; i < bridge.length; i++) {
            Cell cell = bridge[i];
            if (cell.isTicked()) continue;

            if (i == 0 && cell.getDirection() == Direction.LEFT) {
                bridge[0].setDirection(Direction.NONE);
                continue;
            }
            if (i == bridge.length - 1 && cell.getDirection() == Direction.RIGHT) {
                bridge[bridge.length - 1].setDirection(Direction.NONE);
                continue;
            }
            if (i == 0 && cell.getDirection() == Direction.BOTH) {
                bridge[0].setDirection(Direction.RIGHT);
                continue;
            }
            if (i == bridge.length - 1 && cell.getDirection() == Direction.BOTH) {
                bridge[bridge.length - 1].setDirection(Direction.LEFT);
                continue;
            }

            if (i != 0 && i != bridge.length - 1 && cell.getDirection() == Direction.BOTH &&
                    (bridge[i - 1].getDirection() == Direction.RIGHT || bridge[i - 1].getDirection() == Direction.BOTH) &&
                    (bridge[i + 1].getDirection() == Direction.LEFT || bridge[i - 1].getDirection() == Direction.BOTH) &&
                    !bridge[i - 1].isTicked() && !bridge[i + 1].isTicked()) { // For configurations like > ♢ <
                bridge[i].setTicked(true);
                bridge[i - 1].setDirection(Direction.LEFT);
                bridge[i - 1].setTicked(true);
                bridge[i + 1].setDirection(Direction.RIGHT);
                bridge[i + 1].setTicked(true);
            }

            if (cell.getDirection() == Direction.RIGHT && bridge[i + 1].getDirection() == Direction.LEFT) {
                bridge[i].setDirection(Direction.LEFT);
                bridge[i + 1].setDirection(Direction.RIGHT);
                bridge[i].setTicked(true);
                bridge[i + 1].setTicked(true);
            }
        }

        for (int i = 0; i < bridge.length; i++) { // Left walking pass
            Cell cell = bridge[i];
            if (cell.isTicked()) continue;

            if (cell.getDirection() == Direction.LEFT) {
                if (bridge[i - 1].getDirection() == Direction.NONE) {
                    bridge[i].setDirection(Direction.NONE);
                    bridge[i - 1].setDirection(Direction.LEFT);
                }
                if (bridge[i - 1].getDirection() == Direction.RIGHT && bridge[i - 1].isTicked()) { // We have to know if it has been ticked THIS time, but we only wanna know the direction from before we started this tick
                    bridge[i].setDirection(Direction.NONE);
                    bridge[i - 1].setDirection(Direction.BOTH);
                }
            } else if (cell.getDirection() == Direction.NONE) {
//                if (bridgeCopy[i - 1].getDirection() == Direction.RIGHT && !bridgeCopy[i - 1].isTicked() &&
//                        bridgeCopy[i + 1].getDirection() == Direction.LEFT && !bridgeCopy[i + 1].isTicked()) bridge[i].setDirection(Direction.BOTH);
                if (i == bridge.length - 1) continue;
                if (bridge[i + 1].getDirection() == Direction.LEFT && !bridge[i + 1].isTicked()) {
                    bridge[i].setDirection(Direction.LEFT);
                    bridge[i].setTicked(true);
                    bridge[i + 1].setDirection(Direction.NONE);
                } else if (bridge[i + 1].getDirection() == Direction.BOTH && !bridge[i + 1].isTicked()) {
                    bridge[i].setDirection(Direction.LEFT);
                    bridge[i].setTicked(true);
                    bridge[i + 1].setDirection(Direction.RIGHT);
                }
            }
        }

        // From bridge.len-1 to 1, so that all rights move in a chain instead of one every tick
        for (int i = bridge.length - 1; i >= 0; i--) { // Right walking pass
            Cell cell = bridge[i];
            if (cell.isTicked()) continue;

            if (cell.getDirection() == Direction.RIGHT) {
                if (bridge[i + 1].getDirection() == Direction.NONE) {
                    bridge[i].setDirection(Direction.NONE);
                    bridge[i + 1].setDirection(Direction.RIGHT);
                }
                if (bridge[i + 1].getDirection() == Direction.LEFT && bridge[i + 1].isTicked()) {
                    bridge[i].setDirection(Direction.NONE);
                    bridge[i + 1].setDirection(Direction.BOTH);
                }
            } else if (cell.getDirection() == Direction.NONE) {
                if (i == 0) continue;
                if (bridge[i - 1].getDirection() == Direction.RIGHT && !bridge[i - 1].isTicked()) {
                    bridge[i].setDirection(Direction.RIGHT);
                    bridge[i].setTicked(true);
                    bridge[i - 1].setDirection(Direction.NONE);
                } else if (bridge[i - 1].getDirection() == Direction.BOTH && !bridge[i - 1].isTicked()) {
                    bridge[i].setDirection(Direction.RIGHT);
                    bridge[i].setTicked(true);
                    bridge[i - 1].setDirection(Direction.LEFT);
                }
            }
        }

//        for (int i = 0; i < bridge.length; i++) { // Both colliding pass
//            Cell cell = bridgeCopy[i];
//            if (cell.getDirection() == Direction.RIGHT && i != bridge.length - 1 && bridgeCopy[i + 1].getDirection() == Direction.LEFT && !cell.isTicked() && !bridge[i + 1].isTicked()) {
//                bridge[i].setDirection(Direction.LEFT);
//                bridge[i].setTicked(true);
//            } else if (cell.getDirection() == Direction.LEFT && i > 0 && bridgeCopy[i - 1].getDirection() == Direction.RIGHT && !cell.isTicked() && !bridge[i + 1].isTicked()) {
//                bridge[i].setDirection(Direction.RIGHT);
//                bridge[i].setTicked(true);
//            }
//        }

        for (Cell cell : bridge) {
            cell.setTicked(false);
        }
    }

    public static String getBridgeAsString(Cell[] bridge) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Cell cell : bridge) {
            if (cell.getDirection() == Direction.NONE)
                stringBuilder.append(". ");
            else if (cell.getDirection() == Direction.LEFT)
                stringBuilder.append("< ");
            else if (cell.getDirection() == Direction.RIGHT)
                stringBuilder.append("> ");
            else if (cell.getDirection() == Direction.BOTH)
                stringBuilder.append("♢ ");
        }
        return stringBuilder.toString();
    }

}
