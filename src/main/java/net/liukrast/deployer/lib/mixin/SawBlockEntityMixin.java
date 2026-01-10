package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.PackageCanBeHandled;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SawBlockEntity.class)
public class SawBlockEntityMixin {
    @ModifyExpressionValue(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/box/PackageItem;isPackage(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean applyRecipes(boolean original, @Local(name = "input") ItemStack input) {
        if(input.getItem() instanceof PackageCanBeHandled pCBH) return pCBH.canBeHandled(input);
        return original;
    }
}
