package net.liukrast.fluid.content;

import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.fluid.registry.RegisterStockInventoryTypes;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class FluidInventorySummary extends AbstractInventorySummary<Fluid, FluidStack> {

    public static final Supplier<FluidInventorySummary> EMPTY = FluidInventorySummary::new;

    public FluidInventorySummary() {
        super(RegisterStockInventoryTypes.FLUID.get());
    }

    @Override
    public int getCount(FluidStack stack) {
        return stack.getAmount();
    }

    @Override
    public Fluid keyFrom(FluidStack stack) {
        return stack.getFluid();
    }

    @Override
    public boolean isSameKeySameComponents(FluidStack stack1, FluidStack stack2) {
        return FluidStack.isSameFluidSameComponents(stack1, stack2);
    }

    @Override
    public void setCount(FluidStack stack, int count) {
        stack.setAmount(count);
    }

    @Override
    public FluidStack copy(FluidStack stack) {
        return stack.copy();
    }
}
