package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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

    /**
     * Defines the tray model for your packager.
     * @param blockState Context to change model based on the state
     * @param original The default packager partial model, which is always {@link com.simibubi.create.AllPartialModels#PACKAGER_TRAY_DEFRAG}
     * @return The tray model you want to use
     * Also see: {@link AbstractPackagerBlockEntity#getHatchModel(boolean, PartialModel)}
     * */
    public PartialModel getTrayModel(BlockState blockState, PartialModel original) {
        return original;
    }
}
