/*
 *  Java OTR library
 *  Copyright (C) 2008-2009  Ian Goldberg, Muhaimeen Ashraf, Andrew Chung,
 *                           Can Tang
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of version 2.1 of the GNU Lesser General
 *  Public License as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.otr4j.crypto;

import java.math.BigInteger;

public class Util {

    /**
     * Hide the ctor, because this is an utility class.
     */
    private Util() {
    }

    public static boolean arrayEquals(byte[] b1, byte[] b2) {
        if (b1 == null || b2 == null || b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    public static void checkBytes(String s, byte[] bytes) {
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            hexString
                    .append(Integer.toHexString((bytes[i] >>> 4) & 0x0F))
                    .append(Integer.toHexString(0x0F & bytes[i]));
        }
        System.out.println(s + ": " + hexString.toString());
    }

    static void writeInt(byte[] dst, int index, int src) {
        dst[index] = (byte) ((src >> 24) & 0xff);
        dst[index + 1] = (byte) ((src >> 16) & 0xff);
        dst[index + 2] = (byte) ((src >> 8) & 0xff);
        dst[index + 3] = (byte) (src & 0xff);
    }

    static int readInt(byte[] src, int index) {
        int ret = ((int) src[index] << 24)
                | ((int) (src[index + 1] << 16) & 0xff0000)
                | ((int) (src[index + 2] << 8) & 0xff00)
                | ((int) src[index + 3] & 0xff);
        return ret;
    }

    static byte[] hexStringToBytes(String s) {
        byte[] sbytes = s.getBytes();
        if (sbytes.length % 2 != 0) {
            return null;
        }
        byte[] ret = new byte[sbytes.length / 2];
        for (int i = 0; i < ret.length; i++) {
            if (sbytes[2 * i] >= 'A' && sbytes[2 * i] <= 'F') {
                ret[i] = (byte) ((sbytes[2 * i] - ('A' - 10)) << 4);
            } else if (sbytes[2 * i] >= 'a' && sbytes[2 * i] <= 'f') {
                ret[i] = (byte) ((sbytes[2 * i] - ('a' - 10)) << 4);
            } else {
                ret[i] = (byte) ((sbytes[2 * i] - '0') << 4);
            }
            if (sbytes[2 * i + 1] >= 'A' && sbytes[2 * i + 1] <= 'F') {
                ret[i] |= (byte) (sbytes[2 * i + 1] - ('A' - 10));
            } else if (sbytes[2 * i + 1] >= 'a' && sbytes[2 * i + 1] <= 'f') {
                ret[i] |= (byte) (sbytes[2 * i + 1] - ('a' - 10));
            } else {
                ret[i] |= (byte) (sbytes[2 * i + 1] - '0');
            }
        }
        return ret;
    }

    static String bytesToHexString(byte[] mpi) {
        byte[] hex = new byte[2 * mpi.length];
        for (int i = 0; i < mpi.length; i++) {
            int num = (int) (mpi[i] >> 4 & 0xf);
            if (num <= 9) {
                hex[2 * i] = (byte) ('0' + num);
            } else {
                hex[2 * i] = (byte) ('A' + num - 10);
            }
            num = (int) (mpi[i] & 0xf);
            if (num <= 9) {
                hex[2 * i + 1] = (byte) ('0' + num);
            } else {
                hex[2 * i + 1] = (byte) ('A' + num - 10);
            }

        }
        return new String(hex);
    }

    /**
     * Return the passed in value as an unsigned byte array.
     *
     * NOTE: Implementation copied from Bouncy Castle 1.65, which applied an
     * MIT-style license.
     *
     * @param value value to be converted.
     * @return a byte array without a leading zero byte if present in the signed
     * encoding.
     */
    public static byte[] asUnsignedByteArray(BigInteger value) {
        byte[] bytes = value.toByteArray();

        if (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];

            System.arraycopy(bytes, 1, tmp, 0, tmp.length);

            return tmp;
        }

        return bytes;
    }

    /**
     * Return the passed in value as an unsigned byte array.
     *
     * NOTE: Implementation copied from Bouncy Castle 1.65, which applied an
     * MIT-style license.
     *
     * @param value value to be converted.
     * @return a byte array without a leading zero byte if present in the signed
     * encoding.
     */
    public static byte[] asUnsignedByteArray(int length, BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == length) {
            return bytes;
        }

        int start = bytes[0] == 0 ? 1 : 0;
        int count = bytes.length - start;

        if (count > length) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }

        byte[] tmp = new byte[length];
        System.arraycopy(bytes, start, tmp, tmp.length - count, count);
        return tmp;
    }

    /**
     * Write the passed in value as unsigned bytes into the destination array.
     *
     * NOTE: Adapted from implementation copied from Bouncy Castle 1.65, which
     * applied an MIT-style license.
     *
     * @param value value to be converted.
     * @param dst destination byte-array
     * @param length allowed length within dst
     * @param offset offset in dst
     */
    public static void asUnsignedByteArray(BigInteger value, byte[] dst, int offset, int length) {
        if (length > dst.length - offset) {
            throw new IllegalArgumentException("length value is larger than dst array length");
        }
        final byte[] bytes = value.toByteArray();
        final int start = bytes[0] == 0 ? 1 : 0;
        final int count = bytes.length - start;
        if (count > length) {
            throw new IllegalArgumentException("specified length exceeded for value");
        }
        System.arraycopy(bytes, start, dst, offset + length - count, count);
    }
}
