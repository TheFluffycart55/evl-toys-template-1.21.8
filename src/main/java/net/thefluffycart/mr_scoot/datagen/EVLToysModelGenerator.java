package net.thefluffycart.mr_scoot.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.thefluffycart.mr_scoot.item.EVLItems;

public class EVLToysModelGenerator extends FabricModelProvider {
    public EVLToysModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(EVLItems.PLASTIC_GRANULES, Models.GENERATED);
        itemModelGenerator.register(EVLItems.BEER, Models.GENERATED);
        itemModelGenerator.register(EVLItems.MR_SCOOT_ITEM, Models.GENERATED);
    }
}