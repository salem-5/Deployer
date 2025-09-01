package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import net.createmod.catnip.data.Pair;
import net.liukrast.deployer.lib.logistics.packager.*;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.deployer.lib.mixinExtensions.PLBEExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagerLinkBlockEntity.class)
public abstract class PackagerLinkBlockEntityMixin extends LinkWithBulbBlockEntity implements PLBEExtension {

    @Shadow public LogisticallyLinkedBehaviour behaviour;

    private PackagerLinkBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public <K,V,H> AbstractInventorySummary<K,V> deployer$fetchSummaryFromPackager(StockInventoryType<K,V,H> type, @Nullable IdentifiedContainer<H> ignoredHandler) {
        AbstractPackagerBlockEntity<K,V,H> packager = deployer$getPackager(type);
        if (packager == null)
            return type.networkHandler().empty();
        if(packager.isTargetingSameContainer(ignoredHandler))
            return type.networkHandler().empty();
        return packager.getAvailableStacks();
    }

    @Override
    public <K, V, H> Pair<AbstractPackagerBlockEntity<K,V,H>, GenericPackagingRequest<V>> deployer$processRequest(StockInventoryType<K,V,H> type, V stack, int amount, String address, int linkIndex, MutableBoolean finalLink, int orderId, @Nullable GenericOrderContained<V> context, @Nullable IdentifiedContainer<H> ignoredHandler) {
        AbstractPackagerBlockEntity<K,V,H> packager = deployer$getPackager(type);
        if (packager == null)
            return null;
        if(packager.isTargetingSameContainer(ignoredHandler))
            return null;

        AbstractInventorySummary<K,V> summary = packager.getAvailableStacks();
        int availableCount = summary.getCountOf(stack);
        if (availableCount == 0)
            return null;
        int toWithdraw = Math.min(amount, availableCount);
        return Pair.of(packager,
                GenericPackagingRequest.create(stack, toWithdraw, address, linkIndex, finalLink, 0, orderId, context));
    }

    @Override
    public <K,V,H> AbstractPackagerBlockEntity<K,V,H> deployer$getPackager(StockInventoryType<K,V,H> type) {
        BlockState blockState = getBlockState();
        if (behaviour.redstonePower == 15)
            return null;
        BlockPos source = worldPosition.relative(PackagerLinkBlock.getConnectedDirection(blockState)
                .getOpposite());
        assert level != null;
        if(!(level.getBlockEntity(source) instanceof AbstractPackagerBlockEntity<?,?,?> packager))
            return null;
        //noinspection unchecked
        return packager.getStockType() == type ? (AbstractPackagerBlockEntity<K, V, H>) packager : null;
    }

    @ModifyReturnValue(method = "getPackager", at = @At("RETURN"))
    private PackagerBlockEntity getPackager(PackagerBlockEntity original) {
        return original instanceof AbstractPackagerBlockEntity<?,?,?> ? null : original;
    }
}
