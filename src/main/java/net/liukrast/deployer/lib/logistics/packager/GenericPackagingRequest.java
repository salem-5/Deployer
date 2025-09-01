package net.liukrast.deployer.lib.logistics.packager;

import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;

public record GenericPackagingRequest<V>(
        V item,
        MutableInt count,
        String address,
        int linkIndex,
        MutableBoolean finalLink,
        MutableInt packageCounter,
        int orderId,
        @Nullable GenericOrderContained<V> context
) {

    public static <V> GenericPackagingRequest<V> create(
            V stack,
            int count,
            String address,
            int linkIndex,
            MutableBoolean finalLink,
            int packageCount,
            int orderId,
            @Nullable GenericOrderContained<V> context
    ) {
        return new GenericPackagingRequest<>(stack, new MutableInt(count), address, linkIndex, finalLink, new MutableInt(packageCount), orderId, context);
    }

    public int getCount() {
        return count.intValue();
    }

    public void subtract(int toSubtract) {
        count.setValue(getCount() - toSubtract);
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }
}
