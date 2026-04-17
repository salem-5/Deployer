package net.liukrast.deployer.lib.logistics.board.connection;

import java.util.function.ToIntFunction;

public class ConnectionLine {
    private ConnectionLine() {}

    public static <T> ToIntFunction<T> createStatic(int color, boolean dots, boolean flowing) {
        int packed = pack(color, dots, flowing);
        return t -> packed;
    }

    public static int pack(int color) {
        return pack(color, false, false);
    }

    /**
     *
     * */
    public static int pack(int color, boolean dots, boolean flowing) {
        int packed = color & 0x00FFFFFF;          // garantisce che solo i 24 bit RGB siano presenti
        if (dots) packed |= (1 << 30);
        if (flowing) packed |= (1 << 31);
        return packed;
    }

    public static int unpackColor(int packed) {
        return packed & 0x00FFFFFF;
    }

    public static boolean unpackDots(int packed) {
        return (packed & (1 << 30)) != 0;
    }

    public static boolean unpackFlowing(int packed) {
        return (packed & (1 << 31)) != 0;
    }
}
