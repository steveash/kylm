/*
$Rev$

The Kyoto Language Modeling Toolkit.
Copyright (C) 2009 Kylm Development Team

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.github.steveash.kylm.util;

/**
 * A collection of functions that handle functions regarding mathematics.
 * @author neubig
 */
public class KylmMathUtils {

    /**
     * return the sum of a float array
     * @param arr the array
     * @return the sum
     */
    public static final float sum(float[] arr) {
        float ret = 0;
        if (arr != null)
            for (float d : arr)
                ret += d;
        return ret;
    }

    /**
     * return the sum of an int array
     * @param arr the array
     * @return the sum
     */
    public static final int sum(int[] arr) {
        int ret = 0;
        if (arr != null)
            for (int d : arr)
                ret += d;
        return ret;
    }

    //	/**
    //	 * Add two values (probabilities) that are in log format
    //	 * @param a The first value
    //	 * @param b The second value
    //	 * @return The sum of a and b
    //	 */
    //	public static final float logAddition(float a, float b) {
    //		if(b == Double.NEGATIVE_INFINITY)
    //			return a;
    //		return Math.log10(Math.pow(10, a-b)+1)+b;
    //	}

    /**
     * Add two values (probabilities) that are in log format
     * @param a The first value
     * @param b The second value
     * @return The sum of a and b
     */
    public static final float logAddition(float a, float b) {
        if (b == Float.NEGATIVE_INFINITY)
            return a;
        return (float) Math.log10(Math.pow(10, a - b) + 1) + b;
    }
}
