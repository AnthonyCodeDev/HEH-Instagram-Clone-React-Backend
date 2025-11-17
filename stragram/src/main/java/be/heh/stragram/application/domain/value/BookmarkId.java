package be.heh.stragram.application.domain.value;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class BookmarkId {
    UUID value;

    public static BookmarkId generate() {
        return of(UUID.randomUUID());
    }

    public static BookmarkId fromString(String id) {
        return of(UUID.fromString(id));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
