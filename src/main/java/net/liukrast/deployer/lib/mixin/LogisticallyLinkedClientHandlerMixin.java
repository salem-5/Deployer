package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import net.liukrast.deployer.lib.logistics.LogisticallyLinked;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LogisticallyLinkedClientHandler.class)
public class LogisticallyLinkedClientHandlerMixin {
    @Definition(id = "mainHandItem", local = @Local(type = ItemStack.class, name = "mainHandItem"))
    @Definition(id = "getItem", method = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    @Definition(id = "LogisticallyLinkedBlockItem", type = LogisticallyLinkedBlockItem.class)
    @Expression("mainHandItem.getItem() instanceof LogisticallyLinkedBlockItem")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tick(boolean original, @Local(name = "mainHandItem") ItemStack mainHandItem) {
        return original || mainHandItem.getItem() instanceof LogisticallyLinked;
    }
}
