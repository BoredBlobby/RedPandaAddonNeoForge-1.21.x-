package net.sussyit.redpandamod.item;

import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import software.bernie.geckolib.model.GeoModel; // Change this import

public class ScytheModel extends GeoModel<ScytheItem> { // Change this to GeoModel

    @Override
    public ResourceLocation getModelResource(ScytheItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "geo/scythe.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScytheItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/item/scythe.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScytheItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "animations/scythe.animation.json");
    }
}
