package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlock;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagerRenderer.class)
public abstract class PackagerRendererMixin {
    @Shadow
    public static boolean isHatchOpen(PackagerBlockEntity be) {
        throw new AssertionError("Mixin injection failed");
    }

    @ModifyReturnValue(method = "getTrayModel", at = @At("RETURN"))
    private static PartialModel getTrayModel(PartialModel original, @Local(argsOnly = true) BlockState blockState) {
        if(!(blockState.getBlock() instanceof AbstractPackagerBlock apb)) return original;
        return apb.getTrayModel(blockState, original);
    }

    @ModifyReturnValue(method = "getHatchModel", at = @At("RETURN"))
    private static PartialModel getHatchModel(PartialModel original, @Local(argsOnly = true) PackagerBlockEntity be) {
        if(!(be instanceof AbstractPackagerBlockEntity<?,?,?> apbe)) return original;
        return apbe.getHatchModel(isHatchOpen(be), original);
    }
}
