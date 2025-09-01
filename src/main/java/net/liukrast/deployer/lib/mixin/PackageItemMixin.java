package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PackageItem.class)
public class PackageItemMixin {
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private <E> boolean init(List<E> instance, E e) {
        return !(PackageItem.class.cast(this) instanceof GenericPackageItem pack) || pack.cardboard;
    }
}
