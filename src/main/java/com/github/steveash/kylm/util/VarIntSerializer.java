package com.github.steveash.kylm.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Steve Ash
 */
public class VarIntSerializer {

    public static void write(int value, DataOutput out) throws IOException {
        if (value == 0) {
            out.writeByte(0);
        }

        byte[] data = new byte[5];
        int i;
        for (i = 4; value != 0; i--) {
            data[i] = (byte) (value & 0x7f);
            if (i != 4)
                data[i] |= 0x80;
            value >>>= 7;
        }
        for (int j = i + 1; j < 5; j++) {
            out.writeByte(data[j]);
        }
    }

    public static int read(DataInput inp) throws IOException {
        int v = 0;
        for (int i = 0; i < 5; i++) {
            byte b = inp.readByte();
            v = (v << 7) | (b & 0x7f);
            if ((b & 0x80) == 0)
                return v;
        }
        throw new IllegalArgumentException("Invalid number serialized " + v);
    }
}
