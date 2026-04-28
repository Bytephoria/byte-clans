package team.bytephoria.byteclans.bukkitapi.validator;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.result.ClanDisplayNameValidationResult;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;

import java.util.regex.Pattern;

public final class LegacyAmpersandClanDisplayNameValidator implements ClanDisplayNameValidator {

    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9 &:#/_\\-!?.]+");

    @Override
    public ClanDisplayNameValidationResult validate(
            final @NotNull String originalName,
            final @NotNull String newDisplayName
    ) {
        if (INVALID_CHARS_PATTERN.matcher(newDisplayName).find()) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        final String stripped = newDisplayName
                .replaceAll("&[0-9a-fk-orA-FK-OR]", "")
                .replaceAll("&#[0-9a-fA-F]{6}", "");

        if (!stripped.equalsIgnoreCase(originalName)) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        return ClanDisplayNameValidationResult.VALID;
    }
}