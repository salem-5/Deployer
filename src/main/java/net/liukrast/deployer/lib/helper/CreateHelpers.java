package net.liukrast.deployer.lib.helper;

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class CreateHelpers {
    public static <T extends Block> Consumer<T> withStressCapacity(double value) {
        return block -> BlockStressValues.CAPACITIES.register(block, () -> value);
    }

    public static <T extends Block> Consumer<T> withStressImpact(double value) {
        return block -> BlockStressValues.IMPACTS.register(block, () -> value);
    }
}
