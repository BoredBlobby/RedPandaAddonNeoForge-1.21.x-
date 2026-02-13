package net.sussyit.redpandamod.datgen;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.sussyit.redpandamod.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_RING.get())
                .pattern("AAA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.MATCHABOBA.get())
                .pattern("CCC")
                .pattern("CBC")
                .pattern("CAC")
                .define('A', Items.GLASS_BOTTLE)
                .define('B', Items.WATER_BUCKET)
                .define('C', Items.TALL_GRASS)
                .unlockedBy("has_grass", has(Items.TALL_GRASS)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.CHOCOLATEBOX.get())
                .pattern(" B ")
                .pattern(" A ")
                .pattern(" C ")
                .define('A', Items.COCOA_BEANS)
                .define('B', Items.GOLD_INGOT)
                .define('C', Items.OXEYE_DAISY)
                .unlockedBy("has_cocoa", has(Items.COCOA_BEANS)).save(recipeOutput);
    }
}
