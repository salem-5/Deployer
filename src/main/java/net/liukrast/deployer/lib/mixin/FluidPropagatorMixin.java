package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.FluidPropagator;
import net.liukrast.deployer.lib.DeployerConfig;
import net.liukrast.deployer.lib.registry.DeployerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidPropagator.class)
public class FluidPropagatorMixin {
    @ModifyReturnValue(method = "hasFluidCapability", at = @At("RETURN"))
    private static boolean hasFluidCapability(boolean original, @Local(argsOnly = true, name = "arg0") BlockGetter world, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Direction side) {
        BlockState state = world.getBlockState(pos);
        if(!DeployerConfig.Server.BLOCK_CAPABILITY_FIX.getAsBoolean() && !state.is(DeployerTags.Blocks.OVERRIDE_BLOCK_CAPABILITY_FIX)) return original;
        if(!(world instanceof Level level)) return original;
        return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side) != null;
    }
}
