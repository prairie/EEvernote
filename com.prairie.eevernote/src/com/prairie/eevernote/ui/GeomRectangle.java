package com.prairie.eevernote.ui;

import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.NumberUtil;

/*
 * ......                   ......                   ......
 * .    .....................    .....................    .
 * ......   	            ......                   ......
 *   .                                                 .
 *   .                                                 .
 *   .                                                 .
 * ......                                            ......
 * .    .          SAMPLE SCREENSHOT MODEL           .    .
 * ......                                            ......
 *   .                                                 .
 *   .                                                 .
 *   .                                                 .
 * ......					 ......                  ......
 * .    .....................      ..................     .
 * ......		             ......                  ......
 *
 */
public class GeomRectangle implements ConstantsUtil {

    private GeomPoint startPoint;
    private GeomPoint endPoint;

    public GeomRectangle() {
        startPoint = new GeomPoint();
        endPoint = new GeomPoint();
    }

    public GeomRectangle(final GeomPoint startPoint, final GeomPoint endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public GeomPoint getTopLeftPoint() {
        return new GeomPoint(Math.min(startPoint.getX(), endPoint.getX()), Math.min(startPoint.getY(), endPoint.getY()));
    }

    public GeomPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(final GeomPoint topLeftPoint) {
        startPoint = topLeftPoint;
    }

    public GeomPoint getBottomRightPoint() {
        return new GeomPoint(Math.max(startPoint.getX(), endPoint.getX()), Math.max(startPoint.getY(), endPoint.getY()));
    }

    public GeomPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(final GeomPoint bottomRightPoint) {
        endPoint = bottomRightPoint;
    }

    public int getWidth() {
        return getBottomRightPoint().getX() - getTopLeftPoint().getX();
    }

    public int getHeight() {
        return getBottomRightPoint().getY() - getTopLeftPoint().getY();
    }

    public GeomPoint getTopRightPoint() {
        return new GeomPoint(getBottomRightPoint().getX(), getTopLeftPoint().getY());
    }

    public GeomPoint getBottomLeftPoint() {
        return new GeomPoint(getTopLeftPoint().getX(), getBottomRightPoint().getY());
    }

    public GeomPoint getTopPoint() {
        return new GeomPoint((getTopLeftPoint().getX() + getBottomRightPoint().getX()) / TWO, getTopLeftPoint().getY());
    }

    public GeomPoint getLeftPoint() {
        return new GeomPoint(getTopLeftPoint().getX(), (getTopLeftPoint().getY() + getBottomRightPoint().getY()) / TWO);
    }

    public GeomPoint getBottomPoint() {
        return new GeomPoint((getTopLeftPoint().getX() + getBottomRightPoint().getX()) / TWO, getBottomRightPoint().getY());
    }

    public GeomPoint getRightPoint() {
        return new GeomPoint(getBottomRightPoint().getX(), (getTopLeftPoint().getY() + getBottomRightPoint().getY()) / TWO);
    }

    public GeomRectangle getTopRectangle() {
        return new GeomRectangle(new GeomPoint(getTopPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getTopPoint()).move(TWO, TWO));
    }

    public GeomRectangle getBottomRectangle() {
        return new GeomRectangle(new GeomPoint(getBottomPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getBottomPoint()).move(TWO, TWO));
    }

    public GeomRectangle getLeftRectangle() {
        return new GeomRectangle(new GeomPoint(getLeftPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getLeftPoint()).move(TWO, TWO));
    }

    public GeomRectangle getRightRectangle() {
        return new GeomRectangle(new GeomPoint(getRightPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getRightPoint()).move(TWO, TWO));
    }

    public GeomRectangle getTopLeftRectangle() {
        return new GeomRectangle(new GeomPoint(getTopLeftPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getTopLeftPoint()).move(TWO, TWO));
    }

    public GeomRectangle getTopRightRectangle() {
        return new GeomRectangle(new GeomPoint(getTopRightPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getTopRightPoint()).move(TWO, TWO));
    }

    public GeomRectangle getBottomLeftRectangle() {
        return new GeomRectangle(new GeomPoint(getBottomLeftPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getBottomLeftPoint()).move(TWO, TWO));
    }

    public GeomRectangle getBottomRightRectangle() {
        return new GeomRectangle(new GeomPoint(getBottomRightPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(getBottomRightPoint()).move(TWO, TWO));
    }

    public GeomPoint pointAt(final Position position) {
        if (position == Position.EAST) {
            return new GeomPoint(getBottomRightPoint().getX(), (getTopLeftPoint().getY() + getBottomRightPoint().getY()) / TWO);
        } else if (position == Position.SOUTH) {
            return new GeomPoint((getTopLeftPoint().getX() + getBottomRightPoint().getX()) / TWO, getBottomRightPoint().getY());
        } else if (position == Position.WEST) {
            return new GeomPoint(getTopLeftPoint().getX(), (getTopLeftPoint().getY() + getBottomRightPoint().getY()) / TWO);
        } else if (position == Position.NORTH) {
            return new GeomPoint((getTopLeftPoint().getX() + getBottomRightPoint().getX()) / TWO, getTopLeftPoint().getY());
        } else if (position == Position.SOUTHEAST) {
            return getBottomRightPoint();
        } else if (position == Position.SOUTHWEST) {
            return getBottomLeftPoint();
        } else if (position == Position.NORTHEAST) {
            getTopRightPoint();
        } else if (position == Position.NORTHWEST) {
            getTopLeftPoint();
        }
        return null;
    }

    public Position positionOf(final GeomPoint point) {
        if (point.equals(getTopLeftPoint()) || getTopLeftRectangle().contains(point)) {
            return Position.NORTHWEST;
        } else if (point.equals(getTopRightPoint()) || getTopRightRectangle().contains(point)) {
            return Position.NORTHEAST;
        } else if (point.equals(getBottomLeftPoint()) || getBottomLeftRectangle().contains(point)) {
            return Position.SOUTHWEST;
        } else if (point.equals(getBottomRightPoint()) || getBottomRightRectangle().contains(point)) {
            return Position.SOUTHEAST;
        } else if (point.getX() == getTopLeftPoint().getX() && point.getY() > getTopLeftPoint().getY() && point.getY() < getBottomRightPoint().getY() || getTopLeftRectangle().contains(point)) {
            return Position.WEST;
        } else if (point.getX() == getBottomRightPoint().getX() && point.getY() > getTopLeftPoint().getY() && point.getY() < getBottomRightPoint().getY() || getBottomRightRectangle().contains(point)) {
            return Position.EAST;
        } else if (point.getY() == getTopLeftPoint().getY() && point.getX() > getTopLeftPoint().getX() && point.getY() < getBottomRightPoint().getX() || getTopLeftRectangle().contains(point)) {
            return Position.NORTH;
        } else if (point.getY() == getBottomRightPoint().getY() && point.getX() > getTopLeftPoint().getX() && point.getX() < getBottomRightPoint().getX() || getBottomRightRectangle().contains(point)) {
            return Position.SOUTH;
        } else if (point.getX() > getTopLeftPoint().getX() && point.getX() < getBottomRightPoint().getX() && point.getY() > getTopLeftPoint().getY() && point.getY() < getBottomRightPoint().getY()) {
            return Position.INSIDE;
        } else {
            return Position.OUTSIDE;
        }
    }

    public boolean contains(final GeomPoint point) {
        return point.getX() >= getTopLeftPoint().getX() && point.getX() <= getBottomRightPoint().getX() && point.getY() >= getTopLeftPoint().getY() && point.getY() <= getBottomRightPoint().getY();
    }

    public GeomRectangle move(final int x, final int y) {
        if (!startPoint.isMovable(x, y) || !endPoint.isMovable(x, y)) {
            return this;
        }
        startPoint.move(x, y);
        endPoint.move(x, y);
        return this;
    }

    public GeomRectangle resize(final Position position, final int x, final int y) {
        if (position == Position.EAST) {
            if (getStartPoint().equals(getTopRightPoint()) || getStartPoint().equals(getBottomRightPoint())) {
                startPoint.move(x, y);
            } else if (getEndPoint().equals(getTopRightPoint()) || getEndPoint().equals(getBottomRightPoint())) {
                endPoint.move(x, y);
            }
        } else if (position == Position.SOUTH) {
            if (getStartPoint().equals(getBottomLeftPoint()) || getStartPoint().equals(getBottomRightPoint())) {
                startPoint.move(x, y);
            } else if (getEndPoint().equals(getBottomLeftPoint()) || getEndPoint().equals(getBottomRightPoint())) {
                endPoint.move(x, y);
            }
        } else if (position == Position.WEST) {
            if (getStartPoint().equals(getTopLeftPoint()) || getStartPoint().equals(getBottomLeftPoint())) {
                startPoint.move(x, y);
            } else if (getEndPoint().equals(getTopLeftPoint()) || getEndPoint().equals(getBottomLeftPoint())) {
                endPoint.move(x, y);
            }
        } else if (position == Position.NORTH) {
            if (getStartPoint().equals(getTopLeftPoint()) || getStartPoint().equals(getTopRightPoint())) {
                startPoint.move(x, y);
            } else if (getEndPoint().equals(getTopLeftPoint()) || getEndPoint().equals(getTopRightPoint())) {
                endPoint.move(x, y);
            }
        } else if (position == Position.SOUTHWEST) {
            if (getStartPoint().equals(getBottomLeftPoint())) {
                startPoint.move(x, y);
            } else if (getStartPoint().equals(getTopRightPoint())) {
                endPoint.move(x, y);
            } else if (getStartPoint().equals(getTopLeftPoint())) {
                startPoint.move(x, 0);
                endPoint.move(0, y);
            } else if (getStartPoint().equals(getBottomRightPoint())) {
                startPoint.move(0, y);
                endPoint.move(x, 0);
            }
        } else if (position == Position.SOUTHEAST) {
            if (getStartPoint().equals(getBottomRightPoint())) {
                startPoint.move(x, y);
            } else if (getStartPoint().equals(getTopLeftPoint())) {
                endPoint.move(x, y);
            } else if (getStartPoint().equals(getTopRightPoint())) {
                startPoint.move(x, 0);
                endPoint.move(0, y);
            } else if (getStartPoint().equals(getBottomLeftPoint())) {
                startPoint.move(0, y);
                endPoint.move(x, 0);
            }
        } else if (position == Position.NORTHWEST) {
            if (getStartPoint().equals(getTopLeftPoint())) {
                startPoint.move(x, y);
            } else if (getStartPoint().equals(getBottomRightPoint())) {
                endPoint.move(x, y);
            } else if (getStartPoint().equals(getTopRightPoint())) {
                startPoint.move(0, y);
                endPoint.move(x, 0);
            } else if (getStartPoint().equals(getBottomLeftPoint())) {
                startPoint.move(x, 0);
                endPoint.move(0, y);
            }
        } else if (position == Position.NORTHEAST) {
            if (getStartPoint().equals(getTopRightPoint())) {
                startPoint.move(x, y);
            } else if (getStartPoint().equals(getBottomLeftPoint())) {
                endPoint.move(x, y);
            } else if (startPoint.equals(getTopLeftPoint())) {
                startPoint.move(0, y);
                endPoint.move(x, 0);
            } else if (startPoint.equals(getBottomRightPoint())) {
                startPoint.move(x, 0);
                endPoint.move(0, y);
            }
        }
        return this;
    }

    public boolean isRealRectangle() {
        return getWidth() > 0 && getHeight() > 0;
    }

    public void clear() {
        startPoint.clear();
        endPoint.clear();
    }

    @Override
    public String toString() {
        return LEFT_PARENTHESIS + getTopLeftPoint() + COMMA + getWidth() + COMMA + getHeight() + RIGHT_PARENTHESIS;
    }

    enum Position {
        SOUTHWEST, SOUTHEAST, NORTHWEST, NORTHEAST, NORTH, SOUTH, WEST, EAST, OUTSIDE, INSIDE
    }
}
