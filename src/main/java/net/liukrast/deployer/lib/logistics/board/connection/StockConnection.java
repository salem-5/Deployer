package net.liukrast.deployer.lib.logistics.board.connection;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.liukrast.deployer.lib.logistics.board.GenericConnections;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StockConnection<V> {
    private final StockInventoryType<?,V,?> type;
    private final V item;

    public static StockConnection<ItemStack> itemStack(ItemStack stack) {
        return new StockConnection<>(null, stack);
    }

    @Contract("null, _ -> fail; !null, _ -> new")
    public static <V> StockConnection<V> of(StockInventoryType<?, V, ?> type, V item) {
        if(type == null && !(item instanceof ItemStack)) throw new IllegalStateException("type can only be null when item is an ItemStack, got: " + item.getClass().getSimpleName());
        return new StockConnection<>(type, item);
    }

    private StockConnection(StockInventoryType<?, V, ?> type, V item) {
        this.type = type;
        this.item = item;
    }

    public void register(Map<UUID, Map<ItemStack, FactoryPanelBehaviour.ItemStackConnections>> consolidated, Map<StockInventoryType<?,?,?>, Map<UUID, Map<?, GenericConnections<?>>>> otherConsolidated, FactoryPanelBehaviour source, FactoryPanelConnection connection) {
        if(type == null) {
            Map<ItemStack, FactoryPanelBehaviour.ItemStackConnections> networkItemCounts = consolidated.computeIfAbsent(source.network, $ -> new Object2ObjectOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG));
            networkItemCounts.computeIfAbsent((ItemStack) item, $ -> new FactoryPanelBehaviour.ItemStackConnections((ItemStack) item));
            FactoryPanelBehaviour.ItemStackConnections existingConnections = networkItemCounts.get((ItemStack) item);
            existingConnections.add(connection);
            existingConnections.totalAmount += connection.amount;
        } else {
            Map<UUID, Map<?, GenericConnections<?>>> consolidated$1 =
                    otherConsolidated.computeIfAbsent(type, k -> new HashMap<>());
            @SuppressWarnings({"unchecked", "rawtypes"})
            Map<V, GenericConnections<V>> networkItemCounts =
                    (Map<V, GenericConnections<V>>) (Map) consolidated$1.computeIfAbsent(
                            source.network,
                            $ -> new Object2ObjectOpenCustomHashMap<>(
                                    type.valueHandler().hashStrategy()
                            )
                    );
            networkItemCounts.computeIfAbsent(item, $ -> new GenericConnections<>(item));
            GenericConnections<V> existingConnections = networkItemCounts.get(item);
            existingConnections.add(connection);
            existingConnections.totalAmount += connection.amount;
        }
    }

    @Nullable
    public ItemStack getStackValue() {
        if(type == null) return (ItemStack) item;
        return null;
    }
}
