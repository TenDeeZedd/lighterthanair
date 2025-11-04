package net.deezedd.lighterthanair.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractBalloonEntity extends Entity {

    // Uložená data pro synchronizaci barvy z serveru na klienta
    private static final EntityDataAccessor<String> DATA_COLOR =
            SynchedEntityData.defineId(AbstractBalloonEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID =
            SynchedEntityData.defineId(AbstractBalloonEntity.class, EntityDataSerializers.INT);

    public AbstractBalloonEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setInvulnerable(false); // Balón lze zničit
    }

    // --- Ukládání dat (barva) ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        // Defaultní barva je "brown"
        pBuilder.define(DATA_COLOR, "brown");
        pBuilder.define(DATA_VARIANT_ID, 0); // 0 = default, budoucí varianty budou mít > 0
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        // Načtení barvy při načítání chunku
        if (pCompound.contains("BalloonColor", CompoundTag.TAG_STRING)) {
            this.setColor(pCompound.getString("BalloonColor"));
        }
        if (pCompound.contains("BalloonVariant", CompoundTag.TAG_INT)) {
            this.setVariantId(pCompound.getInt("BalloonVariant"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        // Uložení barvy při ukládání chunku
        pCompound.putString("BalloonColor", this.getColor());
        pCompound.putInt("BalloonVariant", this.getVariantId());
    }

    // Public metody pro nastavení a získání barvy
    public void setColor(String color) {
        this.entityData.set(DATA_COLOR, color);
    }

    public String getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setVariantId(int id) {
        this.entityData.set(DATA_VARIANT_ID, id);
    }

    public int getVariantId() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    // --- Zničitelnost ---

    @Override
    public boolean isPushable() {
        return true; // Balón lze odstrčit
    }

    @Override
    public boolean isPickable() {
        // Umožňuje hráči zasáhnout hitbox (pro zničení)
        return !this.isRemoved();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerable() || this.isRemoved()) {
            return false;
        }

        // Prozatím se balón zničí okamžitě
        // TODO: Přidat životy (HP)

        this.discard(); // Odstraní entitu ze světa

        // TODO: Dropnout itemy (až budeme mít "sbalení")

        return true;
    }
}
