package net.thefluffycart.mr_scoot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.Entity;
import net.thefluffycart.mr_scoot.entity.EVLToysEntities;
import net.thefluffycart.mr_scoot.entity.client.MrScootModel;
import net.thefluffycart.mr_scoot.entity.client.MrScootRenderer;
import net.thefluffycart.mr_scoot.entity.custom.MrScootEntity;

public class EVLClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(MrScootModel.MR_SCOOT, MrScootModel::getTexturedModelData);
        EntityRendererRegistry.register(EVLToysEntities.MR_SCOOT, MrScootRenderer::new);
        // Inside your mod's client initializer
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.hasVehicle()) {
                Entity vehicle = client.player.getVehicle();
                if (vehicle instanceof MrScootEntity scoot) {
                    boolean left = client.options.leftKey.isPressed();
                    boolean right = client.options.rightKey.isPressed();
                    boolean forward = client.options.forwardKey.isPressed();
                    boolean back = client.options.backKey.isPressed();
                    scoot.setInputs(left, right, forward, back);
                }
            }
        });

    }

}
