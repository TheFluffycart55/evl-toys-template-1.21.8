package net.thefluffycart.mr_scoot.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.thefluffycart.mr_scoot.EVLToys;
import net.thefluffycart.mr_scoot.entity.EVLToysEntities;
import net.thefluffycart.mr_scoot.entity.custom.MrScootEntity;
import org.joml.Quaternionf;


public class MrScootRenderer extends EntityRenderer<MrScootEntity, MrScootRenderState> {

    public void render(MrScootRenderState mrScootRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0F, 1.5f, 0.0F);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F + mrScootRenderState.yaw));
        EntityModel<MrScootRenderState> entityModel = this.getModel();
        entityModel.setAngles(mrScootRenderState);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.getRenderLayer());
        entityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(mrScootRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public static final Identifier TEXTURE = Identifier.of("mr_scoot:textures/entity/mr_scoot.png");

    private final MrScootModel model;

    public MrScootRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new MrScootModel(context.getPart(MrScootModel.MR_SCOOT));
        this.shadowRadius = 0.5f;
    }

    protected EntityModel<MrScootRenderState> getModel() {
        return this.model;
    }

    protected RenderLayer getRenderLayer() {
        return RenderLayer.getEntityCutoutNoCull(TEXTURE);
    }

    @Override
    public MrScootRenderState createRenderState() {
        return new MrScootRenderState();
    }

    public void updateRenderState(MrScootEntity mrScootEntity, MrScootRenderState mrScootRenderState, float f) {
        super.updateRenderState(mrScootEntity, mrScootRenderState, f);
        mrScootRenderState.yaw = mrScootEntity.getLerpedYaw(f);
    }
}