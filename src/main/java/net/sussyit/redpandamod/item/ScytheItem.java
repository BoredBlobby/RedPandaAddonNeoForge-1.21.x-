package net.sussyit.redpandamod.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ScytheItem extends Item implements GeoItem {
    // 1. The Cache (Stores animation state per item)
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ScytheItem(Properties properties) {
        super(properties);
    }

    // 2. Register the Controller
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    // 3. The Logic (The "Draw" Animation)
    private PlayState predicate(AnimationState<ScytheItem> event) {
        // This chain tells GeckoLib: "Play 'pullOut' once, then loop 'idle' forever."
        // Because we use .thenPlay() followed by .thenLoop(), it handles the transition automatically.
        event.getController().setAnimation(RawAnimation.begin().thenPlay("pullOut").thenLoop("idle"));

        return PlayState.CONTINUE;
    }

    // Inside your ScytheItem class
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ScytheRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new ScytheRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
