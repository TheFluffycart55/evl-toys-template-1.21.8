package net.thefluffycart.mr_scoot.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.thefluffycart.mr_scoot.EVLToys;
import net.thefluffycart.mr_scoot.entity.custom.MrScootEntity;
import net.thefluffycart.mr_scoot.item.EVLItems;

public class EVLToysEntities {
    private static final RegistryKey<EntityType<?>> MR_SCOOT_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(EVLToys.MOD_ID, "mr_scoot"));

    public static final EntityType<MrScootEntity> MR_SCOOT = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(EVLToys.MOD_ID, "mr_scoot"),
            EntityType.Builder.create(MrScootEntity::new, SpawnGroup.MISC)
                    .dimensions(2f, 2.5f).build(MR_SCOOT_KEY));
    public static void registerModEntities() {
        EVLToys.LOGGER.info("Registering Mod Entities for " + EVLToys.MOD_ID);
    }
}
