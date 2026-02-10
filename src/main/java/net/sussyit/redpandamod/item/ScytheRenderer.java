package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ScytheRenderer extends GeoItemRenderer<ScytheItem> {
    public ScytheRenderer() {
        super(new ScytheModel());
    }
    // No overrides needed for basic functionality!
}