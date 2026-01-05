package net.liukrast.fluid.content.energy;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import io.netty.buffer.ByteBuf;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packagerLink.GenericRequestPromise;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStockInventoryType extends StockInventoryType<Void, Integer, IEnergyStorage> {
    private static final Codec<GenericRequestPromise<Integer>> REQUEST_CODEC = GenericRequestPromise.simpleCodec(Codec.INT);

    private static final IValueHandler<Void, Integer, IEnergyStorage> VALUE_HANDLER = new IValueHandler<>() {
        @Override
        public Codec<Integer> codec() {
            return Codec.INT;
        }

        @Override
        public StreamCodec<? extends ByteBuf, Integer> streamCodec() {
            return ByteBufCodecs.INT;
        }

        @Override
        public Void fromValue(Integer key) {
            return null;
        }

        @Override
        public boolean equals(Integer a, Integer b) {
            return true;
        }

        @Override
        public boolean test(FilterItemStack filter, Level level, Integer value) {
            return true;
        }

        @Override
        public int getCount(Integer value) {
            return value;
        }

        @Override
        public void setCount(Integer value, int count) {
            value
        }

        @Override
        public boolean isEmpty(Integer stack) {
            return false;
        }

        @Override
        public Integer create(Void key, int amount) {
            return 0;
        }

        @Override
        public void shrink(Integer stack, int amount) {

        }

        @Override
        public Integer copyWithCount(Integer stack, int amount) {
            return 0;
        }

        @Override
        public Integer copy(Integer stack) {
            return 0;
        }

        @Override
        public boolean isStackable(Integer stack) {
            return false;
        }

        @Override
        public Integer empty() {
            return 0;
        }
    }
}
