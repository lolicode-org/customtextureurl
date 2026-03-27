package org.lolicode.customtextureurl.mixin;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lolicode.customtextureurl.CustomTextureURL.CONFIG;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Mixin(value = TextureUrlChecker.class)
public class TextureUrlCheckerMixin {
    @Shadow @Final @Mutable private static Set<String> ALLOWED_DOMAINS;

    @Unique
    private static final Set<String> BLOCKED_DOMAINS = new HashSet<>();

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClinit(CallbackInfo ci) {
        Set<String> allowed = new HashSet<>(ALLOWED_DOMAINS);
        allowed.addAll(CONFIG.allowedDomains().stream()
                .map(i -> i.toLowerCase(Locale.ROOT)).toList());
        ALLOWED_DOMAINS = Set.copyOf(allowed);

        BLOCKED_DOMAINS.addAll(CONFIG.blockedDomains().stream()
                .map(i -> i.toLowerCase(Locale.ROOT)).toList());
        // The authlib will check if provided domain is lower-cased, and will unconditionally reject it if not
        // So we should make sure that all domains are lower-cased as well
    }

    @Redirect(
            method = "isAllowedTextureDomain(Ljava/lang/String;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z",
                    ordinal = 1
            )
    )
    private static boolean redirectWhitelistCheck(Set instance, Object decodedDomain) {
        assert instance == ALLOWED_DOMAINS : "Expected instance to be ALLOWED_DOMAINS, but got " + instance;
        assert decodedDomain instanceof String : "Expected decodedDomain to be a String, but got " + decodedDomain.getClass();

        if (CONFIG.allowAnyDomain()) {
            return true;
        }

        String domain = ((String) decodedDomain);
        return !isDomainOnList(domain, BLOCKED_DOMAINS) && isDomainOnList(domain, ALLOWED_DOMAINS);
    }

    @Unique
    private static boolean isDomainOnList(final String domain, final Set<String> list) {
        for (String entry : list) {
            if (entry.startsWith("*")) {
                if (domain.endsWith(entry.substring(1))) {
                    return true;
                }
            } else if (entry.startsWith(".")) {
                if (domain.endsWith(entry)) {
                    return true;
                }
            } else {
                if (domain.equals(entry)) {
                    return true;
                }
            }
        }
        return false;
    }
}
