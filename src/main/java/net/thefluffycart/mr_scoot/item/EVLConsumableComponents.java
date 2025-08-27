package net.thefluffycart.mr_scoot.item;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.*;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class EVLConsumableComponents {
    public static final ConsumableComponent BEER;


    public static ConsumableComponent.Builder drink() {
        return ConsumableComponent.builder().consumeSeconds(1.6F).useAction(UseAction.DRINK).sound(SoundEvents.ENTITY_GENERIC_DRINK).consumeParticles(false);
    }

    static {
        BEER = drink().consumeEffect(new ApplyEffectsConsumeEffect(List.of(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 0), new StatusEffectInstance(StatusEffects.HUNGER, 300, 1), new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0)))).build();
    }
}
