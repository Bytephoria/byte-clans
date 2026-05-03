package team.bytephoria.byteclans.bukkitapi.validator;

import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.result.ClanDisplayNameValidationResult;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MiniMessageClanDisplayNameValidator implements ClanDisplayNameValidator {

    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9 :#<>/_\\-!?.]+");
    private static final Pattern TAG_PATTERN = Pattern.compile("<([^>]+)>");

    @Override
    public ClanDisplayNameValidationResult validate(
            final @NotNull String originalName,
            final @NotNull String newDisplayName
    ) {
        if (INVALID_CHARS_PATTERN.matcher(newDisplayName).find()) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        if (this.hasInvalidOrUnbalancedTags(newDisplayName)) {
            return ClanDisplayNameValidationResult.INVALID_FORMAT;
        }

        final String stripped = TAG_PATTERN.matcher(newDisplayName).replaceAll("");
        if (!stripped.equalsIgnoreCase(originalName)) {
            return ClanDisplayNameValidationResult.INVALID_CHARACTERS;
        }

        return ClanDisplayNameValidationResult.VALID;
    }

    private boolean hasInvalidOrUnbalancedTags(final @NotNull String input) {
        final Deque<String> stack = new ArrayDeque<>();
        final Matcher tagMatcher = TAG_PATTERN.matcher(input);

        while (tagMatcher.find()) {
            final String tagContent = tagMatcher.group(1).toLowerCase();
            final boolean isClosing = tagContent.startsWith("/");
            final String tagName = tagContent.replaceFirst("^/", "").split(":")[0];

            if (tagName.matches("#[0-9a-fA-F]{6}")) {
                continue;
            }

            if (isClosing) {
                if (!stack.contains(tagName)) {
                    return true;
                }

                while (!stack.isEmpty() && !stack.peek().equals(tagName)) {
                    stack.pop();
                }

                stack.pop();
            } else {
                if (!this.isKnownTag(tagName)) {
                    return true;
                }

                stack.push(tagName);
            }
        }

        return false;
    }

    private boolean isKnownTag(final @NotNull String tagName) {
        return StandardTags.defaults().has(tagName);
    }

}