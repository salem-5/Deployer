package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An abstraction of {@link PackagerBlock} to handle differently the block
 * */
public abstract class AbstractPackagerBlock extends PackagerBlock {
    /**
     * @param properties The block properties
     * */
    public AbstractPackagerBlock(Properties properties) {
        super(properties);
    }

    /**
     * @return The Blockentity type for this block
     * */
    @Override
    public abstract BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType();

    /**
     * When you place a packager,
     * it will try automatically to set his direction looking towards a chest, or any other container.<br><br>
     * This function will be called for every direction (if there is a block entity on that face),
     * and you can decide whether the side is valid to rotate the custom packager or not
     * @param be The block entity to check
     * @return Whether the side is valid to rotate the packager's face.
     * */
    public abstract boolean isSideValid(BlockEntity be);

    @SuppressWarnings("unused")
    public static class Simple extends AbstractPackagerBlock {
        private final Supplier<BlockEntityType<? extends PackagerBlockEntity>> blockEntityTypeSupplier;
        private final Predicate<BlockEntity> sidePredicate;

        /**
         * Creates a simple implementation of an {@link AbstractPackagerBlock}.
         * Initializes the block with the given properties, a supplier for its block entity type,
         * and a predicate used to determine whether a neighboring block entity is a valid side.
         * The predicate will be evaluated during placement to decide the automatic facing direction of the packager.
         *
         * @param properties
         *     The block properties used by this packager block.
         *
         * @param blockEntityTypeSupplier
         *     A supplier that provides the {@link BlockEntityType} associated with this block.
         *
         * @param sidePredicate
         *     A predicate that determines whether a given block entity represents a valid side for orientation.
         */
        public Simple(Properties properties, Supplier<BlockEntityType<? extends PackagerBlockEntity>> blockEntityTypeSupplier, Predicate<BlockEntity> sidePredicate) {
            super(properties);
            this.blockEntityTypeSupplier = blockEntityTypeSupplier;
            this.sidePredicate = sidePredicate;
        }

        /**
         * @return The Blockentity type for this block
         * */
        @Override
        public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
            return blockEntityTypeSupplier.get();
        }

        /**
         * When you place a packager,
         * it will try automatically to set his direction looking towards a chest,
         * or any other container.<br><br>
         * This function will be called for every direction (if there is a block entity on that face),
         * and you can decide whether the side is valid to rotate the custom packager or not
         * @param be The block entity to check
         * @return Whether the side is valid to rotate the packager's face.
         * */
        @Override
        public boolean isSideValid(BlockEntity be) {
            return sidePredicate.test(be);
        }
    }
}
