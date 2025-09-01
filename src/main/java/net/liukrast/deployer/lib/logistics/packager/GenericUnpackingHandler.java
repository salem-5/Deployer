package net.liukrast.deployer.lib.logistics.packager;

import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public interface GenericUnpackingHandler<V> {
    boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<V> items, @Nullable GenericOrderContained<V> orderContext, boolean simulate);
}
