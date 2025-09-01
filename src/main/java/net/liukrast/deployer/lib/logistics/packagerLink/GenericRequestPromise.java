package net.liukrast.deployer.lib.logistics.packagerLink;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Comparator;

public class GenericRequestPromise<V> {
    public int ticksExisted = 0;
    public V promisedStack;

    public GenericRequestPromise(V promisedStack) {
        this.promisedStack = promisedStack;
    }

    public GenericRequestPromise(int ticksExisted, V promisedStack) {
        this(promisedStack);
        this.ticksExisted = ticksExisted;
    }

    public void tick() {
        ticksExisted++;
    }

    public static Comparator<? super GenericRequestPromise<?>> ageComparator() {
        return (i1, i2) -> Integer.compare(i2.ticksExisted, i1.ticksExisted);
    }

    public static <V> Codec<GenericRequestPromise<V>> simpleCodec(Codec<V> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("ticks_existed").forGetter(i -> i.ticksExisted),
                codec.fieldOf("promised_stack").forGetter(i -> i.promisedStack)
        ).apply(instance, GenericRequestPromise::new));
    }
}
