package net.liukrast.deployer.lib.mixin;

import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StockKeeperRequestScreen.CategoryEntry.class)
public interface StockKeeperRequestScreen$CategoryEntryAccessor {
    @Accessor("y")
    void setY(int value);
    @Accessor("hidden")
    boolean getHidden();
    @Accessor("y")
    int getY();
    @Accessor("name")
    String getName();
}
