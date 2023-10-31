package me.fishydarwin.fractalexplorer.utils;

public class FEMathUtils {

    public static double clamp(double num, double min, double max) {
        return Math.max(Math.min(num, max), min);
    }

}
