package com.netease.util;

public class BitsUtil {
	public static Long maskAndSetBits(long bits, long mask, long settings) {
		long off = ~mask | settings;
		long on = mask & settings;
		long bits1 = bits & off;
		bits1 = bits1 | on;
		if (bits == bits1) {
			return null;
		} else {
			return bits1;
		}
	}

	public static long setBits(long bits, long bitsId, boolean turnOn) {
		if (turnOn) {
			bits = bits | bitsId;
		} else {
			bits = bits & ~bitsId;
		}
		return bits;
	}
	
	public static void main(String[] args) {
		long bits = setBits(0, 1, true);
		bits = setBits(bits, 2, true);
		bits = setBits(300, bits, true);
		System.out.println(bits);
		
		bits = setBits(300, 1, true);
		bits = setBits(bits, 2, true);
		System.out.println(bits);
	}
}
