package net.thefluffycart.mr_scoot.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class EVLBeerComponents  {
    public static final FoodComponent BEER = new FoodComponent.Builder().nutrition(3).saturationModifier(0.25f).alwaysEdible().build();
}