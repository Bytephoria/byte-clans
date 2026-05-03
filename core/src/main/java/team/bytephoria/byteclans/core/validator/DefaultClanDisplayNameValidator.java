package team.bytephoria.byteclans.core.validator;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.api.result.ClanDisplayNameValidationResult;

import java.util.regex.Pattern;

public final class DefaultClanDisplayNameValidator implements ClanDisplayNameValidator {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    public ClanDisplayNameValidationResult validate(
            final @NotNull String originalName,
            final @NotNull String newDisplayName
    ) {

        if (!VALID_PATTERN.matcher(newDisplayName).matches()) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        return ClanDisplayNameValidationResult.VALID;
    }

}