package team.bytephoria.byteclans.api.validator;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.result.ClanNameValidationResult;

public interface ClanNameValidator {

    ClanNameValidationResult validate(final @NotNull String clanName);

}
