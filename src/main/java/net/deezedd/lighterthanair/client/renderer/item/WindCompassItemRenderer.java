package net.deezedd.lighterthanair.client.renderer.item;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.client.ClientWindData;
import net.deezedd.lighterthanair.item.custom.WindCompassBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

class WindCompassItemModel extends GeoModel<WindCompassBlockItem> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "geo/wind_compass.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "textures/block/wind_compass.png");
    private static final ResourceLocation ANIM = ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "animations/wind_compass.animation.json");

    @Override public ResourceLocation getModelResource(WindCompassBlockItem a) { return MODEL; }
    @Override public ResourceLocation getTextureResource(WindCompassBlockItem a) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(WindCompassBlockItem a) { return ANIM; }

    @Override
    public void setCustomAnimations(WindCompassBlockItem animatable, long instanceId, AnimationState<WindCompassBlockItem> animationState) {

        GeoBone compassBaseBone = this.getAnimationProcessor().getBone("compass_base");
        GeoBone arrowBone = this.getAnimationProcessor().getBone("arrow");
        GeoBone wind1Bone = this.getAnimationProcessor().getBone("wind1");
        GeoBone wind2Bone = this.getAnimationProcessor().getBone("wind2");

        float time = (float) animationState.getAnimationTick();

        if (wind1Bone != null)
            wind1Bone.setRotY(time * 0.003f);
        if (wind2Bone != null)
            wind2Bone.setRotY(time * -0.002f);

        // Display Context
        ItemDisplayContext displayContext = animationState.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

        int windDirectionIndex = ClientWindData.getCurrentDirection();

        if (displayContext == ItemDisplayContext.FIXED || displayContext == ItemDisplayContext.GROUND) {

            float blockWindYaw = switch (windDirectionIndex) {
                case 0 -> 0.0f;   // N
                case 1 -> 315.0f; // NE
                case 2 -> 270.0f; // E
                case 3 -> 225.0f; // SE
                case 4 -> 180.0f; // S
                case 5 -> 135.0f; // SW
                case 6 -> 90.0f;  // W
                case 7 -> 45.0f;  // NW
                default -> 0.0f;
            };

            if (displayContext == ItemDisplayContext.FIXED && compassBaseBone != null) {
                compassBaseBone.setRotY(0);
            }

            if (arrowBone != null) {
                arrowBone.setRotY((float) Math.toRadians(blockWindYaw));
            }
        }

        else if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ||
                displayContext == ItemDisplayContext.GUI) {

            float itemWindYaw = switch (windDirectionIndex) {
                case 0 -> 0.0f;   // N
                case 1 -> 45.0f;  // NE
                case 2 -> 90.0f;  // E
                case 3 -> 135.0f; // SE
                case 4 -> 180.0f; // S
                case 5 -> 225.0f; // SW
                case 6 -> 270.0f; // W
                case 7 -> 315.0f; // NW
                default -> 0.0f;
            };

            if (arrowBone != null) {
                LivingEntity player = (LivingEntity) animationState.getData(DataTickets.ENTITY);
                if (player == null) player = Minecraft.getInstance().player;

                if (player != null) {
                    float playerYaw = player.getViewYRot(animationState.getPartialTick()) + 180f;
                    float relativeYaw = playerYaw - itemWindYaw;
                    arrowBone.setRotY((float) Math.toRadians(relativeYaw));
                }
            }
        }

        else if (arrowBone != null) {
            float fallbackYaw = switch (windDirectionIndex) {
                case 0 -> 0.0f;   // N... atd.
                default -> 0.0f;
            };
            arrowBone.setRotY((float) Math.toRadians(fallbackYaw));
        }
    }
}

public class WindCompassItemRenderer extends GeoItemRenderer<WindCompassBlockItem> {
    public WindCompassItemRenderer() {
        super(new WindCompassItemModel());
    }
}