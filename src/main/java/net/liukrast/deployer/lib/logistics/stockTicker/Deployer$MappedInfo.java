package net.liukrast.deployer.lib.logistics.stockTicker;

import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class Deployer$MappedInfo {
    private final Map<StockInventoryType<?,?,?>, List<?>> newlyReceivedStockSnapshot = new HashMap<>();
    private final Map<StockInventoryType<?,?,?>, AbstractInventorySummary<?,?>> lastClientsideStockSnapshotAsSummary = new HashMap<>();
    private final Map<StockInventoryType<?,?,?>, List<List<?>>> lastClientsideStockSnapshot = new HashMap<>();
    private final Map<StockInventoryType<?,?,?>, List<ItemStack>> categories = new HashMap<>();
    private final Map<StockInventoryType<?,?,?>, Integer> activeLinks = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> List<T> getNewlyReceivedStockSnapshot(StockInventoryType<?,T,?> type) {
        return (List<T>) newlyReceivedStockSnapshot.get(type);
    }

    public <T> void setNewlyReceivedStockSnapshot(StockInventoryType<?,T,?> type, List<T> list) {
        newlyReceivedStockSnapshot.put(type, list);
    }

    @SuppressWarnings("unchecked")
    public <K,V> AbstractInventorySummary<K, V> getLastClientsideStockSnapshotAsSummary(StockInventoryType<K,V,?> type) {
        return (AbstractInventorySummary<K, V>) lastClientsideStockSnapshotAsSummary.get(type);
    }

    public <K,V> void setLastClientsideStockSnapshotAsSummary(StockInventoryType<K,V,?> type, AbstractInventorySummary<K, V> summary) {
        lastClientsideStockSnapshotAsSummary.put(type, summary);
    }

    @SuppressWarnings("unchecked")
    public <T> List<List<T>> getLastClientsideStockSnapshot(StockInventoryType<?,T,?> type) {
        return (List<List<T>>) (List<?>) lastClientsideStockSnapshot.get(type);
    }

    @SuppressWarnings("unchecked")
    public <T> void setLastClientsideStockSnapshot(StockInventoryType<?,T,?> type, List<List<T>> list) {
        lastClientsideStockSnapshot.put(type, (List<List<?>>) (List<?>) list);
    }

    /**
     * Custom categories are not present yet
     * */
    @Deprecated
    public List<ItemStack> getCategories(StockInventoryType<?,?,?> type) {
        return categories.get(type);
    }

    /**
     * Custom categories are not present yet
     * */
    @Deprecated
    public List<ItemStack> setCategories(StockInventoryType<?,?,?> type, List<ItemStack> list) {
        categories.put(type, list);
        return list;
    }

    public int getActiveLinks(StockInventoryType<?,?,?> type) {
        return activeLinks.getOrDefault(type, 0);
    }

    public void setActiveLinks(StockInventoryType<?,?,?> type, int value) {
        activeLinks.put(type, value);
    }
}
