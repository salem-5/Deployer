package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PackagerBlockEntity.class)
public class PackagerBlockEntityMixin {
    @WrapWithCondition(method = "addBehaviours", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private <E> boolean addBehaviours(List<E> instance, E e) {
        var i = PackagerBlockEntity.class.cast(this);
        return !(i instanceof AbstractPackagerBlockEntity<?,?,?>);
    }

    @WrapOperation(method = {"lazyTick","activate"}, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;attemptToSend(Ljava/util/List;)V"))
    private void lazyTick(PackagerBlockEntity instance, List<PackagingRequest> extracted, Operation<Void> original) {
        if(instance instanceof AbstractPackagerBlockEntity<?,?,?> pack) {
            pack.attemptToSendSpecial(null);
            return;
        }
        original.call(instance, extracted);
    }
}
