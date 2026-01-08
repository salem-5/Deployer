package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PackageItem.class)
public class PackageItemMixin {
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private <E> boolean init(List<E> instance, E e) {
        return !(PackageItem.class.cast(this) instanceof GenericPackageItem pack) || pack.cardboard;
    }

    @ModifyReturnValue(method = "getOrderId", at = @At("RETURN"))
    private static int getOrderId(int original, @Local(argsOnly = true, name = "arg0") ItemStack box) {
        if(!(box.getItem() instanceof GenericPackageItem generic)) return original;
        var data = generic.getType().packageHandler().packageOrderData();
        if(!box.has(data)) return -1;
        //noinspection DataFlowIssue
        return box.get(data).orderId();
    }
}
