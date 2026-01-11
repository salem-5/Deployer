package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlock;
import net.liukrast.deployer.lib.logistics.LogisticallyLinked;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StockTickerBlock.class)
public class StockTickerBlockMixin {
    @Definition(id = "stack", local = @Local(type = ItemStack.class, argsOnly = true))
    @Definition(id = "getItem", method = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    @Definition(id = "LogisticallyLinkedBlockItem", type = LogisticallyLinkedBlockItem.class)
    @Expression("stack.getItem() instanceof LogisticallyLinkedBlockItem")
    @ModifyExpressionValue(method = "useItemOn", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean useItemOn(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original || stack.getItem() instanceof LogisticallyLinked;
    }
}
