package net.liukrast.deployer.lib.mixin;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Pair;
import net.liukrast.deployer.lib.logistics.packager.*;
import net.liukrast.deployer.lib.logistics.packagerLink.LogisticsGenericManager;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.deployer.lib.mixinExtensions.LLBExtension;
import net.liukrast.deployer.lib.mixinExtensions.PLBEExtension;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(LogisticallyLinkedBehaviour.class)
public abstract class LogisticallyLinkedBehaviourMixin extends BlockEntityBehaviour implements LLBExtension {

    @Shadow public UUID freqId;

    public LogisticallyLinkedBehaviourMixin(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public <K, V, H> Pair<AbstractPackagerBlockEntity<K,V,H>, GenericPackagingRequest<V>> deployer$processRequests(StockInventoryType<K, V, H> type, V stack, int amount, String address, int linkIndex, MutableBoolean finalLink, int orderId, @Nullable GenericOrderContained<V> context, @Nullable IdentifiedContainer<H> ignoredHandler) {
        if (blockEntity instanceof PackagerLinkBlockEntity plbe)
            return ((PLBEExtension)plbe).deployer$processRequest(type, stack, amount, address, linkIndex, finalLink, orderId, context, ignoredHandler);
        return null;
    }

    @Override
    public <K,V,H> AbstractInventorySummary<K, V> deployer$getSummary(StockInventoryType<K,V,H> type, @Nullable IdentifiedContainer<H> ignoredHandler) {
        if (blockEntity instanceof PackagerLinkBlockEntity pLBE)
            return ((PLBEExtension)pLBE).deployer$fetchSummaryFromPackager(type, ignoredHandler);
        return type.networkHandler().empty();
    }

    @Override
    public <K, V, H> void deployer$deductFromAccurateSummary(StockInventoryType<K, V, H> type, H packageContents) {
        AbstractInventorySummary<K,V> summary = LogisticsGenericManager.getAccurateSummaries(type)
                .getIfPresent(freqId);
        if (summary == null)
            return;
        for (int i = 0; i < type.storageHandler().getSlots(packageContents); i++) {
            V orderedStack = type.storageHandler().getStackInSlot(packageContents, i);
            if (type.valueHandler().isEmpty(orderedStack))
                continue;
            summary.add(orderedStack, -Math.min(summary.getCountOf(orderedStack), type.valueHandler().getCount(orderedStack)));
        }
    }
}
