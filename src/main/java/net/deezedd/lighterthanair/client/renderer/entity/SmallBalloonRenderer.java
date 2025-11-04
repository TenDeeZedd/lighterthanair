package net.deezedd.lighterthanair.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.entity.SmallBalloonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class SmallBalloonRenderer extends GeoEntityRenderer<SmallBalloonEntity> {

    private static final String BASE_BONE_NAME = "base";

    // KROK 2: Oprava konstruktoru ResourceLocation
    private static final ResourceLocation BASE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "textures/entity/small_balloon_base.png");

    public SmallBalloonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SmallBalloonModel());

        addRenderLayer(new BaseRenderLayer(this));
    }

    @Override
    public void render(SmallBalloonEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        GeoBone baseBone = getGeoModel().getBone(BASE_BONE_NAME).orElse(null);

        if (baseBone != null) {
            baseBone.setHidden(true);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (baseBone != null) {
            baseBone.setHidden(false);
        }
    }

    protected static class BaseRenderLayer extends GeoRenderLayer<SmallBalloonEntity> {
        public BaseRenderLayer(GeoEntityRenderer<SmallBalloonEntity> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void renderForBone(PoseStack poseStack, SmallBalloonEntity animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  float partialTick, int packedLight, int packedOverlay) {

            if (bone.getName().equals(BASE_BONE_NAME)) {
                // KROK 1: Dočasně nastavíme kost jako viditelnou, aby ji renderRecursively mohlo vykreslit.
                bone.setHidden(false);

                // KROK 2: Získáme správný buffer a typ pro naši texturu
                RenderType baseRenderType = RenderType.entityCutoutNoCull(BASE_TEXTURE);
                VertexConsumer baseBuffer = bufferSource.getBuffer(baseRenderType);

                // KROK 3: Vykreslíme kost (nyní viditelnou) s naší texturou
                getRenderer().renderRecursively(poseStack, animatable, bone, baseRenderType, bufferSource, baseBuffer,
                        true, partialTick, packedLight, packedOverlay, -1);

                // KROK 4: Vrátíme kost do skrytého stavu.
                // (Pro případ, že by po nás přišla další vrstva, která by ji neměla vidět)
                bone.setHidden(true);
            }
        }
    }
}
