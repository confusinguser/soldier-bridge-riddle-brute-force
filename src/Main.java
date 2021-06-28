import java.util.Arrays;

public class Main {

    private static final int bridgeLen = 5;
    private static int currBridgeNum = -1; // Increases instantly in beginning of main

    public static void main(String[] args) {
        while (currBridgeNum < (1 << bridgeLen) - 1) { // same as 2^bridgeLen
            currBridgeNum++;
            Cell[] bridge = generateBridge(currBridgeNum);
            System.out.println(getBridgeAsString(bridge));
            int i = 0;
            while (Arrays.stream(bridge).anyMatch(cell -> cell.getDirection() != Direction.NONE)) {
                i++;
                advanceBridge(bridge);
                System.out.println(getBridgeAsString(bridge));
                if (i >= 100) break;
            }
            System.out.println("-----------");

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
        Cell[] bridgeCopy = new Cell[bridge.length]; // One pass for colliding and one for walking (right, left separate passes from diff directions) and then a both cell colliding one, think about this configuration: -> -> -> <-, correct result after 1 tick: . -> ♢ ->

        for (int i = 0; i < bridge.length; i++) { // For deep copy, update bridgeCopy after each pass
            Cell cell = bridge[i];
            bridgeCopy[i] = cell.copy();
        }

        for (int i = 0; i < bridge.length; i++) {
            Cell cell = bridgeCopy[i];

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

            if (cell.getDirection() == Direction.RIGHT && bridgeCopy[i + 1].getDirection() == Direction.LEFT) {
                bridge[i].setDirection(Direction.LEFT);
                bridge[i].setTicked(true);
            } else if (cell.getDirection() == Direction.LEFT && bridgeCopy[i - 1].getDirection() == Direction.RIGHT) {
                bridge[i].setDirection(Direction.RIGHT);
                bridge[i].setTicked(true);
            }
        }

        for (int i = 0; i < bridge.length; i++) { // For deep copy
            Cell cell = bridge[i];
            bridgeCopy[i] = cell.copy();
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

        for (int i = 0; i < bridge.length; i++) { // For deep copy
            Cell cell = bridge[i];
            bridgeCopy[i] = cell.copy();
        }

        // From bridge.len-1 to 1, so that all rights move in a chain instead of one every tick
        for (int i = bridge.length - 1; i >= 0; i--) { // Right walking pass
            Cell cell = bridge[i];
            if (cell.isTicked()) continue;

            if (cell.getDirection() == Direction.RIGHT) {
                if (bridge[i + 1].getDirection() == Direction.NONE) {
                    bridge[i].setDirection(Direction.NONE);
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

        for (int i = 0; i < bridge.length; i++) { // Both colliding pass
            Cell cell = bridgeCopy[i];
            if (cell.getDirection() == Direction.RIGHT && i != bridge.length - 1 && bridgeCopy[i + 1].getDirection() == Direction.LEFT && !cell.isTicked() && !bridge[i + 1].isTicked()) {
                bridge[i].setDirection(Direction.LEFT);
                bridge[i].setTicked(true);
            } else if (cell.getDirection() == Direction.LEFT && i > 0 && bridgeCopy[i - 1].getDirection() == Direction.RIGHT && !cell.isTicked() && !bridge[i + 1].isTicked()) {
                bridge[i].setDirection(Direction.RIGHT);
                bridge[i].setTicked(true);
            }
        }

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
