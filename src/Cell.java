public final class Cell {
    private Direction direction;
    private boolean ticked;

    public Cell(Direction direction) {
        this.direction = direction;
        this.ticked = false;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public Cell copy() {
        Cell cell = new Cell(direction);
        cell.setTicked(isTicked());
        return cell;
    }
}
