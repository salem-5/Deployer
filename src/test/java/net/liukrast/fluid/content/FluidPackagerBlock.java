package net.liukrast.fluid.content;

import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlock;
import net.liukrast.fluid.registry.RegisterBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;

public class FluidPackagerBlock extends AbstractPackagerBlock {
    public FluidPackagerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
        return RegisterBlockEntityTypes.FLUID_PACKAGER.get();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean isSideValid(BlockEntity be) {
        return be.hasLevel() && be.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null) != null;
    }
}
