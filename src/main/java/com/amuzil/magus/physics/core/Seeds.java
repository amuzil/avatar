package com.amuzil.magus.physics.core;

import java.util.UUID;

// Used for ensuring consistent randomness across client and server
public final class Seeds {
    private Seeds() {
    }

    // 64-bit mix (SplitMix64)
    public static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    public static long mix(long a, long b) {
        return mix64(a ^ mix64(b));
    }

    public static long fromUuid(UUID u) {
        return mix64(u.getMostSignificantBits() ^ u.getLeastSignificantBits());
    }
}