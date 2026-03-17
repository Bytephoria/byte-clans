package team.bytephoria.byteclans.api.validator;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.result.ClanDisplayNameValidationResult;

public interface ClanDisplayNameValidator {

    ClanDisplayNameValidationResult validate(
            final @NotNull String originalName,
            final @NotNull String newDisplayName
    );

}
