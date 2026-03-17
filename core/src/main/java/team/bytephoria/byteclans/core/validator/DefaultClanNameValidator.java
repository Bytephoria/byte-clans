package team.bytephoria.byteclans.core.validator;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.result.ClanNameValidationResult;
import team.bytephoria.byteclans.api.validator.ClanNameValidator;
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.spi.storage.ClanStorage;

import java.util.UUID;
import java.util.regex.Pattern;

public final class DefaultClanNameValidator implements ClanNameValidator {

    private static final Pattern CLAN_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

    private final ClanGlobalSettings globalSettings;
    private final ClanStorage clanStorage;

    public DefaultClanNameValidator(
            final @NotNull ClanGlobalSettings globalSettings,
            final @NotNull ClanStorage clanStorage
    ) {
        this.globalSettings = globalSettings;
        this.clanStorage = clanStorage;
    }

    @Override
    public ClanNameValidationResult validate(final @NotNull String clanName) {
        final int length = clanName.length();
        if (length < this.globalSettings.minimumNameChars()) {
            return ClanNameValidationResult.NAME_TOO_SHORT;
        } else if (length > this.globalSettings.maximumNameChars()) {
            return ClanNameValidationResult.NAME_TOO_LONG;
        }

        if (!CLAN_NAME_PATTERN.matcher(clanName).matches()) {
            return ClanNameValidationResult.NAME_INVALID_CHARACTERS;
        }

        final UUID clanUniqueId = ClanNameUUID.from(clanName);
        if (this.clanStorage.existsByUniqueId(clanUniqueId)) {
            return ClanNameValidationResult.NAME_TAKEN;
        }

        return ClanNameValidationResult.VALID;
    }
}
