package net.liukrast.deployer.lib.mixinExtensions;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.IdentifiedContainer;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrder;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;

import javax.annotation.Nullable;

public interface SCBEExtension {
    <K,V,H> AbstractInventorySummary<K,V> deployer$getRecentSummary(StockInventoryType<K,V,H> type);
    <K,V,H> AbstractInventorySummary<K,V> deployer$getAccurateSummary(StockInventoryType<K,V,H> type);
    <K,V,H> boolean deployer$broadcastPackageRequest(StockInventoryType<K,V,H> type, LogisticallyLinkedBehaviour.RequestType requestType, GenericOrder<V> order, @Nullable IdentifiedContainer<H> ignoredHandler, String address);
    <K,V,H> boolean deployer$broadcastPackageRequest(StockInventoryType<K,V,H> type, LogisticallyLinkedBehaviour.RequestType requestType, GenericOrderContained<V> order, @Nullable IdentifiedContainer<H> ignoredHandler, String address);
}
