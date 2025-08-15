package net.thefluffycart.mr_scoot.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.Identifier;
import net.thefluffycart.mr_scoot.EVLToys;

public class MrScootModel<S extends VehicleEntity> extends EntityModel<MrScootRenderState> {
    public static final EntityModelLayer MR_SCOOT = new EntityModelLayer(Identifier.of(EVLToys.MOD_ID, "mr_scoot"), "main");
    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart eyes;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart bone4;
    private final ModelPart cheeks;

    public MrScootModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
        this.body = root.getChild("body");
        this.eyes = root.getChild("eyes");
        this.bone2 = root.getChild("bone2");
        this.bone3 = root.getChild("bone3");
        this.bone4 = root.getChild("bone4");
        this.cheeks = root.getChild("cheeks");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(62, 94).cuboid(-2.0F, -3.5F, -3.5F, 4.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(12.0F, 20.5F, -10.5F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 53).cuboid(9.0F, 20.0F, -16.5F, 3.0F, 6.0F, 24.0F, new Dilation(0.0F))
                .uv(0, 83).cuboid(9.0F, 26.0F, -15.5F, 3.0F, 6.0F, 9.0F, new Dilation(0.0F))
                .uv(54, 81).cuboid(-12.0F, 26.0F, -22.5F, 24.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(84, 29).cuboid(-12.0F, 20.0F, -22.5F, 24.0F, 6.0F, 6.0F, new Dilation(0.0F))
                .uv(84, 41).cuboid(-12.0F, 20.0F, 7.5F, 24.0F, 6.0F, 4.0F, new Dilation(0.0F))
                .uv(48, 94).cuboid(9.0F, -1.0F, 7.5F, 3.0F, 21.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 53).mirrored().cuboid(-12.0F, 20.0F, -16.5F, 3.0F, 6.0F, 24.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 29).cuboid(-12.0F, 26.0F, -6.5F, 24.0F, 6.0F, 18.0F, new Dilation(0.0F))
                .uv(24, 83).cuboid(-12.0F, 26.0F, -15.5F, 3.0F, 6.0F, 9.0F, new Dilation(0.0F))
                .uv(48, 94).mirrored().cuboid(-12.0F, -1.0F, 7.5F, 3.0F, 21.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 0).cuboid(-12.0F, -2.0F, -13.5F, 24.0F, 4.0F, 25.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -12.0F, 6.5F));

        ModelPartData cube_r1 = body.addChild("cube_r1", ModelPartBuilder.create().uv(54, 53).mirrored().cuboid(-1.5F, -2.0F, -12.0F, 3.0F, 4.0F, 24.0F, new Dilation(0.0F)).mirrored(false)
                .uv(54, 53).cuboid(19.5F, -2.0F, -12.0F, 3.0F, 4.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(-10.5F, 10.0F, -16.0F, 1.1781F, 0.0F, 0.0F));

        ModelPartData eyes = modelPartData.addChild("eyes", ModelPartBuilder.create().uv(98, 0).cuboid(-9.0F, -2.0F, -2.0F, 6.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(84, 94).cuboid(-3.0F, -5.0F, -1.0F, 6.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(3.0F, -17.0F, -7.0F));

        ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(62, 94).mirrored().cuboid(-2.0F, -3.5F, -3.5F, 4.0F, 7.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(-12.0F, 20.5F, 12.5F));

        ModelPartData bone3 = modelPartData.addChild("bone3", ModelPartBuilder.create().uv(62, 94).mirrored().cuboid(-2.0F, -3.5F, -3.5F, 4.0F, 7.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(-12.0F, 20.5F, -10.5F));

        ModelPartData bone4 = modelPartData.addChild("bone4", ModelPartBuilder.create().uv(62, 94).cuboid(-2.0F, -3.5F, -3.5F, 4.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(12.0F, 20.5F, 12.5F));

        ModelPartData cheeks = modelPartData.addChild("cheeks", ModelPartBuilder.create().uv(0, 98).cuboid(18.0F, -5.0F, -1.0F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 98).cuboid(-3.0F, -5.0F, -1.0F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(-10.0F, 14.0F, -16.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }


}
