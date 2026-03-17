package team.bytephoria.byteclans.core.validator;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.api.result.ClanDisplayNameValidationResult;

import java.util.regex.Pattern;

public final class DefaultClanDisplayNameValidator implements ClanDisplayNameValidator {

    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9 &:#<>/_\\-!?.]+");

    @Override
    public ClanDisplayNameValidationResult validate(
            final @NotNull String originalName,
            final @NotNull String newDisplayName
    ) {

        if (INVALID_CHARS_PATTERN.matcher(newDisplayName).matches()) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        final String stripped = newDisplayName
                .replaceAll("&[0-9a-fk-orA-FK-OR]", "")
                .replaceAll("&#[0-9a-fA-F]{6}", "")
                .replaceAll("<[^>]+>", "");

        if (!stripped.equalsIgnoreCase(originalName)) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        return ClanDisplayNameValidationResult.VALID;
    }

}
