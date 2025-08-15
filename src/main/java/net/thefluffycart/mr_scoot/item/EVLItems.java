package net.thefluffycart.mr_scoot.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.thefluffycart.mr_scoot.EVLToys;
import net.thefluffycart.mr_scoot.entity.EVLToysEntities;
import net.thefluffycart.mr_scoot.item.custom.MrScootItem;

import java.util.function.Function;

public class EVLItems {
    public static final Item PLASTIC_GRANULES = registerItem("plastic_granules", Item::new, new Item.Settings());
    public static final Item BEER = registerItem("beer", Item::new, new Item.Settings().food(EVLBeerComponents.BEER, EVLConsumableComponents.BEER).maxCount(1));
    public static final Item MR_SCOOT_ITEM = registerItem(
            "mr_scoot_item",
            (settings) -> new MrScootItem(EVLToysEntities.MR_SCOOT, settings),
            new Item.Settings().maxCount(1)
    );

    private static Item registerItem(String name, Function<Item.Settings, Item> function, Item.Settings settings) {
        Item toRegister = function.apply(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EVLToys.MOD_ID, name))));
        return Registry.register(Registries.ITEM, Identifier.of(EVLToys.MOD_ID, name), toRegister);
    }

    public static void registerModItems() {
        EVLToys.LOGGER.info("Registering Mod Items for " + EVLToys.MOD_ID);
    }
}
