package net.thefluffycart.mr_scoot.entity.custom;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.thefluffycart.mr_scoot.item.EVLItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class MrScootEntity extends VehicleEntity implements Leashable{
    private Supplier<Item> itemSupplier;
    private float ticksUnderwater;
    private float yawVelocity;
    private final PositionInterpolator interpolator = new PositionInterpolator(this, 3);
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;
    private boolean pressingHonk;
    private boolean canHonk;
    private double waterLevel;
    private float nearbySlipperiness;
    private MrScootEntity.Location location;
    private MrScootEntity.Location lastLocation;
    private double fallVelocity;

    @Nullable
    private Leashable.LeashData leashData;

    public MrScootEntity(EntityType<? extends MrScootEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.EVENTS;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry trackerEntry) {
        return new EntitySpawnS2CPacket(this, trackerEntry);
    }

    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable(entity) || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    public boolean isCollidable(@Nullable Entity entity) {
        return true;
    }

    public boolean isPushable() {
        return true;
    }

    @Override
    protected void readCustomData(ReadView view) {

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }

    public Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
        return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
    }

    protected double getPassengerAttachmentY(EntityDimensions dimensions) {
        return (double)(dimensions.height() / 3.0F);
    }

    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        float f = this.getPassengerHorizontalOffset();
        double g = this.getPassengerAttachmentY(dimensions);
        if (this.getPassengerList().size() > 1) {
            int i = this.getPassengerList().indexOf(passenger);
            if (i == 0) {
                f = 0.2F;
            } else {
                f = 0.3F;
                g = 1F;
            }

            if (passenger instanceof AnimalEntity) {
                f += 0.2F;
            }
        }
        return (new Vec3d((double)0.0F, this.getPassengerAttachmentY(dimensions), (double)f)).rotateY(-this.getYaw() * ((float)Math.PI / 180F));
    }

    public void initPosition(double x, double y, double z) {
        this.setPosition(x, y, z);
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
    }

    public void pushAwayFrom(Entity entity) {
        if (entity instanceof MrScootEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }

    }

    @Override
    public boolean canHit() {
        return this.getFirstPassenger() == null;
    }

    public PositionInterpolator getInterpolator() {
        return this.interpolator;
    }

    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.getVehicle() == this) {
            return ActionResult.PASS;
        }

        if (!this.getWorld().isClient) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }



    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (player == this.getFirstPassenger()) {
            return ActionResult.PASS;
        }
        return super.interactAt(player, hitPos, hand);
    }


    public void tick() {
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        if (this.pressingHonk)
        {
            for (int count = 0; count < 1600; count++)
            {

            }
        }
        if (this.location != MrScootEntity.Location.UNDER_WATER && this.location != MrScootEntity.Location.UNDER_FLOWING_WATER) {
            this.ticksUnderwater = 0.0F;
        } else {
            ++this.ticksUnderwater;
        }

        if (!this.getWorld().isClient && this.ticksUnderwater >= 60.0F) {
            this.removeAllPassengers();
        }

        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }

        if (this.getDamageWobbleStrength() > 0.0F) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
        }

        super.tick();
        this.interpolator.tick();
        if (this.isLogicalSideForUpdatingMovement()) {
            this.updateVelocity();

            Vec3d velocity = this.getVelocity();

            this.move(MovementType.SELF, velocity);
        }
        else {
            this.setVelocity(Vec3d.ZERO);
        }

        this.updateVelocity();
        if (this.getWorld().isClient) {
            this.startMovement();
        }

        this.tickBlockCollision();

        List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand((double)0.2F, (double)-0.01F, (double)0.2F), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.getWorld().isClient && !(this.getControllingPassenger() instanceof PlayerEntity);
            for (Entity entity : list) if (!entity.hasPassenger(this)) {
                if (bl && this.getPassengerList().size() < this.getMaxPassengers() && !entity.hasVehicle() && this.isSmallerThanBoat(entity) && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity) && !(entity instanceof PlayerEntity) && !(entity instanceof CreakingEntity)) {
                    entity.startRiding(this);
                } else {
                    this.pushAwayFrom(entity);
                }
            }
        }


    }

    private void startMovement() {
        if (this.hasPassengers()) {
            float f = 0.0F;
            if (this.pressingLeft) {
                --this.yawVelocity;
            }

            if (this.pressingRight) {
                ++this.yawVelocity;
            }

            if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
                f += 0.005F;
            }

            this.setYaw(this.getYaw() + this.yawVelocity);
            if (this.pressingForward) {
                f += 0.085F;
            }

            if (this.pressingBack) {
                f -= 0.015F;
            }

            this.setVelocity(this.getVelocity().add((double)(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180F)) * f), (double)0.0F, (double)(MathHelper.cos(this.getYaw() * ((float)Math.PI / 180F)) * f)));
        }
    }

    @Nullable
    public Leashable.LeashData getLeashData() {
        return this.leashData;
    }

    protected final Item asItem() {
        return (Item)this.itemSupplier.get();
    }

    public final ItemStack getPickBlockStack() {
        return new ItemStack(EVLItems.MR_SCOOT_ITEM);
    }

    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }

    public Vec3d getLeashOffset() {
        return new Vec3d((double)0.0F, (double)(0.88F * this.getHeight()), (double)(0.64F * this.getWidth()));
    }

    public boolean canUseQuadLeashAttachmentPoint() {
        return true;
    }

    public Vec3d[] getQuadLeashOffsets() {
        return Leashable.createQuadLeashOffsets(this, (double)0.0F, 0.64, 0.382, 0.88);
    }

    private MrScootEntity.Location checkLocation() {
        MrScootEntity.Location location = this.getUnderWaterLocation();
        if (location != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return location;
        } else if (this.checkBoatInWater()) {
            return MrScootEntity.Location.IN_WATER;
        } else {
            float f = this.getNearbySlipperiness();
            if (f > 0.0F) {
                this.nearbySlipperiness = f;
                return MrScootEntity.Location.ON_LAND;
            } else {
                return MrScootEntity.Location.IN_AIR;
            }
        }
    }

    public float getWaterHeightBelow() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(box.maxY - this.fallVelocity);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        label39:
        for(int o = k; o < l; ++o) {
            float f = 0.0F;

            for(int p = i; p < j; ++p) {
                for(int q = m; q < n; ++q) {
                    mutable.set(p, o, q);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER)) {
                        f = Math.max(f, fluidState.getHeight(this.getWorld(), mutable));
                    }

                    if (f >= 1.0F) {
                        continue label39;
                    }
                }
            }

            if (f < 1.0F) {
                return (float)mutable.getY() + f;
            }
        }

        return (float)(l + 1);
    }

    public float getNearbySlipperiness() {
        Box box = this.getBoundingBox();
        Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = MathHelper.floor(box2.minX) - 1;
        int j = MathHelper.ceil(box2.maxX) + 1;
        int k = MathHelper.floor(box2.minY) - 1;
        int l = MathHelper.ceil(box2.maxY) + 1;
        int m = MathHelper.floor(box2.minZ) - 1;
        int n = MathHelper.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = VoxelShapes.cuboid(box2);
        float f = 0.0F;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int p = i; p < j; ++p) {
            for(int q = m; q < n; ++q) {
                int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
                if (r != 2) {
                    for(int s = k; s < l; ++s) {
                        if (r <= 0 || s != k && s != l - 1) {
                            mutable.set(p, s, q);
                            BlockState blockState = this.getWorld().getBlockState(mutable);
                            if (!(blockState.getBlock() instanceof LilyPadBlock) && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.getWorld(), mutable).offset(mutable), voxelShape, BooleanBiFunction.AND)) {
                                f += blockState.getBlock().getSlipperiness();
                                ++o;
                            }
                        }
                    }
                }
            }
        }

        return f / (float)o;
    }

    private boolean checkBoatInWater() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.minY + 0.001);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        this.waterLevel = -Double.MAX_VALUE;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int o = i; o < j; ++o) {
            for(int p = k; p < l; ++p) {
                for(int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER)) {
                        float f = (float)p + fluidState.getHeight(this.getWorld(), mutable);
                        this.waterLevel = Math.max((double)f, this.waterLevel);
                        bl |= box.minY < (double)f;
                    }
                }
            }
        }

        return bl;
    }

    @Nullable
    private MrScootEntity.Location getUnderWaterLocation() {
        Box box = this.getBoundingBox();
        double d = box.maxY + 0.001;
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(d);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int o = i; o < j; ++o) {
            for(int p = k; p < l; ++p) {
                for(int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER) && d < (double)((float)mutable.getY() + fluidState.getHeight(this.getWorld(), mutable))) {
                        if (!fluidState.isStill()) {
                            return MrScootEntity.Location.UNDER_FLOWING_WATER;
                        }

                        bl = true;
                    }
                }
            }
        }

        return bl ? MrScootEntity.Location.UNDER_WATER : null;
    }

    protected double getGravity() {
        return 0.06;
    }

    private void updateVelocity() {
        double d = -this.getFinalGravity();
        double e = (double)0.0F;
        float f = 0.05F;
        if (this.lastLocation == MrScootEntity.Location.IN_AIR && this.location != MrScootEntity.Location.IN_AIR && this.location == MrScootEntity.Location.ON_LAND) {
            this.waterLevel = this.getBodyY((double)1.0F);
            double g = (double)(this.getWaterHeightBelow() - this.getHeight()) + 0.101;
            if (this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset((double)0.0F, g - this.getY(), (double)0.0F))) {
                this.setPosition(this.getX(), g, this.getZ());
                this.setVelocity(this.getVelocity().multiply((double)1.0F, (double)0.0F, (double)1.0F));
                this.fallVelocity = (double)0.0F;
            }

            this.location = MrScootEntity.Location.IN_WATER;
        } else {
            if (this.location == MrScootEntity.Location.IN_WATER) {
                e = (this.waterLevel - this.getY()) / (double)this.getHeight();
                f = 0.9F;
            } else if (this.location == MrScootEntity.Location.UNDER_FLOWING_WATER) {
                d = -7.0E-4;
                f = 0.9F;
            } else if (this.location == MrScootEntity.Location.UNDER_WATER) {
                e = (double)0.01F;
                f = 0.45F;
            }
            else if (this.location == MrScootEntity.Location.IN_AIR) {
                f = 0.9F;
            }
            else if (this.location == MrScootEntity.Location.ON_LAND) {
                f = 0.91F;
                e = this.nearbySlipperiness;
                if (this.getControllingPassenger() instanceof PlayerEntity) {
                    this.nearbySlipperiness /= 3.5F;
                }
            }

            Vec3d vec3d = this.getVelocity();
            this.yawVelocity *= f;
            double newY = vec3d.y + d;
            if (Math.abs(newY) < 0.003 && this.location == Location.ON_LAND) {
                newY = 0.0;
            }
            this.setVelocity(vec3d.x * f, newY, vec3d.z * f);
        }

    }

    protected float getPassengerHorizontalOffset() {
        return 0.0F;
    }

    public boolean isSmallerThanBoat(Entity entity) {
        return entity.getWidth() < this.getWidth();
    }

    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
        super.updatePassengerPosition(passenger, positionUpdater);
        if (!passenger.getType().isIn(EntityTypeTags.CAN_TURN_IN_BOATS)) {
            passenger.setYaw(passenger.getYaw() + this.yawVelocity);
            passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
            this.clampPassengerYaw(passenger);
            if (passenger instanceof AnimalEntity && this.getPassengerList().size() == this.getMaxPassengers()) {
                int i = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setBodyYaw(((AnimalEntity)passenger).bodyYaw + (float)i);
                passenger.setHeadYaw(passenger.getHeadYaw() + (float)i);
            }

        }
    }

    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Vec3d vec3d = getPassengerDismountOffset((double)(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO), (double)passenger.getWidth(), passenger.getYaw());
        double d = this.getX() + vec3d.x;
        double e = this.getZ() + vec3d.z;
        BlockPos blockPos = BlockPos.ofFloored(d, this.getBoundingBox().maxY, e);
        BlockPos blockPos2 = blockPos.down();
        if (!this.getWorld().isWater(blockPos2)) {
            List<Vec3d> list = Lists.newArrayList();
            double f = this.getWorld().getDismountHeight(blockPos);
            if (Dismounting.canDismountInBlock(f)) {
                list.add(new Vec3d(d, (double)blockPos.getY() + f, e));
            }

            double g = this.getWorld().getDismountHeight(blockPos2);
            if (Dismounting.canDismountInBlock(g)) {
                list.add(new Vec3d(d, (double)blockPos2.getY() + g, e));
            }

            UnmodifiableIterator var14 = passenger.getPoses().iterator();

            while(var14.hasNext()) {
                EntityPose entityPose = (EntityPose)var14.next();

                for(Vec3d vec3d2 : list) {
                    if (Dismounting.canPlaceEntityAt(this.getWorld(), vec3d2, passenger, entityPose)) {
                        passenger.setPose(entityPose);
                        return vec3d2;
                    }
                }
            }
        }

        return super.updatePassengerForDismount(passenger);
    }

    protected void clampPassengerYaw(Entity passenger) {
        passenger.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(passenger.getYaw() - this.getYaw());
        float g = MathHelper.clamp(f, -105.0F, 105.0F);
        passenger.lastYaw += g - f;
        passenger.setYaw(passenger.getYaw() + g - f);
        passenger.setHeadYaw(passenger.getYaw());
    }

    public void onPassengerLookAround(Entity passenger) {
        this.clampPassengerYaw(passenger);
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;
        if (!this.hasVehicle()) {
            if (onGround) {
                this.onLanding();
            } else if (!this.getWorld().getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < (double)0.0F) {
                this.fallDistance -= (double)((float)heightDifference);
            }

        }
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < this.getMaxPassengers() && !this.isSubmergedIn(FluidTags.WATER);
    }

    protected int getMaxPassengers() {
        return 2;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity var2 = this.getFirstPassenger();
        LivingEntity var10000;
        if (var2 instanceof LivingEntity livingEntity) {
            var10000 = livingEntity;
        } else {
            var10000 = super.getControllingPassenger();
        }

        return var10000;
    }

    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack, boolean pressingHonk) {
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
        this.pressingHonk = pressingHonk;
    }

    public boolean isSubmergedInWater() {
        return this.location == MrScootEntity.Location.UNDER_WATER || this.location == MrScootEntity.Location.UNDER_FLOWING_WATER;
    }

    public static enum Location {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

        private Location() {
        }
    }
}
