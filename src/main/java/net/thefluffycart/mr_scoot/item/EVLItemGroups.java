package net.thefluffycart.mr_scoot.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.thefluffycart.mr_scoot.EVLToys;

public class EVLItemGroups {
    public static final ItemGroup EVL_TOYS_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(EVLToys.MOD_ID, "evl_toys_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(EVLItems.MR_SCOOT_ITEM))
                    .displayName(Text.translatable("itemgroup.mr_scoot.evl_toys_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(EVLItems.PLASTIC_GRANULES);
                        entries.add(EVLItems.MR_SCOOT_ITEM);
                    }).build());

    public static void registerItemGroups() {
        EVLToys.LOGGER.info("Registering Item Groups for " + EVLToys.MOD_ID);
    }

}
