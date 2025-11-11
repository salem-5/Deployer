package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class AbstractPackagerBlock extends PackagerBlock {
    public AbstractPackagerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public abstract BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType();

    public abstract boolean isSideValid(BlockEntity be);
}
