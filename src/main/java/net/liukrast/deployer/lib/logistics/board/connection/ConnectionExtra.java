package net.liukrast.deployer.lib.logistics.board.connection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Represents a supplemental connection to a standard block that does not provide
 * a smart block entity.
 * Used by factory panels to interact with blocks that require additional logic
 * or contextual data without being full behaviors themselves.
 */
@FunctionalInterface
public interface ConnectionExtra<T> {
    /**
     * Invalidates or updates the connection at the given position.
     * This method is invoked when the panel needs to check whether the connection
     * is still valid or to compute additional data related to the target block.
     *
     * @param level the level containing the block
     * @param state the block state at the given position
     * @param pos   the position of the block
     * @param be    the block entity at the position, if any
     * @return an {@link Optional} containing a new value if the connection remains valid,
     *         or {@link Optional#empty()} if the connection should be removed
     *
     * @implNote Implementations should avoid expensive operations, as this may be
     *           executed frequently depending on panel update frequency.
     */
    Optional<T> invalidate(Level level, BlockState state, BlockPos pos, BlockEntity be);
}
