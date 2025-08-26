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
    private final EntityType<? extends MrScootEntity> mrScootType;

    public MrScootItem(EntityType<? extends MrScootEntity> mrScootType, Item.Settings settings) {
        super(settings);
        this.mrScootType = mrScootType;
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.ANY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return ActionResult.PASS;
        } else {
            Vec3d vec3d = user.getRotationVec(1.0F);
            double d = (double)5.0F;
            List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().stretch(vec3d.multiply((double)5.0F)).expand((double)1.0F), EntityPredicates.CAN_HIT);
            if (!list.isEmpty()) {
                Vec3d vec3d2 = user.getEyePos();

                for(Entity entity : list) {
                    Box box = entity.getBoundingBox().expand((double)entity.getTargetingMargin());
                    if (box.contains(vec3d2)) {
                        return ActionResult.PASS;
                    }
                }
            }

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                MrScootEntity mrScootEntity = this.createEntity(world, hitResult, itemStack, user);
                if (mrScootEntity == null) {
                    return ActionResult.FAIL;
                } else {
                    mrScootEntity.setYaw(user.getYaw());
                    if (!world.isSpaceEmpty(mrScootEntity, mrScootEntity.getBoundingBox())) {
                        return ActionResult.FAIL;
                    } else {
                        if (!world.isClient) {
                            world.spawnEntity(mrScootEntity);
                            world.emitGameEvent(user, GameEvent.ENTITY_PLACE, hitResult.getPos());
                            itemStack.decrementUnlessCreative(1, user);
                        }

                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                return ActionResult.PASS;
            }
        }
    }


    @Nullable
    private MrScootEntity createEntity(World world, HitResult hitResult, ItemStack stack, PlayerEntity player) {
        MrScootEntity entity = new MrScootEntity(mrScootType, world);
        if (entity != null) {
            Vec3d vec3d = hitResult.getPos();
            entity.initPosition(vec3d.x, vec3d.y, vec3d.z);
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                EntityType.copier(serverWorld, stack, player).accept(entity);
            }
        }
        return entity;
    }
}
