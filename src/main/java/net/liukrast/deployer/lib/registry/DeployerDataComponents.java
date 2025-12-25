package net.liukrast.deployer.lib.registry;

import net.liukrast.deployer.lib.DeployerConstants;
import net.liukrast.deployer.lib.logistics.OrderStockTypeData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeployerDataComponents {
    private DeployerDataComponents() {}

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, DeployerConstants.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OrderStockTypeData>> ORDER_STOCK_TYPE_DATA = DATA_COMPONENTS.register("order_stock_type_data", () -> DataComponentType.<OrderStockTypeData>builder()
            .persistent(OrderStockTypeData.CODEC)
            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
