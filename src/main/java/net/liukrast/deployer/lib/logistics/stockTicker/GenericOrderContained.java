package net.liukrast.deployer.lib.logistics.stockTicker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record GenericOrderContained<V>(GenericOrder<V> orderedStacks) {

    public List<V> stacks() {
        return orderedStacks.stacks();
    }

    public static <V> GenericOrderContained<V> empty() {
        return new GenericOrderContained<V>(GenericOrder.empty());
    }

    public boolean isEmpty() {
        return orderedStacks.isEmpty();
    }

    public static <V> GenericOrderContained<V> simple(List<V> stacks) {
        return new GenericOrderContained<>(new GenericOrder<>(stacks));
    }

    public static <V> StreamCodec<RegistryFriendlyByteBuf, GenericOrderContained<V>> fromOrderStreamCodec(StreamCodec<? super RegistryFriendlyByteBuf, GenericOrder<V>> codec) {
        return StreamCodec.composite(
                codec, GenericOrderContained::orderedStacks,
                GenericOrderContained::new
        );
    }

    public static <V> Codec<GenericOrderContained<V>> fromOrderCodec(Codec<GenericOrder<V>> codec, Codec<V> alternative) {
        return Codec.withAlternative(
                RecordCodecBuilder.create(i -> i.group(
                        codec.fieldOf("ordered_stacks").forGetter(GenericOrderContained::orderedStacks)
                ).apply(i, GenericOrderContained::new)),
                RecordCodecBuilder.create(i -> i.group(
                        Codec.list(alternative).fieldOf("entries").forGetter(GenericOrderContained::stacks)
                ).apply(i, GenericOrderContained::simple))
        );
    }
}
