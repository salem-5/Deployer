package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.BigItemStack;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.deployer.lib.logistics.stockTicker.LogisticalStockGenericResponsePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractInventorySummary<K, V> {
    private final Map<K, List<V>> items = new IdentityHashMap<>();
    private List<V> stacksByCount;
    private int totalCount;
    public int contributingLinks;
    private final StockInventoryType<K,V,?> type;

    public AbstractInventorySummary(StockInventoryType<K,V,?> type) {
        this.type = type;
    }

    public abstract int getCount(V stack);
    public abstract K keyFrom(V stack);
    public abstract boolean isSameKeySameComponents(V stack1, V stack2);
    public abstract void setCount(V stack, int count);
    public abstract V copy(V stack);
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void add(AbstractInventorySummary<K, V> summary) {
        summary.items.forEach((t,k) -> k.forEach(this::add));
    }

    public void add(V stack) {
        add(stack, getCount(stack));
    }

    public Map<K, List<V>> getItemMap() {
        return items;
    }

    public void addAllStacks(List<V> list) {
        for(V stack : list) add(stack, getCount(stack));
    }

    public AbstractInventorySummary<K, V> copy() {
        AbstractInventorySummary<K, V> summary = type.networkHandler().create();
        items.forEach((i, list) -> list.forEach(summary::add));
        return summary;
    }

    public void add(V stack, int count) {
        if(count == 0 || type.valueHandler().isEmpty(stack)) return;
        if(totalCount < BigItemStack.INF) totalCount+=count; //TODO: Allow users to set the limit for this summary

        List<V> stacks = items.computeIfAbsent(keyFrom(stack),$ -> Lists.newArrayList());
        for(V existing : stacks) {
            if(isSameKeySameComponents(existing, stack)) {
                if(getCount(existing) < BigItemStack.INF)
                    setCount(existing, getCount(existing) + count);
                return;
            }
        }

        stacks.add(copy(stack));
    }

    public boolean erase(V stack) {
        List<V> stacks = items.get(keyFrom(stack));
        if(stacks == null) return false;
        for(Iterator<V> iterator = stacks.iterator(); iterator.hasNext();) {
            V existing = iterator.next();
            if(!isSameKeySameComponents(existing, stack)) continue;
            totalCount -= getCount(existing);
            iterator.remove();
            return true;
        }
        return false;
    }

    public int getCountOf(V stack) {
        List<V> list = items.get(keyFrom(stack));
        if(list == null) return 0;
        for(V entry : list) if(isSameKeySameComponents(entry, stack)) return getCount(entry);
        return 0;
    }

    public int getTotalOfMatching(Predicate<V> filter) {
        MutableInt sum = new MutableInt();
        items.forEach(($, list) -> {
            for(V stack : list) if(filter.test(stack)) sum.add(getCount(stack));
        });
        return sum.getValue();
    }

    public List<V> getStacks() {
        if(stacksByCount == null) {
            List<V> stacks = new ArrayList<>();
            items.forEach((i, list) -> stacks.addAll(list));
            return stacks;
        }
        return stacksByCount;
    }

    public List<V> getStacksByCount() {
        if(stacksByCount == null) {
            stacksByCount = new ArrayList<>();
            items.forEach((i, list) -> stacksByCount.addAll(list));
            stacksByCount.sort(Comparator.comparingInt(this::getCount));
        }
        return stacksByCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void divideAndSendTo(ServerPlayer player, BlockPos pos) {
        List<V> stacks = getStacksByCount();
        int remaining = stacks.size();

        List<V> currentList = null;

        if(stacks.isEmpty()) //TODO: Check later
            CatnipServices.NETWORK.sendToClient(player, new LogisticalStockGenericResponsePacket(true, pos, type, Collections.emptyList()));

        for(V entry : stacks) {
            if(currentList == null)
                currentList = new ArrayList<>(Math.min(100, remaining));

            currentList.add(entry);
            remaining--;

            if(remaining == 0)
                break;
            if(currentList.size() < 100)
                continue;
            //TODO: Check later
            CatnipServices.NETWORK.sendToClient(player, new LogisticalStockGenericResponsePacket(false, pos, type, currentList));
            currentList = null;
        }

        if (currentList != null)
            CatnipServices.NETWORK.sendToClient(player, new LogisticalStockGenericResponsePacket(true, pos, type, currentList));
    }
}
