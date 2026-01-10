package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerItemHandler;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.deployer.lib.logistics.packager.PackageCanBeHandled;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagerItemHandler.class)
public class PackagerItemHandlerMixin {
    @Shadow
    private PackagerBlockEntity blockEntity;

    @WrapOperation(
            method = "insertItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;unwrapBox(Lnet/minecraft/world/item/ItemStack;Z)Z",
                    ordinal = 0)
    )
    private boolean insertItem(PackagerBlockEntity instance, ItemStack box, boolean simulate, Operation<Boolean> original) {
        if(blockEntity instanceof AbstractPackagerBlockEntity<?,?,?> aPBE) {
            if(!(box.getItem() instanceof GenericPackageItem generic)) return true;
            var type = aPBE.getStockType();
            if(generic.getType() != type) return true;
        } else if(box.getItem() instanceof GenericPackageItem) return true;

        return original.call(instance, box, simulate);
    }

    @ModifyReturnValue(method = "isItemValid", at = @At("RETURN"))
    private boolean isItemValid(boolean original, @Local(argsOnly = true, name = "arg2") ItemStack stack) {
        if(stack.getItem() instanceof PackageCanBeHandled pCBH) return pCBH.canBeHandled(stack);
        return original;
    }
}
