package net.liukrast.deployer.lib.logistics.stockTicker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record GenericOrder<V>(List<V> stacks) {

    public static <V> GenericOrder<V> empty() {
        return new GenericOrder<>(Collections.emptyList());
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    public static <V> Codec<GenericOrder<V>> simpleCodec(Codec<V> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(codec).fieldOf("entries").forGetter(GenericOrder::stacks)
        ).apply(instance, GenericOrder::new));
    }

    public static <V> StreamCodec<RegistryFriendlyByteBuf, GenericOrder<V>> simpleStreamCodec(StreamCodec<? super RegistryFriendlyByteBuf, V> codec) {
        return StreamCodec.of(
                (buf, val) -> {
                    buf.writeVarInt(val.stacks.size());
                    for(V v : val.stacks) {
                        codec.encode(buf, v);
                    }
                },
                (buf) -> {
                    int i = buf.readVarInt();
                    List<V> list = new ArrayList<>();

                    for(int j = 0; j < i; j++) {
                        list.add(codec.decode(buf));
                    }
                    return new GenericOrder<>(list);
                }
        );
    }
}
