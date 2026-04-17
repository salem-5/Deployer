package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.RenderFilterSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FilteringRenderer.class)
public class FilteringRendererMixin {

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;isActive()Z"))
    private static boolean tick(boolean original, @Local(name = "b") BlockEntityBehaviour b) {
        if(!original) return false;
        if(!(b instanceof AbstractPanelBehaviour)) return true;
        return b instanceof RenderFilterSlot rfs && rfs.renderSlot();
    }
}
