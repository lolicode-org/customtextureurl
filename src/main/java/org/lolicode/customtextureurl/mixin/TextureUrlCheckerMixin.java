package org.lolicode.customtextureurl.mixin;
import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import org.lolicode.customtextureurl.CustomTextureURL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = TextureUrlChecker.class)
public class TextureUrlCheckerMixin {
    @Shadow @Final @Mutable private static List<String> ALLOWED_DOMAINS;
    @Shadow @Final @Mutable private static List<String> BLOCKED_DOMAINS;

    @Shadow private static boolean isDomainOnList(String domain, List<String> list) { return false; }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClinit(CallbackInfo ci) {
        Set<String> allowed = new HashSet<>(ALLOWED_DOMAINS);
        allowed.addAll(CustomTextureURL.CONFIG.allowedDomains());
        ALLOWED_DOMAINS = List.copyOf(allowed);

        Set<String> blocked = new HashSet<>(BLOCKED_DOMAINS);
        blocked.removeIf(allowed::contains);
        blocked.addAll(CustomTextureURL.CONFIG.blockedDomains());
        BLOCKED_DOMAINS = List.copyOf(blocked);
    }

    @Redirect(
            method = "isAllowedTextureDomain(Ljava/lang/String;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/authlib/yggdrasil/TextureUrlChecker;isDomainOnList(Ljava/lang/String;Ljava/util/List;)Z",
                    ordinal = 0
            )
    )
    private static boolean redirectWhitelistCheck(String decodedDomain, List<String> list) {
        if (CustomTextureURL.CONFIG.allowAnyDomain()) {
            return true;
        }
        return isDomainOnList(decodedDomain, list);
    }

    @Redirect(
            method = "isAllowedTextureDomain(Ljava/lang/String;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/authlib/yggdrasil/TextureUrlChecker;isDomainOnList(Ljava/lang/String;Ljava/util/List;)Z",
                    ordinal = 1
            )
    )
    private static boolean redirectBlacklistCheck(String decodedDomain, List<String> list) {
        if (CustomTextureURL.CONFIG.allowAnyDomain()) {
            return false;
        }
        return isDomainOnList(decodedDomain, list);
    }
}
