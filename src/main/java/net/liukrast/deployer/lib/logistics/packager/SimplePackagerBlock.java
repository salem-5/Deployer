package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

import java.util.Optional;
import java.util.function.Supplier;

public class SimplePackagerBlock extends AbstractPackagerBlock {
    private final Supplier<BlockEntityType<? extends PackagerBlockEntity>> blockEntityTypeSupplier;
    private final BlockCapability<?,?> cap;
    private final Optional<PartialModel> trayModel;

    public SimplePackagerBlock(Properties properties, Supplier<BlockEntityType<? extends PackagerBlockEntity>> blockEntityTypeSupplier, BlockCapability<?,?> cap) {
        this(properties, blockEntityTypeSupplier, cap, Optional.empty());
    }

    public SimplePackagerBlock(Properties properties, Supplier<BlockEntityType<? extends PackagerBlockEntity>> blockEntityTypeSupplier, BlockCapability<?,?> cap, Optional<PartialModel> trayModel) {
        super(properties);
        this.blockEntityTypeSupplier = blockEntityTypeSupplier;
        this.cap = cap;
        this.trayModel = trayModel;
    }

    @Override
    public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
        return blockEntityTypeSupplier.get();
    }

    @Override
    public boolean isSideValid(BlockEntity be) {
        if (!be.hasLevel()) return false;
        assert be.getLevel() != null;
        return be.getLevel().getCapability(cap, be.getBlockPos(), null) != null;
    }

    @Override
    public PartialModel getTrayModel(BlockState blockState, PartialModel original) {
        return trayModel.orElse(original);
    }
}
