package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.OrderStockTypeData;
import net.liukrast.deployer.lib.registry.DeployerDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RepackagerBlockEntity.class)
public class RepackagerBlockEntityMixin {
    @Unique
    int deployer$attemptToRepackage$local$typeIndex;

    @Inject(method = "attemptToRepackage", at = @At("HEAD"))
    private void attemptToRepackage(IItemHandler targetInv, CallbackInfo ci) {
        deployer$attemptToRepackage$local$typeIndex = 0;
    }

    @Inject(method = "attemptToRepackage", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/packager/repackager/PackageRepackageHelper;addPackageFragment(Lnet/minecraft/world/item/ItemStack;)I"))
    private void attemptToRepackage(IItemHandler targetInv, CallbackInfo ci, @Local(name = "extracted") ItemStack extracted) {
        deployer$attemptToRepackage$local$typeIndex = extracted.getOrDefault(DeployerDataComponents.ORDER_STOCK_TYPE_DATA, OrderStockTypeData.EMPTY).index();
    }


    @Definition(id = "getOrderId", method = "Lcom/simibubi/create/content/logistics/box/PackageItem;getOrderId(Lnet/minecraft/world/item/ItemStack;)I")
    @Definition(id = "extracted", local = @Local(type = ItemStack.class, name = "extracted"))
    @Definition(id = "completedOrderId", local = @Local(type = int.class, name = "completedOrderId"))
    @Expression("getOrderId(extracted) != completedOrderId")
    @ModifyExpressionValue(method = "attemptToRepackage", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean attemptToRepackage(boolean original, @Local(name = "extracted") ItemStack extracted) {
        int index = extracted.getOrDefault(DeployerDataComponents.ORDER_STOCK_TYPE_DATA, OrderStockTypeData.EMPTY).index();
        return index != deployer$attemptToRepackage$local$typeIndex || original;
    }
}
