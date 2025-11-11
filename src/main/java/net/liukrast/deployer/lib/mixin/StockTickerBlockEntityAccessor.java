package net.liukrast.deployer.lib.mixin;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StockTickerBlockEntity.class)
public interface StockTickerBlockEntityAccessor {

    @Accessor("ticksSinceLastUpdate")
    void setTicksSinceLastUpdate(int ticks);
}
