package net.liukrast.deployer.lib.helper;

import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public class CodecHelpers {
    private CodecHelpers() {}

    public static class Stream {
        public static <V> StreamCodec<RegistryFriendlyByteBuf, GenericOrderContained<V>> superSimpleStreamCodec(Supplier<StockInventoryType<?, V, ?>> typeSupplier) {
            return deferredStreamCodec(() -> typeSupplier.get().valueHandler().orderContainedStreamCodec());
        }

        public static <B, V> StreamCodec<B, V> deferredStreamCodec(Supplier<StreamCodec<B, V>> supplier) {
            return StreamCodec.of(
                    (buf, val) -> supplier.get().encode(buf, val),
                    buf -> supplier.get().decode(buf)
            );
        }
    }
}
