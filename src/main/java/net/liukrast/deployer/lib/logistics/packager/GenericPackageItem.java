package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class GenericPackageItem extends PackageItem {
    public final boolean cardboard;
    private final Supplier<StockInventoryType<?,?,?>> type;

    private final String descriptionId;

    public GenericPackageItem(Properties properties, CustomPackageStyle style, Supplier<StockInventoryType<?,?,?>> type) {
        this(properties, style, false, type, null);
    }

    public GenericPackageItem(Properties properties, CustomPackageStyle style, Supplier<StockInventoryType<?,?,?>> type, String descriptionId) {
        this(properties, style, false, type, descriptionId);
    }

    public GenericPackageItem(Properties properties, CustomPackageStyle style, boolean cardboard, Supplier<StockInventoryType<?,?,?>> type, String descriptionId) {
        super(properties, style.toOriginal());
        this.cardboard = cardboard;
        this.type = type;
        this.descriptionId = descriptionId;
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
        for(StockInventoryType<?,?,?> type : DeployerRegistries.STOCK_INVENTORY) {
            handleType(type, stack, tooltipContext, tooltipComponents, tooltipFlag);
        }
    }

    private static <K,V,H> void handleType(StockInventoryType<K,V,H> type, ItemStack stack, TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var content = type.packageHandler().getContents(stack);
        if(content == null) return;
        type.packageHandler().appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag, content);
    }

    @Override
    public @NotNull String getDescriptionId() {
        if(descriptionId == null) return super.getDescriptionId();
        return descriptionId;
    }

}
