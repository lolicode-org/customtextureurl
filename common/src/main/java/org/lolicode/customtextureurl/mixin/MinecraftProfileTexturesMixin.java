package org.lolicode.customtextureurl.mixin;

import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import org.lolicode.customtextureurl.CustomTextureURL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftProfileTextures.class, remap = false)
public abstract class MinecraftProfileTexturesMixin {
    @Inject(method = "signatureState", at = @At("HEAD"), cancellable = true)
    private void modifySignatureState(CallbackInfoReturnable<SignatureState> cir) {
        if (CustomTextureURL.CONFIG.skipSignatureCheck()) {
            cir.setReturnValue(SignatureState.SIGNED);
        }
    }
}
