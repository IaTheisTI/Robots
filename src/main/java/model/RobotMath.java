package model;

/**
 * Класс математических вычислений, необходимых для логики и визуалиации робота
 */
public class RobotMath {

    /**
     * расчет расстояния между двумя точками
     *  x1 - х координата первой точки
     *  y1 - у координата первой точки
     *  x2 - х координата второй точки
     *  y2 - у координата второй точки
     */
    public static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * расчет угла наклона вектора к оси абсцисс
     */
    public static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * нормализация угла
     */
    public static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    /**
     * округление
     */
    public static int round(double value)
    {
        return (int)(value + 0.5);
    }
}