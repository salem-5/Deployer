package net.liukrast.deployer.lib.mixinExtensions;

import com.simibubi.create.content.logistics.BigItemStack;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.minecraft.util.RandomSource;

import java.util.List;

public interface PRHExtension {
    <K, V, H> List<BigItemStack> deployer$repack(StockInventoryType<K, V, H> type, int orderId, RandomSource r);
    // Coming in future version
    //<K, V, H> List<V> deployer$repackBasedOnRecipes(AbstractInventorySummary<K, V> summary, GenericOrderContained<V> order, String address, RandomSource r);
    <K,V,H> boolean deployer$isOrderComplete(StockInventoryType<K,V,H> type, int orderId);
}
