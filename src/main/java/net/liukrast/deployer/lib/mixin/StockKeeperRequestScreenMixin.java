package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.stockTicker.*;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.platform.services.NetworkHelper;
import net.liukrast.deployer.lib.DeployerConstants;
import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderRequestPacket;
import net.liukrast.deployer.lib.mixinExtensions.STBEExtension;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Debug(export = true)
@Mixin(StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenMixin extends AbstractSimiContainerScreen<StockKeeperRequestMenu> {
    @Shadow
    StockTickerBlockEntity blockEntity;
    @Shadow
    public List<StockKeeperRequestScreen.CategoryEntry> categories;

    @Unique
    int deployer$cols;

    @Shadow
    protected abstract void clampScrollBar();

    @Shadow
    protected abstract void updateCraftableAmounts();

    @Shadow
    protected abstract void revalidateOrders();

    @Shadow
    int itemsX;
    @Shadow
    int itemsY;
    @Shadow
    int windowHeight;

    @Unique
    private int deployer$colWidth = 20;
    @Unique
    private int deployer$rowHeight = 20;

    @Shadow
    protected abstract void refreshSearchResults(boolean scrollBackUp);

    @Shadow
    int orderY;
    @Shadow
    private boolean canRequestCraftingPackage;
    @Shadow
    public AddressEditBox addressBox;
    @Shadow
    boolean encodeRequester;

    @Shadow
    public EditBox searchBox;

    @Shadow
    public abstract boolean isSchematicListMode();

    @Shadow
    @Final
    Couple<Integer> noneHovered;
    @Shadow
    int windowWidth;
    @Shadow
    public List<CraftableBigItemStack> recipesToOrder;
    @Shadow
    public LerpedFloat itemScroll;
    @Shadow
    @Final
    private int rowHeight;
    @Unique
    private static final ResourceLocation deployer$TEXTURE = DeployerConstants.id("textures/gui/stock_keeper_tabs.png");
    @Unique
    private static final Component deployer$DEFAULT_ICON_TITLE = Component.translatable("stock_inventory_type.items");
    @Unique
    private StockInventoryType<?, ?, ?> deployer$selected = null;
    @Unique
    private final Map<StockInventoryType<?, ?, ?>, List<List<?>>> deployer$displayedItems = DeployerRegistries.STOCK_INVENTORY.stream().collect(Collectors.toMap(
            type -> type,
            type -> new ArrayList<>()
    ));
    @Unique
    private final Map<StockInventoryType<?, ?, ?>, List<List<?>>> deployer$currentItemSource = new HashMap<>();
    @Unique
    private final Map<StockInventoryType<?, ?, ?>, AbstractInventorySummary<?, ?>> deployer$forcedEntries = DeployerRegistries.STOCK_INVENTORY.stream().collect(Collectors.toMap(
            type -> type,
            type -> type.networkHandler().create()
    ));
    @Unique
    private final Map<StockInventoryType<?, ?, ?>, List<?>> deployer$itemsToOrder = DeployerRegistries.STOCK_INVENTORY.stream().collect(Collectors.toMap(
            type -> type,
            type -> new ArrayList<>()
    ));

    public StockKeeperRequestScreenMixin(StockKeeperRequestMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    /* INIT */
    @ModifyExpressionValue(method = "init", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/CreateLang;translateDirect(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent init(MutableComponent original) {
        if (deployer$selected == null) return original;
        var id = DeployerRegistries.STOCK_INVENTORY.getKey(deployer$selected);
        if (id == null) return original;
        return Component.translatable("stock_inventory_type." + id.getNamespace() + "." + id.getPath() + ".search");
    }

    /* REFRESH SEARCH RESULTS */
    @Inject(method = "refreshSearchResults", at = @At("HEAD"))
    private void refreshSearchResults(boolean scrollBackUp, CallbackInfo ci) {
        if (deployer$selected == null) return;
        deployer$displayedItems.put(deployer$selected, Collections.emptyList());
    }

    @Definition(id = "currentItemSource", field = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;currentItemSource:Ljava/util/List;")
    @Expression("this.currentItemSource == null")
    @ModifyExpressionValue(method = "refreshSearchResults", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean refreshSearchResults$1(boolean original) {
        return deployer$selected == null ? original : deployer$currentItemSource.get(deployer$selected) == null;
    }

    @Inject(method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Ljava/lang/String;isBlank()Z"), cancellable = true)
    private void refreshSearchResults(boolean scrollBackUp, CallbackInfo ci, @Local(name = "valueWithPrefix") String valueWithPrefix, @Local(name = "anyItemsInCategory") LocalBooleanRef anyItemsInCategory) {
        if (deployer$selected == null) return;
        var currentItemSource = deployer$currentItemSource.get(deployer$selected);
        if (valueWithPrefix.isBlank()) {
            deployer$displayedItems.put(deployer$selected, new ArrayList<>(currentItemSource));
            int categoryY = 0;
            for (int categoryIndex = 0; categoryIndex < currentItemSource.size(); categoryIndex++) {
                ((StockKeeperRequestScreen$CategoryEntryAccessor) categories.get(categoryIndex)).setY(categoryY);
                List<?> displayedItemsInCategory = deployer$displayedItems.get(deployer$selected).get(categoryIndex);
                if (displayedItemsInCategory.isEmpty()) continue;
                if (categoryIndex < currentItemSource.size() - 1) anyItemsInCategory.set(true);
                categoryY += deployer$rowHeight;
                if (!((StockKeeperRequestScreen$CategoryEntryAccessor) categories.get(categoryIndex)).getHidden())
                    categoryY += (int) (Math.ceil(displayedItemsInCategory.size() / (float) deployer$cols) * deployer$rowHeight);
            }

            if (!anyItemsInCategory.get())
                categories.clear();
            clampScrollBar();
            updateCraftableAmounts();
            ci.cancel();
        }
    }

    @WrapOperation(method = "refreshSearchResults", at = @At(value = "FIELD", ordinal = 2, target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;displayedItems:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
    private void refreshSearchResults(StockKeeperRequestScreen instance, List<List<BigItemStack>> value, Operation<Void> original) {
        if (deployer$selected == null) original.call(instance, value);
        else deployer$displayedItems.put(deployer$selected, new ArrayList<>());
    }

    @WrapOperation(method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void refreshSearchResults(List<List<BigItemStack>> instance, Consumer<List<BigItemStack>> consumer, Operation<Void> original) {
        if (deployer$selected == null) original.call(instance, consumer);
        else
            deployer$currentItemSource.get(deployer$selected).forEach($ -> deployer$displayedItems.get(deployer$selected).add(new ArrayList<>()));
    }

    @Inject(method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void refreshSearchResults$1(boolean scrollBackUp, CallbackInfo ci, @Local(name = "modSearch") boolean modSearch, @Local(name = "tagSearch") boolean tagSearch, @Local(name = "value") String value, @Local(name = "anyItemsInCategory") LocalBooleanRef anyItemsInCategory) {
        if (deployer$selected == null) return;
        deployer$refreshSearchResults$internal(deployer$selected, modSearch, tagSearch, value, anyItemsInCategory);
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$refreshSearchResults$internal(StockInventoryType<K, V, H> stockInventoryType, boolean modSearch, boolean tagSearch, String value, LocalBooleanRef anyItemsInCategory) {
        int categoryY = 0;
        var displayedItems = deployer$displayedItems.get(stockInventoryType);
        var currentItemSource = deployer$currentItemSource.get(stockInventoryType);
        var packageHandler = stockInventoryType.packageHandler();
        for (int categoryIndex = 0; categoryIndex < displayedItems.size(); categoryIndex++) {
            List<V> category = (List<V>) currentItemSource.get(categoryIndex);
            ((StockKeeperRequestScreen$CategoryEntryAccessor) categories.get(categoryIndex)).setY(categoryY);
            if (displayedItems.size() <= categoryIndex) break;

            List<V> displayedItemsInCategory = (List<V>) displayedItems.get(categoryIndex);
            for (V entry : category) {

                if (modSearch) {
                    if (packageHandler.matchesModSearch(entry, value))
                        displayedItemsInCategory.add(entry);
                    continue;
                }

                if (tagSearch) {
                    if (packageHandler.matchesTagSearch(entry, value))
                        displayedItemsInCategory.add(entry);
                    continue;
                }

                if (packageHandler.matchesSearch(entry, value)) {
                    displayedItemsInCategory.add(entry);
                }
            }

            if (displayedItemsInCategory.isEmpty()) continue;
            if (categoryIndex < currentItemSource.size() - 1)
                anyItemsInCategory.set(true);

            categoryY += deployer$rowHeight;

            if (!((StockKeeperRequestScreen$CategoryEntryAccessor) categories.get(categoryIndex)).getHidden())
                categoryY += (int) (Math.ceil(displayedItemsInCategory.size() / (float) deployer$cols) * deployer$rowHeight);
        }
    }

    /* CONTAINER TICK */
    @ModifyVariable(method = "containerTick", at = @At(value = "STORE"), name = "allEmpty")
    private boolean containerTick(boolean allEmpty) {
        if (deployer$selected == null) return allEmpty;
        return deployer$displayedItems.get(deployer$selected).stream().allMatch(List::isEmpty);
    }

    @ModifyExpressionValue(method = "containerTick", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;displayedItems:Ljava/util/List;"))
    private List<List<BigItemStack>> containerTick(List<List<BigItemStack>> original) {
        return deployer$selected == null ? original : Collections.emptyList();
    }

    @ModifyExpressionValue(method = "containerTick", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0))
    private boolean containerTick$1(boolean original) {
        if (deployer$selected == null) return original;
        deployer$containerTick$internal(deployer$selected);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$containerTick$internal(StockInventoryType<K, V, H> type) {
        var forcedEntries = (AbstractInventorySummary<K, V>) deployer$forcedEntries.get(type);
        if (!forcedEntries.isEmpty()) {
            var summary = ((STBEExtension) blockEntity).deployer$getLastClientsideStockSnapshotAsSummary(type);
            for (var stack : forcedEntries.getStacks()) {
                int limitedAmount = -type.valueHandler().getCount(stack) - 1;
                int actualAmount = summary.getCountOf(stack);
                if (actualAmount <= limitedAmount)
                    forcedEntries.erase(stack);
            }
        }
    }

    @ModifyExpressionValue(method = "containerTick", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1))
    private boolean containerTick$2(boolean original) {
        return deployer$selected == null ? original : deployer$itemsToOrder.get(deployer$selected).isEmpty();
    }

    @WrapOperation(method = "containerTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockTickerBlockEntity;getClientStockSnapshot()Ljava/util/List;"))
    private List<List<BigItemStack>> containerTick(StockTickerBlockEntity instance, Operation<List<List<BigItemStack>>> original) {
        return deployer$selected == null ? original.call(instance) : null;
    }

    @Definition(id = "clientStockSnapshot", local = @Local(type = List.class))
    @Definition(id = "currentItemSource", field = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;currentItemSource:Ljava/util/List;")
    @Expression("clientStockSnapshot != this.currentItemSource")
    @ModifyExpressionValue(method = "containerTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean containerTick$3(boolean original) {
        if (deployer$selected == null) return original;
        deployer$containerTick$internal$1(deployer$selected);
        return false;
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$containerTick$internal$1(StockInventoryType<K, V, H> type) {
        List<List<V>> clientStockSnapshot = ((STBEExtension) blockEntity).deployer$getClientStockSnapshot(type);
        if (clientStockSnapshot != (List<?>) deployer$currentItemSource.get(type)) {
            deployer$currentItemSource.put(deployer$selected, (List<List<?>>) (List<?>) clientStockSnapshot);
            refreshSearchResults(false);
            revalidateOrders();
        }
    }

    /* RENDER BACKGROUND */

    /* RENDER BG */
    @Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V", ordinal = 0, shift = At.Shift.AFTER))
    private void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci, @Local(name = "x") int x, @Local(name = "y") int y) {
        if (deployer$selected == null) return;
        if (!deployer$selected.packageHandler().shouldRenderSearchBar())
            graphics.blit(deployer$TEXTURE, x, y + 17, 15, 129, 226, 19);
    }

    @WrapWithCondition(method = "renderBg", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V", ordinal = 7))
    private boolean renderBg(AllGuiTextures instance, GuiGraphics graphics, int x, int y) {
        if (deployer$selected == null) return true;
        return deployer$selected.packageHandler().shouldRenderSearchBar();
    }

    @WrapWithCondition(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private boolean renderBg(EditBox instance, GuiGraphics graphics, int i, int j, float v) {
        if (deployer$selected == null) return true;
        return deployer$selected.packageHandler().shouldRenderSearchBar();
    }

    @ModifyExpressionValue(method = "renderBg", at = @At(value = "INVOKE", target = "Ljava/lang/String;isBlank()Z"))
    private boolean renderBg(boolean original) {
        if (deployer$selected == null) return original;
        return original && deployer$selected.packageHandler().shouldRenderSearchBar();
    }

    /**
     * Reason: Remove items from rendering when you're in a different category (e.g., fluids)
     * */
    @ModifyExpressionValue(method = "renderBg", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;displayedItems:Ljava/util/List;"))
    private List<List<BigItemStack>> renderBg(List<List<BigItemStack>> original) {
        return deployer$selected == null ? original : Collections.emptyList();
    }

    @Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V", ordinal = 2, shift = At.Shift.AFTER))
    private void renderBg$1(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci, @Local(name = "x") int x, @Local(name = "y") int y) {
        if (deployer$selected == null) return;
        graphics.blit(deployer$TEXTURE, x, y, 15, 80, 226, 37);
    }

    @SuppressWarnings({"UnresolvedLocalCapture"})
    @Inject(method = "renderBg", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;isAdmin:Z", opcode = Opcodes.GETFIELD))
    private void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci, @Local PoseStack ms, @Local(ordinal = 2) int x, @Local(ordinal = 3) int y, @Local(ordinal = 1) float currentScroll, @Local Couple<Integer> hoveredSlot) {
        if (deployer$selected == null) return;
        deployer$renderBg$internal(graphics, partialTicks, mouseX, mouseY, ms, currentScroll, x, y, hoveredSlot, deployer$selected);
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$renderBg$internal(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, PoseStack ms, float currentScroll, int x, int y, Couple<Integer> hoveredSlot, StockInventoryType<K, V, H> stockInventoryType) {
        List<List<V>> displayedItems = (List<List<V>>) (List<?>) deployer$displayedItems.get(stockInventoryType);
        var itemsToOrder = (List<V>) deployer$itemsToOrder.get(stockInventoryType);
        var forcedEntries = (AbstractInventorySummary<K, V>) deployer$forcedEntries.get(stockInventoryType);
        for (int categoryIndex = 0; categoryIndex < displayedItems.size(); categoryIndex++) {
            List<V> category = displayedItems.get(categoryIndex);
            StockKeeperRequestScreen$CategoryEntryAccessor categoryEntry = (StockKeeperRequestScreen$CategoryEntryAccessor) (categories.isEmpty() ? null : categories.get(categoryIndex));
            int categoryY = categories.isEmpty() ? 0 : categoryEntry.getY();
            if (category.isEmpty())
                continue;

            if (!categories.isEmpty()) {
                (categoryEntry.getHidden() ? AllGuiTextures.STOCK_KEEPER_CATEGORY_HIDDEN
                        : AllGuiTextures.STOCK_KEEPER_CATEGORY_SHOWN).render(graphics, itemsX, itemsY + categoryY + 6);
                graphics.drawString(font, categoryEntry.getName(), itemsX + 10, itemsY + categoryY + 8, 0x4A2D31, false);
                graphics.drawString(font, categoryEntry.getName(), itemsX + 9, itemsY + categoryY + 7, 0xF8F8EC, false);
                if (categoryEntry.getHidden())
                    continue;
            }
            var data = new StockInventoryType.CategoryRenderData(x, y, itemsX, itemsY, categoryY, deployer$rowHeight, deployer$colWidth, deployer$cols, categories, currentScroll, windowHeight, hoveredSlot, categoryIndex, ms, font);
            stockInventoryType.packageHandler().renderCategory(graphics, partialTicks, mouseX, mouseY, category, itemsToOrder, forcedEntries, data);
        }
    }

    @ModifyExpressionValue(method = "renderBg", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private int renderBg(int original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) float partialTicks, @Local(argsOnly = true, ordinal = 0) int mouseX, @Local(argsOnly = true, ordinal = 1) int mouseY, @Local(ordinal = 2) int x, @Local(ordinal = 3) int y, @Local PoseStack ms, @Local Couple<Integer> hoveredSlot) {
        if (deployer$selected == null) return original;
        deployer$renderBg$internal(deployer$selected, graphics, partialTicks, mouseX, mouseY, x, y, ms, hoveredSlot);
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$renderBg$internal(StockInventoryType<K, V, H> type, GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, int x, int y, PoseStack ms, Couple<Integer> hoveredSlot) {
        type.packageHandler().renderOrderedItems(graphics, partialTicks, mouseX, mouseY, (List<V>) deployer$itemsToOrder.get(type), (AbstractInventorySummary<K, V>) deployer$forcedEntries.get(type),
                new StockInventoryType.OrderRenderData(x, y, itemsX, itemsY, deployer$rowHeight, deployer$colWidth, orderY, deployer$cols, hoveredSlot, ms)
        );
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void renderBg$1(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (DeployerRegistries.STOCK_INVENTORY.size() == 0) return;
        int x = getGuiLeft();
        int y = getGuiTop();
        graphics.blit(deployer$TEXTURE, x - 8, y + 20, 0, deployer$selected == null ? 20 : 0, 20, 20);
        graphics.renderItem(PackageStyles.STANDARD_BOXES.getFirst().getDefaultInstance(), x - 7, y + 21);
        if (mouseX > x - 8 && mouseX < x + 12 && mouseY > y + 20 && mouseY < y + 40)
            graphics.renderTooltip(font, deployer$DEFAULT_ICON_TITLE, mouseX, mouseY);
        int i = 0;
        for (StockInventoryType<?, ?, ?> type : DeployerRegistries.STOCK_INVENTORY) {
            var id = DeployerRegistries.STOCK_INVENTORY.getKey(type);
            graphics.blit(deployer$TEXTURE, x - 8, y + 40 + i * 20, 0, deployer$selected == type ? 20 : 0, 20, 20);
            graphics.renderItem(type.getIcon(), x - 7, y + 41 + i * 20);
            assert id != null;
            if (mouseX > x - 8 && mouseX < x + 12 && mouseY > y + 40 + i * 20 && mouseY < y + 60 + i * 20)
                graphics.renderTooltip(font, Component.translatable("stock_inventory_type." + id.getNamespace() + "." + id.getPath()), mouseX, mouseY);
            i++;
        }
    }

    /* RENDER FOREGROUND */

    @Definition(id = "hoveredSlot", local = @Local(type = Couple.class))
    @Definition(id = "noneHovered", field = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;noneHovered:Lnet/createmod/catnip/data/Couple;")
    @Expression("hoveredSlot != this.noneHovered")
    @ModifyExpressionValue(method = "renderForeground", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean renderForeground(boolean original, @Local(name = "hoveredSlot") Couple<Integer> hoveredSlot, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true, ordinal = 0) int mouseX, @Local(argsOnly = true, ordinal = 1) int mouseY, @Local(argsOnly = true) float partialTicks) {
        if (deployer$selected == null || hoveredSlot.getFirst() == -2) return original;
        if (original)
            deployer$renderForeground$internal(deployer$selected, hoveredSlot, graphics, mouseX, mouseY, partialTicks);
        return false;
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$renderForeground$internal(StockInventoryType<K, V, H> type, Couple<Integer> hoveredSlot, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int slot = hoveredSlot.getSecond();
        boolean orderHovered = hoveredSlot.getFirst() == -1;
        V entry = orderHovered ? (V) deployer$itemsToOrder.get(type).get(slot) : (V) deployer$displayedItems.get(type).get(hoveredSlot.getFirst()).get(slot);
        type.packageHandler().renderTooltip(graphics, mouseX, mouseY, partialTicks, entry, font, orderHovered);
    }

    /* RENDER ITEM ENTRY */
    /* DRAW ITEM COUNT */

    /* GET ORDER FOR V */
    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> V deployer$getOrderForV(StockInventoryType<K, V, H> type, V stack) {
        for (V entry : (List<V>) deployer$itemsToOrder.get(type))
            if (type.valueHandler().equalsIgnoreCount(entry, stack))
                return entry;
        return null;
    }

    /* REVALIDATE ORDERS */
    @Inject(method = "revalidateOrders", at = @At("HEAD"))
    private void revalidateOrders(CallbackInfo ci) {
        for (StockInventoryType<?, ?, ?> type : DeployerRegistries.STOCK_INVENTORY)
            deployer$revalidateOrders$internal(type);
    }

    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    @Unique
    private <K, V, H> void deployer$revalidateOrders$internal(StockInventoryType<K, V, H> type) {
        Set<V> invalid = new HashSet<>((Collection<? extends V>) deployer$itemsToOrder.get(type));
        AbstractInventorySummary<K, V> summary = ((STBEExtension) blockEntity).deployer$getLastClientsideStockSnapshotAsSummary(type);
        if (deployer$currentItemSource.get(type) == null || summary == null) {
            deployer$itemsToOrder.get(type).removeAll(invalid);
            return;
        }
        for (V entry : (Collection<V>) deployer$itemsToOrder.get(type)) {
            type.valueHandler().setCount(entry, Math.min(summary.getCountOf(entry), type.valueHandler().getCount(entry)));
            if (type.valueHandler().getCount(entry) > 0)
                invalid.remove(entry);
        }

        deployer$itemsToOrder.get(type).removeAll(invalid);
    }

    /* GET HOVERED SLOT */
    @Deprecated
    @Definition(id = "size", method = "Ljava/util/List;size()I")
    @Definition(id = "itemsToOrder", field = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;itemsToOrder:Ljava/util/List;")
    @Expression("this.itemsToOrder.size()")
    @ModifyExpressionValue(method = "getHoveredSlot", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int getHoveredSlot(int original) {
        return deployer$selected == null ? original : deployer$itemsToOrder.get(deployer$selected).size();
    }

    @Deprecated
    @Definition(id = "displayedItems", field = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;displayedItems:Ljava/util/List;")
    @Definition(id = "size", method = "Ljava/util/List;size()I")
    @Expression("this.displayedItems.size()")
    @ModifyExpressionValue(method = "getHoveredSlot", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int getHoveredSlot$1(int original) {
        return deployer$selected == null ? original : deployer$displayedItems.get(deployer$selected).size();
    }

    @Deprecated
    @WrapOperation(method = "getHoveredSlot", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 1))
    private Object getHoveredSlot$2(List<Object> instance, int i, Operation<Object> original, @Local(ordinal = 3) int categoryIndex) {
        return deployer$selected == null ? original.call(instance, i) : deployer$displayedItems.get(deployer$selected).get(categoryIndex);
    }


    /**
     * We redirect the function only when a custom stock inventory type is selected, so we avoid breaking other mods
     * */
    @Inject(method = "getHoveredSlot", at = @At("HEAD"), cancellable = true)
    private void getHoveredSlot(int x, int y, CallbackInfoReturnable<Couple<Integer>> cir) {
        if(deployer$selected == null) return;

        x += 1;
        if (x < itemsX || x >= itemsX + deployer$cols * deployer$colWidth || isSchematicListMode()) {
            cir.setReturnValue(noneHovered);
            cir.cancel();
            return;
        }

        // Ordered item is hovered
        if (y >= orderY && y < orderY + rowHeight) { //We should not get more than 20 here
            int col = (x - itemsX) / deployer$colWidth;
            if (deployer$itemsToOrder.get(deployer$selected).size() <= col || col < 0) {
                cir.setReturnValue(noneHovered);
                cir.cancel();
                return;
            }
            cir.setReturnValue(Couple.create(-1, col));
            cir.cancel();
            return;
        }

        // Ordered recipe is hovered
        if (y >= orderY - 31 && y < orderY - 31 + deployer$rowHeight) {
            int jeiX = getGuiLeft() + (windowWidth - deployer$colWidth * recipesToOrder.size()) / 2 + 1;
            int col = Mth.floorDiv(x - jeiX, deployer$colWidth);
            if (recipesToOrder.size() > col && col >= 0) {
                cir.setReturnValue(Couple.create(-2, col));
                cir.cancel();
                return;
            }
        }

        if (y < getGuiTop() + 16 || y > getGuiTop() + windowHeight - 80) {
            cir.setReturnValue(noneHovered);
            cir.cancel();
            return;
        }
        if (!itemScroll.settled()) {
            cir.setReturnValue(noneHovered);
            cir.cancel();
            return;
        }

        int localY = y - itemsY;

        for (int categoryIndex = 0; categoryIndex < deployer$displayedItems.get(deployer$selected).size(); categoryIndex++) {
            StockKeeperRequestScreen.CategoryEntry entry = categories.isEmpty() ? new StockKeeperRequestScreen.CategoryEntry(0, "", 0) : categories.get(categoryIndex);
            var ae = ((StockKeeperRequestScreen$CategoryEntryAccessor)entry);
            if (ae.getHidden())
                continue;

            int row = Mth.floor((localY - (categories.isEmpty() ? 4 : deployer$rowHeight) - ae.getY()) / (float) deployer$rowHeight
                    + itemScroll.getChaseTarget());

            int col = (x - itemsX) / deployer$colWidth;
            int slot = row * deployer$cols + col;

            if (slot < 0) {
                cir.setReturnValue(noneHovered);
                cir.cancel();
                return;
            }
            if (deployer$displayedItems.get(deployer$selected).get(categoryIndex)
                    .size() <= slot)
                continue;

            cir.setReturnValue(Couple.create(categoryIndex, slot));
            cir.cancel();
            return;
        }

        cir.setReturnValue(noneHovered);
        cir.cancel();
        return;
    }

    /* GET HOVERED INGREDIENT */
    /**
     * Completely ignored if you're not in the items TAB.
     * This is used for JEI compat so might need a future implementation for fluid stacks
     *
     */
    @Inject(method = "getHoveredIngredient", at = @At("HEAD"), cancellable = true)
    private void getHoveredIngredient(int mouseX, int mouseY, CallbackInfoReturnable<Optional<Pair<ItemStack, Rect2i>>> cir) {
        if(deployer$selected != null) {
            cir.setReturnValue(Optional.empty());
            cir.cancel();
        }
    }

    /* IS CONFIRM HOVERED */

    /* TROUBLESHOOTING MESSAGE */
    @ModifyExpressionValue(method = "getTroubleshootingMessage", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;currentItemSource:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<List<?>> getTroubleShootingMessage(List<List<?>> original) {
        return deployer$selected == null ? original : deployer$currentItemSource.get(deployer$selected);
    }

    @ModifyExpressionValue(method = "getTroubleshootingMessage", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockTickerBlockEntity;activeLinks:I", opcode = Opcodes.GETFIELD))
    private int getTroubleShootingMessage(int original) {
        return deployer$selected == null ? original : ((STBEExtension) blockEntity).deployer$getMappedInfo().getActiveLinks(deployer$selected);
    }

    /* MOUSE CLICKED */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (DeployerRegistries.STOCK_INVENTORY.size() == 0) return;
        int x = getGuiLeft();
        int y = getGuiTop();
        if (mouseX > x - 8 && mouseX < x + 12 && mouseY > y + 20 && mouseY < y + 40) {
            deployer$selected = null;
            init();
            searchBox.active = true;
            searchBox.visible = true;
            this.deployer$colWidth = 20;
            this.deployer$rowHeight = 20;
            this.deployer$cols = 9;
            cir.cancel();
            return;
        }
        int i = 0;
        for (StockInventoryType<?, ?, ?> type : DeployerRegistries.STOCK_INVENTORY) {
            var id = DeployerRegistries.STOCK_INVENTORY.getKey(type);
            assert id != null;
            if (mouseX > x - 8 && mouseX < x + 12 && mouseY > y + 40 + i * 20 && mouseY < y + 60 + i * 20) {
                deployer$selected = type;
                init();
                boolean search = deployer$selected.packageHandler().shouldRenderSearchBar();
                searchBox.active = search;
                searchBox.visible = search;
                this.deployer$colWidth = deployer$selected.packageHandler().getColWidth();
                this.deployer$rowHeight = deployer$selected.packageHandler().getRowHeight();
                this.deployer$cols = 20*9/deployer$colWidth;
                cir.cancel();
                return;
            }
            i++;
        }
    }

    @ModifyExpressionValue(method = "mouseClicked", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private int mouseClicked$1(int original) {
        if (deployer$selected != null) return deployer$displayedItems.size();
        return original;
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 1))
    private Object mouseClicked$1(List<?> instance, int i, Operation<Object> original, @Local(ordinal = 3) int categoryIndex) {
        if (deployer$selected != null) return deployer$displayedItems.get(deployer$selected).get(categoryIndex);
        return original.call(instance, i);
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/data/Couple;getFirst()Ljava/lang/Object;", ordinal = 1), cancellable = true)
    private void mouseClicked$1(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir, @Local Couple<Integer> hoveredSlot, @Local(ordinal = 1) boolean rmb) {
        if (deployer$selected == null) return;
        deployer$mouseClicked$internal(deployer$selected, hoveredSlot, rmb);
        cir.setReturnValue(true);
        cir.cancel();
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$mouseClicked$internal(StockInventoryType<K, V, H> type, Couple<Integer> hoveredSlot, boolean rmb) {
        boolean orderClicked = hoveredSlot.getFirst() == -1;
        boolean recipeClicked = hoveredSlot.getFirst() == -2;
        if (recipeClicked) return;
        var itemsToOrder = (List<V>) deployer$itemsToOrder.get(type);
        V entry = orderClicked ? itemsToOrder.get(hoveredSlot.getSecond())
                : (V) deployer$displayedItems.get(type).get(hoveredSlot.getFirst()).get(hoveredSlot.getSecond());
        block:
        {
            int transfer = type.packageHandler().clickAmount(Screen.hasControlDown(), Screen.hasShiftDown(), Screen.hasAltDown());
            V existingOrder = deployer$getOrderForV(type, entry);
            if (existingOrder == null) {
                if (itemsToOrder.size() >= deployer$cols || rmb) {
                    break block;
                }
                itemsToOrder.add(existingOrder = type.valueHandler().copyWithCount(entry, 0));
                playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.2f);
                playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 0.8f);
            }

            int current = type.valueHandler().getCount(existingOrder);

            if (rmb || orderClicked) {
                type.valueHandler().setCount(existingOrder, current - transfer);
                if (type.valueHandler().getCount(existingOrder) <= 0) {
                    itemsToOrder.remove(existingOrder);
                    playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.8f);
                    playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 1.8f);
                }
                break block;
            }
            type.valueHandler().setCount(existingOrder, current + Math.min(transfer, type.valueHandler().getCount(entry) - current));
        }
    }

    /* MOUSE RELEASED */

    /* MOUSE SCROLLED */
    @Inject(method = "mouseScrolled", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/data/Couple;getFirst()Ljava/lang/Object;", ordinal = 2, shift = At.Shift.AFTER), cancellable = true)
    private void mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir, @Local Couple<Integer> hoveredSlot, @Local(ordinal = 1) boolean orderClicked) {
        if (deployer$selected == null) return;
        boolean recipeClicked = hoveredSlot.getFirst() == -2;
        if (recipeClicked) return;
        deployer$mouseScrolled$internal(deployer$selected, orderClicked, /*NOTE*/false, hoveredSlot, scrollY);
        cir.setReturnValue(true);
        cir.cancel();
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> void deployer$mouseScrolled$internal(StockInventoryType<K, V, H> type, boolean orderClicked, @SuppressWarnings("SameParameterValue") boolean recipeClicked, Couple<Integer> hoveredSlot, double scrollY) {
        List<V> itemsToOrder = (List<V>) deployer$itemsToOrder.get(type);
        V entry = recipeClicked ? null
                : orderClicked ? itemsToOrder.get(hoveredSlot.getSecond())
                : (V) deployer$displayedItems.get(type).get(hoveredSlot.getFirst())
                .get(hoveredSlot.getSecond());

        boolean remove = scrollY < 0;
        int transfer = Mth.ceil(Math.abs(scrollY)) * type.packageHandler().scrollAmount(hasControlDown(), hasShiftDown(), hasAltDown());

        V existingOrder = orderClicked ? entry : deployer$getOrderForV(type, entry);
        if (existingOrder == null) {
            if (itemsToOrder.size() >= deployer$cols || remove)
                return;
            itemsToOrder.add(existingOrder = type.valueHandler().copyWithCount(entry, 0)); //TODO: Is this unsafe?
            playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.2f);
            playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 0.8f);
        }

        int current = type.valueHandler().getCount(existingOrder);
        if (remove) {
            type.valueHandler().setCount(existingOrder, current - transfer);
            if (type.valueHandler().getCount(existingOrder) <= 0) {
                itemsToOrder.remove(existingOrder);
                playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.8f);
                playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 1.8f);
            } else if (type.valueHandler().getCount(existingOrder) != current)
                playUiSound(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 0.25f, 1.2f);
            return;
        }

        type.valueHandler().setCount(existingOrder, current + Math.min(transfer, ((STBEExtension) blockEntity).deployer$getLastClientsideStockSnapshotAsSummary(type)
                .getCountOf(entry) - current));
        if (type.valueHandler().getCount(existingOrder) != current && current != 0)
            playUiSound(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 0.25f, 1.2f);
    }

    /* CLAMP SCROLL BAR */

    /* GET MAX SCROLL */
    @ModifyExpressionValue(method = "getMaxScroll", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;displayedItems:Ljava/util/List;"))
    private List<List<?>> getMaxScroll(List<List<?>> original) {
        return deployer$selected == null ? original : deployer$displayedItems.get(deployer$selected);
    }

    /* MOUSE DRAGGED */
    /* CHAR TYPED */
    /* KEY PRESSED */
    /* REMOVED */

    /* SEND IT */
    @ModifyExpressionValue(method = "sendIt", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean sendIt(boolean original) {
        return original && deployer$itemsToOrder.values().stream().allMatch(List::isEmpty);
    }

    @WrapOperation(method = "sendIt", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/platform/services/NetworkHelper;sendToServer(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V"))
    private void sendIt(NetworkHelper instance, CustomPacketPayload customPacketPayload, Operation<Void> original, @Local PackageOrderWithCrafts order) {
        Map<StockInventoryType<?, ?, ?>, GenericOrderContained<?>> map = new HashMap<>();
        for (var type : DeployerRegistries.STOCK_INVENTORY) {
            var v = deployer$sendIt$internal(type);
            if (v == null) continue;
            map.put(type, v);
        }

        CatnipServices.NETWORK.sendToServer(new GenericOrderRequestPacket(blockEntity.getBlockPos(), order, map, addressBox.getValue(), encodeRequester));
        /*
                ((StockTickerBlockEntityAccessor) blockEntity).setTicksSinceLastUpdate(10);
        successTicks = 1;

        if (isSchematicListMode())
            menu.player.closeContainer();
         */
    }

    @SuppressWarnings("unchecked")
    @Unique
    private <K, V, H> GenericOrderContained<V> deployer$sendIt$internal(StockInventoryType<K, V, H> type) {
        revalidateOrders();
        List<V> itemsToOrder = (List<V>) deployer$itemsToOrder.get(type);
        if (itemsToOrder.isEmpty()) return null;

        deployer$forcedEntries.put(type, type.networkHandler().create());

        AbstractInventorySummary<K, V> summary = ((STBEExtension) blockEntity).deployer$getLastClientsideStockSnapshotAsSummary(type);
        for (V value : itemsToOrder) {
            int countOf = summary.getCountOf(value);
            //TODO: Check for max count
            ((AbstractInventorySummary<K, V>) deployer$forcedEntries.get(type)).add(type.valueHandler().copy(value), -1 - Math.max(0, countOf - type.valueHandler().getCount(value)));
        }

        GenericOrderContained<V> order = GenericOrderContained.simple(itemsToOrder);

        //noinspection StatementWithEmptyBody,PointlessBooleanExpression
        if (canRequestCraftingPackage && !itemsToOrder.isEmpty() && false) {
            //TODO: Crafting order?
        }

        deployer$itemsToOrder.put(type, new ArrayList<>());
        return order;
        //TODO: Recipes to order?
    }

    /* KEY RELEASED */
    /* GET EXTRA AREAS */
    /* IS SCHEMATIC LIST MODE */
    /* REQUEST SCHEMATIC LIST */
    /* REQUEST CRAFTABLE */

    /* UPDATE CRAFTABLE AMOUNTS */
    /* MAX CRAFTABLE */
    /* REMOVE LEAST ESSENTIAL ITEMSTACK */
    /* REMOVE INGREDIENT AMOUNTS */
    /* SYNC JEI */
}
