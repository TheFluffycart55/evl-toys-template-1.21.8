package net.thefluffycart.mr_scoot;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.util.Identifier;
import net.thefluffycart.mr_scoot.entity.EVLToysEntities;
import net.thefluffycart.mr_scoot.entity.custom.MrScootEntity;
import net.thefluffycart.mr_scoot.item.EVLItemGroups;
import net.thefluffycart.mr_scoot.item.EVLItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EVLToys implements ModInitializer {
	public static final String MOD_ID = "mr_scoot";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		EVLItemGroups.registerItemGroups();
		EVLItems.registerModItems();
		EVLToysEntities.registerModEntities();

		LOGGER.info("Hello Fabric world!");
	}
}