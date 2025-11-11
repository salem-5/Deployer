package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlock;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagerBlock.class)
public class PackagerBlockMixin {
    @ModifyExpressionValue(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getCapability(Lnet/neoforged/neoforge/capabilities/BlockCapability;Lnet/minecraft/core/BlockPos;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object getStateForPlacement(Object original, @Local BlockEntity be) {
        if(PackagerBlock.class.cast(this) instanceof AbstractPackagerBlock apb) return apb.isSideValid(be) ? new Object() : null;
        return original;
    }

    @ModifyExpressionValue(method = "lambda$useItemOn$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;unwrapBox(Lnet/minecraft/world/item/ItemStack;Z)Z", ordinal = 0))
    private static boolean lambda$useItemOn$0(boolean original, @Local(argsOnly = true) PackagerBlockEntity blockEntity, @Local(argsOnly = true) ItemStack box) {
        if(blockEntity instanceof AbstractPackagerBlockEntity<?,?,?> aPBE) {
            if(!(box.getItem() instanceof GenericPackageItem generic)) return true;
            var type = aPBE.getStockType();
            if(generic.getType() != type) return true;
        } else {
            if(box.getItem() instanceof GenericPackageItem) return true;
        }
        return original;
    }
}
