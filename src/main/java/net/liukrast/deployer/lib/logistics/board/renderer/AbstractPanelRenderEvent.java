package net.liukrast.deployer.lib.logistics.board.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.bus.api.Event;

/**
 * Fired whenever a panel needs to be rendered.
 * This event provides all the context required to draw the panel on the client.
 * Invoked on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}
 */
public class AbstractPanelRenderEvent extends Event {
    public final AbstractPanelBehaviour behaviour;
    public final float partialTicks;
    public final PoseStack poseStack;
    public final MultiBufferSource bufferSource;
    public final int light, overlay;

    /**
     * Creates a new render event for a panel.
     *
     * @param behaviour the panel behavior instance associated with this render call.
     * @param partialTicks the frame interpolation factor.
     * @param poseStack the pose stack used for rendering.
     * @param bufferSource the buffer source used to store rendering data.
     * @param light the packed light value.
     * @param overlay the packed overlay value.
     */
    public AbstractPanelRenderEvent(AbstractPanelBehaviour behaviour, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        this.behaviour = behaviour;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.light = light;
        this.overlay = overlay;
    }
}
