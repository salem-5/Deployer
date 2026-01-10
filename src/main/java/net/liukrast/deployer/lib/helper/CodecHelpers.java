package net.liukrast.deployer.lib.helper;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public class CodecHelpers {
    private CodecHelpers() {}

    public static class Stream {
        private Stream() {}
        public static <V> StreamCodec<RegistryFriendlyByteBuf, GenericOrderContained<V>> createContained(Supplier<StockInventoryType<?, V, ?>> typeSupplier) {
            return deferredStreamCodec(() -> typeSupplier.get().valueHandler().orderContainedStreamCodec());
        }

        public static <B, V> StreamCodec<B, V> deferredStreamCodec(Supplier<StreamCodec<B, V>> supplier) {
            return StreamCodec.of(
                    (buf, val) -> supplier.get().encode(buf, val),
                    buf -> supplier.get().decode(buf)
            );
        }
    }

    public static class Normal {
        private Normal() {}
        public static <V> Codec<GenericOrderContained<V>> createContained(Supplier<StockInventoryType<?, V, ?>> typeSupplier) {
            return deferredCodec(() -> typeSupplier.get().valueHandler().orderContainedCodec());
        }

        public static <V> Codec<V> deferredCodec(Supplier<Codec<V>> supplier) {
            return new Codec<>() {
                @Override
                public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
                    return supplier.get().decode(ops, input);
                }

                @Override
                public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
                    return supplier.get().encode(input, ops, prefix);
                }
            };
        }
    }
}
