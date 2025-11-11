package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class GenericPackageItem extends PackageItem {
    public final boolean cardboard;
    private final Supplier<StockInventoryType<?,?,?>> type;
    public GenericPackageItem(Properties properties, CustomPackageStyle style, Supplier<StockInventoryType<?,?,?>> type) {
        this(properties, style, false, type);
    }

    public GenericPackageItem(Properties properties, CustomPackageStyle style, boolean cardboard, Supplier<StockInventoryType<?,?,?>> type) {
        super(properties, style.toOriginal());
        this.cardboard = cardboard;
        this.type = type;
    }

    public StockInventoryType<?,?,?> getType() {
        return type.get();
    }

    public static <K,V,H> void setOrder(StockInventoryType<K,V,H> type, ItemStack box, int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex,
                                        boolean isFinal, @Nullable GenericOrderContained<V> orderContext) {
        GenericPackageOrderData<V> order = new GenericPackageOrderData<>(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, orderContext);
        box.set(type.networkHandler().getComponent(), order);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);
        /*
        if (!stack.has(FluidTestMain.BOTTLE_CONTENTS.get()))
            return;
        SimpleFluidContent contents = getFluidContent(stack);
        tooltipComponents.add(contents.copy().getHoverName()
                .copy()
                .append(" x")
                .append(String.valueOf(contents.getAmount()))
                .withStyle(ChatFormatting.GRAY));*/
    }
}
