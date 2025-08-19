package net.thefluffycart.mr_scoot.item.custom;

import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.thefluffycart.mr_scoot.entity.custom.MrScootEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MrScootItem extends Item {
    private final EntityType<? extends MrScootEntity> boatEntityType;

    public MrScootItem(EntityType<? extends MrScootEntity> boatEntityType, Item.Settings settings) {
        super(settings);
        this.boatEntityType = boatEntityType;
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }

        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        if (!world.canEntityModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            return ActionResult.FAIL;
        }

        MrScootEntity entity = createEntity(world, blockHitResult, itemStack, user);
        if (entity == null) return ActionResult.PASS;

        world.spawnEntity(entity);
        itemStack.decrementUnlessCreative(1, user);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());

        return ActionResult.SUCCESS;
    }


    @Nullable
    private MrScootEntity createEntity(World world, HitResult hitResult, ItemStack stack, PlayerEntity player) {
        MrScootEntity entity = this.boatEntityType.create(world, SpawnReason.SPAWN_ITEM_USE);
        if (entity != null) {
            Vec3d vec3d = hitResult.getPos();
            entity.setPos(vec3d.x, vec3d.y + 0.25, vec3d.z);
            if (world instanceof ServerWorld serverWorld) {
                EntityType.copier(serverWorld, stack, player).accept(entity);
            }
        }
        return entity;
    }
}
