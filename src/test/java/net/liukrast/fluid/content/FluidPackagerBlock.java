package net.liukrast.fluid.content;

import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.liukrast.fluid.registry.RegisterBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FluidPackagerBlock extends PackagerBlock {
    public FluidPackagerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
        return RegisterBlockEntityTypes.FLUID_PACKAGER.get();
    }

}
