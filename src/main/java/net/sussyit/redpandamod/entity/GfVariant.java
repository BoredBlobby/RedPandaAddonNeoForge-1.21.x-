package net.sussyit.redpandamod.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum GfVariant {
    REINDEER(0),
    KOREAN(1),
    IFORGOR(2);

    private static final GfVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(GfVariant::getId)).toArray(GfVariant[]::new);
    private final int id;

    GfVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static GfVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
